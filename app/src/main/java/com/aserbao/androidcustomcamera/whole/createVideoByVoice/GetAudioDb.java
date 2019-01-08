package com.aserbao.androidcustomcamera.whole.createVideoByVoice;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.aserbao.androidcustomcamera.whole.createVideoByVoice.interfaces.IGetVideoDbCallBackListener;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 功能: 解码获取音频帧分贝大小
 * @author aserbao
 * @date : On 2019/1/4 4:05 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.blocks.MediaExtractor.primary
 * @Copyright: 个人版权所有
 */
public class GetAudioDb {
    private static final String TAG = "GetAudioDb";
    public GetAudioDb() {
    }

    private DecoderAACThread mDecoderAACThread;
    private byte[] mPcmData;

    public void start(String inputAudioPath,IGetVideoDbCallBackListener dbCallBackListener){
        mDbCallBackListener  = dbCallBackListener;
        if (mDecoderAACThread == null) {
            mDecoderAACThread = new DecoderAACThread(inputAudioPath);
            mDecoderAACThread.setRunning(true);
            try {
                mDecoderAACThread.start();
            } catch (Exception e) {
                Log.w(TAG, "decode already start");
            }
        }

    }

    public void stop() {
        if (mDecoderAACThread != null) {
            mDecoderAACThread.setRunning(false);
            mDecoderAACThread = null;
        }
    }

    public class DecoderAACThread extends Thread{
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
        private MediaExtractor mMediaExtractor;
        private MediaCodec mMediaCodec;
        private MediaCodec.BufferInfo mBufferInfo;
        private boolean running;

        private void setRunning(boolean running) {
            this.running = running;
        }

        public DecoderAACThread(String inputAudioPath) {
            mInputAudioPath = inputAudioPath;
        }

        @Override
        public void run() {
            super.run();
            if (!prepare()) {
                running = false;
                Log.e(TAG, "音频解码器初始化失败");
                return;
            }
            decode();
            release();
        }


        public boolean prepare(){
            mBufferInfo = new MediaCodec.BufferInfo();
            mMediaExtractor = new MediaExtractor();
            try {
                mMediaExtractor.setDataSource(mInputAudioPath);
                int audioIndex = -1;//音频通道
                int trackCount = mMediaExtractor.getTrackCount();//获取通道总数
                for (int i = 0; i < trackCount; i++) {
                    MediaFormat trackFormat = mMediaExtractor.getTrackFormat(i);
                    mInputAudioMimeType = trackFormat.getString(MediaFormat.KEY_MIME);
                    if (mInputAudioMimeType.startsWith("audio/")) {
                        audioIndex = i;
                    }
                }
                mMediaExtractor.selectTrack(audioIndex);//切换到音频通道
                MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(audioIndex);
                mMediaCodec = MediaCodec.createDecoderByType(mInputAudioMimeType);
                mMediaCodec.configure(mediaFormat, null, null, 0);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            if (mMediaCodec == null) {
                Log.e(TAG, "create mediaDecode failed");
                return false;
            }
            mMediaCodec.start();
            return true;
        }

        private void decode() {
            while (running) {
                int inputIndex = mMediaCodec.dequeueInputBuffer(-1);
                if (inputIndex >= 0) {
                    ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputIndex);
                    if (inputBuffer == null) {
                        return;
                    }
                    inputBuffer.clear();
                    int sampleSize = mMediaExtractor.readSampleData(inputBuffer, 0);
                    if (sampleSize < 0) {
                        mMediaCodec.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        running = false;
                    } else {
                        mMediaCodec.queueInputBuffer(inputIndex, 0, sampleSize, mMediaExtractor.getSampleTime(), 0);
                        mMediaExtractor.advance();
                    }
                }
                int outputIndex = mMediaCodec.dequeueOutputBuffer(mBufferInfo, WAIT_TIME);
                ByteBuffer outputBuffer;
                if (outputIndex >= 0) {
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                        mMediaCodec.releaseOutputBuffer(outputIndex, false);
                        continue;
                    }
                    float cuurGetSampleTime = 0;
                    if (mBufferInfo.size != 0) {
                        outputBuffer = mMediaCodec.getOutputBuffer(outputIndex);
                        if (mPcmData == null || mPcmData.length < mBufferInfo.size) {
                            mPcmData = new byte[mBufferInfo.size];
                        }
                        if (outputBuffer != null) {
                            outputBuffer.get(mPcmData, 0, mBufferInfo.size);
                            outputBuffer.clear();
                        }
                        cuurGetSampleTime = mMediaExtractor.getSampleTime() / (float) (1000);
                        calcFrequency2(mPcmData,cuurGetSampleTime);
                        Log.e(TAG, "解析到的时间点为："+ cuurGetSampleTime + "ms     decode:  mPcmData.length  = " + mPcmData.length + " mBufferInfo "  + mBufferInfo.toString());
                    }
                    mMediaCodec.releaseOutputBuffer(outputIndex, false);
                    if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        mDbCallBackListener.cuurentFrequenty(true,0,cuurGetSampleTime);
                    }
                }
            }
            mMediaExtractor.release();
        }

        /**
         * 释放资源
         */
        private void release() {
            if (mMediaCodec != null) {
                mMediaCodec.stop();
                mMediaCodec.release();
            }
        }
    }


    /**
     * 获取的值范围0 ~ 24366
     * @param pcmdata
     * @param v
     */
    public void calcFrequency2(byte[] pcmdata, float v) {
        short[] music = (!isBigEnd()) ? byteArray2ShortArrayLittle( pcmdata,  pcmdata.length / 2) :
                byteArray2ShortArrayBig( pcmdata,  pcmdata.length / 2);
        calculateRealVolume(music,music.length,v);
    }

    private boolean isBigEnd() {
        short i = 0x1;
        boolean bRet = ((i >> 8) == 0x1);
        return bRet;
    }

    private short[] byteArray2ShortArrayBig(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2 + 1] & 0xff) | (data[i * 2] & 0xff) << 8);

        return retVal;
    }

    private short[] byteArray2ShortArrayLittle(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);

        return retVal;
    }

    private int maxVolume = 0;
    protected void calculateRealVolume(short[] buffer, int readSize, float v) {
        double sum = 0;
        for (int i = 0; i < readSize; i++) {
            sum += buffer[i] * buffer[i];
        }
        if (readSize > 0) {
            double amplitude = sum / readSize;
            int mVolume = (int) Math.sqrt(amplitude);
            Log.e(TAG, "calculateRealVolume: " + mVolume);
            maxVolume = Math.max(mVolume,maxVolume);
            mDbCallBackListener.cuurentFrequenty(false,mVolume,v);
        }
    }

    private IGetVideoDbCallBackListener mDbCallBackListener;




}
