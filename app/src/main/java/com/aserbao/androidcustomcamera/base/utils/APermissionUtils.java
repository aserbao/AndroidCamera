package com.aserbao.androidcustomcamera.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * 功能:
 *
 * @author aserbao
 * @date : On 2019/1/4 5:37 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.base.utils
 * @Copyright: 个人版权所有
 */
public class APermissionUtils {

    public static final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
    };

    /**
     * 检测权限
     * @param activity
     */
    public static void checkPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, BASIC_PERMISSIONS, 1);
        // TODO: 2019/1/4 之后再完善权限请求
        /*for (String basicPermission : BASIC_PERMISSIONS) {
            Log.e("wer", "checkPermission: " );
            if (ContextCompat.checkSelfPermission(activity, basicPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{basicPermission}, 1);
            }
        }*/
    }
}
