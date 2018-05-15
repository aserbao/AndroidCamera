package com.aserbao.androidcustomcamera.whole.record.beans;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;


import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * description:
 * Created by aserbao on 2017/12/4.
 */


public class MediaObject implements Serializable{
    /** 获取所有分块 */
    private LinkedList<MediaPart> mMediaList = new LinkedList<MediaPart>();
    private LinkedList<String> paths = new LinkedList<>();
    public MediaPart mMediaPart;
    /**删除分块*/
    public void removePart(MediaPart part, boolean deleteFile) {
        if (mMediaList != null)
            mMediaList.remove(part);
        if (part != null) {
            // 删除文件
            if (deleteFile) {
                String f = part.mediaPath;
                File file = new File(f);
                if (f != null && f.length() > 0 && file.exists() && !file.isDirectory()) {
                    file.delete();
                    paths.removeLast();
                }
            }
            mMediaList.remove(part);
        }
    }
    public MediaPart buildMediaPart(String videoPath){
        mMediaPart = new MediaPart();
        mMediaPart.mediaPath = videoPath;
        mMediaPart.duration = 0;
        mMediaPart.startTime = System.currentTimeMillis();
        mMediaList.add(mMediaPart);
        paths.add(videoPath);
        return mMediaPart;
    }

    public LinkedList<MediaPart> getMedaParts() {
        return mMediaList;
    }
    public int getListCount(){
        return mMediaList.size();
    }
    public LinkedList<String> getPaths(){
        return paths;
    }
    public int getDuration() {
        int duration = 0;
        if (mMediaList != null) {
            for (int i = 0; i < mMediaList.size(); i++) {
                duration += mMediaList.get(i).getDuration();
                Log.e("getDuration", "getDuration: "  + mMediaList.get(i).getDuration());
            }
            Log.e("getDuration", "getDuration: " + duration);
        }
        return duration;
    }
    public void stopRecord(Context context, MediaObject mediaObject){
        if (mediaObject != null) {
            MediaPart part = mediaObject.getCurrentPart();
            if (part != null ) {
                MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
                mediaMetadata.setDataSource(context, Uri.parse(part.getMediaPath()));
                int mVideoDuration = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                part.duration = mVideoDuration;
            }
        }
    }

    public MediaPart getCurrentPart() {
        /*if (mMediaPart != null)
            return mMediaPart;*/
        if (mMediaList != null && mMediaList.size() > 0)
            mMediaPart = mMediaList.get(mMediaList.size() - 1);
        return mMediaPart;
    }
    public class MediaPart implements Serializable{
        public String mediaPath;
        public int duration;

        public long startTime;
        public boolean remove;
        public String getMediaPath(){
            return mediaPath;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }
        public int getDuration() {
            return duration > 0 ? duration : (int) (System.currentTimeMillis() - startTime);
        }
    }



}
