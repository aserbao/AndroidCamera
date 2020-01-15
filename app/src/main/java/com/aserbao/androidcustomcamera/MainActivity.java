package com.aserbao.androidcustomcamera;

import android.view.View;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.blocks.BlocksActivity;
import com.aserbao.androidcustomcamera.blocks.others.changeVoice.ChangeVoiceActivity;
import com.aserbao.androidcustomcamera.whole.WholeActivity;

public class MainActivity extends RVBaseActivity {

    @Override
    protected void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("每个功能点单独代码实现", BlocksActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("所有功能点整合代码实现", WholeActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("当前调用界面", ChangeVoiceActivity.class));
    }

    @Override
    public void itemClickBack(View view, int position, boolean isLongClick, int comeFrom) {

    }
}
