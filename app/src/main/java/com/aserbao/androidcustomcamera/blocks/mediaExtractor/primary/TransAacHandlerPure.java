package com.aserbao.androidcustomcamera.blocks.mediaExtractor.primary;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
 
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 功能:mp3转换成为aac编码
 *
 * @author aserbao
 * @date : On 2019/1/3 5:37 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.mp3TranslateAAC
 * @Copyright: 个人版权所有
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class TransAacHandlerPure {
    private String srcFile;
    private String outFile;
    private long rangeStart = -1;
    private long rangeEnd = -1;
    private OnProgressListener listener;
 
 
    public TransAacHandlerPure(String srcFile, String outFile) {
        this(srcFile, outFile, null);
    }
 
    public TransAacHandlerPure(String srcFile, String outFile, OnProgressListener listener) {
        this(srcFile, outFile, -1, -1, listener);
    }
 
    public TransAacHandlerPure(String srcFile, String outFile, long rangeStart, long rangeEnd, OnProgressListener listener) {
        this.srcFile = srcFile;
        this.outFile = outFile;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.listener = listener;
    }
 
    public void start() {
        DecodeTask task = new DecodeTask(srcFile, outFile, listener);
        task.setRangeTime(rangeStart, rangeEnd);
        new Thread(task).start();
    }
 
    public void setRangeTime(long rangeStart, long rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }
 
 
    public void setListener(OnProgressListener listener) {
        this.listener = listener;
    }
 
    private static class DecodeTask implements Runnable, IDataObtain {
        private static final long TIME_OUT = 5000;
        private Queue<byte[]> mRawQueue;
        private MediaExtractor extractor;
        private boolean isFinish = false;
        private String srcFile;
        private MediaCodec codec;
        private String outFile;
        private OnProgressListener listener;
        private long rangeStart;
        private long rangeEnd;
        private int duration = 0;
        private OutputStream mOutput;
 
        public void setRangeTime(long rangeStart, long rangeEnd) {
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
        }
 
        public DecodeTask(String srcFile, String outFile, OnProgressListener listener) {
            this.srcFile = srcFile;
            this.outFile = outFile;
            this.listener = listener;
            mRawQueue = new LinkedBlockingQueue<>();
        }
 
        private void pushAvFrame(byte[] frame) {
            if (frame != null) {
                int len = mRawQueue.size();
                while (len > 10) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    len = mRawQueue.size();
                }
                synchronized (mRawQueue) {
                    mRawQueue.offer(frame);
                }
            }
        }
 
 
        @Override
        public void run() {
            TransAacHandlerPure.logMsg("decodec run");
            if (listener != null) {
                listener.onStart();
            }
            boolean isPrepare = false;
            try {
                prepare();
                isPrepare = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            TransAacHandlerPure.logMsg("decodec isPrepare  " + isPrepare);
            if (isPrepare) {
                decode();
            }
            release();
            if (!isPrepare && listener != null) {
                listener.onFail();
            }
            isFinish = true;
        }
 
        private void release() {
            if (extractor != null) {
                extractor.release();
                extractor = null;
            }
            if (codec != null) {
                codec.stop();
                codec.release();
                codec = null;
            }
        }
 
 
        private void prepare() throws IOException {
            extractor = new MediaExtractor();
            extractor.setDataSource(srcFile);
            int numTracks = extractor.getTrackCount();
            for (int i = 0; i < numTracks; i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mine = format.getString(MediaFormat.KEY_MIME);
                if (!TextUtils.isEmpty(mine) && mine.startsWith("audio")) {
                    extractor.selectTrack(i);
                    try {
                        duration = format.getInteger(MediaFormat.KEY_DURATION) / 1000;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(srcFile);
                        mediaPlayer.prepare();
                        duration = mediaPlayer.getDuration();
                        mediaPlayer.release();
                    }
                    codec = MediaCodec.createDecoderByType(mine);
                    codec.configure(format, null, null, 0);
                    codec.start();
                    TransAacHandlerPure.logMsg("New decode codec start:" + format.toString());
                    break;
                }
            }
            createFile(outFile + ".pcm", true);//测试  输出pcm格式
            mOutput = new DataOutputStream(new FileOutputStream(outFile + ".pcm"));
        }
 
        long last;
 
        private void decode() {
            ByteBuffer[] inputBuffers = codec.getInputBuffers();
            ByteBuffer[] outputBuffers = codec.getOutputBuffers();
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            TransAacHandlerPure.logMsg("loopDecode   start");
            if (rangeStart > 0) {//如果有裁剪，seek到裁剪的地方
                extractor.seekTo(rangeStart * 1000, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
            }
            boolean isEOS = false;
            while (true) {
                long timestamp = 0;
                if (!isEOS) {
                    int inIndex = codec.dequeueInputBuffer(TIME_OUT);
                    if (inIndex >= 0) {
                        ByteBuffer buffer = inputBuffers[inIndex];
                        int sampleSize = extractor.readSampleData(buffer, 0);
                        long timestampTemp = extractor.getSampleTime();
                        timestamp = timestampTemp / 1000;
                        TransAacHandlerPure.logMsg("loopDecode  readSampleData end sampleSize  " + sampleSize + "    buffer.capacity()=" + buffer.capacity());
                        TransAacHandlerPure.logMsg("loopDecode  readSampleData end timestamp" + timestamp);
                        if (rangeEnd > 0 && timestamp > rangeEnd) {
                            sampleSize = -1;
                        }
                        if (sampleSize <= 0) {
                            codec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            isEOS = true;
                        } else {
                            codec.queueInputBuffer(inIndex, 0, sampleSize, timestampTemp, 0);
                            extractor.advance();
                        }
                    }
                }
                int outIndex = codec.dequeueOutputBuffer(info, TIME_OUT);
//                TransAacHandlerPure.logMsg(" switch (outIndex)");
                switch (outIndex) {
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        outputBuffers = codec.getOutputBuffers();
                        TransAacHandlerPure.logMsg("dequeueOutputBuffer INFO_OUTPUT_BUFFERS_CHANGED!");
                        break;
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        MediaFormat mf = codec.getOutputFormat();
                        //开始编码线程
                        EncodeTask encodeTask = new EncodeTask(outFile, this, listener);
                        int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                        int pcmEncoding = mf.getInteger(MediaFormat.KEY_PCM_ENCODING);
                        int channelCount = mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                        encodeTask.setAudioParams(sampleRate, pcmEncoding, channelCount);
                        new Thread(encodeTask).start();
                        TransAacHandlerPure.logMsg("New format " + mf.toString());
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        TransAacHandlerPure.logMsg("dequeueOutputBuffer timed out!");
                        break;
                    default:
                        if (last == 0) {
                            last = System.currentTimeMillis();
                        }
                        long now = System.currentTimeMillis();
                        TransAacHandlerPure.logMsg("解码时间：" + (now - last) + " info.size  " + info.size);
                        last = now;
                        ByteBuffer buffer = outputBuffers[outIndex];
                        byte[] outData = new byte[info.size];
                        buffer.get(outData, 0, info.size);
                        codec.releaseOutputBuffer(outIndex, true);
                        try {
                            mOutput.write(outData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        pushAvFrame(outData);
                        if (listener != null) {
                            listener.onProgress(rangeEnd > 0 ? (int) rangeEnd : duration, rangeStart > 0 ? (int) (timestamp - rangeStart) : (int) timestamp);
                        }
                        break;
                }
                // All decoded frames have been rendered, we can stop playing now
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    TransAacHandlerPure.logMsg("OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                    break;
                }
            }
        }
 
 
        @Override
        public byte[] getRawFrame() {
            int len = mRawQueue.size();
            if (len > 0) {
                synchronized (mRawQueue) {
                    return mRawQueue.poll();
                }
            }
            return null;
        }
 
        @Override
        public boolean isFinish() {
            return isFinish;
        }
    }
 
    private static void logMsg(String msg) {
        Log.d(TransAacHandlerPure.class.getSimpleName(), msg);
    }
 
    private static class EncodeTask implements Runnable {
        private static final long TIME_OUT = 5000;
        private IDataObtain obtain;
        private String outFile;
        private MediaCodec encoder;
        private OutputStream mOutput;
        private OnProgressListener listener;
        private long last;
        private int sampleRate;
        private int pcmEncoding;
        private int channelCount;
 
        public EncodeTask(String outFile, IDataObtain obtain, OnProgressListener listener) {
            this.obtain = obtain;
            this.outFile = outFile;
            this.listener = listener;
        }
 
        public void setAudioParams(int sampleRate, int pcmEncoding, int channelCount) {
            this.sampleRate = sampleRate;
            this.pcmEncoding = pcmEncoding;
            this.channelCount = channelCount;
        }
 
        @Override
        public void run() {
            boolean isPrepare = false;
            try {
                prepare();
                isPrepare = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (isPrepare && obtain != null) {
                encode();
            }
            release();
            if (listener != null) {
                if (isPrepare) {
                    listener.onSuccess();
                } else {
                    listener.onFail();
                }
 
            }
 
        }
 
        private void release() {
            if (encoder != null) {
                encoder.stop();
                encoder.release();
                encoder = null;
            }
            if (mOutput != null) {
                try {
                    mOutput.flush();
                    mOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mOutput = null;
            }
        }
 
        private void encode() {
            boolean isFinish = false;
            while (true) {
                if (!isFinish) {
                    byte[] rawData = obtain.getRawFrame();
                    if (rawData == null) {
                        if (obtain.isFinish()) {
                            isFinish = true;
                            int inIndex = encoder.dequeueInputBuffer(TIME_OUT);
                            encoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        } else {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        continue;
                    }
                    ByteBuffer[] inputBuffers = encoder.getInputBuffers();
 
                    int inIndex = encoder.dequeueInputBuffer(TIME_OUT);
                    if (inIndex >= 0) {
                        ByteBuffer inputBuffer = inputBuffers[inIndex];
                        inputBuffer.clear();
                        inputBuffer.put(rawData);
                        encoder.queueInputBuffer(inIndex, 0, rawData.length, System.nanoTime(), 0);
                    }
                }
                ByteBuffer[] outputBuffers = encoder.getOutputBuffers();
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                int outIndex = encoder.dequeueOutputBuffer(info, TIME_OUT);
                if (outIndex >= 0) {
                    if (last == 0) {
                        last = System.currentTimeMillis();
                    }
                    long now = System.currentTimeMillis();
                    TransAacHandlerPure.logMsg("编码码时间：" + (now - last) + " info.size  " + info.size);
                    last = now;
                    while (outIndex >= 0) {
                        ByteBuffer outputBuffer = outputBuffers[outIndex];
                        int len = info.size + 7;
                        byte[] outData = new byte[len];
                        addADTStoPacket(outData, len);
                        outputBuffer.get(outData, 7, info.size);
                        encoder.releaseOutputBuffer(outIndex, false);
                        try {
                            mOutput.write(outData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } catch (Error e) {
                            e.printStackTrace();
                        }
                        outIndex = encoder.dequeueOutputBuffer(info, TIME_OUT);
                    }
                }
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d("123","encode OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                    break;
                }
            }
        }
 
        /**
         * 给编码出的aac裸流添加adts头字段
         *
         * @param packet    要空出前7个字节，否则会搞乱数据
         * @param packetLen
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
 
        private void prepare() throws IOException {
            String mime = MediaFormat.MIMETYPE_AUDIO_AAC;
            encoder = MediaCodec.createEncoderByType(mime);
            MediaFormat format = MediaFormat.createAudioFormat(mime, sampleRate, channelCount);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 96000);
            format.setInteger(MediaFormat.KEY_PCM_ENCODING, pcmEncoding);
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 20 * 1024);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            logMsg(" New  " + format.toString());
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            encoder.start();
            createFile(outFile, true);
            mOutput = new DataOutputStream(new FileOutputStream(outFile));
        }
 
    }
 
    private static boolean createFile(String filePath, boolean recreate) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (recreate) {
                    file.delete();
                    file.createNewFile();
                }
            } else {
                // 如果路径不存在，先创建路径
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
 
    public interface IDataObtain {
        byte[] getRawFrame();
 
        boolean isFinish();
    }
 
    public static interface OnProgressListener {
        void onStart();
 
        void onProgress(int max, int progress);
 
        void onSuccess();
 
        void onFail();
    }
}
