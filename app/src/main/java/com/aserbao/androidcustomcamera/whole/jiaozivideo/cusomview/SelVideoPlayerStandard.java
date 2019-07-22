package com.aserbao.androidcustomcamera.whole.jiaozivideo.cusomview;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
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
public class SelVideoPlayerStandard extends JZVideoPlayerStandard {
    private boolean mIsExit = false;

    public SelVideoPlayerStandard(Context context) {
        super(context);
    }

    public SelVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void init(Context context) {
        super.init(context);
        initView();
        initPosition();
    }

    private void initView() {
        topContainer.setVisibility(GONE);
        bottomProgressBar.setVisibility(VISIBLE);
    }

    private void initPosition() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)bottomProgressBar.getLayoutParams();
        int i = DisplayUtil.dp2px(getContext(), -10);
        layoutParams.setMargins(0,0,0,i);
        bottomProgressBar.setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.fullscreen) {
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                //click quit fullscreen
            } else {
                //click goto fullscreen
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE){
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
//        return true;
        return super.onTouch(v,event);
    }

    @Override
    public void startVideo() {
        super.startVideo();
    }

    /**
     * onPrepared
     */
    @Override
    public void onVideoRendingStart() {
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

}
