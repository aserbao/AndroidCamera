package com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;


import com.aserbao.androidcustomcamera.whole.record.ui.SlideGpuFilterGroup;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoPreviewView extends GLSurfaceView implements GLSurfaceView.Renderer, MediaPlayerWrapper.IMediaCallback {
    private MediaPlayerWrapper mMediaPlayer;
    private VideoDrawer mDrawer;

    /**视频播放状态的回调*/
    private MediaPlayerWrapper.IMediaCallback callback;

    public VideoPreviewView(Context context) {
        super(context,null);
    }

    public VideoPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public void setRotate(int angle){
        mDrawer.setRotation(angle);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        setPreserveEGLContextOnPause(false);
        setCameraDistance(100);
        mDrawer = new VideoDrawer(context,getResources());

        //初始化Drawer和VideoPlayer
        mMediaPlayer = new MediaPlayerWrapper();
        mMediaPlayer.setOnCompletionListener(this);
    }

    /**设置视频的播放地址*/
    public void setVideoPath(List<String> paths){
        mMediaPlayer.setDataSource(paths);
    }

    public void setFilter(int i){
        mDrawer.setFilter(i);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        onDestroy();
        super.surfaceDestroyed(holder);
        Log.e("Atest", "surfaceDestroyed: ");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mDrawer.onSurfaceCreated(gl,config);
        SurfaceTexture surfaceTexture = mDrawer.getSurfaceTexture();
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        Surface surface = new Surface(surfaceTexture);
        mMediaPlayer.setSurface(surface);
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDrawer.onSurfaceChanged(gl,width,height);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        Log.e("a", "onDrawFrame: " );
        mDrawer.onDrawFrame(gl);
    }
    public void onDestroy(){
        if (mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }
    }
    public void onTouch(final MotionEvent event){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mDrawer.onTouch(event);
            }
        });
    }
    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener){
        mDrawer.setOnFilterChangeListener(listener);
    }

    @Override
    public void onVideoPrepare() {
        if (callback!= null){
            callback.onVideoPrepare();
        }
    }

    @Override
    public void onVideoStart() {
        if(callback!=null){
            callback.onVideoStart();
        }
    }

    @Override
    public void onVideoPause() {
        if (callback != null){
            callback.onVideoPause();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (callback != null){
            callback.onCompletion(mp);
        }
    }

    @Override
    public void onVideoChanged(final VideoInfo info) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mDrawer.onVideoChanged(info);
            }
        });
        if(callback!=null){
            callback.onVideoChanged(info);
        }
    }
    /**
     * isPlaying now
     * */
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }
    /**
     * pause play
     * */
    public void pause(){
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }
    /**
     * start play video
     * */
    public void start(){
        mMediaPlayer.start();
    }
    /**
     * 跳转到指定的时间点，只能跳到关键帧
     * */
    public void seekTo(int time){
        requestRender();
        mMediaPlayer.seekTo(time);
    }
    /**
     * 获取当前视频的长度
     * */
    public int getVideoDuration(){
        return mMediaPlayer.getCurVideoDuration();
    }

    /**
     * 切换美颜状态
     * */
    public void switchBeauty(){
        mDrawer.switchBeauty();
    }


    public void setIMediaCallback(MediaPlayerWrapper.IMediaCallback callback){
        this.callback=callback;
    }
}
