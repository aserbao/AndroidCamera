package com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.mp3TranslateAAC;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by turbo on 2018/2/9.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class AudioDecoder {

    private static final String TAG = AudioDecoder.class.getSimpleName();
    private static ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private MediaExtractor mMediaExtractor;
    private MediaCodec mDecoder;
    private static final int TIMEOUT_US = 1000;
    private ByteBuffer[] mInputByteBuffers;
    private ByteBuffer[] mOutputByteBuffers;
    private MediaCodec.BufferInfo mBufferInfo;
    private String mMusicPath;
    private OnCapturePCMListener mOnCapturePCMListener;
    private int mSampleRate = 0;
    private boolean eosReceived;

    public AudioDecoder(String musicPath) throws IOException {
        if (musicPath == null) {
            throw new NullPointerException("musicPath can't be null");
        }
        mMusicPath = musicPath;
        eosReceived = false;
        initDecoder();
        mDecoder.start();
        mExecutorService.execute(new DecodeRunnable());
    }

    private void initDecoder() throws IOException, NullPointerException {
        mMediaExtractor = new MediaExtractor();
        File file = new File(mMusicPath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            FileDescriptor fd = fis.getFD();
            mMediaExtractor.setDataSource(fd);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Release stuff
            mMediaExtractor.release();
            try {
                if(fis != null) {
                    fis.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
//        mMediaExtractor.setDataSource(mMusicPath);
        int channel = 0;
        int numTracks = mMediaExtractor.getTrackCount();
        for (int i = 0; i < numTracks; ++i) {
            MediaFormat format = mMediaExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio")) {
                mMediaExtractor.selectTrack(i);
                mDecoder = MediaCodec.createDecoderByType(mime);
                if (mime.equals(MediaFormat.MIMETYPE_AUDIO_AAC)) {
                    // AAC ADTS头部处理
                    ByteBuffer csd = format.getByteBuffer("csd-0");
                    for (int k = 0; k < csd.capacity(); ++k) {
                        Log.e(TAG, "csd : " + csd.array()[k]);
                    }
                    mSampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    channel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    format = makeADTSData(MediaCodecInfo.CodecProfileLevel.AACObjectLC, mSampleRate, channel);
                }
            }
            mDecoder.configure(format, null, null, 0);
            break;
        }
    }

    private MediaFormat makeADTSData(int audioProfile, int sampleRate, int channelConfig) {
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelConfig);

        int samplingFreq[] = {
                96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050,
                16000, 12000, 11025, 8000
        };

        // Search the Sampling Frequencies
        int sampleIndex = -1;
        for (int i = 0; i < samplingFreq.length; ++i) {
            if (samplingFreq[i] == sampleRate) {
                Log.d(TAG, "kSamplingFreq " + samplingFreq[i] + " i : " + i);
                sampleIndex = i;
            }
        }

        if (sampleIndex == -1) {
            return null;
        }

        ByteBuffer csd = ByteBuffer.allocate(2);
        csd.put((byte) ((audioProfile << 3) | (sampleIndex >> 1)));

        csd.position(1);
        csd.put((byte) ((byte) ((sampleIndex << 7) & 0x80) | (channelConfig << 3)));
        csd.flip();
        format.setByteBuffer("csd-0", csd); // add csd-0

        for (int k = 0; k < csd.capacity(); ++k) {
            Log.e(TAG, "csd : " + csd.array()[k]);
        }

        return format;
    }

    public void decode() {
        mInputByteBuffers = mDecoder.getInputBuffers();
        mOutputByteBuffers = mDecoder.getOutputBuffers();
        mBufferInfo = new MediaCodec.BufferInfo();

        while (!eosReceived) {
            int inIndex = mDecoder.dequeueInputBuffer(TIMEOUT_US);
            if (inIndex >= 0) {
                ByteBuffer buffer = mInputByteBuffers[inIndex];
                int sampleSize = mMediaExtractor.readSampleData(buffer, 0);
                if (sampleSize < 0) {
                    // We shouldn't stop the playback at this point, just pass the EOS
                    // flag to mDecoder, we will get it again from the
                    // dequeueOutputBuffer
                    Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM");
                    mDecoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);

                } else {
                    mDecoder.queueInputBuffer(inIndex, 0, sampleSize, mMediaExtractor.getSampleTime(), 0);
                    mMediaExtractor.advance();
                }

                int outIndex = mDecoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
                switch (outIndex) {
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        Log.d(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
                        mOutputByteBuffers = mDecoder.getOutputBuffers();
                        break;

                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        MediaFormat format = mDecoder.getOutputFormat();
                        Log.d(TAG, "New format " + format);

                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        Log.d(TAG, "dequeueOutputBuffer timed out!");
                        break;

                    default:
                        ByteBuffer outBuffer = mOutputByteBuffers[outIndex];
                        Log.v(TAG, "We can't use this buffer but render it due to the API limit, " + outBuffer);

                        final byte[] chunk = new byte[mBufferInfo.size];
                        outBuffer.get(chunk); // Read the buffer all at once
                        outBuffer.clear(); // ** MUST DO!!! OTHERWISE THE NEXT TIME YOU GET THIS SAME BUFFER BAD THINGS WILL HAPPEN

                        MediaFormat mFormat = mDecoder.getOutputFormat();
                        mOnCapturePCMListener.capturePCM(chunk, mFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE), mFormat.getInteger
                                (MediaFormat.KEY_CHANNEL_COUNT));
                        mDecoder.releaseOutputBuffer(outIndex, false);
                        break;
                }

                // All decoded frames have been rendered, we can stop playing now
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(TAG, "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                    break;
                }
            }
        }

        mDecoder.stop();
        mDecoder.release();
        mDecoder = null;

        mMediaExtractor.release();
        mMediaExtractor = null;
        eosReceived = true;

        mExecutorService.shutdown();
        mExecutorService = null;
    }

    private class DecodeRunnable implements Runnable {

        @Override
        public void run() {
            while (!eosReceived) {
                decode();
            }
        }
    }

    public interface OnCapturePCMListener {
        void capturePCM(byte[] pcm, int sampleRate, int channel);
    }

    public void setOnCapturePCMListener(OnCapturePCMListener OnCapturePCMListener) {
        mOnCapturePCMListener = OnCapturePCMListener;
    }
}
