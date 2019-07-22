package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by msi- on 2018/2/7.
 */

public class StickInfoImageView extends ImageView {
    private long startTime;
    private long endTime;



    public StickInfoImageView(Context context) {
        super(context);
    }

    public StickInfoImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickInfoImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
}
