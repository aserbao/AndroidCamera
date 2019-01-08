package com.aserbao.androidcustomcamera.whole.createVideoByVoice;

import com.aserbao.androidcustomcamera.whole.createVideoByVoice.interfaces.IEncoderVideoCallBackListener;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.interfaces.IMuxerVideoCallBackListener;

import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;

/**
 * 功能:
 *
 * @author aserbao
 * @date : On 2019/1/8 4:25 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.whole.createVideoByVoice
 * @Copyright: 个人版权所有
 */
public class MuxerVoiceAndVideo {
    private IMuxerVideoCallBackListener mIMuxerVideoCallBackListener;

    public MuxerVoiceAndVideo(IMuxerVideoCallBackListener iMuxerVideoCallBackListener) {
        mIMuxerVideoCallBackListener = iMuxerVideoCallBackListener;
    }

    public void startMuxer(String inputVideoPath, String inputMusicPath, float musicTime, String outputVideoPath){
        String cmd = "-y -i "+ inputVideoPath + " -ss 0 -t "+ musicTime + " -i "+ inputMusicPath + " -acodec copy -vcodec copy "+ outputVideoPath;
        EpEditor.execCmd(cmd, 10000,new OnEditorListener() {
            @Override
            public void onSuccess() {
                mIMuxerVideoCallBackListener.success();
            }

            @Override
            public void onFailure() {
                mIMuxerVideoCallBackListener.failed();
            }

            @Override
            public void onProgress(float v) {
            }
        });
    }
}
