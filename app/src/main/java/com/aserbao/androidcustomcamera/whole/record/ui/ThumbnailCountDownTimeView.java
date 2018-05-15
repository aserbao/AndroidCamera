package com.aserbao.androidcustomcamera.whole.record.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.aserbao.androidcustomcamera.R;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */


public class ThumbnailCountDownTimeView  extends View {
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private RectF rectF;
    private int rectWidth,startWidth = 0;
    private Bitmap bitmap;
    private OnScrollBorderListener onScrollBorderListener;
    public int mDp2, mDp9;

    public ThumbnailCountDownTimeView(Context context) {
        super(context);
        init();
    }

    public ThumbnailCountDownTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThumbnailCountDownTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bigicon_adjust);
        mDp2 = (int)getResources().getDimension(R.dimen.dp2);
        mDp9 = (int)getResources().getDimension(R.dimen.dp9);
        rectWidth = bitmap.getWidth();
        mPaint.setStrokeWidth(mDp2);
    }
    public interface OnScrollBorderListener{
        void OnScrollBorder(float start, float end);
        void onScrollStateChange();
    }
    public float getRectLeft(){
        return (float)rectF.right/(float)mWidth;
    }
    public void setOnScrollBorderListener(OnScrollBorderListener listener){
        this.onScrollBorderListener = listener;
    }
    public void setMinWidth(int min){
        startWidth = min;
        /*rectF = new RectF();
        rectF.left = startWidth;
        rectF.top = 0;
        rectF.right = rectF.left + rectWidth;
        rectF.bottom = mHeight;*/
        invalidate();
    }
    public int getTotalWidth(){
        return mWidth;
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mWidth == 0) {
            mWidth = getWidth();
            mHeight = getHeight();
            rectF = new RectF();
            rectF.left = mWidth - rectWidth;
            rectF.top = 0;
            rectF.right = rectF.left + rectWidth;
            rectF.bottom = mHeight;
        }
    }

    private float downX;
    private boolean scrollLeft;
    private boolean scrollRight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        move(event);
        return scrollLeft || scrollRight;
    }

    boolean scrollChange;
    private boolean move(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                if (downX > rectF.left && downX < rectF.right) {
                    scrollLeft = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float scrollX = moveX - downX;
                if (scrollLeft) {
                    rectF.left = rectF.left + scrollX;
                    rectF.right = rectF.left + rectWidth;

                    if(rectF.left < startWidth){
                        rectF.left = startWidth;
                        rectF.right = rectF.left + rectWidth;
                    }
                    if(rectF.right > mWidth){
                        rectF.right = mWidth;
                        rectF.left = mWidth - rectWidth;
                    }


                    scrollChange = true;
                    invalidate();
                }
                if(onScrollBorderListener != null){
                    onScrollBorderListener.OnScrollBorder(rectF.left,rectF.right);
                }
                downX = moveX;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                downX = 0;
                scrollLeft = false;
                scrollRight = false;
                if(scrollChange && onScrollBorderListener != null){
                    onScrollBorderListener.onScrollStateChange();
                }
                scrollChange = false;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(Color.parseColor("#99FAE000"));
        mPaint.setStyle(Paint.Style.FILL);
        RectF rectF3 = new RectF();
        rectF3.left = 0 ;
        rectF3.top = 0 + mDp9;
//        rectF3.right = startWidth + bitmap.getWidth()/2;
        rectF3.right = startWidth ;
        rectF3.bottom = mHeight - mDp9;
        canvas.drawRect(rectF3, mPaint);


        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        Rect rect = new Rect();
        rect.left = (int) rectF.left;
        rect.top = (int) rectF.top;
        rect.right = (int) rectF.right;
        rect.bottom = (int) rectF.bottom;
        canvas.drawBitmap(bitmap,null,rect,mPaint);

    }
}
