package com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit;

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
 * Created by zhaoshuang on 17/8/22.
 */

public class ThumbnailView extends View {

    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private RectF rectF;
    private RectF rectF2;
    private int rectWidth;
    private Bitmap bitmap;
    private OnScrollBorderListener onScrollBorderListener;
    private int minPx;

    public ThumbnailView(Context context) {
        super(context);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThumbnailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        int dp5 = (int) getResources().getDimension(R.dimen.dp5);
        mPaint.setStrokeWidth(dp5);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camera_select_selected);

        rectWidth = (int) getResources().getDimension(R.dimen.dp10);
        minPx = rectWidth;
    }
    public int getMinPx(){
        return minPx;
    }
    public void setMinInterval(int minPx){
        if(mWidth>0 && minPx > mWidth){
            minPx = mWidth;
        }
        this.minPx = minPx;
    }
    public void setWidth(int width){
        mWidth = width;
        rectF2.left = mWidth - rectWidth;
        rectF2.top = 0;
        rectF2.right = mWidth;
        invalidate();
    }

    public interface OnScrollBorderListener{
        void OnScrollBorder(float start, float end);
        void onScrollStateChange();
    }

    public void setOnScrollBorderListener(OnScrollBorderListener listener){
        this.onScrollBorderListener = listener;
    }

    public float getLeftInterval(){
        return rectF.left;
    }

    public float getRightInterval(){
        return rectF2.right;
    }
    public float getTotalWidth(){
        return mWidth;
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

            rectF2 = new RectF();
            rectF2.left = mWidth - rectWidth;
            rectF2.top = 0;
            rectF2.right = mWidth;
            rectF2.bottom = mHeight;
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
                if (downX > rectF.left-rectWidth/2 && downX < rectF.right+rectWidth/2) {
                    scrollLeft = true;
                }
                if (downX > rectF2.left-rectWidth/2 && downX < rectF2.right+rectWidth/2) {
                    scrollRight = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();

                float scrollX = moveX - downX;

                if (scrollLeft) {
                    rectF.left = rectF.left + scrollX;
                    rectF.right = rectF.right + scrollX;

                    if(rectF.left < 0){
                        rectF.left = 0;
                        rectF.right = rectWidth;
                    }
                    if(rectF.left > rectF2.right-minPx){
                        rectF.left = rectF2.right-minPx;
                        rectF.right = rectF.left+rectWidth;
                    }
                    scrollChange = true;
                    invalidate();
                } else if (scrollRight) {
                    rectF2.left = rectF2.left + scrollX;
                    rectF2.right = rectF2.right + scrollX;

                    if(rectF2.right > mWidth){
                        rectF2.right = mWidth;
                        rectF2.left = rectF2.right- rectWidth;
                    }
                    if(rectF2.right < rectF.left+minPx){
                        rectF2.right = rectF.left+minPx;
                        rectF2.left = rectF2.right-rectWidth;
                    }
                    scrollChange = true;
                    invalidate();
                }

                if(onScrollBorderListener != null){
                    onScrollBorderListener.OnScrollBorder(rectF.left, rectF2.right);
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

        mPaint.setColor(Color.parseColor("#FC4253"));

        Rect rect = new Rect();
        rect.left = (int) rectF.left;
        rect.top = (int) rectF.top;
        rect.right = (int) rectF.right;
        rect.bottom = (int) rectF.bottom;
        canvas.drawBitmap(bitmap, null, rectF, mPaint);

        Rect rect2 = new Rect();
        rect2.left = (int) rectF2.left;
        rect2.top = (int) rectF2.top;
        rect2.right = (int) rectF2.right;
        rect2.bottom = (int) rectF2.bottom;
        canvas.drawBitmap(bitmap, null, rectF2, mPaint);



        canvas.drawLine(rectF.left, 0, rectF2.right, 0, mPaint);
        canvas.drawLine(rectF.left, mHeight, rectF2.right, mHeight, mPaint);

        mPaint.setColor(Color.parseColor("#99313133"));

        RectF rectF3 = new RectF();
        rectF3.left = 0;
        rectF3.top = 0;
        rectF3.right = rectF.left;
        rectF3.bottom = mHeight;
        canvas.drawRect(rectF3, mPaint);

        RectF rectF4 = new RectF();
        rectF4.left = rectF2.right;
        rectF4.top = 0;
        rectF4.right = mWidth;
        rectF4.bottom = mHeight;
        canvas.drawRect(rectF4, mPaint);
    }
}