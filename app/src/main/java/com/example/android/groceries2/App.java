package com.example.android.groceries2;

import android.app.Application;
import android.content.Context;

/**
 * Created by takeoff on 012 12 Jul 17.
 */

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext(){
        return mContext;
    }
}