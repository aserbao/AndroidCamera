/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aserbao.androidcustomcamera.whole.record.encoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * This class wraps up the core components used for surface-input video encoding.
 * <p>
 * Once created, frames are fed to the input surface.  Remember to provide the presentation
 * time stamp, and always call drainEncoder() before swapBuffers() to ensure that the
 * producer side doesn't get backed up.
 * <p>
 * This class is not thread-safe, with one exception: it is valid to use the input surface
 * on one thread, and drain the output on a different thread.
 */
public class VideoEncoderCore {
    private static final String TAG = "VideoEncoderCore";
    private static final boolean VERBOSE = false;

    // TODO: these ought to be configurable as well
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int IFRAME_INTERVAL = 1;           // 1 seconds between I-frames

    //音频配置
    private String audioMime = "audio/mp4a-latm";   //音频编码的Mime
    private AudioRecord mRecorder;   //录音器
    private MediaCodec mAudioEnc;   //编码器，用于音频编码
    private int audioRate = 128000;   //音频编码的密钥比特率
    private int sampleRate = 48000;   //音频采样率
    private int channelCount = 2;     //音频编码通道数
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;   //音频录制通道,默认为立体声
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT; //音频录制格式，默认为PCM16Bit
    private int bufferSize;
    private int mAudioTrackIndex;
    private Thread audioThread;
    private AudioEncoder audioRecorder;


    private Surface mInputSurface;
    private MediaMuxer mMuxer;
    private MediaCodec mVideoEncoder;
    private MediaCodec.BufferInfo mBufferInfo;
    private int mVideoTrackIndex;
    private boolean mMuxerStarted;

    private Object lock = new Object();


    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     */
    public VideoEncoderCore(int width, int height, int bitRate, String path)
            throws IOException {
        //audio init
        MediaFormat aFormat = MediaFormat.createAudioFormat(audioMime, sampleRate, channelCount);//创建音频的格式,参数 MIME,采样率,通道数
        aFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);//编码方式
        aFormat.setInteger(MediaFormat.KEY_BIT_RATE, audioRate);//比特率
        mAudioEnc = MediaCodec.createEncoderByType(audioMime);//创建音频编码器
        mAudioEnc.configure(aFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);//配置
        bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);//设置bufferSize为AudioRecord所需最小bufferSize的两倍 15360
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig,
                audioFormat, bufferSize);//初始化录音器
        mAudioEnc.start();
        mRecorder.startRecording();

        //video init
        mBufferInfo = new MediaCodec.BufferInfo();
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        if (VERBOSE) Log.d(TAG, "format: " + format);

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        mVideoEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = mVideoEncoder.createInputSurface();
        mVideoEncoder.start();

        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        mMuxer = new MediaMuxer(path,
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        mVideoTrackIndex = -1;
        mAudioTrackIndex = -1;
        mMuxerStarted = false;

        audioRecorder = new AudioEncoder();
        audioThread = new Thread(audioRecorder);
        audioThread.start();
    }

    /**
     * Returns the encoder's input surface.
     */
    public Surface getInputSurface() {
        return mInputSurface;
    }

    /**
     * Releases encoder resources.
     */
    public void release() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }
        if (mAudioEnc != null) {
            mAudioEnc.stop();
            mAudioEnc.release();
            mAudioEnc = null;
        }
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        if (mMuxer != null) {
            // TODO: stop() throws an exception if you haven't fed it any data.  Keep track
            //       of frames submitted, and don't call stop() if we haven't written anything.
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    /**
     * Extracts all pending data from the encoder and forwards it to the muxer.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     * <p>
     * We're just using the muxer to get a .mp4 file (instead of a raw H.264 stream).  We're
     * not recording audio.
     */
    public void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
            mVideoEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mVideoEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mVideoEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mVideoEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                synchronized (lock) {
                    if (mMuxerStarted) {
                        throw new RuntimeException("format changed twice");
                    }
                    MediaFormat newFormat = mVideoEncoder.getOutputFormat();
                    Log.d(TAG, "encoder output format changed: " + newFormat);

                    // now that we have the Magic Goodies, start the muxer
                    mVideoTrackIndex = mMuxer.addTrack(newFormat);
                    if (mVideoTrackIndex >= 0 && mAudioTrackIndex >= 0) {
                        mMuxer.start();
                        mMuxerStarted = true;
                    }
                }
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
//                    if (!mMuxerStarted) {
//                        throw new RuntimeException("muxer hasn't started");
//                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
//                    encodedData.position(mBufferInfo.offset);
//                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                    if (mMuxerStarted) {
                        mMuxer.writeSampleData(mVideoTrackIndex, encodedData, mBufferInfo);
                        if (VERBOSE) {
                            Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer, ts=" +
                                    mBufferInfo.presentationTimeUs);
                        }
                    }
                }

                mVideoEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        }
    }

    //=============================================audio==================================================
    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_AUDIO_STEP = 2;
    private static final int MSG_QUIT = 3;
    private static final int MSG_PAUSE = 4;
    private static final int MSG_RESUME = 5;

    class AudioEncoder implements Runnable {
        private boolean isRecording = true;
        private boolean cancelFlag = false;
        private long baseTimeStamp = -1;
        private long pauseDelayTime;
        private long oncePauseTime;
        private boolean pausing = false;
        AudioHandler mHandler;
        private Object mReadyFence = new Object();
        private boolean isReady;

        @Override
        public void run() {
            Looper.prepare();
            mHandler = new AudioHandler(this);
            synchronized (mReadyFence){
                isReady=true;
                mReadyFence.notify();
            }
            Looper.loop();
            synchronized (mReadyFence){
                isReady=false;
                mHandler=null;
            }
        }

        public void startRecord() {
            synchronized (mReadyFence){
                if(!isReady){
                    try {
                        mReadyFence.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendEmptyMessage(MSG_START_RECORDING);
            }

        }

        public void pause() {
            mHandler.sendEmptyMessage(MSG_PAUSE);

        }

        public void resume() {
            mHandler.sendEmptyMessage(MSG_RESUME);
        }

        public void stopRecord() {
            mHandler.sendEmptyMessage(MSG_STOP_RECORDING);
        }

        public void handleStartRecord() {
            baseTimeStamp = System.nanoTime();
            mHandler.sendEmptyMessage(MSG_AUDIO_STEP);
        }

        public void handleAudioStep() {
            try {
                if (!cancelFlag) {
                    if (!pausing) {
                        if (isRecording) {
                            audioStep();
                            mHandler.sendEmptyMessage(MSG_AUDIO_STEP);
                        } else {
                            drainEncoder();
                            mHandler.sendEmptyMessage(MSG_QUIT);
                        }
                    } else{
                        if (isRecording){
                            mHandler.sendEmptyMessage(MSG_AUDIO_STEP);
                        }else{
                            drainEncoder();
                            mHandler.sendEmptyMessage(MSG_QUIT);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void drainEncoder() throws IOException {
            while (!audioStep()) {
            }
        }

        public void handleAudioPause() {
            pausing = true;
            oncePauseTime = System.nanoTime();

        }

        public void handleAudioResume() {
            oncePauseTime = System.nanoTime() - oncePauseTime;
            pauseDelayTime += oncePauseTime;
            pausing = false;
        }

        public void handleStopRecord() {
            isRecording = false;
        }

        //TODO Add End Flag
        private boolean audioStep() throws IOException {
            int index = mAudioEnc.dequeueInputBuffer(0);
            if (index >= 0) {
                final ByteBuffer buffer = getInputBuffer(mAudioEnc, index);
                buffer.clear();
                int length = mRecorder.read(buffer, bufferSize);//读入数据
                if (length > 0) {
                    if (baseTimeStamp != -1) {
                        long nano = System.nanoTime();
                        long time = (nano - baseTimeStamp - pauseDelayTime) / 1000;
                        System.out.println("TimeStampAudio=" + time + ";nanoTime=" + nano + ";baseTimeStamp=" + baseTimeStamp + ";pauseDelay=" + pauseDelayTime);
                        mAudioEnc.queueInputBuffer(index, 0, length, time, isRecording ? 0 : MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        mAudioEnc.queueInputBuffer(index, 0, length, 0, isRecording ? 0 : MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    }
                }
            }
            MediaCodec.BufferInfo mInfo = new MediaCodec.BufferInfo();
            int outIndex;
            do {
                outIndex = mAudioEnc.dequeueOutputBuffer(mInfo, 0);
                if (outIndex >= 0) {
                    if ((mInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.e(TAG, "audio end");
                        mAudioEnc.releaseOutputBuffer(outIndex, false);
                        return true;
                    }
                    ByteBuffer buffer = getOutputBuffer(mAudioEnc, outIndex);
                    buffer.position(mInfo.offset);
                    if (mMuxerStarted && mInfo.presentationTimeUs > 0) {
                        try {
                            mMuxer.writeSampleData(mAudioTrackIndex, buffer, mInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mAudioEnc.releaseOutputBuffer(outIndex, false);
                } else if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {

                } else if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    synchronized (lock) {
                        mAudioTrackIndex = mMuxer.addTrack(mAudioEnc.getOutputFormat());
                        Log.e(TAG, "add audio track-->" + mAudioTrackIndex);
                        if (mAudioTrackIndex >= 0 && mVideoTrackIndex >= 0) {
                            mMuxer.start();
                            mMuxerStarted = true;
                        }
                    }
                }
            } while (outIndex >= 0);
            return false;
        }

        private ByteBuffer getInputBuffer(MediaCodec codec, int index) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return codec.getInputBuffer(index);
            } else {
                return codec.getInputBuffers()[index];
            }
        }

        private ByteBuffer getOutputBuffer(MediaCodec codec, int index) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return codec.getOutputBuffer(index);
            } else {
                return codec.getOutputBuffers()[index];
            }
        }
    }

    static class AudioHandler extends Handler {

        private WeakReference<AudioEncoder> encoderWeakReference;

        public AudioHandler(AudioEncoder encoder) {
            encoderWeakReference = new WeakReference(encoder);
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            AudioEncoder audioEncoder = encoderWeakReference.get();
            if (audioEncoder == null) {
                return;
            }
            switch (what) {
                case MSG_START_RECORDING:
                    audioEncoder.handleStartRecord();
                    break;
                case MSG_STOP_RECORDING:
                    audioEncoder.handleStopRecord();
                    break;
                case MSG_AUDIO_STEP:
                    audioEncoder.handleAudioStep();
                    break;
                case MSG_PAUSE:
                    audioEncoder.handleAudioPause();
                    break;
                case MSG_RESUME:
                    audioEncoder.handleAudioResume();
                    break;
                case MSG_QUIT:
                    Looper.myLooper().quit();
                    break;
            }
        }

    }


    public void stopAudRecord() {
        audioRecorder.stopRecord();
        if (audioThread != null) {
            try {
                audioThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startRecord() {
        audioRecorder.startRecord();
    }

    public void pauseRecording() {
        audioRecorder.pause();
    }

    public void resumeRecording() {
        audioRecorder.resume();
    }
}
