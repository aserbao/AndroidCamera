package com.aserbao.androidcustomcamera.whole.jiaozivideo.cusomview;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.DisplayUtil;
import com.aserbao.androidcustomcamera.whole.jiaozivideo.JZMediaManager;
import com.aserbao.androidcustomcamera.whole.jiaozivideo.JZVideoPlayerStandard;


/**
 * 这里可以监听到视频播放的生命周期和播放状态
 * 所有关于视频的逻辑都应该写在这里
 * Created by Nathen on 2017/7/2.
 */
public class MyJZVideoPlayerStandard extends JZVideoPlayerStandard {
    private boolean mIsExit = false;
    private IPlayFinish mIPlayFinish;

    public MyJZVideoPlayerStandard(Context context) {
        super(context);
    }

    public MyJZVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        initPosition();
    }
    public void setProgressBottom(){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)bottomProgressBar.getLayoutParams();
        layoutParams.setMargins(0,0,0,0);
        bottomProgressBar.setLayoutParams(layoutParams);
    }
    public void initPosition() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)bottomProgressBar.getLayoutParams();
        int i = DisplayUtil.dp2px(getContext(), 50);
        layoutParams.setMargins(0,0,0,i);
        bottomProgressBar.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.start) {
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //click quit fullscreen
            } else {
                //click goto fullscreen
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
        /*if(event.getAction() == MotionEvent.ACTION_DOWN){

        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
        }else if(event.getAction() == MotionEvent.ACTION_UP) {
            try {
                MediaPlayer mediaPlayer = JZMediaManager.instance().mediaPlayer;
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        changeUiToPlayingClear();
                        startButton.setImageResource(R.drawable.jz_click_play_selector);
//                        loadingProgressBar.setVisibility(INVISIBLE);
                    } else {
                        mediaPlayer.start();
                        changeUiToPlayingShow();
                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return super.onTouch(v,event);*/
    }
    public void click(){
        try {
            MediaPlayer mediaPlayer = JZMediaManager.instance().mediaPlayer;
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
//                    changeUiToPlayingClear();
                    startButton.setImageResource(R.drawable.jz_click_play_selector);
                    startButton.setVisibility(VISIBLE);
//                        loadingProgressBar.setVisibility(INVISIBLE);
                } else {
                    startButton.setVisibility(INVISIBLE);
                    mediaPlayer.start();
//                    changeUiToPlayingShow();
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            startVideo();
        }
    }

    public void startVideo(boolean isLoop,IPlayFinish playFinish){
        setNeedLoop(isLoop);
        mIPlayFinish = playFinish;
        super.startVideo();
    }

    @Override
    public void startVideo() {
        setNeedLoop(true);
        super.startVideo();
        mIPlayFinish = null;
    }

    /**
     * onPrepared
     */
    @Override
    public void onVideoRendingStart() {
        try {
            if(isCloseVoice){
                MediaPlayer mediaPlayer = JZMediaManager.instance().mediaPlayer;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0,0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(mIsExit){
            JZVideoPlayerStandard.releaseAllVideos();
        }
        super.onVideoRendingStart();
    }
    public void setOnPrepared(boolean isExit){
        mIsExit = isExit;
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
    }



    @Override
    public void onStateError() {
        super.onStateError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        if (mIPlayFinish != null) {
            mIPlayFinish.playfinish();
        }
    }

    @Override
    public void onInfo(int what, int extra) {
        super.onInfo(what, extra);
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }

    @Override
    public void startWindowFullscreen() {
        super.startWindowFullscreen();
    }

    @Override
    public void startWindowTiny() {
        super.startWindowTiny();
    }

    // TODO: 2017/10/27 手动点暂停 
    @Override
    public void handlerOnPause() {
        if (mHandlerClickVideoPauseListener != null) {
            mHandlerClickVideoPauseListener.handlerPause();
        }
    }
    @Override
    public void handlerOnStart() {
        if (mHandlerClickVideoPauseListener != null) {
            mHandlerClickVideoPauseListener.handlerStart();
        }
    }

    private HandlerClickVideoPauseListener mHandlerClickVideoPauseListener;
    public void setHandlerClickVideoPauseListener(HandlerClickVideoPauseListener mHandlerClickVideoPauseListener){
        this.mHandlerClickVideoPauseListener = mHandlerClickVideoPauseListener;
    }
    public interface HandlerClickVideoPauseListener{
        void handlerPause();
        void handlerStart();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean isCloseVoice = false;
    public void clostVoice(){
        isCloseVoice = true;
    }
//    AudioManager audioManager=(AudioManager)getSystemService(Service.AUDIO_SERVICE);
    public void OpenVolume(AudioManager audioManager){
        isCloseVoice = false;
        MediaPlayer mediaPlayer = JZMediaManager.instance().mediaPlayer;
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
        mediaPlayer.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM), audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        mediaPlayer.start();
    }

    public interface IPlayFinish{
        void playfinish();
    }
}
