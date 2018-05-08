package com.aserbao.androidcustomcamera.blocks.mediacodec.recordCamera;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.activity.BaseActivity;
import com.aserbao.androidcustomcamera.blocks.mediacodec.recordCamera.thread.MediaMuxerThread;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aserbao.androidcustomcamera.blocks.mediacodec.recordCamera.thread.VideoEncoderThread.IMAGE_HEIGHT;
import static com.aserbao.androidcustomcamera.blocks.mediacodec.recordCamera.thread.VideoEncoderThread.IMAGE_WIDTH;

public class RecordCameraActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    @BindView(R.id.record_camera_sv)
    SurfaceView mRecordCameraSv;
    @BindView(R.id.btn_record_status)
    Button mBtnRecordStatus;
    public SurfaceHolder mSurfaceHolder;
    public Camera mCamera;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_record_camera;
    }

    @OnClick(R.id.btn_record_status)
    public void onViewClicked() {
        if(mBtnRecordStatus.getText().equals("开始录制")){
            mBtnRecordStatus.setText("结束录制");
            MediaMuxerThread.startMuxer();
        }else if(mBtnRecordStatus.getText().equals("结束录制")){
            mBtnRecordStatus.setText("开始录制");
            MediaMuxerThread.stopMuxer();
        }
    }
    @Override
    public void initView() {
        mSurfaceHolder = mRecordCameraSv.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();

        mCamera.setDisplayOrientation(90);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        // 这个宽高的设置必须和后面编解码的设置一样，否则不能正常处理
        parameters.setPreviewSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        MediaMuxerThread.stopMuxer();
// 停止预览并释放资源
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera = null;
        }
    }

    private static final String TAG = "RecordCameraActivity";
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//        Log.e(TAG, "onPreviewFrame: " + data.toString() );
        MediaMuxerThread.addVideoFrameData(data);
    }


}
