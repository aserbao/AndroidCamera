package com.aserbao.androidcustomcamera

import android.view.View
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean
import com.aserbao.androidcustomcamera.blocks.BlocksActivity
import com.aserbao.androidcustomcamera.blocks.ffmpeg.FFmpegActivity
import com.aserbao.androidcustomcamera.blocks.others.changeVoice.ChangeVoiceActivity
import com.aserbao.androidcustomcamera.whole.WholeActivity

class HomeActivity : RVBaseActivity() {
    override fun initGetData() {
        mBaseRecyclerBeen.add(BaseRecyclerBean("每个功能点单独代码实现", BlocksActivity::class.java))
        mBaseRecyclerBeen.add(BaseRecyclerBean("所有功能点整合代码实现", WholeActivity::class.java))
        mBaseRecyclerBeen.add(BaseRecyclerBean("当前调用界面", FFmpegActivity::class.java))
    }

    override fun itemClickBack(view: View, position: Int, isLongClick: Boolean, comeFrom: Int) {}
}