package com.aserbao.androidcustomcamera.blocks.ffmpeg.beans;

public class WaterFilter {
    String videoPath;
    String picturePath;

    public WaterFilter(String videoPath, String picturePath) {
        this.videoPath = videoPath;
        this.picturePath = picturePath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
