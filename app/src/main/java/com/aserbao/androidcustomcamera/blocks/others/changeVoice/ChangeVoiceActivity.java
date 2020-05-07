package com.aserbao.androidcustomcamera.blocks.others.changeVoice;

import android.view.View;

import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.utils.VoiceUtils;

import org.fmod.FMOD;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChangeVoiceActivity extends RVBaseActivity {
    private ExecutorService fixedThreadPool;
    private PlayerThread playerThread;
    private String path = "file:///android_asset/five.mp3";
    private int type;

    @Override
    protected void initGetData() {
        mBaseRecyclerBeen.add(new BaseRecyclerBean("原声",0));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("萝莉",1));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("大叔",2));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("惊悚",3));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("搞怪",4));
        mBaseRecyclerBeen.add(new BaseRecyclerBean("空灵",5));
        FMOD.init(this);
        fixedThreadPool = Executors.newFixedThreadPool(1);
    }


    @Override
    public void itemClickBack(View view, int position, boolean isLongClick, int comeFrom) {
        switch (position){
            case 0:
                type = VoiceUtils.MODE_NORMAL;
                break;
            case 1:
                type = VoiceUtils.MODE_LUOLI;
                break;
            case 2:
                type = VoiceUtils.MODE_DASHU;
                break;
            case 3:
                type = VoiceUtils.MODE_JINGSONG;
                break;
            case 4:
                type = VoiceUtils.MODE_GAOGUAI;
                break;
            case 5:
                type = VoiceUtils.MODE_KONGLING;
                break;
        }
//        VoiceUtils.fix(path, type);
        playerThread = new PlayerThread();
        fixedThreadPool.execute(playerThread);
    }

    class PlayerThread implements Runnable {
        @Override
        public void run() {
            VoiceUtils.fix(path, type);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FMOD.close();
    }
}
