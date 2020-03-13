package com.aserbao.androidcustomcamera.blocks.others;

import android.view.View;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.blocks.others.changeHue.ChangeHueActivity;
import com.aserbao.androidcustomcamera.blocks.others.changeVoice.ChangeVoiceActivity;

public class OthersActivity extends RVBaseActivity {


    @Override
    protected void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("修改hue", ChangeHueActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("变声", ChangeVoiceActivity.class));
    }

    @Override
    public void itemClickBack(View view, int position, boolean isLongClick, int comeFrom) {

    }
}
