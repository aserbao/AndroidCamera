package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aserbao.androidcustomcamera.R;

/**
 * 从顶部出来的PopupWindow
 */

public class PopTopTipWindow {

    Context context;
    private PopupWindow popupWindow;
    View popupWindowView;
    private String content;
    private TextView tvContent;
    private int layout = -1;

    public PopTopTipWindow(Context context) {
        this.context = context;
        initPopupWindow();
    }

    public PopTopTipWindow(Context context, String content) {
        this.context = context;
        this.content = content;
        initPopupWindow();
    }

    public PopTopTipWindow(Context context, String content, int layout) {
        this.context = context;
        this.content = content;
        this.layout = layout;
    }

    /**
     * 初始化
     */
    public void initPopupWindow() {
        if (popupWindowView != null) {
            popupWindow.dismiss();
        }
        popupWindowView = LayoutInflater.from(context).inflate(R.layout.pop_phone_tip, null);
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.TopSelectAnimationShow);
        // 菜单背景色。加了一点透明度
//        ColorDrawable dw = new ColorDrawable(0xddffffff);
//        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());      //解决部分机型按back键无法退出popupwindow

        // 设置背景半透明
        backgroundAlpha(0.7f);

//        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopTopTipWindow.popupDismissListener());

        popupWindowView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*
                 * if( popupWindow!=null && popupWindow.isShowing()){
                 * popupWindow.dismiss(); popupWindow=null; }
                 */
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });


        if (content != null && !content.equals("")) {
            tvContent = (TextView) popupWindowView.findViewById(R.id.tv_content);
            tvContent.setText(content);
        }

        //TODO 注意：这里的 R.layout.activity_main，不是固定的。你想让这个popupwindow盖在哪个界面上面。就写哪个界面的布局。这里以主界面为例

            popupWindow.showAtLocation(LayoutInflater.from(context).inflate(layout, null),
                    Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);


    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    /**
     * 处理点击事件
     */
    private void dealWithSelect() {
        //点击了关闭图标（右上角图标）
//        popupWindowView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dimss();
//            }
//        });


    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        ((Activity) context).getWindow().setAttributes(lp);
    }

    class popupDismissListener implements PopupWindow.OnDismissListener {

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }
    public void delayMiss(int mill){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            }
        },mill);
    }

    public void dimss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }
}
