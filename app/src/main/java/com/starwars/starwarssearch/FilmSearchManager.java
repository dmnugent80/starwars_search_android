package com.starwars.starwarssearch;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Film Search manager class.
 * This class sets up the GET request url
 * and calls into StarWarsJsonParser in order to
 * retrieve the Film for the provided
 * search url.
 */

public class FilmSearchManager {
    private static final String TAG = FilmSearchManager.class.getSimpleName();

    public static final String FILM_URL_EXTRA = "film_url_extra";

    private boolean mFilmListCancelled = false;
    private Thread mFilmListThread;
    private boolean mFilmCancelled = false;
    private Thread mFilmThread;
    private WeakReference<FilmListSearchCallback> mFilmListCallback;
    private WeakReference<FilmSearchCallback> mFilmCallback;

    public interface FilmSearchCallback {
        void onGotResult(Film film);
        void onFailure();
    }

    public interface FilmListSearchCallback {
        void onGotListResult(List<Film> filmList);
        void onListFailure();
    }

    public void getFilm(final String filmUrl, FilmSearchCallback listener) {
        mFilmCallback = new WeakReference<>(listener);
        mFilmCancelled = false;
        mFilmThread = new Thread() {
            @Override
            public void run() {
                try {
                    StarWarsJsonParser starWarsJsonParser = new StarWarsJsonParser();
                    JSONObject filmJsonObject = starWarsJsonParser.makeHttpRequest(filmUrl, StarWarsJsonParser.GET, null);

                    Log.d(TAG, "filmJsonObject: " + filmJsonObject);

                    // Exit if the process was cancelled
                    if (mFilmCancelled) return;

                    if (filmJsonObject != null) {
                        Film film = starWarsJsonParser.getFilmFromJsonObject(filmJsonObject);
                        if (film != null) {
                            onGotResult(film);
                        } else {
                            onFailure();
                        }
                    } else {
                        onFailure();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure();
                }
            }
        };
        mFilmThread.start();
    }

    public void unregisterFilmCallback() {
        mFilmCancelled = true;
        if (mFilmThread != null && mFilmThread.isAlive()) {
            mFilmThread.interrupt();
        }
    }

    public void getFilms(final List<String> filmUrls, FilmListSearchCallback listener) {
        mFilmListCallback = new WeakReference<>(listener);
        mFilmListCancelled = false;
        mFilmListThread = new Thread() {
            @Override
            public void run() {
                List<Film> filmList = new ArrayList<>();
                try {
                    StarWarsJsonParser starWarsJsonParser = new StarWarsJsonParser();
                    for (String url : filmUrls) {
                        // Exit if the process was cancelled
                        if (mFilmListCancelled) return;
                        // Get the Film info
                        JSONObject filmJsonObject = starWarsJsonParser.makeHttpRequest(url, StarWarsJsonParser.GET, null);
                        if (filmJsonObject != null) {
                            Film film = starWarsJsonParser.getFilmFromJsonObject(filmJsonObject);
                            filmList.add(film);
                            //onGotResult(film, listener);
                        } else {
                            onListFailure();
                        }
                        Log.d(TAG, "filmJsonObject: " + filmJsonObject);
                    }

                    // Exit if the process was cancelled
                    if (mFilmListCancelled) return;

                    if (filmList.size() == filmUrls.size()) {
                        onGotListResult(filmList);
                    } else {
                        onListFailure();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onListFailure();
                }
            }
        };
        mFilmListThread.start();
    }

    public void unregisterFilmListCallback() {
        mFilmListCancelled = true;
        if (mFilmListThread != null && mFilmListThread.isAlive()) {
            mFilmListThread.interrupt();
        }
    }

    private void onGotResult(final Film film) {
        //Put the callback on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FilmSearchCallback listener = mFilmCallback.get();
                if (listener != null && !mFilmCancelled) {
                    listener.onGotResult(film);
                }
            }
        });
    }

    private void onFailure() {
        //Put the callback on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FilmSearchCallback listener = mFilmCallback.get();
                if (listener != null && !mFilmCancelled) {
                    listener.onFailure();
                }
            }
        });
    }

    private void onGotListResult(final List<Film> filmList) {
        //Put the callback on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FilmListSearchCallback listener = mFilmListCallback.get();
                if (listener != null && !mFilmListCancelled) {
                    listener.onGotListResult(filmList);
                }
            }
        });
    }

    private void onListFailure() {
        //Put the callback on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                FilmListSearchCallback listener = mFilmListCallback.get();
                if (listener != null && !mFilmListCancelled) {
                    listener.onListFailure();
                }
            }
        });
    }
}
