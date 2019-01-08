package com.aserbao.androidcustomcamera.whole.createVideoByVoice.frameData;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 功能:基类
 * @author aserbao
 * @date : On 2019/1/8 5:37 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.whole.createVideoByVoice.frameData
 * @Copyright: 个人版权所有
 */
public abstract class BaseFrameData {
    abstract void onDraw(Canvas canvas,Paint paint,float volume,int hueColor);
}
