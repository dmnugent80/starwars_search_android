package com.starwars.starwarssearch;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();
    private static Context sAppContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = this.getApplicationContext();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    public static Context getContext() {
        return sAppContext;
    }
}
