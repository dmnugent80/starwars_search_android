package com.starwars.starwarssearch;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing an individual Person
 */

public class Person implements Parcelable {
    public String mName;
    public String mHeight;
    public String mHairColor;
    public String mEyeColor;
    public String mBirthYear;
    public List<String> mFilms;

    public Person(String name, String height, String hairColor, String eyeColor, String birthYear, List<String> films) {
        mName = name;
        mHeight = height;
        mHairColor = hairColor;
        mEyeColor = eyeColor;
        mBirthYear = birthYear;
        mFilms = films;
    }

    protected Person(Parcel in) {
        mName = in.readString();
        mHeight = in.readString();
        mHairColor = in.readString();
        mEyeColor = in.readString();
        mBirthYear = in.readString();
        if (in.readByte() == 0x01) {
            mFilms = new ArrayList<String>();
            in.readList(mFilms, String.class.getClassLoader());
        } else {
            mFilms = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mHeight);
        dest.writeString(mHairColor);
        dest.writeString(mEyeColor);
        dest.writeString(mBirthYear);
        if (mFilms == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mFilms);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
