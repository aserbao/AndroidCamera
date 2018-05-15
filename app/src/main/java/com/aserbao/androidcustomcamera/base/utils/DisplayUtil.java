package com.aserbao.androidcustomcamera.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Imitation by Abybxc on 16/8/4 21:18
 * weixin:aserbao
 * <p/>
 * 屏幕像素转换工具类
 */
public class DisplayUtil
{

    public static int px2dp(Context context, float pxValue)
    {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dipValue)
    {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int dipToPx(final Context ctx, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, ctx.getResources().getDisplayMetrics());
    }
    public static int dipToPx(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, Resources.getSystem().getDisplayMetrics());
    }
    public static int px2sp(Context context, float pxValue)
    {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue)
    {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Context context)
    {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context)
    {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    public static float getDisplayDensity(Context context)
    {

        if (context == null)
        {
            return -1;
        }
        return context.getResources().getDisplayMetrics().density;
    }
}
