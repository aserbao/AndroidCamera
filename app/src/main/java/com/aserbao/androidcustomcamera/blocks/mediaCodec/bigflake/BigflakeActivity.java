package com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake;

import android.view.View;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.cameraToMpeg.CameraToMpegActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.decodeEditEncode.DecodeEditEncodeActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.encodeAndMux.EncodeAndMuxActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.encodeDecode.EncodeDecodeActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.extractDecodeEditEncodeMux.ExtractDecodeEditEncodeMuxActivity;
import com.aserbao.androidcustomcamera.blocks.mediaCodec.bigflake.extractMpegFrames.ExtractMpegFramesActivity;

public class BigflakeActivity extends RVBaseActivity {


    @Override
    protected void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("EncodeAndMux",EncodeAndMuxActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("CameraToMpeg",CameraToMpegActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("EncodeDecode",EncodeDecodeActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("ExtractDecodeEditEncodeMux",ExtractDecodeEditEncodeMuxActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("DecodeEditEncodeActivity",DecodeEditEncodeActivity.class));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("ExtractMpegFramesActivity",ExtractMpegFramesActivity.class));
    }

    @Override
    public void itemClickBack(View view, int position, boolean isLongClick, int comeFrom) {

    }
}
