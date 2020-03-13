package com.aserbao.androidcustomcamera.whole;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.CreateVideoByAudioDbActivity;
import com.aserbao.androidcustomcamera.whole.record.RecorderActivity;

import java.util.List;

public class WholeActivity extends RVBaseActivity {

    @Override
    protected void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("视频录制这边走", RecorderActivity.class));
    }

    @Override
    public void itemClickBack(View view, int position, boolean isLongClick, int comeFrom) {

    }
}
