package com.aserbao.androidcustomcamera.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static com.aserbao.androidcustomcamera.base.MyApplication.getContext;


/**
 */

public class CheckPermissionUtil {

    private static String TAG = "CheckPermissionUtil";

    // 音频获取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;

    /**
     * @return true 已经授权 获取地理位置权限
     */
    public static boolean isLocationPermGrantedAndOpen(){
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG,"isLocationPermGrantedAndOpen()---  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ");
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                Log.i(TAG,"isLocationPermGrantedAndOpen()---  result = false");
                result = false;
            } else {
                if(isOPenGPS(getContext())){
                    Log.i(TAG,"isLocationPermGrantedAndOpen()---  result = true");
                    result = true;
                }
            }
        } else {
            Log.i(TAG,"isLocationPermGrantedAndOpen()---  Build.VERSION.SDK_INT < Build.VERSION_CODES.M ");
            result = true;
        }
        return result;
    }

    /**
     * @return true 照相机权限
     */
    public static boolean isCameraGranted(){
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG,"isCameraGranted()---  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ");
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                Log.i(TAG,"isCameraGranted()---  result = false");
                result = false;
            } else {
                result = true;
            }
        } else {
            Log.i(TAG,"isCameraGranted()---  Build.VERSION.SDK_INT < Build.VERSION_CODES.M ");
            result = true;
        }
        return result;
    }

    /**
     * 只用这个方法，一些华为手机的地理权限打开与否，不能准确判断出，需要上面的方法 isLocationPermGrantedAndOpen
     * @return true 已经授权 获取地理位置权限
     */
    public static boolean isLocationPermGranted(){
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
            {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = true;
        }
        return result;
    }

    /**
     * @return true
     */
    public static boolean isStoragePermGranted(){
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
            {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = true;
        }
        return result;
    }

    /**
     * Function:判断录音权限,兼容android6.0以下以及以上系统
     */

    /**
     * 判断是是否有录音权限
     */
    public static boolean isHasPermission(final Context context){
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);

        AudioRecord audioRecord = null;
        try {
            // 美图手机这里会抛 IllegalArgumentException
            // https://fabric.io/getremark/android/apps/com.getremark.spot/issues/5b719a816007d59fcdac62f0?time=last-seven-days
            audioRecord =  new AudioRecord(audioSource, sampleRateInHz,
                    channelConfig, audioFormat, bufferSizeInBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        //开始录制音频
        try{
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;

        return true;
    }

    /**
     * @return true 已经授权 获取照相机权限
     */
    public static boolean isCameraPermissionGranted(){
        return isPermissionGranted(Manifest.permission.CAMERA);
    }

    public static boolean isReadStoragePermissionsGranted(){
        return isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean isWriteStoragePermissionsGranted(){
        return isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static boolean isPermissionGranted(String permission) {
        boolean isRecorder = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                isRecorder = false;
            } else {
                isRecorder = true;
            }
        } else {
            isRecorder = true;
        }
        return isRecorder;
    }

    public static boolean isRecordAudioPermissionsGranted(){
        return isPermissionGranted(Manifest.permission.RECORD_AUDIO);
    }

    public static boolean isWriteSettingPermissionsGranted(){
        boolean isRecorder = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                isRecorder = false;
            } else {
                isRecorder = true;
            }
        } else {
            isRecorder = true;
        }
        return isRecorder;
    }

    public static boolean isContactsPermissionGranted() {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                result = false;
            } else {
                result = true;
            }
        } else {
            result = true;
        }
        Log.i(TAG, "isContactsPermissionGranted()---  result = " + result);
        return result;
    }


    public static boolean isReadSmsPermissionGranted() {
        boolean result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                result = false;
            } else {
                result = true;
            }
        }else {
            result =  false;
        }
        return  result;

    }


    public static boolean isOPenGPS(final Context context) {
        LocationManager locationManager
            = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.i("isOPenGPS()","gps = "+gps);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.i("isOPenGPS()","network = "+network);
        if (gps || network) {
            return true;
        }
        return false;
    }
}
