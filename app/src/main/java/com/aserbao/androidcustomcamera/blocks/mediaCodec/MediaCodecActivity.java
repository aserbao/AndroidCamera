package com.aserbao.androidcustomcamera.blocks.mediaCodec;

import android.view.View;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.BigflakeActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.CreatMusicVideoByMediaCodecActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.PrimaryMediaCodecActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.primary.mp3TranslateAAC.Mp3TranslateAACActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.recordBaseCamera.RecordBaseCameraActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.recordCamera.RecordCameraActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.show.MediaCodecShowOnGlSurfaceView;

public class MediaCodecActivity extends RVBaseActivity {

    @Override
    protected void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaCodec基本方法使用Bigflake",BigflakeActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaCodec基本方法使用",PrimaryMediaCodecActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaCodec仅录制相机数据",RecordBaseCameraActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("音视频混合录制，通过SurfaceView显示相机数据",RecordCameraActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaCodec录制随音乐变化的视频",CreatMusicVideoByMediaCodecActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaCodec处理音乐",Mp3TranslateAACActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("MediaCodec解码视频在GlSurfaceView上显示", MediaCodecShowOnGlSurfaceView.class));
    }

    @Override
    public void itemClickBack(View view, int position, boolean isLongClick, int comeFrom) {

    }
}
