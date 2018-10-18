package com.starwars.starwarssearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to retrieve and parse JSON from the Star Wars API
 */

public class StarWarsJsonParser {

    private static final String TAG = StarWarsJsonParser.class.getSimpleName();

    private String mCharset = "UTF-8";
    private HttpURLConnection mConn;
    private DataOutputStream mOutputStream;
    private BufferedReader mBufferedReader;
    private StringBuilder mResult;
    private URL mUrlObject;
    private JSONObject mJsonObject = null;
    private StringBuilder mParamsBuilder;
    private String mParamsString;

    public static final String POST = "POST";
    public static final String GET = "GET";

    private static final String RESULTS = "results";
    private static final String NAME = "name";
    private static final String HEIGHT = "height";
    private static final String HAIR_COLOR = "hair_color";
    private static final String EYE_COLOR = "eye_color";
    private static final String BIRTH_YEAR = "birth_year";
    private static final String FILMS = "films";

    private static final String TITLE = "title";
    private static final String OPENING_CRAWL = "opening_crawl";
    private static final String DIRECTOR = "director";
    private static final String PRODUCER = "producer";
    private static final String RELEASE_DATE = "release_date";
    private static final String URL = "url";

    public List<Person> getPersonListFromJsonObject(JSONObject personJsonObject) {
        List<Person> personList = new ArrayList<>();
        try {
            JSONArray personJsonArray = personJsonObject.getJSONArray(RESULTS);
            for (int i = 0; i < personJsonArray.length(); i++) {
                JSONObject personObj = personJsonArray.getJSONObject(i);
                String name = personObj.getString(NAME);
                String height = personObj.getString(HEIGHT);
                String hairColor = personObj.getString(HAIR_COLOR);
                String eyeColor = personObj.getString(EYE_COLOR);
                String birthYear = personObj.getString(BIRTH_YEAR);
                List<String> filmList = new ArrayList<>();
                JSONArray filmArray = personObj.getJSONArray(FILMS);
                for (int j = 0; j < filmArray.length(); j++) {
                    String filmUrl = filmArray.getString(j);
                    filmList.add(filmUrl);
                }
                Log.d(TAG, "found person: " + name);
                Person person = new Person(name, height, hairColor, eyeColor, birthYear, filmList);
                personList.add(person);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return personList;
    }

    public Film getFilmFromJsonObject(JSONObject filmJsonObject) {
        Film film = null;
        try {
            String title = filmJsonObject.getString(TITLE);
            String openingCrawl = filmJsonObject.getString(OPENING_CRAWL);
            String director = filmJsonObject.getString(DIRECTOR);
            String producer = filmJsonObject.getString(PRODUCER);
            String releaseDate = filmJsonObject.getString(RELEASE_DATE);
            String url = filmJsonObject.getString(URL);
            film = new Film(title, director, producer, releaseDate, openingCrawl, url);
            Log.d(TAG, "found film: " + title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return film;
    }

    public JSONObject makeHttpRequest(String urlString, String method, Map<String, String> params) {

        mParamsBuilder = new StringBuilder();
        if (params != null) {
            int i = 0;
            for (String key : params.keySet()) {
                try {
                    if (i != 0) {
                        mParamsBuilder.append("&");
                    }
                    mParamsBuilder.append(key).append("=")
                            .append(URLEncoder.encode(params.get(key), mCharset));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                i++;
            }
        }

        if (method.equals(POST)) {
            // request method is POST
            try {
                mUrlObject = new URL(urlString);
                mConn = (HttpURLConnection) mUrlObject.openConnection();
                mConn.setDoOutput(true);
                mConn.setRequestMethod(POST);
                mConn.setRequestProperty("Accept-Charset", mCharset);
                mConn.setReadTimeout(10000);
                mConn.setConnectTimeout(15000);
                mConn.connect();

                mParamsString = mParamsBuilder.toString();
                mOutputStream = new DataOutputStream(mConn.getOutputStream());
                mOutputStream.writeBytes(mParamsString);
                mOutputStream.flush();
                mOutputStream.close();
                mOutputStream = null;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //Just to be on the safe side...
                if (mOutputStream != null) {
                    try {
                        mOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if(method.equals(GET)){
            // request method is GET

            if (mParamsBuilder.length() != 0) {
                mParamsBuilder.insert(0, "?");
                urlString += mParamsBuilder.toString();
            }
            Log.d(TAG, "GET urlString: " + urlString);
            try {
                mUrlObject = new URL(urlString);
                mConn = (HttpURLConnection) mUrlObject.openConnection();
                mConn.setDoOutput(false);
                mConn.setRequestMethod(GET);
                mConn.setRequestProperty("Accept-Charset", mCharset);
                mConn.setConnectTimeout(15000);
                mConn.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(mConn.getInputStream());
            mBufferedReader = new BufferedReader(new InputStreamReader(in));
            mResult = new StringBuilder();
            String line;
            while ((line = mBufferedReader.readLine()) != null) {
                mResult.append(line);
            }

            mBufferedReader.close();
            mBufferedReader = null;
            Log.d(TAG, "mResult: " + mResult.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mBufferedReader != null) {
                try {
                    mBufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        mConn.disconnect();

        // try to parse the string to a JSON object
        try {
            mJsonObject = new JSONObject(mResult.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        // return JSON Object
        return mJsonObject;
    }

}
