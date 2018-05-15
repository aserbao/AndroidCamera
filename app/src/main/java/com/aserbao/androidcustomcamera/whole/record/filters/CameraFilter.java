package com.aserbao.androidcustomcamera.whole.record.filters;

import android.content.res.Resources;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */


public class CameraFilter extends OESFilter {

    public CameraFilter(Resources mRes) {
        super(mRes);
    }
    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        float[] coord;
        if(getFlag()==1){    //前置摄像头 顺时针旋转90,并上下颠倒
            coord=new float[]{
                    1.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,
            };
        }else{               //后置摄像头 顺时针旋转90度
            coord=new float[]{
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
            };
        }
        mTexBuffer.clear();
        mTexBuffer.put(coord);
        mTexBuffer.position(0);
    }

}
