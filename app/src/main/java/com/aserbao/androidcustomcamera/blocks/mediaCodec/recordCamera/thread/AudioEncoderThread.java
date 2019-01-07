package com.aserbao.androidcustomcamera.blocks.mediaCodec.recordCamera.thread;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * 音频编码线程
 * Created by renhui on 2017/9/25.
 */
public class AudioEncoderThread extends Thread {

    public static final String TAG = "AudioEncoderThread";

    public static final int SAMPLES_PER_FRAME = 1024;
    public static final int FRAMES_PER_BUFFER = 25;
    private static final int TIMEOUT_USEC = 10000;
    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 16000;
    private static final int BIT_RATE = 64000;
    private static final int[] AUDIO_SOURCES = new int[]{MediaRecorder.AudioSource.DEFAULT};


    private final Object lock = new Object();
    private MediaCodec mMediaCodec;                // API >= 16(Android4.1.2)
    private volatile boolean isExit = false;
    private WeakReference<MediaMuxerThread> mediaMuxerRunnable;
    private AudioRecord audioRecord;
    private MediaCodec.BufferInfo mBufferInfo;        // API >= 16(Android4.1.2)
    private volatile boolean isStart = false;
    private volatile boolean isMuxerReady = false;
    private long prevOutputPTSUs = 0;
    private MediaFormat audioFormat;

    public AudioEncoderThread(WeakReference<MediaMuxerThread> mediaMuxerRunnable) {
        this.mediaMuxerRunnable = mediaMuxerRunnable;
        mBufferInfo = new MediaCodec.BufferInfo();
        prepare();
    }

    private static final MediaCodecInfo selectAudioCodec(final String mimeType) {
        MediaCodecInfo result = null;
        // get the list of available codecs
        Log.e("111", "selectAudioCodec");
        final int numCodecs = MediaCodecList.getCodecCount();
        Log.e("111", "selectAudioCodec。。。" + numCodecs);
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                Log.i(TAG, "supportedType:" + codecInfo.getName() + ",MIME=" + types[j]);
                if (types[j].equalsIgnoreCase(mimeType)) {
                    if (result == null) {
                        result = codecInfo;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private void prepare() {
        MediaCodecInfo audioCodecInfo = selectAudioCodec(MIME_TYPE);
        if (audioCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }
        Log.e(TAG, "selected codec: " + audioCodecInfo.getName());

        audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
        Log.e(TAG, "format: " + audioFormat);
    }

    private void startMediaCodec() throws IOException {
        if (mMediaCodec != null) {
            return;
        }
        mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
        Log.i(TAG, "prepareAudio finishing");

        prepareAudioRecord();

        isStart = true;
    }

    private void stopMediaCodec() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
            mMediaCodec = null;
        }
        isStart = false;
        Log.e("angcyo-->", "stop audio 录制...");
    }

    public synchronized void restart() {
        isStart = false;
        isMuxerReady = false;
    }

    private void prepareAudioRecord() {
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        try {
            final int min_buffer_size = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
            if (buffer_size < min_buffer_size)
                buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;

            audioRecord = null;
            for (final int source : AUDIO_SOURCES) {
                try {
                    audioRecord = new AudioRecord(source, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                    if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
                        audioRecord = null;
                } catch (Exception e) {
                    audioRecord = null;
                }
                if (audioRecord != null) break;
            }
        } catch (final Exception e) {
            Log.e(TAG, "AudioThread#run", e);
        }

        if (audioRecord != null) {
            audioRecord.startRecording();
        }
    }

    public void exit() {
        isExit = true;
    }

    public void setMuxerReady(boolean muxerReady) {
        synchronized (lock) {
            Log.e("angcyo-->", Thread.currentThread().getId() + " audio -- setMuxerReady..." + muxerReady);
            isMuxerReady = muxerReady;
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
        int readBytes;
        while (!isExit) {

            /*启动或者重启*/
            if (!isStart) {
                stopMediaCodec();

                Log.e(TAG, Thread.currentThread().getId() + " audio -- run..." + isMuxerReady);

                if (!isMuxerReady) {
                    synchronized (lock) {
                        try {
                            Log.e(TAG, "audio -- 等待混合器准备...");
                            lock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }

                if (isMuxerReady) {
                    try {
                        Log.e(TAG, "audio -- startMediaCodec...");
                        startMediaCodec();
                    } catch (IOException e) {
                        e.printStackTrace();
                        isStart = false;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            } else if (audioRecord != null) {
                buf.clear();
                readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                if (readBytes > 0) {
                    // set audio data to encoder
                    buf.position(readBytes);
                    buf.flip();
                    Log.e("ang-->", "解码音频数据:" + readBytes);
                    try {
                        encode(buf, readBytes, getPTSUs());
                    } catch (Exception e) {
                        Log.e(TAG, "解码音频(Audio)数据 失败");
                        e.printStackTrace();
                    }
                }
            }

        }
        Log.e(TAG, "Audio 录制线程 退出...");
    }

    private void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
        if (isExit) return;
        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            /*向编码器输入数据*/
        if (inputBufferIndex >= 0) {
            final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            if (buffer != null) {
                inputBuffer.put(buffer);
            }
            if (length <= 0) {
                Log.i(TAG, "send BUFFER_FLAG_END_OF_STREAM");
                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, length, presentationTimeUs, 0);
            }
        } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            // wait for MediaCodec encoder is ready to encode
            // nothing to do here because MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
            // will wait for maximum TIMEOUT_USEC(10msec) on each call
        }

        /*获取解码后的数据*/
        final MediaMuxerThread muxer = mediaMuxerRunnable.get();
        if (muxer == null) {
            Log.w(TAG, "MediaMuxerRunnable is unexpectedly null");
            return;
        }
        ByteBuffer[] encoderOutputBuffers = mMediaCodec.getOutputBuffers();
        int encoderStatus;

        do {
            encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                encoderOutputBuffers = mMediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                final MediaFormat format = mMediaCodec.getOutputFormat(); // API >= 16
                MediaMuxerThread mediaMuxerRunnable = this.mediaMuxerRunnable.get();
                if (mediaMuxerRunnable != null) {
                    Log.e(TAG, "添加音轨 INFO_OUTPUT_FORMAT_CHANGED " + format.toString());
                    mediaMuxerRunnable.addTrackIndex(MediaMuxerThread.TRACK_AUDIO, format);
                }

            } else if (encoderStatus < 0) {
                Log.e(TAG, "encoderStatus < 0");
            } else {
                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0 && muxer != null && muxer.isMuxerStart()) {
                    mBufferInfo.presentationTimeUs = getPTSUs();
                    Log.e(TAG, "发送音频数据 " + mBufferInfo.size);
                    muxer.addMuxerData(new MediaMuxerThread.MuxerData(MediaMuxerThread.TRACK_AUDIO, encodedData, mBufferInfo));
                    prevOutputPTSUs = mBufferInfo.presentationTimeUs;
                }
                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
            }
        } while (encoderStatus >= 0);
    }

    /**
     * get next encoding presentationTimeUs
     *
     * @return
     */
    private long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }
}
