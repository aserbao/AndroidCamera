package com.aserbao.androidcustomcamera.whole.createVideoByVoice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.frameData.BackgroundFrame;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.frameData.MapFrame;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.frameData.TopPopFrame;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.interfaces.IEncoderVideoCallBackListener;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.interfaces.IGetVideoDbCallBackListener;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.interfaces.IMuxerVideoCallBackListener;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更加音频振幅生成变色视频
 */
public class CreateVideoByAudioDbActivity extends AppCompatActivity {
    private static final String TAG = "CreateVideoByAudioDbAct";
    @BindView(R.id.create_video_analyze_btn)
    Button mCreateVideoAnalyzeBtn;
    @BindView(R.id.play_video_btn)
    Button mPlayVideoBtn;

    private EncoderVideo mEncoderVideo;
    private MuxerVoiceAndVideo mMuxerVoiceAndVideo;
    public String inputAudioPath, outputMediaPath;
    private GetAudioDb mGetAudioDb;
    private File encoderOutputFile;
    private boolean isEncoderFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video_by_audio_db);
        ButterKnife.bind(this);
    }

    private float mStartTime;

    @OnClick({R.id.create_video_analyze_btn, R.id.play_video_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.create_video_analyze_btn:
                mStartTime = System.currentTimeMillis();
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                encoderOutputFile = new File(path + "/ouput_dj_dance.mp4");
                inputAudioPath = path + "/dj_dance.mp3";
                outputMediaPath = path + "/output_dj_asebrao.mp4";
                mMuxerVoiceAndVideo = new MuxerVoiceAndVideo(new IMuxerVideoCallBackListener() {
                    @Override
                    public void success() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CreateVideoByAudioDbActivity.this, "成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e(TAG, "update 合成 success: " + (System.currentTimeMillis() - mStartTime)+ "s");
                    }

                    @Override
                    public void failed() {
                        Log.e(TAG, "update 合成failed: ");
                    }
                });
//                mMuxerVoiceAndVideo.startMuxer(encoderOutputFile.toString(), inputAudioPath,10,outputMediaPath);
                mEncoderVideo = new EncoderVideo(new IEncoderVideoCallBackListener() {
                    @Override
                    public void success(final String outputMeidaPath, final float finalMediaTime) {
                        isEncoderFinish = true;
                        mGetAudioDb.stop();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateVideoByAudioDbActivity.this, "编码成功", Toast.LENGTH_SHORT).show();
                                        mMuxerVoiceAndVideo.startMuxer(outputMeidaPath, inputAudioPath,finalMediaTime,outputMediaPath);
                                        Log.e(TAG, "update 编码 success: 耗时： " + (System.currentTimeMillis() - mStartTime) + "s");
                                    }
                                },100);
                            }
                        });
                    }

                    @Override
                    public void failed() {
                        Log.e(TAG, "update 编码 failed: ");
                    }
                });

                mEncoderVideo.addBaseDataFrameData(new BackgroundFrame());
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.katong);
                mEncoderVideo.addBaseDataFrameData(new MapFrame(bitmap));
                mEncoderVideo.addBaseDataFrameData(new TopPopFrame("party 是我家，party party 是我家"));

                mEncoderVideo.startRecording(getResources(), encoderOutputFile);

                mGetAudioDb = new GetAudioDb();
                mGetAudioDb.start(inputAudioPath, new IGetVideoDbCallBackListener() {
                    @Override
                    public void cuurentFrequenty(boolean isEnd, double volume, float cuurTime) {
                        float volume1 = (float) volume / 100;
                        if (!isEncoderFinish) {
                            mEncoderVideo.update(isEnd, volume1, cuurTime);
                            if (cuurTime > 950) {
                                Log.e(TAG, "update cuurentFrequenty: isEnd : " + isEnd + " volume1 = " + volume1 + " cuurTime = " + cuurTime);
                            }
                        }
                    }
                });

                break;
            case R.id.play_video_btn:
                mMuxerVoiceAndVideo.startMuxer(encoderOutputFile.toString(), inputAudioPath, 10, outputMediaPath);
                break;
        }
    }
}
