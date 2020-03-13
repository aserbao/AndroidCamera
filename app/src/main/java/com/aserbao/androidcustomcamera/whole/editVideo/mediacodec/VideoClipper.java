package com.aserbao.androidcustomcamera.whole.editVideo.mediacodec;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;

import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.encodeDecode.InputSurface;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.VideoInfo;
import com.aserbao.androidcustomcamera.whole.editVideo.view.BaseImageView;
import com.aserbao.androidcustomcamera.whole.record.filters.gpuFilters.baseFilter.GPUImageFilter;
import com.aserbao.androidcustomcamera.whole.record.other.MagicFilterFactory;
import com.aserbao.androidcustomcamera.whole.record.other.MagicFilterType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.media.MediaExtractor.SEEK_TO_PREVIOUS_SYNC;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class VideoClipper {
    private static final String TAG = "VideoClipper";

    final int TIMEOUT_USEC = 0;
    private String mInputVideoPath;
    private String mOutputVideoPath;

    MediaCodec videoDecoder;
    MediaCodec videoEncoder;
    MediaCodec audioDecoder;
    MediaCodec audioEncoder;

    MediaExtractor mVideoExtractor;
    MediaExtractor mAudioExtractor;
    MediaMuxer mMediaMuxer;
    static ExecutorService executorService = Executors.newFixedThreadPool(4);
    int muxVideoTrack = -1;
    int muxAudioTrack = -1;
    int videoTrackIndex = -1;
    int audioTrackIndex = -1;
    long startPosition;
    long clipDur;
    int videoWidth;
    int videoHeight;
    int videoRotation;
    OutputSurface outputSurface = null;
    InputSurface inputSurface = null;
    MediaFormat videoFormat;
    MediaFormat audioFormat;
    GPUImageFilter mFilter;
    boolean isOpenBeauty;
    boolean videoFinish = false;
    boolean audioFinish = false;
    boolean released = false;
    long before;
    long after;
    Object lock = new Object();
    boolean muxStarted = false;
    OnVideoCutFinishListener listener;

    public VideoClipper() {
        try {
            videoDecoder = MediaCodec.createDecoderByType("video/avc");
            videoEncoder = MediaCodec.createEncoderByType("video/avc");
            audioDecoder = MediaCodec.createDecoderByType("audio/mp4a-latm");
            audioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInputVideoPath(String inputPath) {
        mInputVideoPath = inputPath;
        initVideoInfo();
    }

    public void setOutputVideoPath(String outputPath) {
        mOutputVideoPath = outputPath;
    }


    public void setOnVideoCutFinishListener(OnVideoCutFinishListener listener) {
        this.listener = listener;
    }

    public void setFilter(GPUImageFilter filter) {
        if (filter == null ) {
            mFilter = null;
            return;
        }
        mFilter = filter;
    }
    public void setFilterType(MagicFilterType type) {
        if (type == null || type == MagicFilterType.NONE) {
            mFilter = null;
            return;
        }
        mFilter = MagicFilterFactory.initFilters(type);
    }

    public void showBeauty(){
        isOpenBeauty = true;
    }

    private ArrayList<BaseImageView> mViews = new ArrayList<>();
    private Resources mResources;
    public void clipVideo(long startPosition, long clipDur, ArrayList<BaseImageView> views, Resources resources) throws IOException {
        mViews = views;
        mResources = resources;
        before = System.currentTimeMillis();
        this.startPosition = startPosition;
        this.clipDur = clipDur;
        mVideoExtractor = new MediaExtractor();
        mAudioExtractor = new MediaExtractor();
        mVideoExtractor.setDataSource(mInputVideoPath);
        mAudioExtractor.setDataSource(mInputVideoPath);
        mMediaMuxer = new MediaMuxer(mOutputVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        for (int i = 0; i < mVideoExtractor.getTrackCount(); i++) {
            MediaFormat format = mVideoExtractor.getTrackFormat(i);
            if (format.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                videoTrackIndex = i;
                videoFormat = format;
                continue;
            }
            if (format.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                audioTrackIndex = i;
                audioFormat = format;
                continue;
            }
            Log.e(TAG, "clipVideo: audioTrackIndex= " + audioTrackIndex + " videoTrackIndex = " + videoTrackIndex);
        }
        executorService.execute(videoCliper);
        executorService.execute(audioCliper);
    }

    private Runnable videoCliper = new Runnable() {
        @Override
        public void run() {
            mVideoExtractor.selectTrack(videoTrackIndex);

            long firstVideoTime = mVideoExtractor.getSampleTime();
            mVideoExtractor.seekTo(firstVideoTime + startPosition, SEEK_TO_PREVIOUS_SYNC);

            initVideoCodec();
            startVideoCodec(videoDecoder, videoEncoder, mVideoExtractor, inputSurface, outputSurface, firstVideoTime, startPosition, clipDur);

            videoFinish = true;
            release();
        }
    };

    private Runnable audioCliper = new Runnable() {
        @Override
        public void run() {
            mAudioExtractor.selectTrack(audioTrackIndex);
            initAudioCodec();
            startAudioCodec(audioDecoder, audioEncoder, mAudioExtractor, mAudioExtractor.getSampleTime(), startPosition, clipDur);
            audioFinish = true;
            release();
        }
    };

    private void initVideoInfo() {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(mInputVideoPath);
        String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        videoWidth = Integer.parseInt(width);
        videoHeight = Integer.parseInt(height);
        if(rotation.equals("180") && Integer.parseInt(width) > Integer.parseInt(height)){
            videoRotation = 180;
        }else {
            videoRotation = Integer.parseInt(rotation);
        }
    }

    private void initAudioCodec() {
        audioDecoder.configure(audioFormat, null, null, 0);
        audioDecoder.start();
        MediaFormat format = MediaFormat.createAudioFormat("audio/mp4a-latm", 44100, /*channelCount*/2);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 3000000);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        audioEncoder.start();
    }

    private void startAudioCodec(MediaCodec decoder, MediaCodec encoder, MediaExtractor extractor, long firstSampleTime, long startPosition, long duration) {
        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
        ByteBuffer[] decoderOutputBuffers = decoder.getOutputBuffers();
        ByteBuffer[] encoderInputBuffers = encoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo outputInfo = new MediaCodec.BufferInfo();
        boolean done = false;
        boolean inputDone = false;
        boolean decodeDone = false;
        extractor.seekTo(firstSampleTime + startPosition, SEEK_TO_PREVIOUS_SYNC);
        int decodeinput=0;
        int encodeinput=0;
        int encodeoutput=0;
        long lastEncodeOutputTimeStamp=-1;
        while (!done) {
            if (!inputDone) {
                int inputIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputIndex >= 0) {
                    ByteBuffer inputBuffer = decoderInputBuffers[inputIndex];
                    inputBuffer.clear();
                    int readSampleData = extractor.readSampleData(inputBuffer, 0);
                    long dur = extractor.getSampleTime() - firstSampleTime - startPosition;
                    if ((dur < duration) && readSampleData > 0) {
                        decoder.queueInputBuffer(inputIndex, 0, readSampleData, extractor.getSampleTime(), 0);
                        decodeinput++;
                        extractor.advance();
                    } else {
                        decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    }
                }
            }
            if (!decodeDone) {
                int index = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    decoderOutputBuffers = decoder.getOutputBuffers();
                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = decoder.getOutputFormat();
                } else if (index < 0) {
                } else {
                    boolean canEncode = (info.size != 0 && info.presentationTimeUs - firstSampleTime > startPosition);
                    boolean endOfStream = (info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                    if (canEncode&&!endOfStream) {
                        ByteBuffer decoderOutputBuffer = decoderOutputBuffers[index];

                        int encodeInputIndex = encoder.dequeueInputBuffer(TIMEOUT_USEC);
                        if(encodeInputIndex>=0){
                            ByteBuffer encoderInputBuffer = encoderInputBuffers[encodeInputIndex];
                            encoderInputBuffer.clear();
                            if (info.size < 4096) {
                                byte[] chunkPCM = new byte[info.size];
                                decoderOutputBuffer.get(chunkPCM);
                                decoderOutputBuffer.clear();
                                byte[] stereoBytes = new byte[info.size * 2];
                                for (int i = 0; i < info.size; i += 2) {
                                    stereoBytes[i * 2 + 0] = chunkPCM[i];
                                    stereoBytes[i * 2 + 1] = chunkPCM[i + 1];
                                    stereoBytes[i * 2 + 2] = chunkPCM[i];
                                    stereoBytes[i * 2 + 3] = chunkPCM[i + 1];
                                }
                                encoderInputBuffer.put(stereoBytes);
                                encoder.queueInputBuffer(encodeInputIndex, 0, stereoBytes.length, info.presentationTimeUs, 0);
                                encodeinput++;
                            }else{
                                encoderInputBuffer.put(decoderOutputBuffer);
                                encoder.queueInputBuffer(encodeInputIndex, info.offset, info.size, info.presentationTimeUs, 0);
                                encodeinput++;
                            }
                        }
                    }
                    if(endOfStream){
                        int encodeInputIndex = encoder.dequeueInputBuffer(TIMEOUT_USEC);
                        encoder.queueInputBuffer(encodeInputIndex, 0, 0, info.presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        decodeDone = true;
                    }
                    decoder.releaseOutputBuffer(index, false);
                }
            }
            boolean encoderOutputAvailable = true;
            while (encoderOutputAvailable) {
                int encoderStatus = encoder.dequeueOutputBuffer(outputInfo, TIMEOUT_USEC);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    encoderOutputAvailable = false;
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    encoderOutputBuffers = encoder.getOutputBuffers();
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = encoder.getOutputFormat();
                    startMux(newFormat, 1);
                } else if (encoderStatus < 0) {
                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    done = (outputInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                    if (done) {
                        encoderOutputAvailable = false;
                    }
                    if (outputInfo.presentationTimeUs == 0 && !done) {
                        continue;
                    }
                    if (outputInfo.size != 0&&outputInfo.presentationTimeUs>0) {
                        /*encodedData.position(outputInfo.offset);
                        encodedData.limit(outputInfo.offset + outputInfo.size);*/
                        if(!muxStarted){
                            synchronized (lock){
                                if(!muxStarted){
                                    try {
                                        lock.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        if(outputInfo.presentationTimeUs>lastEncodeOutputTimeStamp){
                            encodeoutput++;
                            System.out.println("videoCliper audio encodeOutput"+encodeoutput+" dataSize"+outputInfo.size+" sampeTime"+outputInfo.presentationTimeUs);
                            mMediaMuxer.writeSampleData(muxAudioTrack, encodedData, outputInfo);
                            lastEncodeOutputTimeStamp=outputInfo.presentationTimeUs;
                        }
                    }

                    encoder.releaseOutputBuffer(encoderStatus, false);
                }
                if (encoderStatus != MediaCodec.INFO_TRY_AGAIN_LATER) {
                    continue;
                }
            }
        }
    }

    private void initVideoCodec() {
        int encodeW = videoWidth;
        int encodeH = videoHeight;
        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", encodeW, encodeH);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3000000);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        videoEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        inputSurface = new InputSurface(videoEncoder.createInputSurface());
        inputSurface.makeCurrent();
        videoEncoder.start();
        VideoInfo info = new VideoInfo();
        info.width = videoWidth ;
        info.height = videoHeight;
        info.rotation = videoRotation;
        outputSurface = new OutputSurface(info,mViews,mResources);
        outputSurface.isBeauty(isOpenBeauty);

        if (mFilter != null) {
            outputSurface.addGpuFilter(mFilter);
        }

        videoDecoder.configure(videoFormat, outputSurface.getSurface(), null, 0);
        videoDecoder.start();
    }


    private void startVideoCodec(MediaCodec decoder, MediaCodec encoder, MediaExtractor extractor, InputSurface inputSurface, OutputSurface outputSurface, long firstSampleTime, long startPosition, long duration) {
        ByteBuffer[] decoderInputBuffers = decoder.getInputBuffers();
        ByteBuffer[] encoderOutputBuffers = encoder.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo outputInfo = new MediaCodec.BufferInfo();
        boolean done = false;
        boolean inputDone = false;
        boolean decodeDone = false;
        while (!done) {
            if (!inputDone) {
                int inputIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (inputIndex >= 0) {
                    ByteBuffer inputBuffer = decoderInputBuffers[inputIndex];
                    inputBuffer.clear();
                    int readSampleData = extractor.readSampleData(inputBuffer, 0);
                    long dur = extractor.getSampleTime() - firstSampleTime - startPosition;
                    if ((dur < duration) && readSampleData > 0) {
                        decoder.queueInputBuffer(inputIndex, 0, readSampleData, extractor.getSampleTime(), 0);
                        extractor.advance();
                    } else {
                        decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        inputDone = true;
                    }
                }
            }
            if (!decodeDone) {
                int index = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC);
                if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {

                } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {

                } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                    MediaFormat newFormat = decoder.getOutputFormat();

                } else if (index < 0) {
                } else {
                    boolean doRender = (info.size != 0 && info.presentationTimeUs - firstSampleTime > startPosition);
                    decoder.releaseOutputBuffer(index, doRender);
                    if (doRender) {

                        outputSurface.awaitNewImage();

                        outputSurface.drawImage(info.presentationTimeUs / 1000);
                        listener.onProgress((float)info.presentationTimeUs / (float)duration);

                        inputSurface.setPresentationTime(info.presentationTimeUs * 1000);
                        inputSurface.swapBuffers();
                    }
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        encoder.signalEndOfInputStream();
                        decodeDone = true;
                    }
                }
            }
            boolean encoderOutputAvailable = true;
            while (encoderOutputAvailable) {
                int encoderStatus = encoder.dequeueOutputBuffer(outputInfo, TIMEOUT_USEC);
                if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {

                    encoderOutputAvailable = false;
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    encoderOutputBuffers = encoder.getOutputBuffers();
                } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    MediaFormat newFormat = encoder.getOutputFormat();
                    startMux(newFormat, 0);
                } else if (encoderStatus < 0) {
                } else {
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    done = (outputInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0;
                    if (done) {
                        encoderOutputAvailable = false;
                    }

                    if (outputInfo.presentationTimeUs == 0 && !done) {
                        continue;
                    }
                    if (outputInfo.size != 0) {
                        encodedData.position(outputInfo.offset);
                        encodedData.limit(outputInfo.offset + outputInfo.size);
                        if(!muxStarted){
                            synchronized (lock){
                                if(!muxStarted){
                                    try {
                                        lock.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        mMediaMuxer.writeSampleData(muxVideoTrack, encodedData, outputInfo);
                    }

                    encoder.releaseOutputBuffer(encoderStatus, false);
                }
                if (encoderStatus != MediaCodec.INFO_TRY_AGAIN_LATER) {

                    continue;
                }
            }
        }
    }

    private void startMux(MediaFormat mediaFormat, int flag) {
        if (flag == 0) {
            muxVideoTrack = mMediaMuxer.addTrack(mediaFormat);
        } else if (flag == 1) {
            muxAudioTrack = mMediaMuxer.addTrack(mediaFormat);
        }
        synchronized (lock) {
            if (muxAudioTrack != -1 && muxVideoTrack != -1 && !muxStarted) {
                mMediaMuxer.start();
                muxStarted = true;
                lock.notify();
            }
        }
    }

    private synchronized void release() {
        if (!videoFinish || !audioFinish || released) {
            return;
        }
        mVideoExtractor.release();
        mAudioExtractor.release();
        mMediaMuxer.stop();
        mMediaMuxer.release();
        if (outputSurface != null) {
            outputSurface.release();
        }
        if (inputSurface != null) {
            inputSurface.release();
        }
        videoDecoder.stop();
        videoDecoder.release();
        videoEncoder.stop();
        videoEncoder.release();
        audioDecoder.stop();
        audioDecoder.release();
        audioEncoder.stop();
        audioEncoder.release();
        released = true;
        after = System.currentTimeMillis();
        if (listener != null) {
            listener.onFinish();
        }
    }

    public interface OnVideoCutFinishListener {
        void onFinish();
        void onProgress(float percent);
    }
}
