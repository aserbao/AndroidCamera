package com.aserbao.androidcustomcamera.blocks;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;
import com.aserbao.androidcustomcamera.blocks.audioRecord.AudioRecordActivity;
import com.aserbao.androidcustomcamera.blocks.others.changeHue.ChangeHueActivity;
import com.aserbao.androidcustomcamera.blocks.mediaExtractor.MediaExtractorActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.MediaCodecActivity;
import com.aserbao.androidcustomcamera.blocks.mediaMuxer.MediaMuxerActivity;
import com.aserbao.androidcustomcamera.blocks.mediaMuxer.functions.CreateVideoAddAudioToMp4;

import java.util.List;

public class BlocksActivity extends RVBaseActivity {

    @Override
    public List<ClassBean> initData() {
        mClassBeans.add(new ClassBean("修改hue", ChangeHueActivity.class));
        mClassBeans.add(new ClassBean("AudioRecord", AudioRecordActivity.class));
        mClassBeans.add(new ClassBean("MediaCodec", MediaCodecActivity.class));
        mClassBeans.add(new ClassBean("MediaExtractor", MediaExtractorActivity.class));
        mClassBeans.add(new ClassBean("MediaMuxer", MediaMuxerActivity.class));
        mClassBeans.add(new ClassBean("合成", CreateVideoAddAudioToMp4.class));
        return mClassBeans;
    }

}
