package com.starwars.starwarssearch;


import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PersonListAdapter.PersonClickCallback,
        PeopleSearchManager.PersonSearchCallback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PERSON_LIST = "person_list";
    private static final String SEARCH_TERM = "search_term";

    private LinearLayout mErrorLayout;
    private LinearLayout mNoResultsLayout;
    private RecyclerView mPersonRecyclerView;
    private EditText mPersonSearchEditText;
    private ImageButton mSearchButton;
    private PersonListAdapter mPersonAdapter;
    private RelativeLayout mInitialLayout;
    private PeopleSearchManager mPeopleSearchManager = new PeopleSearchManager();
    private List<Person> mPersonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorLayout = findViewById(R.id.movie_error_layout);
        mNoResultsLayout = findViewById(R.id.no_results_layout);
        mPersonRecyclerView = findViewById(R.id.movie_recycler_view);
        mPersonSearchEditText = findViewById(R.id.search_text);
        mSearchButton = findViewById(R.id.search_button);
        mInitialLayout = findViewById(R.id.initial_layout);

        mPersonAdapter = new PersonListAdapter(mPersonList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPersonRecyclerView.setLayoutManager(layoutManager);
        mPersonRecyclerView.setAdapter(mPersonAdapter);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String personToSearch = mPersonSearchEditText.getText().toString();
                searchForPerson(personToSearch);
            }
        });

        mPersonSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "Done button clicked");
                    String personToSearch = mPersonSearchEditText.getText().toString();
                    searchForPerson(personToSearch);
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            String personToSearch = savedInstanceState.getString(SEARCH_TERM);
            List<Person> personList = savedInstanceState.getParcelableArrayList(PERSON_LIST);
            if (personList != null) {
                //We already have search results to show
                showResults();
                mPersonList.clear();
                mPersonList.addAll(personList);
                mPersonAdapter.notifyDataSetChanged();
            } else if (personToSearch != null) {
                //Just in case the user already searched, but does not have results yet
                searchForPerson(personToSearch);
            }
        }

        int options = mPersonSearchEditText.getImeOptions();
        mPersonSearchEditText.setImeOptions(options|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
    }

    @Override
    public void onDestroy() {
        if (mPeopleSearchManager != null) {
            mPeopleSearchManager.unregisterPeopleSearchCallback();
        }
        super.onDestroy();
    }

    private void searchForPerson(String personToSearch) {
        //Hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPersonSearchEditText.getWindowToken(), 0);

        Log.d(TAG, "Star Wars search: " + personToSearch);

        if (personToSearch != null && !personToSearch.isEmpty()) {
            mPeopleSearchManager.searchPeople(personToSearch, this);
        } else {
            showNoResults();
        }
    }

    @Override
    public void onGotResult(List<Person> personList) {
        if (!isFinishing()) {
            showResults();
            mPersonList.clear();
            mPersonList.addAll(personList);
            mPersonAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNoResult() {
        if (!isFinishing()) {
            showNoResults();
        }
    }

    @Override
    public void onFailure() {
        if (!isFinishing()) {
            showErrorLayout();
        }
    }

    private void showErrorLayout() {
        mInitialLayout.setVisibility(View.GONE);
        mPersonRecyclerView.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        mInitialLayout.setVisibility(View.GONE);
        mPersonRecyclerView.setVisibility(View.VISIBLE);
        mNoResultsLayout.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.GONE);
    }

    private void showNoResults() {
        mInitialLayout.setVisibility(View.GONE);
        mPersonRecyclerView.setVisibility(View.GONE);
        mNoResultsLayout.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String personToSearch = mPersonSearchEditText.getText().toString();
        if (personToSearch != null) {
            outState.putString(SEARCH_TERM, personToSearch);
        }
        if (!mPersonList.isEmpty()) {
            outState.putParcelableArrayList(PERSON_LIST, (ArrayList<? extends Parcelable>) mPersonList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(Person person) {
        PeopleSearchManager.setCurrentPerson(person);
        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        startActivity(detailIntent);
    }
}
