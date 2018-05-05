package com.aserbao.androidcustomcamera;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;
import com.aserbao.androidcustomcamera.blocks.BlocksActivity;
import com.aserbao.androidcustomcamera.whole.WholeActivity;

import java.util.List;

public class MainActivity extends RVBaseActivity {
    @Override
    public List<ClassBean> initData() {
        mClassBeans.add(new ClassBean("每个功能点单独代码实现", BlocksActivity.class));
        mClassBeans.add(new ClassBean("所有功能点整合代码实现", WholeActivity.class));
        return mClassBeans;
    }
}
