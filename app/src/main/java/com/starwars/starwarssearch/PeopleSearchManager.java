package com.starwars.starwarssearch;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * People Search manager class.
 * This class sets up the GET request url
 * and calls into StarWarsJsonParser in order to
 * retrieve the person list for the provided
 * search string.
 */

public class PeopleSearchManager {

    private static final String TAG = PeopleSearchManager.class.getSimpleName();

    private static final String PERSON_URL = "https://swapi.co/api/people/";
    private static final String SEARCH = "search";
    private static final String COUNT = "count";

    private static Person sCurrentPerson = null;

    private boolean mPeopleSearchCancelled = false;
    private Thread mPeopleSearchThread;
    private WeakReference<PersonSearchCallback> mCallback;

    public interface PersonSearchCallback {
        void onGotResult(List<Person> personList);
        void onNoResult();
        void onFailure();
    }

    public static void setCurrentPerson(Person person) {
        sCurrentPerson = person;
    }

    public static Person getCurrentPerson() {
        return sCurrentPerson;
    }

    public void searchPeople(final String searchString, PersonSearchCallback listener) {
        mCallback = new WeakReference<>(listener);
        mPeopleSearchCancelled = false;
        mPeopleSearchThread = new Thread() {
            @Override
            public void run() {
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put(SEARCH, searchString);

                    StarWarsJsonParser starWarsJsonParser = new StarWarsJsonParser();
                    JSONObject personJsonObject = starWarsJsonParser.makeHttpRequest(PERSON_URL, StarWarsJsonParser.GET, params);

                    Log.d(TAG, "personJsonObject: " + personJsonObject);

                    if (mPeopleSearchCancelled) return;

                    if (personJsonObject != null) {
                        int count = personJsonObject.getInt(COUNT);
                        if (count > 0) {
                            //Get the Person list from the JSON
                            List<Person> PersonList = starWarsJsonParser.getPersonListFromJsonObject(personJsonObject);

                            onGotResult(PersonList);
                        } else {
                            onNoResult();
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
        mPeopleSearchThread.start();
    }

    public void unregisterPeopleSearchCallback() {
        mPeopleSearchCancelled = true;
        if (mPeopleSearchThread != null && mPeopleSearchThread.isAlive()) {
            mPeopleSearchThread.interrupt();
        }
    }

    private void onGotResult(final List<Person> PersonList) {
        //Put the callback on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PersonSearchCallback listener = mCallback.get();
                if (listener != null && !mPeopleSearchCancelled) {
                    listener.onGotResult(PersonList);
                }
            }
        });
    }

    private void onNoResult() {
        //Put the callback on the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PersonSearchCallback listener = mCallback.get();
                if (listener != null && !mPeopleSearchCancelled) {
                    listener.onNoResult();
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
                PersonSearchCallback listener = mCallback.get();
                if (listener != null && !mPeopleSearchCancelled) {
                    listener.onFailure();
                }
            }
        });
    }
}
