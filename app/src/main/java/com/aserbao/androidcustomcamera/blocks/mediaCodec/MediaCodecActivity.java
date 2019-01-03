package com.aserbao.androidcustomcamera.blocks.mediaCodec;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.BigflakeActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.CreatMusicVideoByMediaCodecActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.PrimaryMediaCodecActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.mp3TranslateAAC.Mp3TranslateAACActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.recordBaseCamera.RecordBaseCameraActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.recordCamera.RecordCameraActivity;

import java.util.List;

public class MediaCodecActivity extends RVBaseActivity {
    @Override
    public List<ClassBean> initData() {
        mClassBeans.add(new ClassBean("MediaCodec基本方法使用Bigflake",BigflakeActivity.class));
        mClassBeans.add(new ClassBean("MediaCodec基本方法使用",PrimaryMediaCodecActivity.class));
        mClassBeans.add(new ClassBean("MediaCodec仅录制相机数据",RecordBaseCameraActivity.class));
        mClassBeans.add(new ClassBean("音视频混合录制，通过SurfaceView显示相机数据",RecordCameraActivity.class));
        mClassBeans.add(new ClassBean("MediaCodec录制随音乐变化的视频",CreatMusicVideoByMediaCodecActivity.class));
        mClassBeans.add(new ClassBean("MediaCodec处理音乐",Mp3TranslateAACActivity.class));
        return mClassBeans;
    }
}
