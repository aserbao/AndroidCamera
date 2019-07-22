package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.whole.editVideo.adpaters.PasterAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     author : Administrator (Jacket)
 *     e-mail : 378315764@qq.com
 *     time   : 2018/01/31
 *     desc   :
 *     version: 3.2
 * </pre>
 */

public class PopPasterView implements PasterAdapter.PasterItemSelectListener {
    private String TAG = PopPasterView.class.getSimpleName();
    private Context context;
    private PopupWindow popupWindow;
    private View popupWindowView;
    private String reportType;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private int[] images = new int[]{
            R.drawable.aini, R.drawable.dengliao, R.drawable.baituole, R.drawable.burangwo, R.drawable.bufuhanzhe, R.drawable.nizabushagntian, R.drawable.zan, R.drawable.mudengkoudai, R.drawable.buyue,  R.drawable.nizaidouwo, R.drawable.gandepiaoliang, R.drawable.xiase
    };

    public PopPasterView(Context context) {
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
        popupWindowView = LayoutInflater.from(context).inflate(R.layout.pop_paster_view, null);
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

        initView();
    }

    private void initView() {
        PasterAdapter pasterAdapter = new PasterAdapter(context, images);
        pasterAdapter.setPasterItemSelectListener(this);
        recyclerView.setAdapter(pasterAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cb_release_pornographic_content:    //发布色情内容
                    Log.e(TAG, " 发布色情内容");
                    reportType = "0";
                    break;
                case R.id.cb_issue_violence_content:           //发布暴力内容
                    Log.e(TAG, "发布暴力内容");
                    reportType = "1";
                    break;
                case R.id.cb_harass:                             //被骚扰
                    Log.e(TAG, "被骚扰");
                    reportType = "2";
                    break;
                case R.id.btn_submit:                            //提交
                    Log.e(TAG, "提交");
                    if (reportType == null || reportType.equals("")) {
                        Toast.makeText(context, "请至少选择一个", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
            }
        }
    };


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

    public void dimss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    ;

    public void show() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(LayoutInflater.from(context).inflate(R.layout.activity_update_personal_info, null),
                    Gravity.BOTTOM, 0, 0);
        }
    }

    public interface PasterSelectListener {
        void pasterSelect(int resourceId, int gifId);
    }

    PasterSelectListener pasterSelectListener;

    public void setPasterSelectListener(PasterSelectListener pasterSelectListener) {
        this.pasterSelectListener = pasterSelectListener;
    }


    @Override
    public void pasterItemSelect(int resourseId, int gifId) {
        pasterSelectListener.pasterSelect(resourseId, gifId);
        dimss();
    }
}
