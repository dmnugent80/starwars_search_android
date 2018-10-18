package com.starwars.starwarssearch;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements
        FilmListAdapter.FilmClickCallback,
        FilmSearchManager.FilmListSearchCallback {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String FILM_LIST = "film_list";

    private Person mCurrentPerson;
    private TextView mName, mYear, mHeight, mHairColor, mEyeColor;
    private RecyclerView mFilmsRecyclerView;
    private FilmListAdapter mFilmListAdapter;
    private ProgressBar mLoadingProgress;
    private FilmSearchManager mFilmSearchManager = new FilmSearchManager();
    private List<Film> mFilmList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mName = findViewById(R.id.tv_name);
        mYear = findViewById(R.id.tv_year);
        mHeight = findViewById(R.id.tv_height);
        mHairColor = findViewById(R.id.tv_hair_color);
        mEyeColor = findViewById(R.id.tv_eye_color);
        mFilmsRecyclerView = findViewById(R.id.rc_film_list);
        mLoadingProgress = findViewById(R.id.loading_progress);

        initViews(savedInstanceState);
    }

    @Override
    public void onClick(Film film) {
        launchFilmActivity(film.mUrl);
    }

    @Override
    public void onDestroy() {
        if (mFilmSearchManager != null) {
            mFilmSearchManager.unregisterFilmListCallback();
        }
        super.onDestroy();
    }

    private void initViews(Bundle savedInstanceState) {
        mCurrentPerson = PeopleSearchManager.getCurrentPerson();

        mName.setText(mCurrentPerson.mName);
        mYear.setText(mCurrentPerson.mBirthYear);
        mHeight.setText(mCurrentPerson.mHeight);
        mHairColor.setText(mCurrentPerson.mHairColor);
        mEyeColor.setText(mCurrentPerson.mEyeColor);

        mFilmListAdapter = new FilmListAdapter(mFilmList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mFilmsRecyclerView.setLayoutManager(layoutManager);
        mFilmsRecyclerView.setAdapter(mFilmListAdapter);

        if (savedInstanceState == null) {
            getFilms();
        } else {
            List<Film> savedFilmList = savedInstanceState.getParcelableArrayList(FILM_LIST);
            if (savedFilmList != null) {
                mFilmList.clear();
                mFilmList.addAll(savedFilmList);
                mFilmListAdapter.notifyDataSetChanged();
            } else {
                getFilms();
            }
        }
    }

    private void getFilms() {
        showLoadingProgress();
        mFilmSearchManager.getFilms(mCurrentPerson.mFilms, this);
    }

    @Override
    public void onGotListResult(List<Film> filmList) {
        if (!isFinishing()) {
            hideLoadingProgress();
            mFilmList.clear();
            mFilmList.addAll(filmList);
            mFilmListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onListFailure() {
        //TODO: show a failure state in the UI layout
        if (!isFinishing()) {
            Toast.makeText(App.getContext(), getString(R.string.error_text), Toast.LENGTH_LONG).show();
            hideLoadingProgress();
        }
    }

    private void launchFilmActivity(String url) {
        Intent filmIntent = new Intent(DetailActivity.this, FilmActivity.class);
        filmIntent.putExtra(FilmSearchManager.FILM_URL_EXTRA, url);
        startActivity(filmIntent);
    }

    public void showLoadingProgress() {
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    public void hideLoadingProgress() {
        mLoadingProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!mFilmList.isEmpty()) {
            outState.putParcelableArrayList(FILM_LIST, (ArrayList<? extends Parcelable>) mFilmList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
