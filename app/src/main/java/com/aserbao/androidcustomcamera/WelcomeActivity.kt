package com.aserbao.androidcustomcamera

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.util.Log
import com.aserbao.androidcustomcamera.base.activity.BaseActivity
import com.aserbao.androidcustomcamera.utils.CheckPermissionUtil
import com.aserbao.androidcustomcamera.whole.record.RecorderActivity
import kotlinx.android.synthetic.main.activity_welcome.*


class WelcomeActivity : BaseActivity() {

    override fun setLayoutId(): Int {
        return R.layout.activity_welcome
    }

    override fun initView() {
        super.initView()
    }

    fun exectorAnimator(){
        val valuesHolder0 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f)
        val valuesHolder1 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f)
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(bgIV, valuesHolder0, valuesHolder1)
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                startActivity(Intent(this@WelcomeActivity, RecorderActivity::class.java))
                finish()
            }
        })
        objectAnimator.setDuration(2000).start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.e("TAG", "onRequestPermissionsResult: $requestCode")
        if(CheckPermissionUtil.isCameraGranted()) {
            exectorAnimator()
        }else{
            startRequestPermission()
        }
    }
}