package com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.Surface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/29 0029.
 * desc：MediaPlayer的代理类 支持循环播放多个视频
 */

public class MediaPlayerWrapper implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private MediaPlayer mCurMediaPlayer;    //current player
    private List<MediaPlayer> mPlayerList;  //player list
    private List<String> mSrcList;          //video src list
    private List<VideoInfo> mInfoList;      //video info list
    private Surface surface;
    private IMediaCallback mCallback;
    private int curIndex;                   //current player index

    public MediaPlayerWrapper() {
        mPlayerList = new ArrayList<>();
        mInfoList = new ArrayList<>();
    }

    public void setOnCompletionListener(IMediaCallback callback) {
        this.mCallback = callback;
    }

    /**
     * get video info and store
     *
     * @param dataSource
     */
    public void setDataSource(List<String> dataSource) {
        this.mSrcList = dataSource;
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        for (int i = 0; i < dataSource.size(); i++) {
            VideoInfo info = new VideoInfo();
            String path=dataSource.get(i);
            retr.setDataSource(path);
            String rotation = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            info.path=path;
            try {
                info.rotation = Integer.parseInt(rotation);
                info.width = Integer.parseInt(width);
                info.height = Integer.parseInt(height);
                info.duration = Integer.parseInt(duration);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }finally {
                mInfoList.add(info);
            }
        }
    }
    public List<VideoInfo> getVideoInfo(){
        return mInfoList;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public void prepare() throws IOException {
        for (int i = 0; i < mSrcList.size(); i++) {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.setDataSource(mSrcList.get(i));
            player.prepare();
            mPlayerList.add(player);
            if (i == 0) {
                mCurMediaPlayer = player;
                if (mCallback != null) {
                    mCallback.onVideoChanged(mInfoList.get(0));
                }
            }
        }
        if (mCallback != null) {
            mCallback.onVideoPrepare();
        }
    }

    public void start() {
        mCurMediaPlayer.setSurface(surface);
        mCurMediaPlayer.start();
        if (mCallback != null) {
            mCallback.onVideoStart();
        }
    }

    public void pause() {
        if(mCurMediaPlayer != null) {
            mCurMediaPlayer.pause();
            if (mCallback != null) {
                mCallback.onVideoPause();
            }
        }
    }
    public int getCurVideoDuration(){
        return mInfoList.get(curIndex).duration;
    }

    public int getVideoDuration() {
        if (mSrcList.size() == 0) {
            throw new IllegalStateException("please set video src first");
        }
        int duration = 0;
        for (int i = 0; i < mSrcList.size(); i++) {
            duration += mInfoList.get(i).duration;
        }
        return duration;
    }

    public int getCurPosition() {
        int position = 0;
        for (int i = 0; i < curIndex; i++) {
            position += mInfoList.get(i).duration;
        }
        position += mCurMediaPlayer.getCurrentPosition();
        return position;
    }

    public void seekTo(int time) {
        int duration = 0;
        mCurMediaPlayer.seekTo(time);
        /*mCurMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mCurMediaPlayer.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCurMediaPlayer.pause();
                    }
                },1000);
            }
        });*/
    }

    public boolean isPlaying() {
        if (mCurMediaPlayer != null) {
            return mCurMediaPlayer.isPlaying();
        }else{
            return  false;
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        curIndex++;
        if (curIndex >= mSrcList.size()) {
            curIndex = 0;
            if (mCallback != null) {
                mCallback.onCompletion(mp);
            }
        }
//        switchPlayer(mp);

    }

    private void switchPlayer(MediaPlayer mp) {
        mp.setSurface(null);
        if (mCallback != null) {
            mCallback.onVideoChanged(mInfoList.get(curIndex));
        }
        mCurMediaPlayer = mPlayerList.get(curIndex);
        mCurMediaPlayer.setSurface(surface);
        mCurMediaPlayer.start();
    }

    public void stop() {
        mCurMediaPlayer.stop();
    }

    public void release() {
        for (int i = 0; i < mPlayerList.size(); i++) {
            mPlayerList.get(i).release();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    public void setVolume(float volume) {
        for(int i=0;i<mPlayerList.size();i++){
            MediaPlayer mediaPlayer = mPlayerList.get(i);
            mediaPlayer.setVolume(volume,volume);
        }
    }
    public int[] getVideoWH(){
        int[] ints = new int[2];
        int videoWidth = mCurMediaPlayer.getVideoWidth();
        int videoHeight = mCurMediaPlayer.getVideoHeight();
        ints[0] = videoWidth;
        ints[1] = videoHeight;
        return ints;
    }
    public interface IMediaCallback {
        /**
         * callback when all the player prepared
         */
        void onVideoPrepare();

        /**
         * callback when player start
         */
        void onVideoStart();

        /**
         * callback when player pause
         */
        void onVideoPause();

        /**
         * callback when all the videos have been played
         *
         * @param mp
         */
        void onCompletion(MediaPlayer mp);

        /**
         * callback when video changed
         *
         * @param info
         */
        void onVideoChanged(VideoInfo info);
    }
}
