package com.aserbao.androidcustomcamera.blocks.others.changeHue;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import javax.*;

import com.aserbao.androidcustomcamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeHueActivity extends AppCompatActivity {

    @BindView(R.id.change_hue_iv)
    ImageView mChangeHueIv;
    @BindView(R.id.change_hue_tv)
    TextView mChangeHueTv;
    @BindView(R.id.change_hue_sb)
    SeekBar mChangeHueSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_hue);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mChangeHueSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.change_hue);
//                Bitmap hue = hue(bitmap, progress);
//                mChangeHueIv.setImageBitmap(hue);
                mChangeHueTv.setText(String.valueOf(progress));
                changeHue(progress);
//                ColorFilter colorFilter = ColorFilterGenerator.adjustHue(progress);
//                mChangeHueIv.setColorFilter(colorFilter);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public static Bitmap hue(Bitmap bitmap, float hue) {
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        final int width = newBitmap.getWidth();
        final int height = newBitmap.getHeight();
        float [] hsv = new float[3];

        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int pixel = newBitmap.getPixel(x,y);
                Color.colorToHSV(pixel,hsv);
                hsv[0] = hue;
                newBitmap.setPixel(x,y,Color.HSVToColor(Color.alpha(pixel),hsv));
            }
        }

//        bitmap.recycle();
//        bitmap = null;

        return newBitmap;
    }


    public void changeHue(int progress){
        float[] hsbVals = new float[3];
        int inputColor = Color.parseColor("#FFF757");
        Color.colorToHSV(inputColor,hsbVals);
        float v = (float) progress / (float) 360;
        hsbVals[0] = (float) progress;
        int color = Color.HSVToColor(hsbVals);
        mChangeHueTv.setBackgroundColor(color);
    }
}
