package com.aserbao.androidcustomcamera.whole.createVideoByVoice.interfaces;

/**
 * 功能:
 *
 * @author aserbao
 * @date : On 2019/1/8 4:23 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.whole.createVideoByVoice.interfaces
 * @Copyright: 个人版权所有
 */
public interface IEncoderVideoCallBackListener {
    void success(String outputMeidaPath, float finalMediaTime);
    void failed();
}
