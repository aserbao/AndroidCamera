package com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.mp3TranslateAAC;

import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.blocks.MediaExtractor.combineTwoVideo.CombineTwoVideos;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Mp3TranslateAACActivity extends AppCompatActivity {

    private AudioCodec audioCodec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3_translate_aac);
        ButterKnife.bind(this);
    }

    String path = Environment.getExternalStorageDirectory().getAbsolutePath();

    @OnClick({R.id.start1_btn, R.id.start2_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.start1_btn:
                audioCodec = AudioCodec.newInstance();
                audioCodec.setEncodeType(MediaFormat.MIMETYPE_AUDIO_MPEG);
//        audioCodec.setIOPath(path + "/codec.aac", path + "/five.mp3");
                audioCodec.setIOPath(path + "/five.mp3", path + "/codec.aac");
                audioCodec.prepare();
                audioCodec.startAsync();
                audioCodec.setOnCompleteListener(new AudioCodec.OnCompleteListener() {
                    @Override
                    public void completed() {
                        Toast.makeText(Mp3TranslateAACActivity.this, "成功", Toast.LENGTH_SHORT).show();
                        audioCodec.release();
                    }
                });
                break;
            case R.id.start2_btn:
                break;
        }
    }
}
