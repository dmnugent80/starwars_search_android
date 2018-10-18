package com.starwars.starwarssearch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class representing an individual Film
 */

public class Film implements Parcelable {
    public String mTitle;
    public String mDirector;
    public String mProducer;
    public String mReleaseDate;
    public String mCrawl;
    public String mUrl;

    public Film(String title, String director, String producer, String releaseDate, String crawl, String url) {
        mTitle = title;
        mDirector = director;
        mProducer = producer;
        mReleaseDate = releaseDate;
        mCrawl = crawl;
        mUrl = url;
    }

    protected Film(Parcel in) {
        mTitle = in.readString();
        mDirector = in.readString();
        mProducer = in.readString();
        mReleaseDate = in.readString();
        mCrawl = in.readString();
        mUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDirector);
        dest.writeString(mProducer);
        dest.writeString(mReleaseDate);
        dest.writeString(mCrawl);
        dest.writeString(mUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Film> CREATOR = new Parcelable.Creator<Film>() {
        @Override
        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        @Override
        public Film[] newArray(int size) {
            return new Film[size];
        }
    };
}
