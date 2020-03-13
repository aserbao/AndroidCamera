package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.List;



public class BaseImageView extends ImageView {

    protected float X;
    protected float Y;
    protected float viewWidth;
    protected float viewHeight;
    protected int resourceId;
    protected float rotateDegree;
    protected long startTime;
    protected long endTime;
    protected boolean isGif;//是否是gif
    protected String resourceGif;//gif位置

    protected float mScaleX;
    protected float mScaleY;

    protected float posX;
    protected float posY;

    protected float leftBottomX;//左下角坐标
    protected float leftBottomY;

    protected Matrix matrix = new Matrix();


    protected int gifId;

    protected long timeStamp;

    private int frameIndex;  //播放gif图的第几帧
    private List<Bitmap> bitmaps;


    public float getLeftBottomX() {
        return leftBottomX;
    }

    public void setLeftBottomX(float leftBottomX) {
        this.leftBottomX = leftBottomX;
    }

    public float getLeftBottomY() {
        return leftBottomY;
    }

    public void setLeftBottomY(float leftBottomY) {
        this.leftBottomY = leftBottomY;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(int frameIndex) {
        if (bitmaps != null && bitmaps.size() > 0) {
            this.frameIndex = frameIndex % bitmaps.size();
        }
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getGifId() {
        return gifId;
    }

    public void setGifId(int gifId) {
        this.gifId = gifId;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }


    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public float getmScaleX() {
        return mScaleX;
    }

    public void setmScaleX(float mScaleX) {
        this.mScaleX = mScaleX;
    }

    public float getmScaleY() {
        return mScaleY;
    }

    public void setmScaleY(float mScaleY) {
        this.mScaleY = mScaleY;
    }

    /**
     * 记录动画开始的时间
     */
    private long mMovieStart;

    /**
     * 播放GIF动画的关键类
     */
    private Movie mMovie;


    /**
     * GIF图片的宽度
     */
    private int mImageWidth;

    /**
     * GIF图片的高度
     */
    private int mImageHeight;


    public boolean isGif() {
        return isGif;
    }

    public void setGif(boolean gif) {
        isGif = gif;
    }

    public String getResourceGif() {
        return resourceGif;
    }

    public void setResourceGif(String resourceGif) {
        this.resourceGif = resourceGif;
    }

    public BaseImageView(Context context) {
        super(context);
        initData();
    }


    public BaseImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public BaseImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        endTime = 2000;
    }


    @Override
    public float getX() {
        return X;
    }

    @Override
    public void setX(float x) {
        X = x;
    }

    @Override
    public float getY() {
        return Y;
    }

    @Override
    public void setY(float y) {
        Y = y;
    }

    public float getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(float viewWidth) {
        this.viewWidth = viewWidth;
    }

    public float getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(float viewHeight) {
        this.viewHeight = viewHeight;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public float getRotateDegree() {
        return rotateDegree;
    }

    public void setRotateDegree(float rotateDegree) {
        this.rotateDegree = rotateDegree;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Bitmap getBitmap(){
        return null;
    };


    public float getParentX(){
        return super.getX();
    }

    public float getParentY(){
        return super.getY();
    }

}
