package com.aserbao.androidcustomcamera.whole.pickvideo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by Vincent Woo
 * Date: 2016/10/21
 * Time: 16:50
 */

public class Util {
    public static boolean detectIntent(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static String getDurationString(long duration) {
//        long days = duration / (1000 * 60 * 60 * 24);
        long hours = (duration % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (duration % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (duration % (1000 * 60)) / 1000;

        String hourStr = (hours < 10) ? "0" + hours : hours + "";
        String minuteStr = (minutes < 10) ? "0" + minutes : minutes + "";
        String secondStr = (seconds < 10) ? "0" + seconds : seconds + "";

        if (hours != 0) {
            return hourStr + ":" + minuteStr + ":" + secondStr;
        } else {
            return minuteStr + ":" + secondStr;
        }
    }

    public static int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Extract the file name in a URL
     * /storage/emulated/legacy/Download/sample.pptx = sample.pptx
     *
     * @param url String of a URL
     * @return the file name of URL with suffix
     */
    public static String extractFileNameWithSuffix(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * Extract the file name in a URL
     * /storage/emulated/legacy/Download/sample.pptx = sample
     *
     * @param url String of a URL
     * @return the file name of URL without suffix
     */
    public static String extractFileNameWithoutSuffix(String url) {
        try {
            return url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Extract the path in a URL
     * /storage/emulated/legacy/Download/sample.pptx = /storage/emulated/legacy/Download/
     *
     * @param url String of a URL
     * @return the path of URL with the file separator
     */
    public static String extractPathWithSeparator(String url) {
        return url.substring(0, url.lastIndexOf("/") + 1);
    }

    /**
     * Extract the path in a URL
     * /storage/emulated/legacy/Download/sample.pptx = /storage/emulated/legacy/Download
     *
     * @param url String of a URL
     * @return the path of URL without the file separator
     */
    public static String extractPathWithoutSeparator(String url) {
        return url.substring(0, url.lastIndexOf("/"));
    }

    /**
     * Extract the suffix in a URL
     * /storage/emulated/legacy/Download/sample.pptx = pptx
     *
     * @param url String of a URL
     * @return the suffix of URL
     */
    public static String extractFileSuffix(String url) {
        if (url.contains(".")) {
            return url.substring(url.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
