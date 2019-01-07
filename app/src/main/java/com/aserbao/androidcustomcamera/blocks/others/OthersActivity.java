package com.aserbao.androidcustomcamera.blocks.others;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;
import com.aserbao.androidcustomcamera.blocks.others.changeHue.ChangeHueActivity;

import java.util.List;

public class OthersActivity extends RVBaseActivity {

    @Override
    public List<ClassBean> initData() {
        mClassBeans.add(new ClassBean("修改hue", ChangeHueActivity.class));
        return mClassBeans;
    }

}
