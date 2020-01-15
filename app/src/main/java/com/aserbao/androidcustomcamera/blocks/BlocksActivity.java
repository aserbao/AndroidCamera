package com.aserbao.androidcustomcamera.blocks;

import android.view.View;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.blocks.audioRecord.AudioRecordActivity;
import com.aserbao.androidcustomcamera.blocks.others.OthersActivity;
import com.aserbao.androidcustomcamera.blocks.others.changeHue.ChangeHueActivity;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.MediaExtractorActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.MediaCodecActivity;
import com.aserbao.androidcustomcamera.blocks.mediaMuxer.MediaMuxerActivity;
import com.aserbao.androidcustomcamera.blocks.mediaMuxer.functions.CreateVideoAddAudioToMp4;

public class BlocksActivity extends RVBaseActivity {

    @Override
    protected void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("修改hue", ChangeHueActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("AudioRecord", AudioRecordActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaCodec", MediaCodecActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaExtractor", MediaExtractorActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaMuxer", MediaMuxerActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("合成", CreateVideoAddAudioToMp4.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("其他", OthersActivity.class));
    }

    @Override
    public void itemClickBack(View view, int position, boolean isLongClick, int comeFrom) {

    }
}
