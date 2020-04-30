package com.aserbao.androidcustomcamera.base.utils;

import com.aserbao.androidcustomcamera.R;

import java.util.Random;

/**
 * description:
 * Created by aserbao on 2018/1/25.
 */


public class ConstantUtils {

    public static int getDrawable(){
        return drawables[new Random().nextInt(drawables.length)];
    }
    public static int[] drawables = {
        R.drawable.emoji_00,
        R.drawable.emoji_01,
        R.drawable.emoji_02,
        R.drawable.emoji_03,
        R.drawable.emoji_04,
    };
}
