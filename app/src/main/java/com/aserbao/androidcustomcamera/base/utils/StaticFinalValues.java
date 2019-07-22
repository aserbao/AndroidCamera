package com.aserbao.androidcustomcamera.base.utils;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */


public class StaticFinalValues {
    //int final
    public static final int RECORD_MIN_TIME = 5 * 1000;
    //=======================handler
    public static final int DELAY_DETAL = 1;
    public static final int MY_TOPIC_ADAPTER = 9;
    public static final int CHANGE_IMAGE = 10;
    public static final int OVER_CLICK = 11;//视频定时结束
    //================================================path
    public static final String SAVETOPHOTOPATH = "/storage/emulated/0/DCIM/Camera/";//保存至本地相册路径
    public static final String ISSAVEVIDEOTEMPEXIST = "/storage/emulated/0/ych/drafts";
    public static final String VIDEOTEMP = "/storage/emulated/0/ych/videotemp/";

    //======================string
    public static final String MAX_NUMBER = "MaxNumber";
    public static final String RESULT_PICK_VIDEO = "ResultPickVideo";
    public static final String VIDEOFILEPATH = "VideoFilePath";
    public static final String MISNOTCOMELOCAL = "mIsNotComeLocal";//0表示本地视频，1表示非本地视频
    public static final String BUNDLE = "bundle";
    private static final String VIDEO_PATH = "video_path";

    //======================int
    public static final int REQUEST_CODE_PICK_VIDEO = 0x200;
    public static final int REQUEST_CODE_TAKE_VIDEO = 0x201;
}
