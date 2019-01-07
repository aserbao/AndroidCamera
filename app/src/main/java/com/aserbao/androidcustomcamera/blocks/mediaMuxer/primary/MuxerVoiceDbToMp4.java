package com.aserbao.androidcustomcamera.blocks.mediaMuxer.primary;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Math.log10;

/**
 * 功能: 解码获取音频帧分贝大小
 * @author aserbao
 * @date : On 2019/1/4 4:05 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.blocks.MediaExtractor.primary
 * @Copyright: 个人版权所有
 */
public class MuxerVoiceDbToMp4 {
    private static final String TAG = "DecoderAudioAAC2PCMPlay";

    private Bitmap mBitmap;
    private EncoderThread mEncoderThread;

    public MuxerVoiceDbToMp4() {
        mBitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.katong);
    }

    private DecoderAACThread mDecoderAACThread;
    private byte[] mPcmData;
    
    public void start(String inputAudioPath,String outputVideoPath,String mimeType,DbCallBackListener dbCallBackListener){
        initMediaMux(outputVideoPath);
        mDbCallBackListener  = dbCallBackListener;
        /*mEncoderThread = new EncoderThread();
        mEncoderThread.start();
        mEncoderThread.prepareVideo();*/
        for (int i = 0; i < 50; i++) {
            if (i == 49){
                mEncoderThread.start(true,i);
            }else{
                mEncoderThread.start(false,i);
            }
        }


        /*if (mDecoderAACThread == null) {
            mDecoderAACThread = new DecoderAACThread(inputAudioPath, outputVideoPath, mimeType, new IRefreshCallBack() {
                @Override
                public void refresh(boolean isEnd) {
                    mEncoderThread.start(isEnd,cuurFrame);
                    cuurFrame ++;
                }
            });
            mDecoderAACThread.setRunning(true);
            try {
                mDecoderAACThread.start();
            } catch (Exception e) {
                Log.w(TAG, "decode already start");
            }
        }*/

    }

    public void stop() {
        if (mDecoderAACThread != null) {
            mDecoderAACThread.setRunning(false);
            mDecoderAACThread = null;
        }
    }
    String MIME_TYPE = "audio/mp4a-latm";
    int KEY_CHANNEL_COUNT = 2;
    int KEY_SAMPLE_RATE = 44100;
    int KEY_BIT_RATE = 64000;
    int KEY_AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
    int WAIT_TIME = 10000;
    int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    int CHANNEL_MODE = AudioFormat.CHANNEL_IN_STEREO;
    int BUFFFER_SIZE = 2048;

    private String mInputAudioPath;//音频路径
    private String mInputAudioMimeType;
    private MediaExtractor mAudioMediaExtractor;
    private MediaCodec mAudioMediaCodec,mVideoMediaCodec;
    private AudioTrack mPcmPlayer;
    private MediaCodec.BufferInfo mAudioBufferInfo,mVideoBufferInfo;
    private MediaMuxer mediaMuxer;
    private int mWriteAudioTrackIndex,mWriteVideoTrackIndex;
    private int cuurFrame =0;


    public void initMediaMux(String outputVideoPath){
        try {
            mediaMuxer = new MediaMuxer(outputVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class DecoderAACThread extends Thread{
        private boolean running;
        private String outputVideoPath;
        private IRefreshCallBack mIRefreshCallBack;
        private void setRunning(boolean running) {
            this.running = running;
        }

        public DecoderAACThread(String inputAudioPath,String outputVideoPath,String mimeType,IRefreshCallBack iRefreshCallBack) {
            mInputAudioPath = inputAudioPath;
            mInputAudioMimeType = mimeType;
            this.outputVideoPath = outputVideoPath;
            mIRefreshCallBack = iRefreshCallBack;
        }

        @Override
        public void run() {
            super.run();
            if (!prepareAudio() ) {
                running = false;
                Log.e(TAG, "音频解码器初始化失败");
                return;
            }
            decode();
            release();
        }


        public boolean prepareAudio(){
            mAudioBufferInfo = new MediaCodec.BufferInfo();
            mAudioMediaExtractor = new MediaExtractor();
            mPcmPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, KEY_SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AUDIO_FORMAT, BUFFFER_SIZE, AudioTrack.MODE_STREAM);
            mPcmPlayer.play();
            try {
                mAudioMediaExtractor.setDataSource(mInputAudioPath);
                int audioIndex = -1;//音频通道
                int trackCount = mAudioMediaExtractor.getTrackCount();//获取通道总数
                for (int i = 0; i < trackCount; i++) {
                    MediaFormat trackFormat = mAudioMediaExtractor.getTrackFormat(i);
                    String string = trackFormat.getString(MediaFormat.KEY_MIME);
                    if (string.startsWith("audio/")) {
                        audioIndex = i;
                    }//获取音频通道
                }
                mAudioMediaExtractor.selectTrack(audioIndex);//切换到音频通道
                MediaFormat mediaFormat = mAudioMediaExtractor.getTrackFormat(audioIndex);
                mAudioMediaCodec = MediaCodec.createDecoderByType(mInputAudioMimeType);
                mAudioMediaCodec.configure(mediaFormat, null, null, 0);
                mWriteAudioTrackIndex = mediaMuxer.addTrack(mediaFormat);
                mediaMuxer.start();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            if (mAudioMediaCodec == null) {
                Log.e(TAG, "create mediaDecode failed");
                return false;
            }
            mAudioMediaCodec.start();
            return true;
        }

        private void decode() {
            while (running) {
                int inputIndex = mAudioMediaCodec.dequeueInputBuffer(-1);
                if (inputIndex >= 0) {
                    ByteBuffer inputBuffer = mAudioMediaCodec.getInputBuffer(inputIndex);
                    if (inputBuffer == null) {
                        return;
                    }
                    inputBuffer.clear();
                    int sampleSize = mAudioMediaExtractor.readSampleData(inputBuffer, 0);
                    if (sampleSize < 0) {
                        mAudioMediaCodec.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        running = false;
                    } else {
                        mAudioMediaCodec.queueInputBuffer(inputIndex, 0, sampleSize, mAudioMediaExtractor.getSampleTime(), 0);
                        mAudioMediaExtractor.advance();
                    }
                }
                int outputIndex = mAudioMediaCodec.dequeueOutputBuffer(mAudioBufferInfo, WAIT_TIME);
                ByteBuffer outputBuffer;
                if (outputIndex >= 0) {
                    // Simply ignore codec config buffers.
                    if ((mAudioBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        Log.i(TAG, "audio encoder: codec config buffer");
                        mAudioMediaCodec.releaseOutputBuffer(outputIndex, false);
                        continue;
                    }
                    if (mAudioBufferInfo.size != 0) {
                        outputBuffer = mAudioMediaCodec.getOutputBuffer(outputIndex);
                        mediaMuxer.writeSampleData(mWriteAudioTrackIndex,outputBuffer,mAudioBufferInfo);
                        if (mPcmData == null || mPcmData.length < mAudioBufferInfo.size) {
                            mPcmData = new byte[mAudioBufferInfo.size];
                        }
                        if (outputBuffer != null) {
                            outputBuffer.get(mPcmData, 0, mAudioBufferInfo.size);
                            outputBuffer.clear();
                        }
                        float v = mAudioMediaExtractor.getSampleTime() / (float) (1000 * 1000);
                        calcFrequency(mPcmData,KEY_SAMPLE_RATE);
                        
//                        Log.e(TAG, "解析到的时间点为："+ v + "s     decode:  mPcmData.length  = " + mPcmData.length + " mAudioBufferInfo "  + mAudioBufferInfo.toString());
                        mPcmPlayer.write(mPcmData, 0, mAudioBufferInfo.size);
                    }
                    mIRefreshCallBack.refresh(false);
                    mAudioMediaCodec.releaseOutputBuffer(outputIndex, false);
                    if ((mAudioBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.i(TAG, "saw output EOS.");
                    }
                }else{
                    mIRefreshCallBack.refresh(true);
                    Log.e(TAG, "decode: 播完了" );
                }
            }
            mAudioMediaExtractor.release();
        }

        /**
         * 释放资源
         */
        private void release() {
            if (mAudioMediaCodec != null) {
                mAudioMediaCodec.stop();
                mAudioMediaCodec.release();
            }
            if (mPcmPlayer != null) {
                mPcmPlayer.stop();
                mPcmPlayer.release();
                mPcmPlayer = null;
            }
        }
        public void calcFrequency(byte[] fft, int samplingRate){
            float[] magnitudes = new float[fft.length / 2];
            int max = 0;
            for (int i = 0; i < magnitudes.length; i++) {
                magnitudes[i] = (float) Math.hypot(fft[2 * i], fft[2 * i + 1]);
                if (magnitudes[max] < magnitudes[i]) {
                    max = i;
                }
            }

            int  currentFrequency = max * samplingRate / fft.length;
            if (currentFrequency<0){
                return;
            }
            long v = 0;
            for (int i = 0; i < fft.length; i++) {
                v += Math.pow(fft[i], 2);
            }

            double volume = 10 * log10(v / (double) fft.length);
            mDbCallBackListener.cuurentFrequenty(currentFrequency,volume);
            Log.e(TAG, "calcFrequency: currentFrequency = " + currentFrequency + "   volume =  " + volume + "  max =  " + max );
        }

    }

    public class EncoderThread extends Thread{
        @Override
        public void run() {
            super.run();
//            prepareVideo();
        }
        public void start(boolean isEnd,int frameNum){
            if (isEnd){
                drainEncoder(isEnd);
                generateFrame(frameNum);
            }else{
                drainEncoder(true);
                releaseEncoder();
            }
        }

        private Surface mInputSurface;
        private static final int WIDTH = 720;
        private static final int HEIGHT = 1280;
        private static final int BIT_RATE = 4000000;
        private static final int FRAMES_PER_SECOND = 4;
        private static final int IFRAME_INTERVAL = 5;
        public boolean prepareVideo(){
            try {
                mVideoBufferInfo = new MediaCodec.BufferInfo();
                MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, WIDTH, HEIGHT);

                //1. 设置一些属性。没有指定其中的一些可能会导致MediaCodec.configure()调用抛出一个无用的异常。
                format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
                format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);//比特率(比特率越高，音视频质量越高，编码文件越大)
                format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND);//设置帧速
                format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);//设置关键帧间隔时间

                //2.创建一个MediaCodec编码器，并配置格式。获取一个我们可以用于输入的表面，并将其封装到处理EGL工作的类中。
                mVideoMediaCodec = MediaCodec.createEncoderByType("video/avc");
                mVideoMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                mInputSurface = mVideoMediaCodec.createInputSurface();
                mVideoMediaCodec.start();


                MediaFormat newFormat = mVideoMediaCodec.getOutputFormat();
                mWriteVideoTrackIndex = mediaMuxer.addTrack(newFormat);
                mediaMuxer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        private long mFakePts;
        private void drainEncoder(boolean endOfStream) {
            final int TIMEOUT_USEC = 10000;
            if (endOfStream) {
                mVideoMediaCodec.signalEndOfInputStream();//在输入信号end-of-stream。相当于提交一个空缓冲区。视频编码完结
            }
            ByteBuffer[] encoderOutputBuffers = mVideoMediaCodec.getOutputBuffers();
            while (true) {
                int outputBufferIndex = mVideoMediaCodec.dequeueOutputBuffer(mVideoBufferInfo, TIMEOUT_USEC);
                Log.e(TAG, "drainEncoder: " + outputBufferIndex);
                if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {//没有可以输出的数据使用时
                    if (!endOfStream) {
                        break;      // out of while
                    }
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    //输出缓冲区已经更改，客户端必须引用新的
                    encoderOutputBuffers = mVideoMediaCodec.getOutputBuffers();
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                } else if (outputBufferIndex < 0) {
                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[outputBufferIndex];
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + outputBufferIndex +
                                " was null");
                    }
                    if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        //当我们得到的时候，编解码器的配置数据被拉出来，并给了muxer。这时候可以忽略。
                        mVideoBufferInfo.size = 0;
                    }
                    if (mVideoBufferInfo.size != 0) {
                        //调整ByteBuffer值以匹配BufferInfo。
                        encodedData.position(mVideoBufferInfo.offset);
                        encodedData.limit(mVideoBufferInfo.offset + mVideoBufferInfo.size);
                        mVideoBufferInfo.presentationTimeUs = mFakePts;
                        mFakePts += 1000000L / FRAMES_PER_SECOND;

                        mediaMuxer.writeSampleData(mWriteVideoTrackIndex, encodedData, mVideoBufferInfo);
                    }
                    mVideoMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                    if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (!endOfStream) {
                            Log.e(TAG, "意外结束");
                        } else {
                            Log.e(TAG, "正常结束");
                        }
                        break;
                    }
                }
            }
        }


        private void generateFrame(int frameNum){

            Canvas canvas = mInputSurface.lockCanvas(null);
            Paint paint = new Paint();
            try {
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                String  color = "#FFCA39";
                if (frameNum %2 == 0 ){
                    color = "#FFCA39";
                }else{
                    color = "#FFF353";
                }
                int color1 = Color.parseColor(color);
                canvas.drawColor(color1);
                paint.setTextSize(100);
                paint.setColor(0xff000000);
                canvas.drawText("第"+ String.valueOf(frameNum) + "帧",width/2,height/2,paint);
                Rect srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
                int margain = 30;
                Rect decRect = new Rect(margain, margain, width - margain, height-margain);
                canvas.drawBitmap(mBitmap,srcRect,decRect,paint);

                int roundMargain = 60;
                int roundHeight = 300;
                int roundRadius = 25;
                int roundLineWidth = 10;
                paint.setStyle(Paint.Style.FILL);//充满
                paint.setAntiAlias(true);// 设置画笔的锯齿效果
                RectF roundRect1 = new RectF(roundMargain - roundLineWidth,roundMargain - roundLineWidth,width - roundMargain + roundLineWidth,roundHeight + roundMargain + roundLineWidth);
                paint.setColor(Color.BLACK);
                canvas.drawRoundRect(roundRect1,roundRadius,roundRadius,paint);
                paint.setColor(color1);
                RectF roundRect2 = new RectF(roundMargain,roundMargain,width - roundMargain,roundHeight + roundMargain);
                canvas.drawRoundRect(roundRect2,roundRadius,roundRadius,paint);

//            paint.setStyle(Paint.Style.STROKE);//充满
                int timeMargain = roundMargain + 50;
                String sTime = "2018/12/29 00:39";
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(40);
                paint.setColor(Color.BLACK);
                canvas.drawText(sTime,width/2,timeMargain,paint);

                int soundMargain = timeMargain + 80;
                String soundTime = "party 是我家";
                String soundTime2 = "party party 是我家";
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(80);
                canvas.drawText(soundTime,width/2,soundMargain,paint);
                canvas.drawText(soundTime2,width/2,soundMargain + 80,paint);

            } finally {
                mInputSurface.unlockCanvasAndPost(canvas);
            }

        }
        private void releaseEncoder() {
            if (mVideoMediaCodec != null) {
                mVideoMediaCodec.stop();
                mVideoMediaCodec.release();
                mVideoMediaCodec = null;
            }

            if (mInputSurface != null) {
                mInputSurface.release();
                mInputSurface = null;
            }
            if (mediaMuxer != null) {
                mediaMuxer.stop();
                mediaMuxer.release();
                mediaMuxer = null;
            }
        }
    }

    private Surface mInputSurface;
    private static final int WIDTH = 720;
    private static final int HEIGHT = 1280;
    private static final int BIT_RATE = 4000000;
    private static final int FRAMES_PER_SECOND = 4;
    private static final int IFRAME_INTERVAL = 5;
    public boolean prepareVideo(){
        try {
            mVideoBufferInfo = new MediaCodec.BufferInfo();
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, WIDTH, HEIGHT);

            //1. 设置一些属性。没有指定其中的一些可能会导致MediaCodec.configure()调用抛出一个无用的异常。
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);//比特率(比特率越高，音视频质量越高，编码文件越大)
            format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND);//设置帧速
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);//设置关键帧间隔时间

            //2.创建一个MediaCodec编码器，并配置格式。获取一个我们可以用于输入的表面，并将其封装到处理EGL工作的类中。
            mVideoMediaCodec = MediaCodec.createEncoderByType("video/avc");
            mVideoMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mInputSurface = mVideoMediaCodec.createInputSurface();
            mVideoMediaCodec.start();


            MediaFormat newFormat = mVideoMediaCodec.getOutputFormat();
            mWriteVideoTrackIndex = mediaMuxer.addTrack(newFormat);
            mediaMuxer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private long mFakePts;
    private void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (endOfStream) {
            mVideoMediaCodec.signalEndOfInputStream();//在输入信号end-of-stream。相当于提交一个空缓冲区。视频编码完结
        }
        ByteBuffer[] encoderOutputBuffers = mVideoMediaCodec.getOutputBuffers();
        while (true) {
            int outputBufferIndex = mVideoMediaCodec.dequeueOutputBuffer(mVideoBufferInfo, TIMEOUT_USEC);
            Log.e(TAG, "drainEncoder: " + outputBufferIndex);
            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {//没有可以输出的数据使用时
                if (!endOfStream) {
                    break;      // out of while
                }
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                //输出缓冲区已经更改，客户端必须引用新的
                encoderOutputBuffers = mVideoMediaCodec.getOutputBuffers();
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

            } else if (outputBufferIndex < 0) {
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[outputBufferIndex];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + outputBufferIndex +
                            " was null");
                }
                if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    //当我们得到的时候，编解码器的配置数据被拉出来，并给了muxer。这时候可以忽略。
                    mVideoBufferInfo.size = 0;
                }
                if (mVideoBufferInfo.size != 0) {
                    //调整ByteBuffer值以匹配BufferInfo。
                    encodedData.position(mVideoBufferInfo.offset);
                    encodedData.limit(mVideoBufferInfo.offset + mVideoBufferInfo.size);
                    mVideoBufferInfo.presentationTimeUs = mFakePts;
                    mFakePts += 1000000L / FRAMES_PER_SECOND;

                    mediaMuxer.writeSampleData(mWriteVideoTrackIndex, encodedData, mVideoBufferInfo);
                }
                mVideoMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                if ((mVideoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.e(TAG, "意外结束");
                    } else {
                        Log.e(TAG, "正常结束");
                    }
                    break;
                }
            }
        }
    }


    private void generateFrame(int frameNum){

        Canvas canvas = mInputSurface.lockCanvas(null);
        Paint paint = new Paint();
        try {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            String  color = "#FFCA39";
            if (frameNum %2 == 0 ){
                color = "#FFCA39";
            }else{
                color = "#FFF353";
            }
            int color1 = Color.parseColor(color);
            canvas.drawColor(color1);
            paint.setTextSize(100);
            paint.setColor(0xff000000);
            canvas.drawText("第"+ String.valueOf(frameNum) + "帧",width/2,height/2,paint);
            Rect srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            int margain = 30;
            Rect decRect = new Rect(margain, margain, width - margain, height-margain);
            canvas.drawBitmap(mBitmap,srcRect,decRect,paint);

            int roundMargain = 60;
            int roundHeight = 300;
            int roundRadius = 25;
            int roundLineWidth = 10;
            paint.setStyle(Paint.Style.FILL);//充满
            paint.setAntiAlias(true);// 设置画笔的锯齿效果
            RectF roundRect1 = new RectF(roundMargain - roundLineWidth,roundMargain - roundLineWidth,width - roundMargain + roundLineWidth,roundHeight + roundMargain + roundLineWidth);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(roundRect1,roundRadius,roundRadius,paint);
            paint.setColor(color1);
            RectF roundRect2 = new RectF(roundMargain,roundMargain,width - roundMargain,roundHeight + roundMargain);
            canvas.drawRoundRect(roundRect2,roundRadius,roundRadius,paint);

//            paint.setStyle(Paint.Style.STROKE);//充满
            int timeMargain = roundMargain + 50;
            String sTime = "2018/12/29 00:39";
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(40);
            paint.setColor(Color.BLACK);
            canvas.drawText(sTime,width/2,timeMargain,paint);

            int soundMargain = timeMargain + 80;
            String soundTime = "party 是我家";
            String soundTime2 = "party party 是我家";
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(80);
            canvas.drawText(soundTime,width/2,soundMargain,paint);
            canvas.drawText(soundTime2,width/2,soundMargain + 80,paint);

        } finally {
            mInputSurface.unlockCanvasAndPost(canvas);
        }

    }
    private void releaseEncoder() {
        if (mVideoMediaCodec != null) {
            mVideoMediaCodec.stop();
            mVideoMediaCodec.release();
            mVideoMediaCodec = null;
        }

        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        if (mediaMuxer != null) {
            mediaMuxer.stop();
            mediaMuxer.release();
            mediaMuxer = null;
        }
    }




    public interface IRefreshCallBack{
        void refresh(boolean isEnd);
    }

    private DbCallBackListener mDbCallBackListener;
    public interface DbCallBackListener {
        void cuurentFrequenty(int cuurentFrequenty, double volume);
    }


}
