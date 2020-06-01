package com.aserbao.androidcustomcamera.blocks.ffmpeg.utils;

import android.content.Context;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Jni.FFmpegCmd;
import Jni.FileUtils;
import Jni.TrackUtils;
import Jni.VideoUitls;
import VideoHandle.CmdList;
import VideoHandle.EpDraw;
import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class FFmpegUtils {
    private static final int DEFAULT_WIDTH = 480;//默认输出宽度
    private static final int DEFAULT_HEIGHT = 360;//默认输出高度

    public enum Format {
        MP3, MP4
    }

    public enum PTS {
        VIDEO, AUDIO, ALL
    }

    private FFmpegUtils() {
    }



    /**
     * 添加背景音乐
     *
     * @param videoin          视频文件
     * @param audioin          音频文件
     * @param output           输出路径
     * @param videoVolume      视频原声音音量(例:0.7为70%)
     * @param audioVolume      背景音乐音量(例:1.5为150%)
     * @param onEditorListener 回调监听
     */
    public static void music(String videoin, String audioin, String output, float videoVolume, float audioVolume, OnEditorListener onEditorListener) {
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(videoin);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int at = TrackUtils.selectAudioTrack(mediaExtractor);
        CmdList cmd = new CmdList();
        cmd.append("ffmpeg").append("-y").append("-i").append(videoin);
        if (at == -1) {
            int vt = TrackUtils.selectVideoTrack(mediaExtractor);
            float duration = (float) mediaExtractor.getTrackFormat(vt).getLong(MediaFormat.KEY_DURATION) / 1000 / 1000;
            cmd.append("-ss").append("0").append("-t").append(duration).append("-i").append(audioin).append("-acodec").append("copy").append("-vcodec").append("copy");
        } else {
            cmd.append("-i").append(audioin).append("-filter_complex")
                    .append("[0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + videoVolume + "[a0];[1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=" + audioVolume + "[a1];[a0][a1]amix=inputs=2:duration=first[aout]")
                    .append("-map").append("[aout]").append("-ac").append("2").append("-c:v")
                    .append("copy").append("-map").append("0:v:0");
        }
        cmd.append(output);
        mediaExtractor.release();
        long d = VideoUitls.getDuration(videoin);
        execCmd(cmd, d, onEditorListener);
    }

    /**
     * 给视频添加配乐
     * @param inputVideoPath
     * @param inputMusicPath
     * @param videoVolume   0~1
     * @param musicVolume   0~1
     * @param outputVideoPath
     */
    public static void addMusicForMp4(String inputVideoPath,String inputMusicPath,float videoVolume,float musicVolume,String outputVideoPath,final OnEditorListener onEditorListener){
//        ffmpeg -y -i 123.mp4 -i 5.aac -filter_complex "[0:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=1.0[a0];
//        [1:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=stereo,volume=0.5[a1];[a0][a1]amix=inputs=2:duration=first[aout]" -map "[aout]" -ac 2 -c:v copy -map 0:v:0 output.mp4
        CmdList cmd = new CmdList();
        cmd.append("ffmpeg").append("-y").append("-i").append(inputVideoPath)
                .append("-i").append(inputMusicPath)
                .append("-filter_complex")
                .append("[0:a]volume=" + videoVolume + "[a0];[1:a]volume=" + musicVolume + "[a1];[a0][a1]amix=inputs=2:duration=first[aout]")
                .append("-map")
                .append("[aout]")
                .append("-ac")
                .append("2")
                /*.append("-c:v")
                .append("-copy")*/
                .append("-map")
                .append("0:v:0")
                .append(outputVideoPath);
        long d = VideoUitls.getDuration(inputVideoPath);
        execCmd(cmd, d, onEditorListener);
    }

    /**
     * 音视频分离
     *
     * @param videoin          视频文件
     * @param out              输出文件路径
     * @param format           输出类型
     * @param onEditorListener 回调监听
     */
    public static void demuxer(String videoin, String out, EpEditor.Format format, OnEditorListener onEditorListener) {
        CmdList cmd = new CmdList();
        cmd.append("ffmpeg").append("-y").append("-i").append(videoin);
        switch (format) {
            case MP3:
                cmd.append("-vn").append("-acodec").append("libmp3lame");
                break;
            case MP4:
                cmd.append("-vcodec").append("copy").append("-an");
                break;
        }
        cmd.append(out);
        long d = VideoUitls.getDuration(videoin);
        execCmd(cmd, d, onEditorListener);
    }

    /**
     * 音视频倒放
     *
     * @param videoin          视频文件
     * @param out              输出文件路径
     * @param vr               是否视频倒放
     * @param ar               是否音频倒放
     * @param onEditorListener 回调监听
     */
    public static void reverse(String videoin, String out, boolean vr, boolean ar, OnEditorListener onEditorListener) {
        if (!vr && !ar) {
            Log.e("ffmpeg", "parameter error");
            onEditorListener.onFailure();
            return;
        }
        CmdList cmd = new CmdList();
        cmd.append("ffmpeg").append("-y").append("-i").append(videoin).append("-filter_complex");
        String filter = "";
        if (vr) {
            filter += "[0:v]reverse[v];";
        }
        if (ar) {
            filter += "[0:a]areverse[a];";
        }
        cmd.append(filter.substring(0, filter.length() - 1));
        if (vr) {
            cmd.append("-map").append("[v]");
        }
        if (ar) {
            cmd.append("-map").append("[a]");
        }
        if (ar && !vr) {
            cmd.append("-acodec").append("libmp3lame");
        }
        cmd.append("-preset").append("superfast").append(out);
        long d = VideoUitls.getDuration(videoin);
        execCmd(cmd, d, onEditorListener);
    }

    /**
     * 音视频变速
     *
     * @param videoin          音视频文件
     * @param out              输出路径
     * @param times            倍率（调整范围0.25-4）
     * @param pts              加速类型
     * @param onEditorListener 回调接口
     */
    public static void changePTS(String videoin, String out, float times, EpEditor.PTS pts, OnEditorListener onEditorListener) {
        if (times < 0.25f || times > 4.0f) {
            Log.e("ffmpeg", "times can only be 0.25 to 4");
            onEditorListener.onFailure();
            return;
        }
        CmdList cmd = new CmdList();
        cmd.append("ffmpeg").append("-y").append("-i").append(videoin);
        String t = "atempo=" + times;
        if (times < 0.5f) {
            t = "atempo=0.5,atempo=" + (times / 0.5f);
        } else if (times > 2.0f) {
            t = "atempo=2.0,atempo=" + (times / 2.0f);
        }
        Log.v("ffmpeg", "atempo:" + t);
        switch (pts) {
            case VIDEO:
                cmd.append("-filter_complex").append("[0:v]setpts=" + (1 / times) + "*PTS").append("-an");
                break;
            case AUDIO:
                cmd.append("-filter:a").append(t);
                break;
            case ALL:
                cmd.append("-filter_complex").append("[0:v]setpts=" + (1 / times) + "*PTS[v];[0:a]" + t + "[a]")
                        .append("-map").append("[v]").append("-map").append("[a]");
                break;
        }
        cmd.append("-preset").append("superfast").append(out);
        long d = VideoUitls.getDuration(videoin);
        double dd = d / times;
        long ddd = (long) dd;
        execCmd(cmd, ddd, onEditorListener);
    }

    /**
     * 视频转图片
     *
     * @param videoin			音视频文件
     * @param out				输出路径
     * @param w					输出图片宽度
     * @param h					输出图片高度
     * @param rate				每秒视频生成图片数
     * @param onEditorListener	回调接口
     */
    public static void video2pic(String videoin, String out, int w, int h, float rate, OnEditorListener onEditorListener) {
        if (w <= 0 || h <= 0) {
            Log.e("ffmpeg", "width and height must greater than 0");
            onEditorListener.onFailure();
            return;
        }
        if(rate <= 0){
            Log.e("ffmpeg", "rate must greater than 0");
            onEditorListener.onFailure();
            return;
        }
        CmdList cmd = new CmdList();
        cmd.append("ffmpeg").append("-y").append("-i").append(videoin)
                .append("-r").append(rate).append("-s").append(w+"x"+h).append("-q:v").append(2)
                .append("-f").append("image2").append("-preset").append("superfast").append(out);
        long d = VideoUitls.getDuration(videoin);
        execCmd(cmd, d, onEditorListener);
    }

    /**
     * 图片转视频
     *
     * @param videoin			视频文件
     * @param out				输出路径
     * @param w					输出视频宽度
     * @param h					输出视频高度
     * @param rate				输出视频帧率
     * @param onEditorListener	回调接口
     */
    public static void pic2video(String videoin, String out, int w, int h, float rate, OnEditorListener onEditorListener) {
        if (w < 0 || h < 0) {
            Log.e("ffmpeg", "width and height must greater than 0");
            onEditorListener.onFailure();
            return;
        }
        if(rate <= 0){
            Log.e("ffmpeg", "rate must greater than 0");
            onEditorListener.onFailure();
            return;
        }
        CmdList cmd = new CmdList();
        cmd.append("ffmpeg").append("-y").append("-f").append("image2").append("-i").append(videoin)
                .append("-vcodec").append("libx264")
                .append("-r").append(rate);
//				.append("-b").append("10M");
        if(w > 0 && h > 0) {
            cmd.append("-s").append(w + "x" + h);
        }
        cmd.append(out);
        long d = VideoUitls.getDuration(videoin);
        execCmd(cmd, d, onEditorListener);
    }


    /**
     * 输出选项设置
     */
    public static class OutputOption {
        static final int ONE_TO_ONE = 1;// 1:1
        static final int FOUR_TO_THREE = 2;// 4:3
        static final int SIXTEEN_TO_NINE = 3;// 16:9
        static final int NINE_TO_SIXTEEN = 4;// 9:16
        static final int THREE_TO_FOUR = 5;// 3:4

        String outPath;//输出路径
        public int frameRate = 0;//帧率
        public int bitRate = 0;//比特率(一般设置10M)
        public String outFormat = "";//输出格式(目前暂时只支持mp4,x264,mp3,gif)
        private int width = 0;//输出宽度
        private int height = 0;//输出高度
        private int sar = 6;//输出宽高比

        public OutputOption(String outPath) {
            this.outPath = outPath;
        }

        /**
         * 获取宽高比
         *
         * @return 1
         */
        public String getSar() {
            String res;
            switch (sar) {
                case ONE_TO_ONE:
                    res = "1/1";
                    break;
                case FOUR_TO_THREE:
                    res = "4/3";
                    break;
                case THREE_TO_FOUR:
                    res = "3/4";
                    break;
                case SIXTEEN_TO_NINE:
                    res = "16/9";
                    break;
                case NINE_TO_SIXTEEN:
                    res = "9/16";
                    break;
                default:
                    res = width + "/" + height;
                    break;
            }
            return res;
        }

        public void setSar(int sar) {
            this.sar = sar;
        }

        /**
         * 获取输出信息
         *
         * @return 1
         */
        String getOutputInfo() {
            StringBuilder res = new StringBuilder();
            if (frameRate != 0) {
                res.append(" -r ").append(frameRate);
            }
            if (bitRate != 0) {
                res.append(" -b ").append(bitRate).append("M");
            }
            if (!outFormat.isEmpty()) {
                res.append(" -f ").append(outFormat);
            }
            return res.toString();
        }

        /**
         * 设置宽度
         *
         * @param width 宽
         */
        public void setWidth(int width) {
            if (width % 2 != 0) width -= 1;
            this.width = width;
        }

        /**
         * 设置高度
         *
         * @param height 高
         */
        public void setHeight(int height) {
            if (height % 2 != 0) height -= 1;
            this.height = height;
        }
    }

    /**
     * 开始处理
     *
     * @param cmd              命令
     * @param duration         视频时长（单位微秒）
     * @param onEditorListener 回调接口
     */
    public static void execCmd(String cmd, long duration, final OnEditorListener onEditorListener) {
        cmd = "ffmpeg " + cmd;
        String[] cmds = cmd.split(" ");
        FFmpegCmd.exec(cmds, duration, new OnEditorListener() {
            @Override
            public void onSuccess() {
                onEditorListener.onSuccess();
            }

            @Override
            public void onFailure() {
                onEditorListener.onFailure();
            }

            @Override
            public void onProgress(final float progress) {
                onEditorListener.onProgress(progress);
            }
        });
    }

    /**
     * 开始处理
     *
     * @param cmd              命令
     * @param duration         视频时长（单位微秒）
     * @param onEditorListener 回调接口
     */
    private static void execCmd(CmdList cmd, long duration, final OnEditorListener onEditorListener) {
        String[] cmds = cmd.toArray(new String[cmd.size()]);
        StringBuffer sb = new StringBuffer();
        for (String ss : cmds) {
            sb.append(ss).append(" ");
        }
        Log.v("使用的命令为：", "cmd: = " + sb.toString());
        FFmpegCmd.exec(cmds, duration, new OnEditorListener() {
            @Override
            public void onSuccess() {
                onEditorListener.onSuccess();
            }

            @Override
            public void onFailure() {
                onEditorListener.onFailure();
            }

            @Override
            public void onProgress(final float progress) {
                onEditorListener.onProgress(progress);
            }
        });
    }


}
