package com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;

import java.util.List;

public class BigflakeActivity extends RVBaseActivity {

    @Override
    public List<ClassBean> initData() {
        mClassBeans.add(new ClassBean("EncodeAndMux",EncodeAndMuxActivity.class));
        mClassBeans.add(new ClassBean("CameraToMpeg",CameraToMpegActivity.class));
        return mClassBeans;
    }
}
