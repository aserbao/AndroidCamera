package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.List;

/**
 * <pre>
 *     author : Administrator (Jacket)
 *     e-mail : 378315764@qq.com
 *     time   : 2018/03/05
 *     desc   :
 *     version: 3.2
 * </pre>
 */

public class DynamicImageView extends ImageView {
    private long startTime;
    private long endTime;
    private int frameIndex;  //播放gif图的第几帧
    private List<Bitmap> bitmaps;

    public DynamicImageView(Context context) {
        super(context);
    }

    public DynamicImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(int frameIndex) {
        if (bitmaps != null && bitmaps.size() > 0) {
            this.frameIndex = frameIndex % bitmaps.size();
        }
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }
}
