package com.aserbao.androidcustomcamera.whole.record.filters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;



import java.io.InputStream;

/**
 * 水印的Filter
 */

public class WaterMarkFilter extends NoFilter{
    private int x,y,w,h;
    private int width,height;
    private Bitmap mBitmap;
    private Bitmap mGifBitmap;
    private NoFilter mFilter;
    public android.graphics.Matrix mMatrix;
    public GifDecoder mGifDecoder;

    public WaterMarkFilter(Resources mRes) {
        super(mRes);
        mFilter=new NoFilter(mRes){
            @Override
            protected void onClear() {
            }
        };
    }
    private boolean mIsGif = false;
    private int mGifId;
    private int mRotateDegree;
    private Resources mResources;
    public WaterMarkFilter(Resources res, boolean isGif, int bitRes, float rotateDegree) {
        super(res);
        mResources = res;
        mGifId = bitRes;
        mIsGif = isGif;
        mRotateDegree = (int)rotateDegree;
        mFilter=new NoFilter(mRes){
            @Override
            protected void onClear() {
            }
        };
    }

    public void setWaterMark(Bitmap bitmap){
        if(this.mBitmap!=null && !mBitmap.isRecycled()){
            this.mBitmap.recycle();
            mBitmap = null;
        }
        if (mGifBitmap != null && !mGifBitmap.isRecycled()) {
            mGifBitmap.recycle();
            mGifBitmap = null;
        }
        this.mBitmap=bitmap;
    }
    private long mStartTime,mEndTime;
    public void setShowTime(long startTime,long endTime){
        mStartTime = startTime;
        mEndTime = endTime;
    }
    private float[] mRotationMatrix = new float[16];
    @Override
    public void draw() {
        super.draw();
        GLES20.glViewport(x,y,w == 0 ? mBitmap.getWidth():w,h==0?mBitmap.getHeight():h);
        blendFunc();
        mFilter.draw();
    }
    @Override
    public void draw(long time) {
        super.draw();
        if(mIsGif){
            createTexture();
        }
        if(time > mStartTime && time < mEndTime) {
            int i = (int) (mBitmap.getWidth() * 1.15);
            int i1 = (int) (mBitmap.getHeight() * 1.15);
            GLES20.glViewport(x, y, w == 0 ? i : w, h == 0 ? i1 : h);
            blendFunc();
            mFilter.draw();
        }
    }

    private void blendFunc() {
        GLES20.glEnable(GLES20.GL_BLEND);
//      GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
      GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);//使用这个混合算法可以合成带透明度的贴纸。参考：https://www.jianshu.com/p/2fb9d90b57f0
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        mFilter.create();
        if(mIsGif){
            mGifDecoder = new GifDecoder();
            InputStream inputStream  = mResources.openRawResource(mGifId);
            mGifDecoder.read(inputStream);
            mMatrix = new android.graphics.Matrix();
            mMatrix.postRotate(mRotateDegree);
        }
        createTexture();
    }
    private int[] textures=new int[1];
    private void createTexture() {
        if(mBitmap!=null){
            GLES20.glGenTextures(1,textures,0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            if(!mIsGif) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0,  mBitmap, 0);
            }else {
                mGifBitmap = mGifDecoder.nextBitmap();
                if (mGifBitmap != null) {
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, Bitmap.createBitmap(mGifBitmap, 0, 0, mGifBitmap.getWidth(), mGifBitmap.getHeight(), mMatrix, true), 0);
                }
            }
            //对画面进行矩阵旋转
//            MatrixUtils.flip(mFilter.getMatrix(),false,true);

            mFilter.setTextureId(textures[0]);
        }
    }
    public void setMatrix(Matrix matrix){
        mMatrix = matrix;
    }
    @Override
    protected void onSizeChanged(int width, int height) {
        this.width=width;
        this.height=height;
        /*GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);*/
        mFilter.setSize(width,height);
    }
    public void setPosition(int x,int y,int width,int height){
        this.x=x;
        this.y=y;
        this.w=width;
        this.h=height;
    }
}
