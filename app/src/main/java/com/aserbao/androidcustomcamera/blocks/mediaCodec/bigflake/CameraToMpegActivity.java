package com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.whole.videoPlayer.VideoPlayerActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraToMpegActivity extends AppCompatActivity {
    CameraToMpegTest mCameraMpeg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigflake_item);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.start, R.id.player})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start:
                mCameraMpeg = new CameraToMpegTest();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCameraMpeg != null) {
                            try {
                                mCameraMpeg.testEncodeCameraToMp4();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();
                break;
            case R.id.player:
                if (mCameraMpeg != null) {
                    String mOutputPath = mCameraMpeg.getOutputPath();
                    if (!TextUtils.isEmpty(mOutputPath)) {
                        VideoPlayerActivity.launch(CameraToMpegActivity.this, mOutputPath);
                    }
                }
                break;
        }
    }
}
