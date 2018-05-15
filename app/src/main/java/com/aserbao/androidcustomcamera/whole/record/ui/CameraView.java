package com.aserbao.androidcustomcamera.whole.record.ui;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aserbao.androidcustomcamera.whole.record.camera.CameraController;
import com.aserbao.androidcustomcamera.whole.record.draw.CameraDrawer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */


public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener{

    public CameraDrawer mCameraDrawer;
    public CameraController mCameraController;
    private int dataWidth=0,dataHeight=0;
    private int cameraId;
    private boolean isSetParm = false;

    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private void init() {
        /**初始化OpenGL的相关信息*/
        setEGLContextClientVersion(2);//设置版本
        setRenderer(this);//设置Renderer
        setRenderMode(RENDERMODE_WHEN_DIRTY);//主动调用渲染
        setPreserveEGLContextOnPause(true);//保存Context当pause时
        setCameraDistance(100);//相机距离

        /**初始化Camera的绘制类*/
        mCameraDrawer = new CameraDrawer(getResources());
        /**初始化相机的管理类*/
        mCameraController = new CameraController();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDrawer.onSurfaceCreated(gl,config);
        if (!isSetParm){
            open(cameraId);
            stickerInit();
        }
        mCameraDrawer.setPreviewSize(dataWidth,dataHeight);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer.onSurfaceChanged(gl,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (isSetParm){
            mCameraDrawer.onDrawFrame(gl);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.requestRender();
    }
    private void open(int cameraId){
        mCameraController.close();//释放相机
        mCameraController.open(cameraId);//打开相机
        mCameraDrawer.setCameraId(cameraId);
        final Point previewSize=mCameraController.getPreviewSize();
        dataWidth=previewSize.x;
        dataHeight=previewSize.y;
        SurfaceTexture texture = mCameraDrawer.getTexture();
        texture.setOnFrameAvailableListener(this);
        mCameraController.setPreviewTexture(texture);
        mCameraController.preview();
    }
    public void switchCamera(){
        cameraId = cameraId==0?1:0;
        open(cameraId);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(isSetParm){
            open(cameraId);
        }
    }
    public void onDestroy(){
        if (mCameraController != null){
            mCameraController.close();
        }
    }
    public int getCameraId(){
        return cameraId;
    }
    public int getBeautyLevel() {
        return mCameraDrawer.getBeautyLevel();
    }

    public void changeBeautyLevel(final int level) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.changeBeautyLevel(level);
            }
        });
    }
    public void startRecord(){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.startRecord();
            }
        });
    }

    public void stopRecord(){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.stopRecord();
            }
        });
    }
    public void setSavePath(String path) {
        mCameraDrawer.setSavePath(path);
    }
    public void resume(final boolean auto) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.onResume(auto);
            }
        });
    }
    public void pause(final boolean auto) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.onPause(auto);
            }
        });
    }
    public void onTouch(final MotionEvent event){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer.onTouch(event);
            }
        });
    }
    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener){
        mCameraDrawer.setOnFilterChangeListener(listener);
    }
    /**
     * 摄像头聚焦
     * */
    public void onFocus(Point point, Camera.AutoFocusCallback callback){
        mCameraController.onFocus(point,callback);
    }


    private void stickerInit(){
        if(!isSetParm&&dataWidth>0&&dataHeight>0) {
            isSetParm = true;
        }
    }
}
