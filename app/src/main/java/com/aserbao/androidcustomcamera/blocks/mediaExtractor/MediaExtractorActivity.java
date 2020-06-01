package com.aserbao.androidcustomcamera.blocks.mediaExtractor;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.interfaces.IDetailCallBackListener;
import com.aserbao.androidcustomcamera.blocks.interfaces.ICallBackListener;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.combineTwoVideo.CombineTwoVideos;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.combineTwoVideo.CombineVideoAndMusic;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.FrequencyView;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.TransAacHandlerPure;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.decoder.DecoderAudioAAC2PCMPlay;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.decoder.DecoderAudioAndGetDb;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.decoder.DecoderMp3FromMp4;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.decoder.DecoderNoVoiceMp4FromMp4;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.encoder.EncoderAudioAAC;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.official.AMediaExtractorOfficial;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.media.MediaFormat.MIMETYPE_AUDIO_AAC;
import static android.media.MediaFormat.MIMETYPE_AUDIO_MPEG;

public class MediaExtractorActivity extends AppCompatActivity implements IDetailCallBackListener {
    private static final String TAG = "MediaExtractorActivity";
    @BindView(R.id.record_and_encoder_mp3)
    Button mRecordAndEncoderMp3;
    @BindView(R.id.record_mp3_stop)
    Button mRecordMp3Stop;
    @BindView(R.id.frequency_view)
    FrequencyView mFrequencyView;
    private DecoderAudioAAC2PCMPlay decoderAAC;
    private EncoderAudioAAC encoderAudioAAC;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_extractor);
        ButterKnife.bind(this);
    }

    String path = Environment.getExternalStorageDirectory().getAbsolutePath();

    @OnClick({R.id.audio_extractor_data,R.id.extractor_mp3_from_mp4,R.id.extractor_no_voice_mp4_from_mp4,R.id.extractor_video_and_audio, R.id.exchange_video_and_audio,
            R.id.decoder_aac_and_player, R.id.decoder_mp3_and_player,
            R.id.record_and_encoder_mp3, R.id.record_mp3_stop, R.id.mp3_translate_aac_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.audio_extractor_data:
                    AMediaExtractorOfficial.mediaExtractorDecoderAudio(path + "/dj_dance.mp3");
                break;
            case R.id.extractor_mp3_from_mp4:
//                new DecoderMp3FromMp4(path + "/123.mp4", path + "/out_aserbao.mp3", new ICallBackListener() {
//                new DecoderMp3FromMp4(path + "/123.mp4", path + "/out_aserbao.pcm", new ICallBackListener() {
                new DecoderMp3FromMp4(path + "/123.mp4", path + "/out_aserbao.mp3", new ICallBackListener() {
                    @Override
                    public void success() {
                        Toast.makeText(MediaExtractorActivity.this, "成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(Exception e) {
                        Toast.makeText(MediaExtractorActivity.this, "失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }).start();
                break;
            case R.id.extractor_no_voice_mp4_from_mp4:
                new DecoderNoVoiceMp4FromMp4(path + "/lan.mp4", path + "/out_aserbao", new ICallBackListener() {
                    @Override
                    public void success() {
                        Toast.makeText(MediaExtractorActivity.this, "成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(Exception e) {
                        Toast.makeText(MediaExtractorActivity.this, "失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }).start();
                break;
            case R.id.extractor_video_and_audio:
                String audioMp3Path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/five.mp3";
//                String audioMp3Path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aac.aac";
//                String audioMp3Path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/own.m4a";
                DecoderAudioAndGetDb decoderAudioAndGetDb = new DecoderAudioAndGetDb();
                decoderAudioAndGetDb.start(audioMp3Path1, MIMETYPE_AUDIO_MPEG, new DecoderAudioAndGetDb.DbCallBackListener() {
                    @Override
                    public void cuurentFrequenty(int cuurentFrequenty, double volume) {
                        mFrequencyView.addInt(cuurentFrequenty/100);
                    }
                });
//                decoderAudioAndGetDb.start(audioMp3Path1, MIMETYPE_AUDIO_MPEG);
                break;
            case R.id.exchange_video_and_audio:
//                CombineTwoVideos.combineTwoVideos(path + "/aserbao.mp4", 0, path + "/lan.mp4", new File(path + "/aserbao.mp3"), this);
                String inputVideo = "/storage/emulated/0/douyin.mp4";
                String outputVideo = "/storage/emulated/0/douyinOut.mp4";
                String inputMusic = "/storage/emulated/0/pg/.bgm/40e613e5e3695ab44b4f31e25088d7ac";
                CombineVideoAndMusic.combineTwoVideos(inputMusic, 0, inputVideo, new File(outputVideo), this);
                break;
            case R.id.decoder_aac_and_player:
                String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aac.aac";
                decoderAAC = new DecoderAudioAAC2PCMPlay();
                decoderAAC.start(audioPath, MIMETYPE_AUDIO_AAC);
                break;
            case R.id.decoder_mp3_and_player:
                String audioMp3Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/five.mp3";
                decoderAAC = new DecoderAudioAAC2PCMPlay();
                decoderAAC.start(audioMp3Path, MIMETYPE_AUDIO_MPEG);
                break;
            case R.id.record_and_encoder_mp3:
                mRecordMp3Stop.setVisibility(View.VISIBLE);
                mRecordAndEncoderMp3.setVisibility(View.GONE);
                String encoderAACPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/encoder_aac.aac";
                if (encoderAudioAAC == null) {
                    encoderAudioAAC = new EncoderAudioAAC();
                }
                encoderAudioAAC.start(encoderAACPath);
                break;
            case R.id.record_mp3_stop:
                mRecordAndEncoderMp3.setVisibility(View.VISIBLE);
                mRecordMp3Stop.setVisibility(View.GONE);
                if (encoderAudioAAC != null) {
                    encoderAudioAAC.stop();
                    encoderAudioAAC = null;
                }
                break;
            case R.id.mp3_translate_aac_btn:
                TransAacHandlerPure aacHandlerPure = new TransAacHandlerPure(path + "/bell.mp3", path + "/codec.aac");
                aacHandlerPure.setListener(new TransAacHandlerPure.OnProgressListener() {
                    @Override
                    public void onStart() {
                        startTime = System.currentTimeMillis();
                        Log.e(TAG, "onStart: " + startTime);
                    }

                    @Override
                    public void onProgress(int max, int progress) {
                        Log.e(TAG, "onProgress: ");
                    }

                    @Override
                    public void onSuccess() {
                        float v = (System.currentTimeMillis() - startTime) / (float) 1000;
                        Log.d(TAG, "onSuccess() called 一共耗时 ： " + v + "s");// 10s的mp3转aac差不多2.5s
                    }

                    @Override
                    public void onFail() {
                        Log.d(TAG, "onFail() called");
                    }
                });
                aacHandlerPure.start();
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
