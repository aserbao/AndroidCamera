package com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit;

/**
 * Created by Administrator on 2017/6/29 0029.
 * 视频的信息bean
 */

public class VideoInfo {
    public String path;//路径
    public int rotation;//旋转角度
    public int width;//宽
    public int height;//高
    public int bitRate;//比特率
    public int frameRate;//帧率
    public int frameInterval;//关键帧间隔
    public int duration;//时长

    public int expWidth;//期望宽度
    public int expHeight;//期望高度
    public int cutPoint;//剪切的开始点
    public int cutDuration;//剪切的时长
}
