package com.aserbao.androidcustomcamera.blocks.ffmpeg.utils

import VideoHandle.OnEditorListener
import android.view.View
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity

class TEst : RVBaseActivity(), OnEditorListener {
    override fun onSuccess() {}
    override fun onFailure() {}
    override fun onProgress(progress: Float) {}
    override fun initGetData() {}
    override fun itemClickBack(view: View, position: Int, isLongClick: Boolean, comeFrom: Int) {}
    override fun onPointerCaptureChanged(hasCapture: Boolean) {}
}