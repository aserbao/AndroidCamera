package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Administrator (Jacket)
 *     e-mail : 378315764@qq.com
 *     time   : 2018/01/31
 *     desc   :
 *     version: 3.2
 * </pre>
 */

public class VideoEditView extends RelativeLayout implements VideoEditProgressView.PlayStateListener {

    private String TAG = VideoEditView.class.getSimpleName();

    private Context context;

    private VideoEditProgressView videoEditProgressView;
    private LinearLayout llPlayVideoView;
    private ImageView ivCenter;
    private int viewWidth;
    private int viewHeight;
    private int screenWidth;
    private boolean isVideoPlaying = false;//视频是否处于播放状态
    private ImageView bigiconPlay;
    private RelativeLayout rlCurrentLayout;
    private TextView tvTotalTime;
    private TextView tvCurrentTime;
    //存储贴纸列表
    private ArrayList<View> mViews = new ArrayList<>();

    public VideoEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context, attrs);
    }

    //初始化控件
    private void initView(Context context, AttributeSet attrs) {
        Resources resources = context.getResources();  //获取屏幕的宽度
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;

        rlCurrentLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.rl_current_layout, null);
        RelativeLayout.LayoutParams rlCurrentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(rlCurrentLayout, rlCurrentParams);

        tvTotalTime = (TextView) rlCurrentLayout.findViewById(R.id.tv_totalTime);
        tvCurrentTime = (TextView) rlCurrentLayout.findViewById(R.id.tv_currentTime);

        videoEditProgressView = new VideoEditProgressView(context, attrs);  //添加ViewEditProgressView
        RelativeLayout.LayoutParams videoEditParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ViewGroup.LayoutParams params = new LayoutParams(200, ViewGroup.LayoutParams.MATCH_PARENT);
        videoEditProgressView.setLayoutParams(params);
        videoEditProgressView.setPlayStateListener(this);
        videoEditParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        addView(videoEditProgressView, videoEditParams);

        llPlayVideoView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.ll_play_video_view, null);  //添加llPlayVideoView
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(DisplayUtil.dipToPx(context, 60), DisplayUtil.dipToPx(context, 60));
        rlParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        addView(llPlayVideoView, rlParams);

        ivCenter = new ImageView(context);            //添加ivCenter
        ivCenter.setImageResource(R.drawable.bigicon_center);
//        RelativeLayout.LayoutParams ivRarams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DisplayUtil.dipToPx(context, 60));
        RelativeLayout.LayoutParams ivRarams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ivRarams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        addView(ivCenter, ivRarams);

        bigiconPlay = (ImageView) findViewById(R.id.bigicon_play);
    }


    /**
     * 当布局文件加载完成的时候回调这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 在测量方法里，得到各个控件的高和宽
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = videoEditProgressView.getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //指定菜单的位置
        videoEditProgressView.layout(screenWidth / 2, 0, screenWidth / 2 + viewWidth, viewHeight);
    }


    public void addImageView(List<Bitmap> bitmaps) {
//        int width = DisplayUtil.dipToPx(context, 45) * bitmaps.size();
        if (bitmaps != null) {
            int width = screenWidth * bitmaps.size() / 8;
            ViewGroup.LayoutParams layoutParams = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
            videoEditProgressView.setLayoutParams(layoutParams);
            videoEditProgressView.addImageView(bitmaps);
        }
    }

    ArrayList<BaseImageView> baseImageViews;

    public void videoPlay(ArrayList<BaseImageView> baseImageViews) {
        this.baseImageViews = baseImageViews;
        if (isVideoPlaying) {
            isVideoPlaying = false;
            bigiconPlay.setImageResource(R.drawable.camera_play);
        } else {
            isVideoPlaying = true;
            bigiconPlay.setImageResource(R.drawable.bigicon_timeout_small);
        }
        if (onSelectTimeChangeListener != null) {
            onSelectTimeChangeListener.playChange(isVideoPlaying);
        }
        videoEditProgressView.togglePlayVideo(isVideoPlaying, baseImageViews);

    }


    @Override
    public void playStateChange(boolean playState) {
        isVideoPlaying = playState;
        if (isVideoPlaying) {
            bigiconPlay.setImageResource(R.drawable.bigicon_timeout_small);
        } else {
            bigiconPlay.setImageResource(R.drawable.camera_play);
            if (onSelectTimeChangeListener != null) {
                onSelectTimeChangeListener.playChange(false);
            }
        }
    }

    public void setTotalTime(int totalTime) {
        if (tvTotalTime != null) {
            tvTotalTime.setText(totalTime / 1000 + "s");
        }
        if (videoEditProgressView != null) {
            videoEditProgressView.setTotalTime(totalTime);
        }
    }



    public interface OnSelectTimeChangeListener {
        void selectTimeChange(long startTime, long endTime);

        void playChange(boolean isPlayVideo);

        void videoProgressUpdate(long currentTime, boolean isVideoPlaying);
    }

    public OnSelectTimeChangeListener onSelectTimeChangeListener;

    public void setOnSelectTimeChangeListener(OnSelectTimeChangeListener onSelectTimeChangeListener) {
        this.onSelectTimeChangeListener = onSelectTimeChangeListener;
    }

    //开始时间和结束时间回调
    @Override
    public void selectTimeChange(long startTime, long endTime) {
        if (onSelectTimeChangeListener != null) {
            onSelectTimeChangeListener.selectTimeChange(startTime, endTime);
        }
    }


    public void recoverView(ArrayList<BaseImageView> baseImageViews, BaseImageView baseImageView, boolean isEdit) {
        if (videoEditProgressView != null) {
            videoEditProgressView.recoverView(baseImageViews,baseImageView,isEdit);
        }
    }

    @Override
    public void videoProgressUpdate(long currentTime, boolean isVideoPlaying) {
        if (tvCurrentTime != null) {
            Log.e(TAG, "进度更新" );
            tvCurrentTime.setText(currentTime/1000+"s");
        }
        if (onSelectTimeChangeListener != null) {
            onSelectTimeChangeListener.videoProgressUpdate(currentTime, isVideoPlaying);
        }
    }

    public void recoverView(){
        bigiconPlay.setImageResource(R.drawable.camera_play);
        if (onSelectTimeChangeListener != null) {
            onSelectTimeChangeListener.playChange(false);
        }
        if (videoEditProgressView != null) {
            videoEditProgressView.recoverView();
        }
    }

//    public void selectAreaChange(BaseImageView baseImageView){
//        if (videoEditProgressView != null) {
//            videoEditProgressView.selectAreaChange(baseImageView);
//        }
//    }
}
