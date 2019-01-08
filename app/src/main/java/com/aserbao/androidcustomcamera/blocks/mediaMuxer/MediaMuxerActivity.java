package com.aserbao.androidcustomcamera.blocks.mediaMuxer;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.blocks.interfaces.ICallBackListener;
import com.aserbao.androidcustomcamera.blocks.mediaMuxer.primary.MuxerVoiceAndVideoToMp4;
import com.aserbao.androidcustomcamera.blocks.mediaMuxer.primary.MuxerVoiceDbToMp4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.MediaFormat.MIMETYPE_AUDIO_AAC;

public class MediaMuxerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_muxer);
        ButterKnife.bind(this);
    }
    String path = Environment.getExternalStorageDirectory().getAbsolutePath();

    @OnClick({R.id.muxer_aac_video_to_mp4, R.id.muxer_aac_db_to_mp4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.muxer_aac_video_to_mp4:
                new MuxerVoiceAndVideoToMp4(path + "/own.m4a", path + "/aserbao.mp4", path + "/out_aserbao.mp4", new ICallBackListener() {
                    @Override
                    public void success() {
                        Toast.makeText(MediaMuxerActivity.this, "成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(Exception e) {
                        Toast.makeText(MediaMuxerActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    }
                }).start();
                break;
            case R.id.muxer_aac_db_to_mp4:
                new MuxerVoiceDbToMp4().start(path + "/own.m4a", path + "/output_aserbao1.mp4", MIMETYPE_AUDIO_AAC, new MuxerVoiceDbToMp4.DbCallBackListener() {
                    @Override
                    public void cuurentFrequenty(int cuurentFrequenty, double volume) {
                    }
                });
                break;
        }
    }


}
