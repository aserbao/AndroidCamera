package com.aserbao.androidcustomcamera.whole;

import android.view.View;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.whole.record.RecorderActivity;

public class WholeActivity extends RVBaseActivity {


    @Override
    protected void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("视频录制这边走", RecorderActivity.class));
    }

    @Override
    public void itemClickBack(View view, int position, boolean isLongClick, int comeFrom) {

    }
}
