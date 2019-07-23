package com.aserbao.androidcustomcamera.whole.record.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;

import java.lang.ref.WeakReference;


/**
 * description:
 * Created by aserbao on 2018/7/24.
 */


public class CustomRecordImageView extends View {

    private static final String TAG = "CustomRecordImageView";

    public static final int START = 1;
    public static final int STOP = 2;
    public static final int PROCESS = 3;
    public Paint mPaint;
    private int radiu = 0;
    private boolean isAdd = true;
    private int isChangeNum = 0;
    private int changeTime = 10;//中间改变需要几次
    private int mStartHWidth = 150;
    private int mStopHWidth = 100;
    private int mStartPaintWidth = 10;
    private int mStopPaintWidth = 15;
    private int mStopIntervalWidth = 10;
    private int mCorner = 10;//圆角半径
    private boolean isRecording = false;//是否正在录制中
    private int cuurStatus = 1 ;//0表示正在录制，1表示暂停录制，2表示暂停到开始中间过程，3表示录制到暂停的中间过程

    public CustomRecordImageView(Context context) {
        this(context,null);
    }

    public CustomRecordImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomRecordImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.parseColor("#fc4253"));
        mPaint.setAntiAlias(true);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (cuurStatus){
            case 0:
                if (isAdd) {
                    if (radiu < 15) {
                        radiu++;
                    } else {
                        isAdd = false;
                    }
                } else {
                    if (radiu > 0) {
                        radiu--;
                    } else {
                        isAdd = true;
                    }
                }
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(radiu + mStartPaintWidth);
                RectF rectF = new RectF(getWidth() / 2 - mStartHWidth, getHeight() / 2 - mStartHWidth, getWidth() / 2 + mStartHWidth, getHeight() / 2 + mStartHWidth);
                canvas.drawArc(rectF, 0, 360, false, mPaint);
                int hald = mStartHWidth / 3;
                RectF rectF1 = new RectF(getWidth() / 2 - hald, getHeight() / 2 - hald, getWidth() / 2 + hald, getHeight() / 2 + hald);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(rectF1, mCorner + radiu, mCorner + radiu , mPaint);
                invalidate();
                break;
            case 1:
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(mStopPaintWidth);
                RectF rectF2 = new RectF(getWidth() / 2 - mStopHWidth, getHeight() / 2 - mStopHWidth, getWidth() / 2 + mStopHWidth, getHeight() / 2 + mStopHWidth);
                canvas.drawArc(rectF2, 0, 360, false, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(getWidth() / 2,getHeight() / 2, mStopHWidth - mStopPaintWidth - mStopIntervalWidth , mPaint);
                break;
            case 2:
                isChangeNum ++ ;
                if(isChangeNum <= changeTime){
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setStrokeWidth(mStopPaintWidth);
                    if(isRecording) {
                        int hWidth = (mStartHWidth - mStopHWidth) / changeTime * isChangeNum + mStopHWidth;
                        RectF rectF3 = new RectF(getWidth() / 2 - hWidth, getHeight() / 2 - hWidth, getWidth() / 2 + hWidth, getHeight() / 2 + hWidth);
                        canvas.drawArc(rectF3, 0, 360, false, mPaint);
                        int hald2 = mStartHWidth / 3;
                        mPaint.setStyle(Paint.Style.FILL);
                        int middle = changeTime / 2;
                        if (isChangeNum > middle) {
                            RectF rectF22 = new RectF(getWidth() / 2 - hald2, getHeight() / 2 - hald2, getWidth() / 2 + hald2, getHeight() / 2 + hald2);
                            int cuurCorner = 50 - (50 - mCorner) / middle * (isChangeNum - middle);
                            canvas.drawRoundRect(rectF22, cuurCorner, cuurCorner, mPaint);
                        }else{
                            int radius1 = mStopHWidth - mStopPaintWidth - mStopIntervalWidth;
                            int radius = radius1 - (radius1 - hald2) / middle * isChangeNum;
                            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);
                        }
                    }else{
                        int hWidth =mStartHWidth -  (mStartHWidth - mStopHWidth) / changeTime * isChangeNum ;
                        RectF rectF3 = new RectF(getWidth() / 2 - hWidth, getHeight() / 2 - hWidth, getWidth() / 2 + hWidth, getHeight() / 2 + hWidth);
                        canvas.drawArc(rectF3, 0, 360, false, mPaint);
                        mPaint.setStyle(Paint.Style.FILL);
                        int hald2 = mStartHWidth / 3;
                        int middle = changeTime / 2;
                        if (isChangeNum < middle) {
                            RectF rectF22 = new RectF(getWidth() / 2 - hald2, getHeight() / 2 - hald2, getWidth() / 2 + hald2, getHeight() / 2 + hald2);
                            int cuurCorner = mCorner + (50 - mCorner) / middle * isChangeNum ;
                            canvas.drawRoundRect(rectF22, cuurCorner, cuurCorner, mPaint);
                        }else{
                            int radius1 = mStopHWidth - mStopPaintWidth - mStopIntervalWidth;
                            int radius = hald2 + (radius1 - hald2) / middle * (isChangeNum - middle);
                            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);
                        }
                    }
                }else{
                    isChangeNum = 0;
                    if(isRecording){
                        cuurStatus = 0;
                    }else{
                        cuurStatus = 1;
                    }
                }
                invalidate();
                Log.e(TAG, "onDraw: " );
                break;
        }
    }

    public boolean getRecordStatus(){
        return isRecording;
    }
    public void startRecord(){
        isRecording = true;
        cuurStatus = 2;
        invalidate();
    }
    public void stopRecord(){
        isRecording = false;
        cuurStatus = 2;
        invalidate();
    }

    private MyHandler mMyHandler = new MyHandler(new WeakReference<CustomRecordImageView>(this));

    public class MyHandler extends Handler {
        private WeakReference<CustomRecordImageView> mCustomRecordImageViewWeakReference;

        public MyHandler(WeakReference<CustomRecordImageView> customRecordImageViewWeakReference) {
            mCustomRecordImageViewWeakReference = customRecordImageViewWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CustomRecordImageView customRecordImageView = mCustomRecordImageViewWeakReference.get();
            if (customRecordImageView != null) {
                switch (msg.what){
                    case StaticFinalValues.EMPTY:
                        while(true) {
                            customRecordImageView.invalidate();
                        }
                    case STOP:
                        break;
                    case START:
                        break;
                }
            }
        }
    }

}
