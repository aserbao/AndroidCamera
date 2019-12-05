package com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;


import com.aserbao.androidcustomcamera.whole.record.filters.AFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.GroupFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.NoFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.ProcessFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.RotationOESFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.WaterMarkFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.gpuFilters.baseFilter.GPUImageFilter;
import com.aserbao.androidcustomcamera.whole.record.filters.gpuFilters.baseFilter.MagicBeautyFilter;
import com.aserbao.androidcustomcamera.whole.record.ui.SlideGpuFilterGroup;
import com.aserbao.androidcustomcamera.whole.record.utils.EasyGlUtils;
import com.aserbao.androidcustomcamera.whole.record.utils.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



public class VideoDrawer implements GLSurfaceView.Renderer {
    private float[] OM;
    private float[] SM = new float[16];
    private SurfaceTexture surfaceTexture;
    private RotationOESFilter mPreFilter;
    private AFilter mShow;
    private MagicBeautyFilter mBeautyFilter;
    private AFilter mProcessFilter;
    private final GroupFilter mBeFilter;
    private SlideGpuFilterGroup mSlideFilterGroup;

    private GPUImageFilter mGroupFilter;
    private int viewWidth;
    private int viewHeight;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private int rotation;
    private boolean isBeauty = false;

    private static final String TAG = "VideoDrawer";

    public VideoDrawer(Context context, Resources res){
        Log.e(TAG, "VideoDrawer: " );
        mPreFilter = new RotationOESFilter(res);
        mShow = new NoFilter(res);
        mBeFilter = new GroupFilter(res);
        mBeautyFilter = new MagicBeautyFilter();

        mProcessFilter=new ProcessFilter(res);

        mSlideFilterGroup = new SlideGpuFilterGroup();
        OM = MatrixUtils.getOriginalMatrix();
        MatrixUtils.flip(OM,false,true);
        mShow.setMatrix(OM);
    }

    public void addWaterMarkFilter(Resources res, int x, int y, int width, int height, long startTime, long endTime, Bitmap bitmap, int bitRes, boolean isGif, float rotateDegree){
        WaterMarkFilter waterMarkFilter = new WaterMarkFilter(res,isGif,bitRes,rotateDegree);
        waterMarkFilter.setWaterMark(bitmap);
        waterMarkFilter.setPosition(x, y, 0, 0);
        waterMarkFilter.setShowTime(startTime, endTime);
        mBeFilter.addFilter(waterMarkFilter);
        Log.e(TAG, "addWaterMarkFilter: ");
    }
    public void addWaterMarkFilter(Resources res, int x, int y, int width, int height, long startTime, long endTime, Bitmap bitmap, int bitRes, boolean isGif, float rotateDegree, Matrix matrix){
            WaterMarkFilter waterMarkFilter = new WaterMarkFilter(res,isGif,bitRes,rotateDegree);
            waterMarkFilter.setWaterMark(bitmap);
            waterMarkFilter.setPosition(x, y, 0, 0);
            waterMarkFilter.setShowTime(startTime, endTime);
            waterMarkFilter.setMatrix(matrix);
            mBeFilter.addFilter(waterMarkFilter);
            Log.e(TAG, "addWaterMarkFilter: ");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.e(TAG, "onSurfaceCreated: ");
        int texture[]=new int[1];
        GLES20.glGenTextures(1,texture,0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES ,texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        surfaceTexture = new SurfaceTexture(texture[0]);
        mPreFilter.create();
        mPreFilter.setTextureId(texture[0]);

        mBeFilter.create();
        mProcessFilter.create();
        mShow.create();
        mBeautyFilter.init();
        mBeautyFilter.setBeautyLevel(3);
        mSlideFilterGroup.init();
    }
    public void onVideoChanged(VideoInfo info){
        Log.e(TAG, "onVideoChanged: ");
        setRotation(info.rotation);
        MatrixUtils.flip(SM,false,true);
        if(info.rotation==0||info.rotation==180){
            MatrixUtils.getShowMatrix(SM,info.width,info.height,viewWidth,viewHeight);
        }else{
            MatrixUtils.getShowMatrix(SM,info.height,info.width,viewWidth,viewHeight);
        }

        mPreFilter.setMatrix(SM);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e(TAG, "onSurfaceChanged: ");
        viewWidth=width;
        viewHeight=height;
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);

        GLES20.glGenFramebuffers(1,fFrame,0);
        EasyGlUtils.genTexturesWithParameter(1,fTexture,0, GLES20.GL_RGBA,viewWidth,viewHeight);

        mBeFilter.setSize(viewWidth,viewHeight);
        mProcessFilter.setSize(viewWidth,viewHeight);
        mBeautyFilter.onDisplaySizeChanged(viewWidth,viewHeight);
        mBeautyFilter.onInputSizeChanged(viewWidth,viewHeight);
        mSlideFilterGroup.onSizeChanged(viewWidth,viewHeight);
    }

    private long mTime;
    public void setMediaTime(long time){
        mTime = time;
        onDrawFrame(null);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        surfaceTexture.updateTexImage();
        EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);
        GLES20.glViewport(0,0,viewWidth,viewHeight);
        mPreFilter.draw();
        EasyGlUtils.unBindFrameBuffer();

        mBeFilter.setTextureId(fTexture[0]);
        mBeFilter.draw(mTime);

        if (mBeautyFilter != null && isBeauty && mBeautyFilter.getBeautyLevel() != 0){
            EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);
            GLES20.glViewport(0,0,viewWidth,viewHeight);
            mBeautyFilter.onDrawFrame(mBeFilter.getOutputTexture());
            EasyGlUtils.unBindFrameBuffer();
            mProcessFilter.setTextureId(fTexture[0]);
        }else {
            mProcessFilter.setTextureId(mBeFilter.getOutputTexture());
        }
        mProcessFilter.draw();

        mSlideFilterGroup.onDrawFrame(mProcessFilter.getOutputTexture());
        if (mGroupFilter != null){
            EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);
            GLES20.glViewport(0,0,viewWidth,viewHeight);
            mGroupFilter.onDrawFrame(mSlideFilterGroup.getOutputTexture());
            EasyGlUtils.unBindFrameBuffer();
            mProcessFilter.setTextureId(fTexture[0]);
        }else {
            mProcessFilter.setTextureId(mSlideFilterGroup.getOutputTexture());
        }
        mProcessFilter.draw();

        GLES20.glViewport(0,0,viewWidth,viewHeight);

        mShow.setTextureId(mProcessFilter.getOutputTexture());
        mShow.draw();
    }
    public SurfaceTexture getSurfaceTexture(){
        return surfaceTexture;
    }

    public void setRotation(int rotation){
        this.rotation=rotation;
        if(mPreFilter!=null){
            mPreFilter.setRotation(this.rotation);
        }
    }
    public void switchBeauty(){
        isBeauty = !isBeauty;
    }
    public void isOpenBeauty(boolean isBeauty){
        this.isBeauty = isBeauty;
    }

    public void onTouch(MotionEvent event){
//        mSlideFilterGroup.onTouchEvent(event);
    }
    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener){
        mSlideFilterGroup.setOnFilterChangeListener(listener);
    }
    public void setFilter(int i){
        mSlideFilterGroup.setFilter(i);
    }
    public void checkGlError(String s) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(s + ": glError " + error);
        }
    }

    public void setGpuFilter(GPUImageFilter filter) {
        if (filter != null){
            mGroupFilter = filter;
            mGroupFilter.init();
            mGroupFilter.onDisplaySizeChanged(viewWidth, viewWidth);
            mGroupFilter.onInputSizeChanged(viewWidth,viewHeight);
        }

    }
}
