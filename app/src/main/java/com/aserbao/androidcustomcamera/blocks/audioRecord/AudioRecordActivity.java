package com.aserbao.androidcustomcamera.blocks.audioRecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aserbao.androidcustomcamera.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AudioRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.audio_record_btn)
    public void onViewClicked() {
        new AudioRecordDemo().getNoiseLevel();
    }

}
