package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : Administrator (Jacket)
 *     e-mail : 378315764@qq.com
 *     time   : 2018/01/31
 *     desc   :
 *     version: 3.2
 * </pre>
 */

public class PopBubbleView {
    private String TAG = PopBubbleView.class.getSimpleName();

    private Context context;
    private PopupWindow popupWindow;
    private View popupWindowView;
    private String reportType;

    public PopBubbleView(Context context) {
        this.context = context;
        initPopupWindow();
    }

    /**
     * 初始化
     */
    public void initPopupWindow() {
        if (popupWindowView != null) {
            popupWindow.dismiss();
        }
        popupWindowView = LayoutInflater.from(context).inflate(R.layout.pop_bubble_view, null);
        ButterKnife.bind(this, popupWindowView);
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setAnimationStyle(R.style.popup_window_scale);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 菜单背景色。加了一点透明度
//        ColorDrawable dw = new ColorDrawable(0xddffffff);
//        popupWindow.setBackgroundDrawable(dw);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());      //解决部分机型按back键无法退出popupwindow


        // 设置背景半透明
//        backgroundAlpha(0.7f);

        popupWindow.setOnDismissListener(new popupDismissListener());

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

    @OnClick({R.id.ll_bubble_one, R.id.ll_bubble_two, R.id.ll_bubble_three, R.id.ll_bubble_four, R.id.ll_bubble_five, R.id.ll_bubble_six, R.id.ll_bubble_seven, R.id.ll_bubble_eight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_bubble_one:
                if (bubbleSelectListener != null) {
                    bubbleSelectListener.bubbleSelect(0);
                }
                dimss();
                break;
            case R.id.ll_bubble_two:
                if (bubbleSelectListener != null) {
                    bubbleSelectListener.bubbleSelect(1);
                }
                dimss();
                break;
            case R.id.ll_bubble_three:
                if (bubbleSelectListener != null) {
                    bubbleSelectListener.bubbleSelect(2);
                }
                dimss();
                break;
            case R.id.ll_bubble_four:
                if (bubbleSelectListener != null) {
                    bubbleSelectListener.bubbleSelect(3);
                }
                dimss();
                break;
            case R.id.ll_bubble_five:
                if (bubbleSelectListener != null) {
                    bubbleSelectListener.bubbleSelect(4);
                }
                dimss();
                break;
            case R.id.ll_bubble_six:
                if (bubbleSelectListener != null) {
                    bubbleSelectListener.bubbleSelect(5);
                }
                dimss();
                break;
            case R.id.ll_bubble_seven:
                if (bubbleSelectListener != null) {
                    bubbleSelectListener.bubbleSelect(6);
                }
                dimss();
                break;
            case R.id.ll_bubble_eight:
                if (bubbleSelectListener != null) {
                    bubbleSelectListener.bubbleSelect(7);
                }
                dimss();
                break;
        }
    }


    class popupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    public void dimss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }


    public void show() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(LayoutInflater.from(context).inflate(R.layout.activity_update_personal_info, null),
                    Gravity.BOTTOM, 0, 0);
        }
    }

    public interface BubbleSelectListener {
        void bubbleSelect(int bubbleIndex);
    }

    public BubbleSelectListener bubbleSelectListener;

    public void setBubbleSelectListener(BubbleSelectListener bubbleSelectListener) {
        this.bubbleSelectListener = bubbleSelectListener;
    }
}
