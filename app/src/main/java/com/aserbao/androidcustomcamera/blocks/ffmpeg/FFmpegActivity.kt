package com.aserbao.androidcustomcamera.blocks.ffmpeg

import Jni.VideoUitls
import VideoHandle.CmdList
import VideoHandle.EpEditor
import VideoHandle.EpVideo
import VideoHandle.OnEditorListener
import android.os.Environment
import android.util.Log
import android.view.View
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean
import com.aserbao.androidcustomcamera.blocks.ffmpeg.utils.FFmpegUtils

class FFmpegActivity : RVBaseActivity(),OnEditorListener {

    override fun initGetData() {
        mBaseRecyclerBeen.add(BaseRecyclerBean("视频中抽取音频", 0))
        mBaseRecyclerBeen.add(BaseRecyclerBean("多段视频合成", 1))
    }
    var path1 = Environment.getExternalStorageDirectory().absolutePath + "/123.mp4"
    var path2 = Environment.getExternalStorageDirectory().absolutePath + "/4.mp4"
    var path3 = Environment.getExternalStorageDirectory().absolutePath + "/5.mp4"
    var outputMusicPath = Environment.getExternalStorageDirectory().absolutePath + "/out_aserbao.mp3"
    var outputPathMp4 = Environment.getExternalStorageDirectory().absolutePath + "/out_aserbao.mp4"
    override fun itemClickBack(view: View, position: Int, isLongClick: Boolean, comeFrom: Int) {
        when(position){
            0 ->{
                FFmpegUtils.demuxer(path1,outputMusicPath,EpEditor.Format.MP3,this)
            }
            1 ->{
                val list = listOf<EpVideo>(EpVideo(path1),EpVideo(path2),EpVideo(path3))
                var outputOption = EpEditor.OutputOption(outputPathMp4)
                EpEditor.mergeByLc(this@FFmpegActivity,list,outputOption,this)
            }
        }
    }

    override fun onSuccess() {
        Log.e(TAG, ": onSuccess");
    }

    override fun onFailure() {
        Log.e(TAG, ": onFailure");
    }

    override fun onProgress(progress: Float) {
        Log.e(TAG, ": onProgress" + progress );
    }


}