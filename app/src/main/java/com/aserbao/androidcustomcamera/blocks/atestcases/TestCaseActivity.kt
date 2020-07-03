package com.aserbao.androidcustomcamera.blocks.atestcases

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.aserbao.androidcustomcamera.base.activity.RVBaseActivity
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean

/**
 * https://android.googlesource.com/platform/cts/+/b04c81bfc2761b21293f9c095da38c757e570fd3/tests/tests/media/src/android/media
 */
class TestCaseActivity : RVBaseActivity() {
    override fun itemClickBack(view: View?, position: Int, isLongClick: Boolean, comeFrom: Int) {
        when(position){
//            0 -> EncodeDecodeTest
        }
    }
    override fun initGetData() {
        mBaseRecyclerBeen.add(BaseRecyclerBean("EncodeDecodeTest",0))
    }

}