package com.aserbao.androidcustomcamera.blocks.ffmpeg

import VideoHandle.EpDraw
import VideoHandle.EpEditor
import VideoHandle.EpVideo
import VideoHandle.OnEditorListener
import android.os.Environment
import android.util.Log
import android.view.View
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean
import com.aserbao.androidcustomcamera.blocks.ffmpeg.utils.FFmpegUtils


var absolutePath = Environment.getExternalStorageDirectory().absolutePath

class FFmpegActivity : RVBaseActivity(),OnEditorListener {

    override fun initGetData() {
        mBaseRecyclerBeen.add(BaseRecyclerBean("视频中抽取音频", 0))
        mBaseRecyclerBeen.add(BaseRecyclerBean("视频添加水印", 1))
        mBaseRecyclerBeen.add(BaseRecyclerBean("无损视频合并", 2))
        mBaseRecyclerBeen.add(BaseRecyclerBean("多段视频合并", 3))
        mBaseRecyclerBeen.add(BaseRecyclerBean("多段视频加水印并合成", 4))
    }
    var videoPath1 = absolutePath + "/123.mp4"
    var videoPath2 = absolutePath + "/4.mp4"
    var videoPath3 = absolutePath + "/5.mp4"

    var png1 = absolutePath + "/1.png"
    var png2 = absolutePath + "/2.png"
    var png3 = absolutePath + "/3.png"

    var outputMusicPath = absolutePath + "/out_aserbao.mp3"
    var outputPath1 = absolutePath + "/out_aserbao1.mp4"
    var outputPath2 = absolutePath + "/out_aserbao2.mp4"
    var outputPath3 = absolutePath + "/out_aserbao3.mp4"
    var outputPathMp4 = absolutePath + "/out_aserbao.mp4"

    var mStartTime:Long = 0
    override fun itemClickBack(view: View, position: Int, isLongClick: Boolean, comeFrom: Int) {
        mStartTime = System.currentTimeMillis()
        when(position){
            0 ->{
                FFmpegUtils.demuxer(videoPath1,outputMusicPath,EpEditor.Format.MP3,this)
            }
            1 ->{
                var epVideo1 = EpVideo(videoPath1)
                epVideo1.addDraw(EpDraw(png1,0,0,576f,1024f,false))
                val outputOption = EpEditor.OutputOption(outputPathMp4)
                EpEditor.exec(epVideo1, outputOption,this)
            }
            2 ->{
                var epVideo1 = EpVideo(videoPath1)
                var epVideo2 = EpVideo(videoPath2)
                var epVideo3 = EpVideo(videoPath3)
                epVideo1.addDraw(EpDraw(png1,0,0,576f,1024f,false))
                epVideo2.addDraw(EpDraw(png2,0,0,576f,1024f,false))
                epVideo3.addDraw(EpDraw(png3,0,0,576f,1024f,false))
                val list = listOf<EpVideo>(epVideo1, epVideo2,epVideo3)
                var outputOption = EpEditor.OutputOption(outputPathMp4)
                EpEditor.mergeByLc(this@FFmpegActivity,list,outputOption,this)
            }
            3 ->{
                var epVideo1 = EpVideo(videoPath1)
                var epVideo2 = EpVideo(videoPath2)
                var epVideo3 = EpVideo(videoPath3)
                val list = listOf<EpVideo>(epVideo1, epVideo2,epVideo3)
                var outputOption = EpEditor.OutputOption(outputPathMp4)
                EpEditor.merge(list,outputOption,this)
            }
            4 ->{
                addWaterFilter(0)
            }
        }
    }

    fun addWaterFilter(index:Int){
        when(index){
            0 ->{
                var epVideo1 = EpVideo(videoPath1)
                epVideo1.addDraw(EpDraw(png1,0,0,576f,1024f,false))
                val outputOption1 = EpEditor.OutputOption(outputPath1)
                EpEditor.exec(epVideo1, outputOption1,this)
            }
            1 ->{
                var epVideo2 = EpVideo(videoPath2)
                epVideo2.addDraw(EpDraw(png2,0,0,576f,1024f,false))
                val outputOption2 = EpEditor.OutputOption(outputPath2)
                EpEditor.exec(epVideo2, outputOption2,this)
            }
            2 ->{
                var epVideo3 = EpVideo(videoPath3)
                epVideo3.addDraw(EpDraw(png3,0,0,576f,1024f,false))
                val outputOption3 = EpEditor.OutputOption(outputPath3)
                EpEditor.exec(epVideo3, outputOption3,this)
            }
        }

    }

    var cuurIndex = 0;

    override fun onSuccess() {
        cuurIndex++
        addWaterFilter(cuurIndex)
        if(cuurIndex == 3)
        Log.e(TAG, ": onSuccess 耗时： "  + (System.currentTimeMillis() - mStartTime) );
    }

    override fun onFailure() {
        Log.e(TAG, ": onFailure");
    }

    override fun onProgress(progress: Float) {
        Log.e(TAG, ": onProgress" + progress );
    }


}