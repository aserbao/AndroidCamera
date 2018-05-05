package com.aserbao.androidcustomcamera.blocks.mediacodec.primary;

import android.os.Bundle;
import android.widget.Button;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrimaryMediaCodecActivity extends BaseActivity {

    @BindView(R.id.btn_recording)
    Button mBtnRecording;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_primary_media_codec;
    }

    @OnClick(R.id.btn_recording)
    public void onViewClicked() {
        if(mBtnRecording.getText().equals("开始录制")){
            mBtnRecording.setText("停止录制");
        }else if(mBtnRecording.getText().equals("停止录制")){
            mBtnRecording.setText("开始录制");
        }
    }
}
