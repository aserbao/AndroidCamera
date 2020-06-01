package com.aserbao.androidcustomcamera.whole.record.beans;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;


import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.VIDEOTEMP;


/**
 * description:
 * Created by aserbao on 2017/12/4.
 */


public class MediaObject implements Serializable{
    private static final String TAG = "MediaObject";
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
                String s = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int mVideoDuration = 0;
                try {
                    mVideoDuration = Integer.parseInt(s);
                    part.duration = mVideoDuration;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Log.e(TAG, "stopRecord: 是不是int型，打个日志自己查看一下" );
                }
            }
        }
    }

    public MediaPart getCurrentPart() {
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

    //=====================================视频合成=============
    public String mergeVideo() {
        long begin = System.currentTimeMillis();
        List<Movie> movies = new ArrayList<>();
        String filePath = "";
        if(paths.size() == 1){
            return paths.get(0);
        }
        try {
            for (int i = 0; i < paths.size(); i++) {
                if(paths != null  && paths.get(i) != null) {
                    Movie movie = MovieCreator.build(paths.get(i));
                    movies.add(movie);
                }
            }
            List<Track> videoTracks = new ArrayList<>();
            List<Track> audioTracks = new ArrayList<>();
            for (Movie movie : movies) {
                for (Track track : movie.getTracks()) {
                    if ("vide".equals(track.getHandler())) {
                        videoTracks.add(track);
                    }
                    if ("soun".equals(track.getHandler())) {
                        audioTracks.add(track);
                    }
                }
            }
            Movie result = new Movie();
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
            }
            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            Container container = new DefaultMp4Builder().build(result);
            filePath = getRecorderPath();
//            FileChannel fc = new FileOutputStream(filePath).getChannel();
            FileChannel fc = new RandomAccessFile(String.format(filePath), "rw").getChannel();
            container.writeContainer(fc);
            fc.close();
        }  catch (Exception e) {
            e.printStackTrace();
            return paths.get(0);
        }
        long end = System.currentTimeMillis();
        Log.e("test", "merge use time:" + (end - begin));
//        deteleVideoPath();
        return filePath;
    }

    private void deteleVideoPath() {
        for (int i = 0; i < paths.size(); i++) {
            new File(paths.get(i)).delete();
        }
    }
    private String getRecorderPath() {
        File file = new File(VIDEOTEMP);
        if (!file.exists()) {
            file.mkdirs();
        }
        String path = file.getPath() + "/" + System.currentTimeMillis() + ".mp4";
        Log.e("test", "path=" + path);
        return path;
    }


}
