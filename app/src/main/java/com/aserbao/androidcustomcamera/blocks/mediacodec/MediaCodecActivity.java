package com.aserbao.androidcustomcamera.blocks.mediacodec;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;
import com.aserbao.androidcustomcamera.blocks.mediacodec.primary.PrimaryMediaCodecActivity;

import java.util.List;

public class MediaCodecActivity extends RVBaseActivity {

    @Override
    public List<ClassBean> initData() {
        mClassBeans.add(new ClassBean("MediaCodec基本方法使用",PrimaryMediaCodecActivity.class));
        return mClassBeans;
    }
}
