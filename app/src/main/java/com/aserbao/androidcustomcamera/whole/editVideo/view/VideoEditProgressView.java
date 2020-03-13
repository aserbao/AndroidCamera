package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.DisplayUtil;
import com.aserbao.androidcustomcamera.whole.editVideo.beans.SelectViewBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : Administrator (Jacket)
 *     e-mail : 378315764@qq.com
 *     time   : 2018/01/30
 *     desc   :
 *     version: 3.2
 * </pre>
 */

public class VideoEditProgressView extends RelativeLayout {

    private String TAG = VideoEditProgressView.class.getSimpleName();

    /**
     * Content的宽
     */
    private int maxScrollWidth;

    private int minScrollWidth;

    private int screenWidth;

    private LinearLayout editBarLeft;

    private LinearLayout editBarRight;

    private ImageView ivEditBarLeft;

    private ImageView ivEditBarRight;

    private TextView tvStartTime;

    private TextView tvEndTime;

    private LinearLayout imageList;

    private int editBarLeftWidth;
    private int editBarLeftHeight;

    private int editBarRightWidth;
    private int editBarRightHeight;

    private RelativeLayout.LayoutParams editBarLeftParamsBar;
    private RelativeLayout.LayoutParams editBarRightParamsBar;
    private RelativeLayout.LayoutParams selectedParams;

    private Context context;

    private int videoEditProgressWidth;

    private LinearLayout selectdAreaView; //选中的区域

    private long totalTime = 15 * 1000;
    private long startTime = 0;
    private long endTime = 1;
    private long currentTime = 0;
    private float minSelectTimeWidth = 0;

    private LinearLayout.LayoutParams tvStartTimeParams;
    private LinearLayout.LayoutParams tvEndTimeParams;
    private List<SelectViewBean> selectViewBeans; //选中的时间集合

    public VideoEditProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        Resources resources = context.getResources();  //获取屏幕的宽度
        DisplayMetrics dm = resources.getDisplayMetrics();
        screenWidth = dm.widthPixels;

        selectViewBeans = new ArrayList<SelectViewBean>();

        minSelectTimeWidth = screenWidth / 8 + DisplayUtil.dipToPx( 10);

        imageList = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageList.setOrientation(LinearLayout.HORIZONTAL);
        imageList.setGravity(Gravity.CENTER_VERTICAL);
        addView(imageList, layoutParams);


        selectedParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        selectdAreaView = new LinearLayout(context);  //选中的背景
        selectdAreaView.setBackgroundColor(Color.parseColor("#3fff0000"));
        selectedParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        addView(selectdAreaView, selectedParams);


        editBarLeftParamsBar = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editBarLeft = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.edit_bar_layout, null); //添加左边编辑棒
        editBarLeftParamsBar.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        editBarLeftParamsBar.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        ivEditBarLeft = (ImageView) editBarLeft.findViewById(R.id.iv_edit_bar_left);
        addView(editBarLeft, editBarLeftParamsBar);
        tvStartTime = (TextView) editBarLeft.findViewById(R.id.tv_start_time);


        editBarRightParamsBar = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editBarRight = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.edit_bar_two_layout, null); //添加右边边编辑棒
        editBarRightParamsBar.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        ivEditBarRight = (ImageView) editBarRight.findViewById(R.id.iv_edit_bar_right);
        addView(editBarRight, editBarRightParamsBar);
        tvEndTime = (TextView) editBarRight.findViewById(R.id.tv_end_time);


        tvStartTimeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvEndTimeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        selectdAreaView.setVisibility(GONE);
        editBarLeft.setVisibility(GONE);
        editBarRight.setVisibility(GONE);

        tvStartTimeParams.leftMargin = DisplayUtil.dipToPx( 3);
        tvStartTime.setLayoutParams(tvStartTimeParams);


        editBarLeft.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //1.按下记录坐标
                        startLeftBarX = event.getX();
                        ivEditBarLeft.setImageResource(R.drawable.camera_select_selected);
                        Log.e(TAG, "getX(): " + getX());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //2.记录结束值
                        float endX = event.getX();
                        //3.计算偏移量
                        float distanceX = endX - startLeftBarX;
                        float toX = editBarLeft.getX() + distanceX;
                        if (toX < -DisplayUtil.dipToPx( 20)) {
                            toX = -DisplayUtil.dipToPx( 20);
                        }
                        if (toX > editBarRight.getX() - minSelectTimeWidth) {
                            toX = editBarRight.getX() - minSelectTimeWidth;
                        }
                        selectdAreaView.layout((int) editBarLeft.getX() + DisplayUtil.dipToPx( 20), DisplayUtil.dipToPx( 26), (int) editBarRight.getX() + DisplayUtil.dipToPx( 10), DisplayUtil.dipToPx( 85));
                        editBarLeft.setX(toX);
                        if (toX == -DisplayUtil.dipToPx( 20)) {
                            startTime = 0;
                        } else {
                            startTime = totalTime * selectdAreaView.getLeft() / getMeasuredWidth();
                        }
                        if (toX == videoEditProgressWidth - DisplayUtil.dipToPx( 17)) {
                            endTime = totalTime;
                        } else {
                            endTime = totalTime * selectdAreaView.getRight() / getMeasuredWidth();
                        }
                        Log.e(TAG, "startTime: " + startTime);
                        if (tvStartTime != null) {
                            tvStartTime.setText(startTime / 1000 + "s");
                            if (startTime == 0) {
                                tvStartTimeParams.leftMargin = DisplayUtil.dipToPx( 3);
                            } else {
                                tvStartTimeParams.leftMargin = 0;
                            }
                            tvStartTime.setLayoutParams(tvStartTimeParams);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        selectdAreaView.layout((int) editBarLeft.getX() + DisplayUtil.dipToPx( 20), DisplayUtil.dipToPx( 26), (int) editBarRight.getX() + DisplayUtil.dipToPx( 10), DisplayUtil.dipToPx( 85));
                        if (playStateListener != null) {
                            Log.e(TAG, "startTime:" + startTime + ",endTime:" + endTime);
                            playStateListener.selectTimeChange(startTime, endTime);
                        }
                        ivEditBarLeft.setImageResource(R.drawable.camera_select_normal);
                        break;
                }
                return true;
            }
        });


        editBarRight.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //1.按下记录坐标
                        startRightBarX = event.getX();
                        ivEditBarRight.setImageResource(R.drawable.camera_select_selected);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //2.记录结束值
                        float endX = event.getX();
                        //3.计算偏移量
                        float distanceX = endX - startRightBarX;
                        float toX = editBarRight.getX() + distanceX;
                        if (toX < editBarLeft.getX() + minSelectTimeWidth) {
                            toX = editBarLeft.getX() + minSelectTimeWidth;
                        }
                        if (toX > videoEditProgressWidth - DisplayUtil.dipToPx( 17)) {
                            toX = videoEditProgressWidth - DisplayUtil.dipToPx( 17);
                        }
                        selectdAreaView.layout((int) editBarLeft.getX() + DisplayUtil.dipToPx( 20), DisplayUtil.dipToPx( 26), (int) editBarRight.getX() + DisplayUtil.dipToPx( 10), DisplayUtil.dipToPx( 85));
                        editBarRight.setX(toX);
                        if (toX == -DisplayUtil.dipToPx( 20)) {
                            startTime = 0;
                        } else {
                            startTime = totalTime * selectdAreaView.getLeft() / getMeasuredWidth();
                        }
                        if (toX == videoEditProgressWidth - DisplayUtil.dipToPx( 17)) {
                            endTime = totalTime;
                        } else {
                            endTime = totalTime * selectdAreaView.getRight() / getMeasuredWidth();
                        }
                        Log.e(TAG, "getRight(): " + selectdAreaView.getRight());
                        Log.e(TAG, "getMeasuredWidth(): " + getMeasuredWidth());
                        Log.e(TAG, "endTime: " + endTime);
                        if (tvEndTime != null) {
                            tvEndTime.setText(endTime / 1000 + "s");
                            if (endTime == 15000) {
                                tvEndTimeParams.rightMargin = DisplayUtil.dipToPx( 6);
                            } else {
                                tvEndTimeParams.rightMargin = 0;
                            }
                            tvEndTime.setLayoutParams(tvEndTimeParams);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        ivEditBarRight.setImageResource(R.drawable.camera_select_normal);
                        selectdAreaView.layout((int) editBarLeft.getX() + DisplayUtil.dipToPx( 20), DisplayUtil.dipToPx( 26), (int) editBarRight.getX() + DisplayUtil.dipToPx( 10), DisplayUtil.dipToPx( 85));
                        if (playStateListener != null) {
                            Log.e(TAG, "startTime:" + startTime + ",endTime:" + endTime);
                            playStateListener.selectTimeChange(startTime, endTime);
                        }
                        break;
                }
                return true;
            }
        });
    }

    //添加视频处理关键帧图片
    public void addImageView(List<Bitmap> bitmaps) {
        if (imageList != null) {
//            int width = DisplayUtil.dipToPx( 45) * bitmaps.size();
//            minScrollWidth = screenWidth / 2 - width;
            int imageWidth = screenWidth / 8;
            ViewGroup.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageList.setLayoutParams(layoutParams);
            for (Bitmap bitmap : bitmaps) {
                ImageView imageView = new ImageView(context);
                LayoutParams params = new LayoutParams(imageWidth, DisplayUtil.dipToPx( 60));
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageBitmap(bitmap);
                imageList.addView(imageView);
            }
        }
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
        minScrollWidth = screenWidth / 2 - getMeasuredWidth(); //初始位置（屏幕中心）减去ViewEditProgressView的宽度
        maxScrollWidth = screenWidth / 2;
        editBarLeftWidth = editBarLeft.getMeasuredWidth();
        editBarLeftHeight = getMeasuredHeight();
        videoEditProgressWidth = getMeasuredWidth();
        editBarRightWidth = editBarRight.getMeasuredWidth();
        editBarRightHeight = getMeasuredHeight();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        editBarRight.layout(editBarLeft.getRight() + DisplayUtil.dipToPx( 4), 0, editBarLeft.getRight() + DisplayUtil.dipToPx( 4) + editBarRightWidth, editBarRightHeight);
//        editBarLeft.setX(0);
//        editBarLeft.setY(0);
//        editBarRight.setX(DisplayUtil.dipToPx(40));
//        editBarRight.setY(0);
        editBarLeft.layout(-DisplayUtil.dipToPx( 20), DisplayUtil.dipToPx( 8), editBarLeftWidth, editBarLeftHeight + DisplayUtil.dipToPx( 8));
        editBarRight.layout(editBarRight.getLeft(), DisplayUtil.dipToPx( 8), editBarRight.getRight(), editBarRightHeight + DisplayUtil.dipToPx( 8));
        selectdAreaView.layout((int) editBarLeft.getX() + DisplayUtil.dipToPx( 20), DisplayUtil.dipToPx( 26), (int) editBarRight.getX() + DisplayUtil.dipToPx( 10), DisplayUtil.dipToPx( 85));
    }

    private float startX;
    private float startLeftBarX;
    private float startRightBarX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //1.按下记录坐标
                startX = event.getX();
                playState = false;
                if (playStateListener != null) {
                    playStateListener.playStateChange(playState);
                }
                handler.removeCallbacksAndMessages(null);
                break;
            case MotionEvent.ACTION_MOVE:
                //2.记录结束值
                float endX = event.getX();
                //3.计算偏移量
                float distanceX = endX - startX;
                float toX = getX() + distanceX;
                if (toX < minScrollWidth) {
                    toX = minScrollWidth;
                }
                if (toX > maxScrollWidth) {
                    toX = maxScrollWidth;
                }
                setX(toX);
                currentTime = (long) (totalTime * (screenWidth / 2 - getX()) / getMeasuredWidth());
                Log.e(TAG, "currentTime: " + currentTime);
                if (playStateListener != null) {
                    playStateListener.videoProgressUpdate(currentTime, false);
                }
                break;
        }

        return true;
    }

    private Handler handler = new Handler(Looper.getMainLooper());
    private float toX;
    private boolean playState;
    private List<BaseImageView> baseImageViews;
    private List<View> selectedTimeView = new ArrayList<>();

    public void togglePlayVideo(final boolean playState, List<BaseImageView> baseImageViews) {
        this.playState = playState;
        this.baseImageViews = baseImageViews;
        if (playState) {
            selectdAreaView.setVisibility(GONE);
            editBarLeft.setVisibility(GONE);
            editBarRight.setVisibility(GONE);

            if (selectedTimeView != null && selectedTimeView.size() > 0) {
                for (View view : selectedTimeView) {
                    removeView(view);
                }
            }
            if (baseImageViews != null && baseImageViews.size() > 0) {
                selectedTimeView.clear();
                for (BaseImageView baseImageView : baseImageViews) {
                    LinearLayout selectdView; //选中的区域
                    long startX = baseImageView.getStartTime() * videoEditProgressWidth / totalTime;
                    long endX = baseImageView.getEndTime() * videoEditProgressWidth / totalTime;
                    Log.e(TAG, "startTime：" + baseImageView.getStartTime());
                    Log.e(TAG, "endTime：" + baseImageView.getEndTime());
                    Log.e(TAG, "1--------->startTime：" + baseImageView.getStartTime());
                    Log.e(TAG, "1--------->endTime：" + baseImageView.getEndTime());
                    Log.e(TAG, "1--------->totalTime：" + totalTime);
                    int width = (int) (endX - startX);
                    if (totalTime - baseImageView.getEndTime() <= 1000) {
                        Log.e(TAG, "尾部=======>");
                        width += DisplayUtil.dipToPx( 10);
                    } else {
                        width += DisplayUtil.dipToPx( 4);
                    }
                    RelativeLayout.LayoutParams selectedParams = new LayoutParams(width, DisplayUtil.dipToPx( 60));
                    selectdView = new LinearLayout(context);  //选中的背景
                    selectdView.setX(startX);
                    //startTime = totalTime * selectdAreaView.getLeft() / getMeasuredWidth();
                    selectdView.setBackgroundColor(Color.parseColor("#7f000000"));
                    selectedParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    addView(selectdView, selectedParams);
                    selectedTimeView.add(selectdView);
                }
            }

        }

        int perTotalTime = (int) (totalTime / 1000);
        Log.e(TAG, "perTotalTime:" + perTotalTime);
        if (perTotalTime != 0) {
//            int perX = (maxScrollWidth - minScrollWidth) / (20 * perTotalTime);
            final int perX;
            final long delayMillis;
            if(perTotalTime > 18){
                perX = screenWidth / (perTotalTime * 8);
                delayMillis = 108;
            }else if (perTotalTime > 16) {
                perX = screenWidth / (perTotalTime * 8);
                delayMillis = 125;
            } else {
                perX = screenWidth / 160;
                delayMillis = 100;
            }
            toX = getX() - 4 * perX;
            handler.removeCallbacksAndMessages(null);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (toX >= minScrollWidth && playState) {
                        setX(toX);
                        if (playStateListener != null) {
                            playStateListener.videoProgressUpdate(currentTime, true);
                        }
                        currentTime = (long) (totalTime * (screenWidth / 2 - getX()) / getMeasuredWidth());
                        toX -= perX;
                        if (toX < minScrollWidth) {
                            setX(maxScrollWidth);
                            if (playStateListener != null) {
                                playStateListener.videoProgressUpdate(0, false);
                                playStateListener.playStateChange(false);
                            }
                        }
                        handler.postDelayed(this, delayMillis);
                    }
                }
            });
        }

    }


    public void recoverView() {
        setX(maxScrollWidth);
        handler.removeCallbacksAndMessages(null);
        if (playStateListener != null) {
            playStateListener.videoProgressUpdate(0, false);
            playStateListener.playStateChange(false);
        }
    }


    public interface PlayStateListener {
        void playStateChange(boolean playState);

        void selectTimeChange(long startTime, long endTime);

        void videoProgressUpdate(long currentTime, boolean isPlay);
    }

    PlayStateListener playStateListener;

    public void setPlayStateListener(PlayStateListener playStateListener) {
        this.playStateListener = playStateListener;
    }


    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }


    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    //恢复View的初始化位置
    public void recoverView(List<BaseImageView> baseImageViews, BaseImageView baseImageView, boolean isEdit) {
//        editBarLeft.layout(0, 0, editBarLeftWidth, editBarLeftHeight);
        Log.e(TAG, "isEdit=" + isEdit);
        startTime = 0;
        endTime = 2;
        float leftX = screenWidth / 2 - getX() - DisplayUtil.dipToPx( 20);

        if (selectedTimeView != null && selectedTimeView.size() > 0) {
            for (View view : selectedTimeView) {
                removeView(view);
            }
        }
        if (baseImageViews != null && baseImageViews.size() > 0) {
            selectedTimeView.clear();
            for (BaseImageView baseImageView1 : baseImageViews) {
                if (baseImageView != null && baseImageView.getTimeStamp() == baseImageView1.getTimeStamp()) {
                    if (isEdit) {
                        Log.e(TAG, "11111111111111,endTime:" + baseImageView.getEndTime());
                        long startX = baseImageView.getStartTime() * videoEditProgressWidth / totalTime - DisplayUtil.dipToPx( 20);
                        long endX = baseImageView.getEndTime() * videoEditProgressWidth / totalTime - DisplayUtil.dipToPx( 10);
//                        float leftX = screenWidth / 2 - getX() - DisplayUtil.dipToPx( 20);
                        editBarLeft.setX(startX);

                        if (endX > videoEditProgressWidth - DisplayUtil.dipToPx( 17)) { //防止滑棒滑出界限
                            endX = videoEditProgressWidth - DisplayUtil.dipToPx( 17);
                        }
                        editBarRight.setX(endX);
                    } else {
                        Log.e(TAG, "666666");
                    }
                } else {
                    LinearLayout selectdView; //选中的区域
                    long startX = baseImageView1.getStartTime() * videoEditProgressWidth / totalTime;
                    long endX = baseImageView1.getEndTime() * videoEditProgressWidth / totalTime;
                    Log.e(TAG, "2--------->startTime：" + baseImageView1.getStartTime());
                    Log.e(TAG, "2--------->endTime：" + baseImageView1.getEndTime());
                    Log.e(TAG, "2--------->totalTime：" + totalTime);
                    int width = (int) (endX - startX);
                    if ((totalTime - baseImageView1.getEndTime()) <= 1000) {
                        width += DisplayUtil.dipToPx( 10);
                    } else {
                        width += DisplayUtil.dipToPx( 4);
                    }
                    RelativeLayout.LayoutParams selectedParams = new LayoutParams(width, DisplayUtil.dipToPx( 60));
                    selectdView = new LinearLayout(context);  //选中的背景
                    selectdView.setX(startX);
                    //startTime = totalTime * selectdAreaView.getLeft() / getMeasuredWidth();
                    selectdView.setBackgroundColor(Color.parseColor("#7f000000"));
                    selectedParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    addView(selectdView, selectedParams);
                    selectedTimeView.add(selectdView);
                }

            }
        }

        this.baseImageViews = baseImageViews;

        removeView(editBarLeft);   //清除左右编辑棒和选择区域，让其覆盖到布局最顶端
        removeView(editBarRight);
        removeView(selectdAreaView);

        addView(selectdAreaView, selectedParams);
        addView(editBarLeft, editBarLeftParamsBar);
        addView(editBarRight, editBarRightParamsBar);

        if (!isEdit) {
            Log.e(TAG, "222222222222222");
            editBarLeft.setX(leftX);
            minSelectTimeWidth = videoEditProgressWidth * 2000 / totalTime + DisplayUtil.dipToPx( 10);
            float rightX = (leftX + minSelectTimeWidth > (getMeasuredWidth() - DisplayUtil.dipToPx( 16))) ? (getMeasuredWidth() - DisplayUtil.dipToPx( 16)) : (leftX + minSelectTimeWidth);
//            float rightX = leftX + minSelectTimeWidth;
            Log.e(TAG, "rightX=" + rightX);
            Log.e(TAG, "width=" + getMeasuredWidth());
            editBarRight.setX(rightX);
//        selectdAreaView.layout((int) (screenWidth / 2 - getX()) +DisplayUtil.dipToPx( 20), DisplayUtil.dipToPx( 20), (int) editBarRight.getX() + DisplayUtil.dipToPx( 10), DisplayUtil.dipToPx( 82));
        }
        if (baseImageViews.size() == 0) {
            editBarLeft.setVisibility(GONE);
            editBarRight.setVisibility(GONE);
            selectdAreaView.setVisibility(GONE);
        } else {
            editBarLeft.setVisibility(VISIBLE);
            editBarRight.setVisibility(VISIBLE);
            selectdAreaView.setVisibility(VISIBLE);
        }

        if (baseImageViews.indexOf(baseImageView) == -1) {
            editBarLeft.setVisibility(GONE);
            editBarRight.setVisibility(GONE);
            selectdAreaView.setVisibility(GONE);
        }


    }


//    //选中状态View更改方法
//    public void selectAreaChange(BaseImageView baseImageView) {
//        Log.e(TAG,"666");
////        startTime = totalTime * selectdAreaView.getLeft() / getMeasuredWidth();
//        long leftX = baseImageView.getStartTime() * getMeasuredWidth() / totalTime;
//        editBarLeft.setX(leftX);
//        editBarRight.setX(leftX + DisplayUtil.dipToPx( 50));
//        selectdAreaView.layout((int) editBarLeft.getX() + DisplayUtil.dipToPx( 20), DisplayUtil.dipToPx( 26), (int) editBarRight.getX() + DisplayUtil.dipToPx( 10), DisplayUtil.dipToPx( 85));
//    }

//    public void getFormatTime(long time) {
//        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
//        //取整
//        String hms = formatter.format(time);
//        Log.e(TAG, "时间:" + hms);
////        //时
////        shiTv.setText(hms.substring(0,2));
////        //分
////        fenTv.setText(hms.substring(3,5));
////        //秒
////        miaoTv.setText(hms.substring(6,hms.length()));
//    }
}
