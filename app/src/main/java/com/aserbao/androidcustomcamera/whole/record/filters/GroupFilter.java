package com.aserbao.androidcustomcamera.whole.record.filters;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.aserbao.androidcustomcamera.whole.record.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */


public class GroupFilter extends BaseFilter{
    private Queue<AFilter> mFilterQueue;
    private List<AFilter> mFilters;
    private int width=0, height=0;
    private int size=0;

    public GroupFilter(Resources mRes) {
        super(mRes);
        mFilters = new ArrayList<>();
        mFilterQueue = new ConcurrentLinkedDeque<>();
    }


    public void addFilter(final AFilter filter){
        MatrixUtils.flip(filter.getMatrix(),false,true);
        mFilterQueue.add(filter);
    }
    public boolean removeFilter(BaseFilter filter){
        boolean b=mFilters.remove(filter);
        if(b){
            size--;
        }
        return b;
    }

    public AFilter removeFilter(int index){
        AFilter f= mFilters.remove(index);
        if(f!=null){
            size--;
        }
        return f;
    }
    /**
     * 双Texture,一个输入一个输出,循环往复
     */
    public void draw(long time){
        updateFilter();
        textureIndex=0;
        for (AFilter filter:mFilters){
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, fTexture[textureIndex%2], 0);
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, fRender[0]);
            GLES20.glViewport(0,0,width,height);
            if(textureIndex==0){
                filter.setTextureId(getTextureId());
            }else{
                filter.setTextureId(fTexture[(textureIndex-1)%2]);
            }
            filter.draw(time);
            unBindFrame();
            textureIndex++;
        }
    }

    private void updateFilter(){
        AFilter f;
        while ((f=mFilterQueue.poll())!=null){
            f.create();
            f.setSize(width,height);
            mFilters.add(f);
            size++;
        }
    }
    @Override
    public int getOutputTexture(){
        return size==0?getTextureId():fTexture[(textureIndex-1)%2];
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.width=width;
        this.height=height;
        updateFilter();
        createFrameBuffer();
    }

    //创建离屏buffer
    private int fTextureSize = 2;
    private int[] fFrame = new int[1];
    private int[] fRender = new int[1];
    private int[] fTexture = new int[fTextureSize];
    private int textureIndex=0;

    //创建FrameBuffer
    private boolean createFrameBuffer() {
        GLES20.glGenFramebuffers(1, fFrame, 0);
        GLES20.glGenRenderbuffers(1, fRender, 0);

        genTextures();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width,
                height);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[0], 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);
        unBindFrame();
        return false;
    }

    //生成Textures
    private void genTextures() {
        GLES20.glGenTextures(fTextureSize, fTexture, 0);
        for (int i = 0; i < fTextureSize; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[i]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        }
    }

    //取消绑定Texture
    private void unBindFrame() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    private void deleteFrameBuffer() {
        GLES20.glDeleteRenderbuffers(1, fRender, 0);
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }
}
