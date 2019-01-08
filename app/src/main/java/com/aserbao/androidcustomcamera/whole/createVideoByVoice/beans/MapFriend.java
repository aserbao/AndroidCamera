package com.aserbao.androidcustomcamera.whole.createVideoByVoice.beans;

import android.graphics.Bitmap;

/**
 * 功能:
 *
 * @author aserbao
 * @date : On 2019/1/8 5:59 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.whole.createVideoByVoice.beans
 * @Copyright: 个人版权所有
 */
public class MapFriend {
    private float screenX;
    private float screenY;
    private Bitmap friendHeadBitmap;

    /**
     * @param screenX
     * @param screenY
     * @param friendHeadBitmap
     */
    public MapFriend(float screenX, float screenY, Bitmap friendHeadBitmap) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.friendHeadBitmap = friendHeadBitmap;
    }
}
