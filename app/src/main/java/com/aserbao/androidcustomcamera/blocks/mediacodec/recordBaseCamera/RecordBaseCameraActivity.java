package com.aserbao.androidcustomcamera.blocks.mediacodec.recordBaseCamera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.activity.BaseActivity;
import com.aserbao.androidcustomcamera.whole.videoplayer.VideoPlayerActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

public class RecordBaseCameraActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "RecordBaseCameraActivit";

    @BindView(R.id.record_base_camera_sv)
    SurfaceView mRecordBaseCameraSv;
    @BindView(R.id.btn_record_base_status)
    Button mBtnRecordBaseStatus;
    @BindView(R.id.btn_record_base_player)
    Button mBtnRecordBasePlayer;

    Camera mCamera;
    public SurfaceHolder surfaceHolder;
    int width = 1080;
    int height = 1920;
    int framerate = 30;
    H264Encoder mEncoder;


    @Override
    protected int setLayoutId() {
        return R.layout.activity_record_base_camera;
    }

    public void initView() {
        surfaceHolder = mRecordBaseCameraSv.getHolder();
        surfaceHolder.addCallback(this);
    }

    ;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mEncoder != null) {
            mEncoder.putData(data);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        choosePreviewSize(parameters, width, height);
        try {
            mCamera.setParameters(parameters);
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mEncoder = new H264Encoder(width, height, framerate);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera = null;
        }

        if (mEncoder != null) {
            mEncoder.stopEncoder();
        }
    }

    public static void choosePreviewSize(Camera.Parameters parms, int width, int height) {
        Camera.Size ppsfv = parms.getPreferredPreviewSizeForVideo();
        if (ppsfv != null) {
            Log.d(TAG, "Camera preferred preview size for video is " +
                    ppsfv.width + "x" + ppsfv.height);
        }
        for (Camera.Size size : parms.getSupportedPreviewSizes()) {
            if (size.width == width && size.height == height) {
                parms.setPreviewSize(width, height);
                return;
            }
        }

        Log.w(TAG, "Unable to set preview size to " + width + "x" + height + "useWidth:: " + ppsfv.width + "ppsfv.height:" + ppsfv.height);
        if (ppsfv != null) {
            parms.setPreviewSize(ppsfv.width, ppsfv.height);
        }
        // else use whatever the default size is
    }

    @OnClick({R.id.btn_record_base_status, R.id.btn_record_base_player})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_record_base_status:
                if(mBtnRecordBaseStatus.getText().equals("开始录制")){
                    mBtnRecordBaseStatus.setText("结束录制");
                    if (mEncoder != null) {
                        mEncoder.startEncoder();
                    }
                }else if(mBtnRecordBaseStatus.getText().equals("结束录制")){
                    mBtnRecordBaseStatus.setText("开始录制");
                    if (mEncoder != null) {
                        mEncoder.stopEncoder();
                    }
                }
                break;
            case R.id.btn_record_base_player:
                if (mEncoder != null && !TextUtils.isEmpty(mEncoder.getPath())) {
                    VideoPlayerActivity.launch(RecordBaseCameraActivity.this, mEncoder.getPath());
                }
                break;
        }
    }
}
