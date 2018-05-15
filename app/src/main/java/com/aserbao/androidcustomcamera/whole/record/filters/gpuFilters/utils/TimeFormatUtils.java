package com.aserbao.androidcustomcamera.whole.record.filters.gpuFilters.utils;

/**
 * Created by Administrator on 2017/6/30 0030.
 */

public class TimeFormatUtils {
    /**
     * 00:00:00 时分秒
     * @param millisec
     * @return
     */
    public static String formatMillisec(int millisec){
        int sec=millisec/1000;
        int min=sec/60;
        int hour=min/60;
        min=min%60;
        sec=sec%60;
        String t="";
        t=hour>=10?t+hour:t+"0"+hour+":";
        t=min>=10?t+min:t+"0"+min+":";
        t=sec>=10?t+sec:t+"0"+sec;
        return t;
    }

    /**
     * 00:00 分秒
     * @param millisec
     * @return
     */
    public static String formatMillisecWithoutHours(int millisec){
        int sec=millisec/1000;
        int min=sec/60;
        sec=sec%60;
        String t="";
        t=min>=10?t+min:t+"0"+min+":";
        t=sec>=10?t+sec:t+"0"+sec;
        return t;
    }
}
