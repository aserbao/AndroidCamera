package com.aserbao.androidcustomcamera.blocks.ffmpeg

import Jni.FFmpegCmd
import VideoHandle.*
import android.os.Environment
import android.support.annotation.MainThread
import android.util.Log
import android.view.View
import android.widget.Toast
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean
import com.aserbao.androidcustomcamera.blocks.ffmpeg.beans.WaterFilter
import com.aserbao.androidcustomcamera.blocks.ffmpeg.utils.FFmpegUtils
import java.util.*
import kotlin.collections.ArrayList


var absolutePath = Environment.getExternalStorageDirectory().absolutePath

class FFmpegActivity : RVBaseActivity(),OnEditorListener {

    override fun initGetData() {
        mBaseRecyclerBeen.add(BaseRecyclerBean("取消", 100))
        mBaseRecyclerBeen.add(BaseRecyclerBean("视频中抽取音频", 0))
        mBaseRecyclerBeen.add(BaseRecyclerBean("视频添加水印", 1))
        mBaseRecyclerBeen.add(BaseRecyclerBean("无损视频合并", 2))
        mBaseRecyclerBeen.add(BaseRecyclerBean("多段视频合并", 3))
        mBaseRecyclerBeen.add(BaseRecyclerBean("多段视频加水印并合成", 4))
        mBaseRecyclerBeen.add(BaseRecyclerBean("视频添加配乐并调整音量大小", 5))

        mInputs.add(WaterFilter(videoPath1,png1))
        mInputs.add(WaterFilter(videoPath2,png2))
        mInputs.add(WaterFilter(videoPath3,png3))
    }
    var testVideoPath = "/storage/emulated/0/playground/temp/1588820387250.mp4"
    var testPicPath = "/storage/emulated/0/playground/temp/1588820387250.png"

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

    var mInputs : MutableList<WaterFilter> = ArrayList()


    var mStartTime:Long = 0
    override fun itemClickBack(view: View, position: Int, isLongClick: Boolean, comeFrom: Int) {
        mStartTime = System.currentTimeMillis()
        when(position){
            100 ->{
//                FFmpegCmd.exit()
                addMusicToVideo1()
            }
            0 ->{
                FFmpegUtils.demuxer(videoPath1,outputMusicPath,EpEditor.Format.MP3,this)
            }
            1 ->{
                var tempVideoPath = "/storage/emulated/0/Android/data/com.getremark.playground/files/Movies/15871817738614870009935443.mp4"
                var tempBitmapPath = "/storage/emulated/0/playground/temp/123.png"
                var epVideo1 = EpVideo(tempVideoPath)
//                var epVideo1 = EpVideo(videoPath1)
                epVideo1.addDraw(EpDraw(tempBitmapPath,0,0,576f,1024f,false))
                val outputOption = EpEditor.OutputOption(outputPathMp4)
                EpEditor.exec(epVideo1, outputOption,this)
            }
            2 ->{
                var epVideo1 = EpVideo(outputPath1)
                var epVideo2 = EpVideo(outputPath2)
                var epVideo3 = EpVideo(outputPath3)
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
//                addWaterFilter(0)
                addWaterFilterOneLine()
            }
            5 ->{
                addMusicToVideo()
            }
        }
    }

    fun addMusicToVideo(){
        var inputVideo = absolutePath + "/5.mp4"
//        var inputVideo = absolutePath + "/temp.mp4"
        var inputMusic = absolutePath + "/input.mp3"
        var outputVideo = absolutePath + "/output.mp4"
        var videoVolume = 0.5f
        var musicVolume = 1f
        FFmpegUtils.music(inputVideo,inputVideo,outputVideo,videoVolume,musicVolume,this)
//        FFmpegUtils.addMusicForMp4(inputVideo,inputMusic,videoVolume,musicVolume,outputVideo,this)
    }
    fun addMusicToVideo1(){
//        var inputVideo = absolutePath + "/5.mp4"
//        var inputVideo = absolutePath + "/temp.mp4"
//        var inputMusic = absolutePath + "/input.mp3"
//        var inputVideo = "/storage/emulated/0/playground/temp/.capture/.remark-1588920936552.mp4"
        var inputVideo = absolutePath + "/test1.mp4"
        var inputMusic = absolutePath +"/er.m4a"
        var outputVideo = absolutePath + "/output.mp4"
        var videoVolume = 1f
        var musicVolume = 1f
        FFmpegUtils.music(inputVideo,inputMusic,outputVideo,videoVolume,musicVolume,this)
    }


    private fun addWaterFilterOneLine() {
//        ffmpeg -i 2.mp4 -i 3.mp4  -i img1.png -i img2.png -filter_complex "[0:v][2:v]overlay=0:0[in1];[1:v][3:v]overlay=0:10[in2];[in1][in2]concat" -y output.mp4
        //开始处理
        var sb= StringBuffer()
        val cmd = CmdList()
        cmd.append("ffmpeg")
        cmd.append("-y")
        mInputs.forEachIndexed{i,waterFilter ->
            cmd.append("-i")
            cmd.append(waterFilter.videoPath)
            cmd.append("-i")
            cmd.append(waterFilter.picturePath)
        }
        cmd.append("-filter_complex")
//        cmd.append("&#8220")
        cmd.append("\"")
        for(i in 0 until mInputs.size){
            var inflag = "[in" +i.toString()+"]"
            var firstIndex = i * 2
            var firstElement = firstIndex.toString()
            var secondElement = (firstIndex+1).toString()
            cmd.append("[$firstElement:v][$secondElement:v]overlay=0:0").append(inflag).append(";")
            sb.append(inflag)
        }
//        sb.append("concat&#8221")
        sb.append("concat\"")
        cmd.append(sb.toString())
        cmd.append(outputPathMp4)
        val cmds = cmd.toTypedArray()
        var cmdLog = ""
        for (ss in cmds) {
            cmdLog += cmds
        }
        Log.v(TAG, "cmd:$cmdLog")
        for (s in cmd) {
            Log.e(TAG,"------："+ s);
        }
        FFmpegCmd.exec(cmds, 46*1000, this)
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
        /*cuurIndex++
        addWaterFilter(cuurIndex)
        if(cuurIndex == 3){
            itemClickBack(mBaseRv,2,false,2)
        }*/

        Log.e(TAG, ": onSuccess 耗时： "  + (System.currentTimeMillis() - mStartTime) );
    }

    override fun onFailure() {
        Log.e(TAG, ": onFailure");
    }

    override fun onProgress(progress: Float) {
        Log.e(TAG, ": onProgress" + progress );
    }
}