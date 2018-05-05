package com.aserbao.androidcustomcamera.blocks.mediacodec.primary;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.activity.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.OnClick;

public class PrimaryMediaCodecActivity extends BaseActivity {
    private static final String TAG = "PrimaryMediaCodecActivi";
    private static final String MIME_TYPE = "video/avc";
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int BIT_RATE = 4000000;
    private static final int FRAMES_PER_SECOND = 4;
    private static final int IFRAME_INTERVAL = 5;

    private static final int NUM_FRAMES = 4 * 100 ;
    private static final int START_RECORDING = 0;
    private static final int STOP_RECORDING = 1;

    @BindView(R.id.btn_recording)
    Button mBtnRecording;
    @BindView(R.id.primary_mc_tv)
    TextView mPrimaryMcTv;
    public MediaCodec.BufferInfo mBufferInfo;
    public MediaCodec mEncoder;
    private Surface mInputSurface;
    public MediaMuxer mMuxer;
    private boolean mMuxerStarted;
    private int mTrackIndex;
    private long mFakePts;
    private boolean isRecording;

    private int cuurFrame = 0;

    private MyHanlder mMyHanlder = new MyHanlder(this);
    private static class MyHanlder extends Handler{
        private WeakReference<PrimaryMediaCodecActivity> mPrimaryMediaCodecActivityWeakReference;

        public MyHanlder(PrimaryMediaCodecActivity activity) {
            mPrimaryMediaCodecActivityWeakReference = new WeakReference<PrimaryMediaCodecActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PrimaryMediaCodecActivity activity = mPrimaryMediaCodecActivityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case START_RECORDING:
                        activity.drainEncoder(false);
                        activity.generateFrame(activity.cuurFrame);
                        Log.e(TAG, "handleMessage: " +activity.cuurFrame);
                        if(activity.cuurFrame < NUM_FRAMES){
                            this.sendEmptyMessage(START_RECORDING);
                        }else{
                            activity.drainEncoder(true);
                            activity.mBtnRecording.setText("开始录制");
                            activity.releaseEncoder();
                        }
                        activity.cuurFrame ++;
                        break;
                    case STOP_RECORDING:
                        Log.e(TAG, "handleMessage: STOP_RECORDING");
                        activity.drainEncoder(true);
                        activity.mBtnRecording.setText("开始录制");
                        activity.releaseEncoder();
                        break;
                }
            }
        }
    }
    @Override
    protected int setLayoutId() {
        return R.layout.activity_primary_media_codec;
    }

    @OnClick(R.id.btn_recording)
    public void onViewClicked() {
        if(mBtnRecording.getText().equals("开始录制")){
            try {
                File outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), System.currentTimeMillis() + ".mp4");
                startRecording(outputFile);
                mPrimaryMcTv.setText("文件保存路径为："+ outputFile.toString());
                mBtnRecording.setText("停止录制");
                isRecording = true;
            } catch (IOException e) {
                e.printStackTrace();
                mBtnRecording.setText("出现异常了，请查明原因");
            }
        }else if(mBtnRecording.getText().equals("停止录制")){
            mBtnRecording.setText("开始录制");
            stopRecording();
        }
    }



    private void startRecording(File outputFile) throws IOException {
        cuurFrame = 0;
        prepareEncoder(outputFile);
        mMyHanlder.sendEmptyMessage(START_RECORDING);
    }

    private void stopRecording() {
        mMyHanlder.removeMessages(START_RECORDING);
        mMyHanlder.sendEmptyMessage(STOP_RECORDING);
    }
    private void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (endOfStream) {
            mEncoder.signalEndOfInputStream();//在输入信号end-of-stream。相当于提交一个空缓冲区。视频编码完结
        }

        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {//没有可以输出的数据使用时
                if (!endOfStream) {
                    break;      // out of while
                }
            }else if(encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
                //输出缓冲区已经更改，客户端必须引用新的
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            }else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //输出格式发生了变化，后续数据将使用新的数据格式。
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            }else if (encoderStatus < 0) {
            }else{
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    //当我们得到的时候，编解码器的配置数据被拉出来，并给了muxer。这时候可以忽略。
                    mBufferInfo.size = 0;
                }
                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                    mBufferInfo.presentationTimeUs = mFakePts;
                    mFakePts += 1000000L / FRAMES_PER_SECOND;

                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                }
                mEncoder.releaseOutputBuffer(encoderStatus, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.e(TAG, "意外结束");
                    } else {
                        Log.e(TAG, "正常结束");
                    }
                    isRecording = false;
                    break;
                }
            }
        }
    }

    /**
     * Prepares the video encoder, muxer, and an input surface.
     */
    private void prepareEncoder(File outputFile) throws IOException {
        mBufferInfo = new MediaCodec.BufferInfo();
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, WIDTH, HEIGHT);

        //1. 设置一些属性。没有指定其中的一些可能会导致MediaCodec.configure()调用抛出一个无用的异常。
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);//比特率(比特率越高，音视频质量越高，编码文件越大)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND);//设置帧速
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);//设置关键帧间隔时间

        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = mEncoder.createInputSurface();
        mEncoder.start();

        mMuxer = new MediaMuxer(outputFile.toString(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        mMuxerStarted = false;
        mTrackIndex = -1;
    }


    /**
     * Generates a frame, writing to the Surface via the "software" API (lock/unlock).
     * <p>
     * There's no way to set the time stamp.
     */
    private void generateFrame(int frameNum) {
        Canvas canvas = mInputSurface.lockCanvas(null);
        try {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            float sliceWidth = width / 8;
            Paint paint = new Paint();
            for (int i = 0; i < 8; i++) {
                int color = 0xff000000;
                if ((i & 0x01) != 0) {
                    color |= 0x00ff0000;
                }
                if ((i & 0x02) != 0) {
                    color |= 0x0000ff00;
                }
                if ((i & 0x04) != 0) {
                    color |= 0x000000ff;
                }
                paint.setColor(color);
                canvas.drawRect(sliceWidth * i, 0, sliceWidth * (i+1), height, paint);
            }

            paint.setColor(0x80808080);
            float sliceHeight = height / 8;
            int frameMod = frameNum % 8;
            canvas.drawRect(0, sliceHeight * frameMod, width, sliceHeight * (frameMod+1), paint);
            paint.setTextSize(50);
            paint.setColor(0xffffffff);

            canvas.drawText("aserbao",0,sliceHeight * frameMod,paint);
            canvas.drawText("aserbao",1 * sliceWidth,sliceHeight * (frameMod+1),paint);
            canvas.drawText("aserbao",2 * sliceWidth,sliceHeight * frameMod,paint);
            canvas.drawText("aserbao",3 * sliceWidth,sliceHeight * (frameMod+1),paint);
            canvas.drawText("aserbao",4 * sliceWidth,sliceHeight * frameMod,paint);
            canvas.drawText("aserbao",5 * sliceWidth,sliceHeight * (frameMod+1),paint);
            canvas.drawText("aserbao",6 * sliceWidth,sliceHeight * frameMod,paint);
            canvas.drawText("aserbao",7 * sliceWidth,sliceHeight * (frameMod+1),paint);
            canvas.drawText("aserbao",8 * sliceWidth,sliceHeight * frameMod,paint);
        } finally {
            mInputSurface.unlockCanvasAndPost(canvas);
        }
    }

    private void releaseEncoder() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }
}
