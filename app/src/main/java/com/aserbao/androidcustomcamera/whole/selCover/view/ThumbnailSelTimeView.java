package com.aserbao.androidcustomcamera.whole.selCover.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.aserbao.androidcustomcamera.R;

/**
 * Created by zhaoshuang on 17/8/22.
 */

public class ThumbnailSelTimeView extends View {

    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private RectF rectF;
    private int rectWidth = 100;
    private OnScrollBorderListener onScrollBorderListener;
    public int mDp2;

    public ThumbnailSelTimeView(Context context) {
        this(context,null);
    }

    public ThumbnailSelTimeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ThumbnailSelTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mDp2 = (int)getResources().getDimension(R.dimen.dp2);
        rectWidth = (int)getResources().getDimension(R.dimen.dp48);
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
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mWidth == 0) {
            mWidth = getWidth();
            mHeight = getHeight();

            rectF = new RectF();
            rectF.left = 0;
            rectF.top = 0;
            rectF.right = rectWidth;
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

                    if(rectF.left < 0){
                        rectF.left = 0;
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

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rectF.left,rectF.top,rectF.left + rectWidth,rectF.bottom,mPaint);
        mPaint.setColor(Color.parseColor("#99313133"));
        mPaint.setStyle(Paint.Style.FILL);
        RectF rectF3 = new RectF();
        rectF3.left = 0;
        rectF3.top = 0;
        rectF3.right = rectF.left - mDp2;
        rectF3.bottom = mHeight;
        canvas.drawRect(rectF3, mPaint);

        RectF rectF4 = new RectF();
        rectF4.left = rectF.right + mDp2;
        rectF4.top = 0;
        rectF4.right = mWidth;
        rectF4.bottom = mHeight;
        canvas.drawRect(rectF4, mPaint);
    }
}