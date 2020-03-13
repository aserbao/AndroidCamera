package com.aserbao.androidcustomcamera.blocks.mediaCodec.show;

import android.content.Intent;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.LocalVideoActivity;
import com.aserbao.androidcustomcamera.whole.editVideo.VideoEditActivity;
import com.aserbao.androidcustomcamera.whole.editVideo.mediacodec.VideoClipper;
import com.aserbao.androidcustomcamera.whole.editVideo.view.BaseImageView;
import com.aserbao.androidcustomcamera.whole.pickvideo.VideoPickActivity;
import com.aserbao.androidcustomcamera.whole.pickvideo.beans.VideoFile;
import com.aserbao.androidcustomcamera.whole.record.RecorderActivity;
import com.aserbao.androidcustomcamera.whole.record.other.MagicFilterType;
import com.aserbao.androidcustomcamera.whole.videoPlayer.VideoPlayerActivity2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.MAX_NUMBER;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.STORAGE_TEMP_VIDEO_PATH;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.STORAGE_TEMP_VIDEO_PATH1;
import static com.aserbao.androidcustomcamera.whole.pickvideo.BaseActivity.IS_NEED_FOLDER_LIST;
import static com.aserbao.androidcustomcamera.whole.pickvideo.VideoPickActivity.IS_NEED_CAMERA;

/**
 * MediaCodec解码显示到GlSurfaceView上
 */
public class MediaCodecShowOnGlSurfaceView extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final String TAG = "MediaCodecShowOnGlSurfa";
    @BindView(R.id.mSurface)
    SurfaceView mSurfaceView;

    public SurfaceHolder mHolder;
    private long mStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec_show_on_gl_surface_view);
        ButterKnife.bind(this);
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
    }

    @OnClick({R.id.sel_btn, R.id.decode_show_btn,R.id.detail_video_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sel_btn:
                Intent intent2 = new Intent(this, VideoPickActivity.class);
                intent2.putExtra(IS_NEED_CAMERA, false);
                intent2.putExtra(MAX_NUMBER, 1);
                intent2.putExtra(IS_NEED_FOLDER_LIST, true);
                startActivityForResult(intent2, StaticFinalValues.REQUEST_CODE_PICK_VIDEO);
                break;
            case R.id.decode_show_btn:
                /*MediaCodecUtil1 mediaCodecUtil1 = new MediaCodecUtil1(videoFileName, mHolder.getSurface());
                mediaCodecUtil1.start();*/

                break;
            case R.id.detail_video_btn:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String outputPath1 = STORAGE_TEMP_VIDEO_PATH1;
                        videoClipper(videoFileName1,outputPath1);
                        final String outputPath = STORAGE_TEMP_VIDEO_PATH;
                        videoClipper(videoFileName,outputPath);
                    }
                }).start();
                break;

        }
    }

//    String videoFileName = "/storage/emulated/0/12345.mp4";
    String videoFileName1 = "/storage/emulated/0/DCIM/Camera/VIDEO_2019122719_06011577444761071.mp4";
    String videoFileName = "/storage/emulated/0/DCIM/Camera/VIDEO_2019122622_50551577371855425.mp4";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case StaticFinalValues.REQUEST_CODE_PICK_VIDEO:
                if (resultCode == RESULT_OK) {
                    ArrayList<VideoFile> list = data.getParcelableArrayListExtra(StaticFinalValues.RESULT_PICK_VIDEO);
                    for (VideoFile file : list) {
                        videoFileName = file.getPath();
                    }
                    Toast.makeText(this, "视频已选择成功\n" + videoFileName, Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public class MediaCodecUtil1 {
        private String mFilePath;
        private MediaCodec mMediaCodec;
        private MediaExtractor mMediaExtractor;
        private Surface mSurface;
        private boolean mIsAvailable;
        private ByteBuffer[] mInputBuffers;
        private ByteBuffer[] mOutputBuffers;

        public MediaCodecUtil1(String filePath, Surface surface) {
            mFilePath = filePath;
            mSurface = surface;
        }

        private void init() {
            mIsAvailable = false;
            mMediaExtractor = new MediaExtractor();
            try {
                mMediaExtractor.setDataSource(mFilePath);
                int trackCount = mMediaExtractor.getTrackCount();
                for (int i = 0; i < trackCount; i++) {
                    MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(i);
                    String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
                    if (mime.startsWith("video/")) {
                        mMediaExtractor.selectTrack(i);
                        mMediaCodec = MediaCodec.createDecoderByType(mime);
                        mMediaCodec.configure(mediaFormat, mSurface, null, 0);
                        mIsAvailable = true;
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void start() {
            init();
            if (mIsAvailable) {
                mMediaCodec.start();
                mInputBuffers = mMediaCodec.getInputBuffers();
                mOutputBuffers = mMediaCodec.getOutputBuffers();
                new Thread(new EncoderThread()).start();
            }
        }

        private class EncoderThread implements Runnable {
            @Override
            public void run() {
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo(); //每个缓冲区元数据包括指定相关编解码器(输出)缓冲区中有效数据范围的偏移量和大小。
                long startTime = System.currentTimeMillis();
                while (mIsAvailable) {
                    int inputBufferIndex = mMediaCodec.dequeueInputBuffer(-1);//获取输入队列中有效数据的索引
                    if (inputBufferIndex >= 0) {
                        ByteBuffer inputBuffer = mInputBuffers[inputBufferIndex];
                        inputBuffer.clear();
                        int sampleSize = mMediaExtractor.readSampleData(inputBuffer, 0); //检索当前已编码的示例并将其存储到字节缓冲区中，从给定的偏移量开始。
                        if (sampleSize > 0) {
                            mMediaExtractor.advance();
                            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, mMediaExtractor.getSampleTime(), 0);// 通知MediaDecode解码刚刚传入的数据
                        }
                    }

                    int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);//返回已成功解码的输出缓冲区的索引
                    if (outputBufferIndex >= 0) {
                        long sleepTime = bufferInfo.presentationTimeUs / 1000 - (System.currentTimeMillis() - startTime);
                        if (sleepTime > 0) {
                            SystemClock.sleep(sleepTime);
                        }
//                        ByteBuffer outBuffer = mOutputBuffers[outputBufferIndex];
                        mMediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                    }
                }
                mMediaExtractor.release();
                mMediaCodec.stop();
                mMediaCodec.release();
                Log.i("==", "播放完成");
            }
        }

        public void stop() {
            mIsAvailable = false;
        }

    }

    public void videoClipper(String videoFileName,final String outputPath){
        mStartTime = System.currentTimeMillis();
        VideoClipper clipper = new VideoClipper();
        clipper.setInputVideoPath(videoFileName);
        clipper.setFilterType(MagicFilterType.NONE);
        clipper.setOutputVideoPath(outputPath);
        clipper.setOnVideoCutFinishListener(new VideoClipper.OnVideoCutFinishListener() {
            @Override
            public void onFinish() {
                Log.e(TAG, "onFinish: 生成完成耗时"  +  ((System.currentTimeMillis() - mStartTime) / 1000));
                VideoPlayerActivity2.launch(MediaCodecShowOnGlSurfaceView.this,outputPath);
            }

            @Override
            public void onProgress(float percent) {
                Log.e(TAG, "onProgress: " +percent );
            }
        });
        try {
            final MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
            mediaMetadata.setDataSource(this, Uri.parse(videoFileName));
            int clipDur = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            Log.e(TAG, "onViewClicked: 时长 " + clipDur);
//                    int clipDur = 5032000;
            clipper.clipVideo(0, clipDur * 1000,new ArrayList<BaseImageView>(), getResources());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
