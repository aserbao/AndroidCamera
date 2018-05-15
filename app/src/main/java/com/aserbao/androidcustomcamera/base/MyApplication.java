package com.aserbao.androidcustomcamera.base;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */


public class MyApplication extends Application {
    public static boolean DEBUG = true;
    private static Context mContext;
    public static int screenWidth;
    public static int screenHeight;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        DisplayMetrics mDisplayMetrics = getApplicationContext().getResources()
                .getDisplayMetrics();
        screenWidth = mDisplayMetrics.widthPixels;
        screenHeight = mDisplayMetrics.heightPixels;
    }

    public static Context getContext() {
        return mContext;
    }
}
