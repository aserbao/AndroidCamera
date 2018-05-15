package com.aserbao.androidcustomcamera.whole.record.filters;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.aserbao.androidcustomcamera.whole.record.utils.MatrixUtils;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */


public class WaterMarkFilter extends NoneFilter{
    private NoneFilter mFilter;
    private Bitmap mBitmap;
    private int x,y,w,h;
    private int width,height;
    public WaterMarkFilter(Resources mRes) {
        super(mRes);
        mFilter=new NoneFilter(mRes);
    }

    @Override
    protected void onCreate() {
        mFilter.create();
        createTexture();
    }

    private int[] textures=new int[1];
    private void createTexture() {
        if(mBitmap!=null){
            //生成纹理
            GLES20.glGenTextures(1,textures,0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textures[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            //对画面进行矩阵旋转
            MatrixUtils.flip(mFilter.getMatrix(),false,true);

            mFilter.setTextureId(textures[0]);
        }
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.width=width;
        this.height=height;
        mFilter.setSize(width,height);
    }
    @Override
    public void draw(){
        GLES20.glViewport(x,y,w == 0 ? mBitmap.getWidth():w,h==0?mBitmap.getHeight():h);
        mFilter.draw();
    }
    public void setPosition(int x,int y,int width,int height){
        this.x=x;
        this.y=y;
        this.w=width;
        this.h=height;
    }
}
