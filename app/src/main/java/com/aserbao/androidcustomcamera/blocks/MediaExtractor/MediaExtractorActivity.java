package com.aserbao.androidcustomcamera.blocks.MediaExtractor;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.interfaces.IDetailCallBackListener;
import com.aserbao.androidcustomcamera.blocks.MediaExtractor.combineTwoVideo.CombineTwoVideos;
import com.aserbao.androidcustomcamera.blocks.MediaExtractor.primary.DecoderAudio;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.MediaFormat.MIMETYPE_AUDIO_AAC;
import static android.media.MediaFormat.MIMETYPE_AUDIO_MPEG;

public class MediaExtractorActivity extends AppCompatActivity implements IDetailCallBackListener{

    private DecoderAudio decoderAAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_extractor);
        ButterKnife.bind(this);
    }

    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    @OnClick({R.id.extractor_video_and_audio, R.id.extractor_start2_btn,R.id.decoder_aac_and_player,R.id.decoder_mp3_and_player})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.extractor_video_and_audio:
                CombineTwoVideos.combineTwoVideos(path + "/five.mp3", 0, path + "/lan.mp4", new File(path + "/aserbao.mp4"),this);
                break;
            case R.id.extractor_start2_btn:
                CombineTwoVideos.combineTwoVideos(path + "/cai.mp4", 0, path + "/lan.mp4", new File(path + "/aserbao.mp3"),this);
                break;
            case R.id.decoder_aac_and_player:
                String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aac.aac";
                decoderAAC = new DecoderAudio();
                decoderAAC.start(audioPath,MIMETYPE_AUDIO_AAC);
                break;
            case R.id.decoder_mp3_and_player:
                String audioMp3Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/five.mp3";
                decoderAAC = new DecoderAudio();
                decoderAAC.start(audioMp3Path,MIMETYPE_AUDIO_MPEG);
                break;
        }
    }

    @Override
    public void success() {
        Toast.makeText(MediaExtractorActivity.this, "成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failed(Exception e) {
        Toast.makeText(MediaExtractorActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
    }
}
