package com.aserbao.androidcustomcamera.base.pop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.MyApplication;
import com.aserbao.androidcustomcamera.whole.record.ui.ThumbnailCountDownTimeView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * description:
 * Created by aserbao on 2018/5/15.
 */
public class PopupManager {
    private Context mContext;
    public String mV = "30";
    private boolean isClick = false;

    public PopupManager(Context context) {
        mContext = context;
    }
    //显示倒计时
    public void showCountDown(Resources res, int mStartTime, final SelTimeBackListener pop) {
        View showCountDown = LayoutInflater.from(mContext).inflate(R.layout.pop_window_count_down, null);
        final PopupWindow mShowCountDownPW = new PopupWindow(showCountDown.getRootView(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mShowCountDownPW.setOutsideTouchable(true);
        mShowCountDownPW.setFocusable(true);
        mShowCountDownPW.setAnimationStyle(R.style.expression_dialog_anim_style);
        mShowCountDownPW.setBackgroundDrawable(new BitmapDrawable());
        mShowCountDownPW.showAtLocation(showCountDown.getRootView(), Gravity.BOTTOM, 0, 0);
        final ThumbnailCountDownTimeView thumbnailCountDownTimeView = (ThumbnailCountDownTimeView) showCountDown.findViewById(R.id.thumb_count_view);
        final TextView selTimeTv = (TextView) showCountDown.findViewById(R.id.sel_time_tv);
        selTimeTv.setText("30s");
        int min = mStartTime * (MyApplication.screenWidth - (int) res.getDimension(R.dimen.dp20)) / 30;
        selTimeTv.layout(min, 0, selTimeTv.getHeight(), selTimeTv.getWidth());
        selTimeTv.setTranslationX((MyApplication.screenWidth - (int) res.getDimension(R.dimen.dp40)));
        thumbnailCountDownTimeView.setMinWidth(min);
        final DecimalFormat fnum = new DecimalFormat("##0.0");
        mShowCountDownPW.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                pop.selTime(mV, isClick);
            }
        });
        thumbnailCountDownTimeView.setOnScrollBorderListener(new ThumbnailCountDownTimeView.OnScrollBorderListener() {
            @Override
            public void OnScrollBorder(float start, float end) {
                selTimeTv.setTranslationX(start - 10);
                mV = fnum.format((end * 30 / thumbnailCountDownTimeView.getWidth()));
                selTimeTv.setText(mV + "s");
            }

            @Override
            public void onScrollStateChange() {

            }
        });
        showCountDown.findViewById(R.id.count_down_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClick = true;
//                pop.selTime(mV,false,isClick);
                mShowCountDownPW.dismiss();
            }
        });
    }

    //显示选择美颜等级
    private int mCuurLevel = 0;
    private int[] mInts = {R.id.none_iv, R.id.one_tv, R.id.two_tv, R.id.three_tv, R.id.four_tv, R.id.five_tv};
    private List<View> mBeautyList = new ArrayList<>();
    public void showBeautyLevel(int level, final SelBeautyLevel selBeautyLevel) {
        mCuurLevel = level;
        View beautyLevel = LayoutInflater.from(mContext).inflate(R.layout.pop_window_beauty_level, null);
        ButterKnife.bind(this, beautyLevel);
        final PopupWindow mBeautyLevelPW = new PopupWindow(beautyLevel.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mBeautyLevelPW.setOutsideTouchable(true);
        mBeautyLevelPW.setFocusable(true);
        mBeautyLevelPW.setAnimationStyle(R.style.expression_dialog_anim_style);
        mBeautyLevelPW.setBackgroundDrawable(new BitmapDrawable());
        mBeautyLevelPW.showAtLocation(beautyLevel.getRootView(), Gravity.BOTTOM, 0, 0);
        for (int i = 0; i < mInts.length; i++) {
            View view = beautyLevel.findViewById(mInts[i]);
            view.setTag(i);
            if (view != null) {
                mBeautyList.add(view);
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickFilter(((int) v.getTag()));
                }
            });
        }
        clickFilter(level);
        mBeautyLevelPW.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                selBeautyLevel.selBeautyLevel(mCuurLevel);
            }
        });
    }
    public void clickFilter(int position){
        mCuurLevel = position;
        for (int i = 0; i < mBeautyList.size(); i++) {
            View view = mBeautyList.get(i);
            if(i == position){
                if(i == 0) {
                    ((ImageView) view).setImageResource(R.drawable.bigicon_no_light);
                }else{
                    ((TextView) view).setTextColor(Color.parseColor("#ffffff"));
                }
                view.setBackgroundResource(R.drawable.tv_circle_white40_bg);
            }else{
                if(i == 0){
                    ((ImageView) view).setImageResource(R.drawable.bigicon_no);
                }else{
                    ((TextView) view).setTextColor(Color.parseColor("#7fffffff"));
                }
                view.setBackgroundResource(R.drawable.tv_circle_white10_bg);
            }
        }
    }


    public interface SelTimeBackListener {
        void selTime(String selTime, boolean isDismiss);
    }
    public interface SelBeautyLevel{
        void selBeautyLevel(int level);
    }
}
