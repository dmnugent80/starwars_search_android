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

public class FilmActivity extends AppCompatActivity {
    private static final String TAG = FilmActivity.class.getSimpleName();
    public static final String FILM = "film";

    private TextView mTitle, mReleaseDate, mDirector, mProducer, mOpeningCrawl;
    private ProgressBar mLoadingProgress;
    private TableLayout mDataLayout;
    private ImageView mLogo;
    private Film mFilm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film);

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
            Intent intent = getIntent();
            if (intent != null) {
                Film film = intent.getParcelableExtra(FILM);
                showFilmLayout();
                showFilmData(film);
            }
        } else {
            Film film = savedInstanceState.getParcelable(FILM);
            if (film != null) {
                showFilmLayout();
                showFilmData(film);
            }
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
