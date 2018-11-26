package com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.cameraToMpeg.CameraToMpegActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.decodeEditEncode.DecodeEditEncodeActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.encodeAndMux.EncodeAndMuxActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.encodeDecode.EncodeDecodeActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.extractDecodeEditEncodeMux.ExtractDecodeEditEncodeMuxActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.extractMpegFrames.ExtractMpegFramesActivity;

import java.util.List;

public class BigflakeActivity extends RVBaseActivity {

    @Override
    public List<ClassBean> initData() {
        mClassBeans.add(new ClassBean("EncodeAndMux",EncodeAndMuxActivity.class));
        mClassBeans.add(new ClassBean("CameraToMpeg",CameraToMpegActivity.class));
        mClassBeans.add(new ClassBean("EncodeDecode",EncodeDecodeActivity.class));
        mClassBeans.add(new ClassBean("ExtractDecodeEditEncodeMux",ExtractDecodeEditEncodeMuxActivity.class));
        mClassBeans.add(new ClassBean("DecodeEditEncodeActivity",DecodeEditEncodeActivity.class));
        mClassBeans.add(new ClassBean("ExtractMpegFramesActivity",ExtractMpegFramesActivity.class));
        return mClassBeans;
    }
}
