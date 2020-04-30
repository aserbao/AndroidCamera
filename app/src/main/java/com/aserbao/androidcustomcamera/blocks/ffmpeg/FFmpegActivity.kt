package com.aserbao.androidcustomcamera.blocks.ffmpeg

import Jni.VideoUitls
import VideoHandle.CmdList
import VideoHandle.EpEditor
import VideoHandle.OnEditorListener
import android.os.Environment
import android.util.Log
import android.view.View
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean
import com.aserbao.androidcustomcamera.blocks.ffmpeg.utils.FFmpegUtils

class FFmpegActivity : RVBaseActivity() {

    override fun initGetData() {
        mBaseRecyclerBeen.add(BaseRecyclerBean("视频中抽取音频", 0))
    }
    var path = Environment.getExternalStorageDirectory().absolutePath + "/123.mp4"
    var outputMusicPath = Environment.getExternalStorageDirectory().absolutePath + "/out_aserbao.mp3"
    override fun itemClickBack(view: View, position: Int, isLongClick: Boolean, comeFrom: Int) {
        when(position){
            0 ->{
                FFmpegUtils.demuxer(path,outputMusicPath,EpEditor.Format.MP3,object : OnEditorListener{
                    override fun onSuccess() {
                        Log.e(TAG, ": onSuccess");
                    }

                    override fun onFailure() {
                        Log.e(TAG, ": onFailure");
                    }

                    override fun onProgress(progress: Float) {
                        Log.e(TAG, ": onProgress" + progress );
                    }

                })
            }
        }
    }


}