package com.aserbao.androidcustomcamera.whole.jiaozivideo;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;


import com.danikula.videocache.HttpProxyCacheServer;

import java.lang.reflect.Method;
import java.util.Map;

import static com.aserbao.androidcustomcamera.base.MyApplication.getProxy;


/**
 * <p>统一管理MediaPlayer的地方,只有一个mediaPlayer实例，那么不会有多个视频同时播放，也节省资源。</p>
 * <p>Unified management MediaPlayer place, there is only one MediaPlayer instance, then there will be no more video broadcast at the same time, also save resources.</p>
 * Created by Nathen
 * On 2015/11/30 15:39
 */
public class JZMediaManager implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {
    public static final int HANDLER_PREPARE = 0;
    public static final int HANDLER_RELEASE = 2;
    public static final String TAG = "JiaoZiVideoPlayer";
    public static JZResizeTextureView textureView;
    public static SurfaceTexture savedSurfaceTexture;
    public static Surface surface;
    public static String CURRENT_PLAYING_URL;
    public static boolean CURRENT_PLING_LOOP;
    public int music_type = AudioManager.STREAM_MUSIC;
    public static Map<String, String> MAP_HEADER_DATA;
    private static JZMediaManager JZMediaManager;
    public MediaPlayer mediaPlayer = new MediaPlayer();
    public int currentVideoWidth = 0;
    public int currentVideoHeight = 0;
    HandlerThread mMediaHandlerThread;
    MediaHandler mMediaHandler;
    Handler mainThreadHandler;
    public int positionInList = -1;

    public JZMediaManager() {
        mMediaHandlerThread = new HandlerThread(TAG);
        mMediaHandlerThread.start();
        mMediaHandler = new MediaHandler((mMediaHandlerThread.getLooper()));
        mainThreadHandler = new Handler();
    }

    public static JZMediaManager instance() {
        if (JZMediaManager == null) {
            JZMediaManager = new JZMediaManager();
        }
        return JZMediaManager;
    }
    public void setMusic_type(int music){
        music_type = music;
    }
    public Point getVideoSize() {
        if (currentVideoWidth != 0 && currentVideoHeight != 0) {
            return new Point(currentVideoWidth, currentVideoHeight);
        } else {
            return null;
        }
    }

    public void prepare() {
        releaseMediaPlayer();
        Message msg = new Message();
        msg.what = HANDLER_PREPARE;
        mMediaHandler.sendMessage(msg);
    }

    public void releaseMediaPlayer() {
        Message msg = new Message();
        msg.what = HANDLER_RELEASE;
        mMediaHandler.sendMessage(msg);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.i(TAG, "onSurfaceTextureAvailable [" + JZVideoPlayerManager.getCurrentJzvd().hashCode() + "] ");
        if (savedSurfaceTexture == null) {
            savedSurfaceTexture = surfaceTexture;
            prepare();
        } else {
            textureView.setSurfaceTexture(savedSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        // 如果SurfaceTexture还没有更新Image，则记录SizeChanged事件，否则忽略
        Log.i(TAG, "onSurfaceTextureSizeChanged [" + JZVideoPlayerManager.getCurrentJzvd().hashCode() + "] ");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return savedSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }
    private boolean isNeedVoice = false;
    public void setIsNeedVoice(boolean isNeedV){
        isNeedVoice = isNeedV;
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        if(isNeedVoice){
            mp.setVolume(0,0);
        }
        mediaPlayer.start();
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onPrepared();
                }
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onAutoCompletion();
                }
            }
        });
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, final int percent) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().setBufferProgress(percent);
                }
            }
        });
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onSeekComplete();
                }
            }
        });
    }

    @Override
    public boolean onError(MediaPlayer mp, final int what, final int extra) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, final int what, final int extra) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onInfo(what, extra);
                }
            }
        });
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        currentVideoWidth = width;
        currentVideoHeight = height;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onVideoSizeChanged();
                }
            }
        });
    }

    public class MediaHandler extends Handler {
        public MediaHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_PREPARE:
                    try {
                        currentVideoWidth = 0;
                        currentVideoHeight = 0;
                        mediaPlayer.release();
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(music_type);
                        mediaPlayer.setLooping(CURRENT_PLING_LOOP);
                        mediaPlayer.setOnPreparedListener(JZMediaManager.this);
                        mediaPlayer.setOnCompletionListener(JZMediaManager.this);
                        mediaPlayer.setOnBufferingUpdateListener(JZMediaManager.this);
                        mediaPlayer.setScreenOnWhilePlaying(true);
                        mediaPlayer.setOnSeekCompleteListener(JZMediaManager.this);
                        mediaPlayer.setOnErrorListener(JZMediaManager.this);
                        mediaPlayer.setOnInfoListener(JZMediaManager.this);
                        mediaPlayer.setOnVideoSizeChangedListener(JZMediaManager.this);
                        Class<MediaPlayer> clazz = MediaPlayer.class;
                        Method method = clazz.getDeclaredMethod("setDataSource", String.class, Map.class);
                        if(CURRENT_PLAYING_URL.startsWith("http")) {
                            HttpProxyCacheServer proxy = getProxy();
                            String proxyUrl = proxy.getProxyUrl(CURRENT_PLAYING_URL);
                            method.invoke(mediaPlayer, proxyUrl, MAP_HEADER_DATA);
                        }else {
                            method.invoke(mediaPlayer, CURRENT_PLAYING_URL, MAP_HEADER_DATA);
                        }
                        mediaPlayer.prepareAsync();
                        if (surface != null) {
                            surface.release();
                        }
                        surface = new Surface(savedSurfaceTexture);
                        mediaPlayer.setSurface(surface);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDLER_RELEASE:
//                    CURRENT_PLAYING_URL = null;
//                    CURRENT_PLING_LOOP = false;
//                    MAP_HEADER_DATA = null;
                    mediaPlayer.release();
                    break;
            }
        }
    }

}
