package com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary.encoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 功能:录音编码生成aac音频文件
 * @author aserbao
 * @date : On 2019/1/4 5:24 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.blocks.MediaExtractor.primary
 * @Copyright: 个人版权所有
 */
public class EncoderAudioAAC {
    private static final String TAG = "EncoderAudioAAC";
    private byte[] mFrameByte;
    private EncodeAudioThread mEncodeAudioThread;
    
    public void start(String outputAudioPath){
        if (mEncodeAudioThread == null) {
            mEncodeAudioThread = new EncodeAudioThread(outputAudioPath);
            mEncodeAudioThread.setRunning(true);
            try {
                mEncodeAudioThread.start();
            } catch (Exception e) {
                Log.w(TAG, "encode already start");
            }
        }
    }

    public void stop() {
        if (mEncodeAudioThread != null) {
            mEncodeAudioThread.setRunning(false);
            mEncodeAudioThread = null;
        }
    }
    
    public class EncodeAudioThread extends Thread{
        String MIME_TYPE = "audio/mp4a-latm";
        int KEY_CHANNEL_COUNT = 2;
        int KEY_SAMPLE_RATE = 44100;
        int KEY_BIT_RATE = 64000;
        int KEY_AAC_PROFILE = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
        int WAIT_TIME = 10000;
        int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
        int CHANNEL_MODE = AudioFormat.CHANNEL_IN_STEREO;
        int BUFFFER_SIZE = 2048;
        
        private final int mFrameSize = 2048;
        private byte[] mBuffer;
        private boolean running;
        private MediaCodec mEncoder;
        private AudioRecord mRecord;
        private MediaCodec.BufferInfo mBufferInfo;
        private String mOutputPath;
        private FileOutputStream fileOutputStream;

        public EncodeAudioThread(String mOutputPath) {
            this.mOutputPath = mOutputPath;
        }

        @Override
        public void run() {
            if (!prepare()) {
                Log.d(TAG, "音频编码器初始化失败");
                running = false;
            }
            while (running) {
                int num = mRecord.read(mBuffer, 0, mFrameSize);
                Log.d(TAG,"num = " + num);
                try {
                    encode(mBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            release();
        }

        private void setRunning(boolean running) {
            this.running = running;
        }

        /**
         * 释放资源
         */
        private void release() {
            if (mEncoder != null) {
                mEncoder.stop();
                mEncoder.release();
            }
            if (mRecord != null) {
                mRecord.stop();
                mRecord.release();
                mRecord = null;
            }
        }

        private boolean prepare() {
            try {
                fileOutputStream = new FileOutputStream(mOutputPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                mBufferInfo = new MediaCodec.BufferInfo();
                mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
                MediaFormat mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE, KEY_SAMPLE_RATE, KEY_CHANNEL_COUNT);
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, KEY_BIT_RATE);
                mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, KEY_AAC_PROFILE);
                mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                mEncoder.start();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            mBuffer = new byte[mFrameSize];
            int minBufferSize = AudioRecord.getMinBufferSize(KEY_SAMPLE_RATE, CHANNEL_MODE, AUDIO_FORMAT);
            mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, KEY_SAMPLE_RATE, CHANNEL_MODE, AUDIO_FORMAT, minBufferSize * 2);
            mRecord.startRecording();
            return true;
        }

        private void encode(byte[] data) throws IOException {
            int inputBufferIndex = mEncoder.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = mEncoder.getInputBuffer(inputBufferIndex);
                if (inputBuffer == null) return;
                inputBuffer.clear();
                inputBuffer.put(data);
                inputBuffer.limit(data.length);
                mEncoder.queueInputBuffer(inputBufferIndex, 0, data.length,
                        System.nanoTime(), 0);
            }
            int outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            while (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = mEncoder.getOutputBuffer(outputBufferIndex);
                if (outputBuffer == null) return;
                //给adts头字段空出7的字节
                int length = mBufferInfo.size + 7;
                if (mFrameByte == null || mFrameByte.length < length) {
                    mFrameByte = new byte[length];
                }
                addADTStoPacket(mFrameByte, length);
                outputBuffer.get(mFrameByte, 7, mBufferInfo.size);
                //TODO mFrameByte编码返回的数据
                fileOutputStream.write(mFrameByte, 0, mFrameByte.length);
                mEncoder.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mEncoder.dequeueOutputBuffer(mBufferInfo, 0);
            }
        }

        /**
         * 给编码出的aac裸流添加adts头字段
         *
         * @param packet    要空出前7个字节，否则会搞乱数据
         * @param packetLen 7
         */
        private void addADTStoPacket(byte[] packet, int packetLen) {
            int profile = 2;  //AAC LC
            int freqIdx = 4;  //44.1KHz
            int chanCfg = 2;  //CPE
            packet[0] = (byte) 0xFF;
            packet[1] = (byte) 0xF9;
            packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
            packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
            packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
            packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
            packet[6] = (byte) 0xFC;
        }
    }
}
