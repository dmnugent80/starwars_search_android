package com.starwars.starwarssearch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FilmActivity extends AppCompatActivity implements FilmSearchManager.FilmSearchCallback {
    private static final String TAG = FilmActivity.class.getSimpleName();
    private static final String FILM = "film";

    private TextView mTitle, mReleaseDate, mDirector, mProducer, mOpeningCrawl;
    private String mSearchUrl;
    private ProgressBar mLoadingProgress;
    private TableLayout mDataLayout;
    private ImageView mLogo;
    private Film mFilm;
    private FilmSearchManager mFilmSearchManager = new FilmSearchManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film);

        Intent intent = getIntent();
        if (intent != null) {
            mSearchUrl = intent.getStringExtra(FilmSearchManager.FILM_URL_EXTRA);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        mTitle = findViewById(R.id.tv_title);
        mReleaseDate = findViewById(R.id.tv_release_date);
        mDirector = findViewById(R.id.tv_director);
        mProducer = findViewById(R.id.tv_producer);
        mOpeningCrawl = findViewById(R.id.tv_crawl);
        mLoadingProgress = findViewById(R.id.loading_progress);
        mDataLayout = findViewById(R.id.layout_data);
        mLogo = findViewById(R.id.iv_c3);

        if (savedInstanceState == null) {
            getFilm();
        } else {
            Film film = savedInstanceState.getParcelable(FILM);
            if (film != null) {
                showFilmData(film);
            } else {
                getFilm();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mFilmSearchManager != null) {
            mFilmSearchManager.unregisterFilmCallback();
        }
        super.onDestroy();
    }

    private void getFilm() {
        if (mSearchUrl != null) {
            hideFilmLayout();
            showLoadingProgress();
            mFilmSearchManager.getFilm(mSearchUrl, this);
        }
    }

    @Override
    public void onGotResult(Film film) {
        if (!isFinishing()) {
            hideLoadingProgress();
            if (film != null) {
                showFilmLayout();
                showFilmData(film);
            }
        }
    }

    @Override
    public void onFailure() {
        //TODO: show a failure state in the UI
        Toast.makeText(App.getContext(), getString(R.string.error_text), Toast.LENGTH_LONG).show();
        if (!isFinishing()) {
            hideLoadingProgress();
        }
    }

    public void showFilmData(Film film) {
        if (film != null) {
            mFilm = film;
            mTitle.setText(mFilm.mTitle);
            mReleaseDate.setText(mFilm.mReleaseDate);
            mDirector.setText(mFilm.mDirector);
            mProducer.setText(mFilm.mProducer);
            mOpeningCrawl.setText(mFilm.mCrawl);
        }
    }

    public void showLoadingProgress() {
        mLoadingProgress.setVisibility(View.VISIBLE);
    }

    public void hideLoadingProgress() {
        mLoadingProgress.setVisibility(View.GONE);
    }

    public void showFilmLayout() {
        mDataLayout.setVisibility(View.VISIBLE);
        mLogo.setVisibility(View.VISIBLE);
    }

    public void hideFilmLayout() {
        mLogo.setVisibility(View.INVISIBLE);
        mDataLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mFilm != null) {
            outState.putParcelable(FILM, mFilm);
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
