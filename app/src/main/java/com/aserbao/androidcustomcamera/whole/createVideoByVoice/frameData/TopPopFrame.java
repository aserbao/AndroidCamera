package com.aserbao.androidcustomcamera.whole.createVideoByVoice.frameData;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 功能:最上层歌词显示框
 * @author aserbao
 * @date : On 2019/1/8 6:09 PM
 * @email: 1142803753@qq.com
 * @project:AndroidCamera
 * @package:com.aserbao.androidcustomcamera.whole.createVideoByVoice.frameData
 * @Copyright: 个人版权所有
 */
public class TopPopFrame extends BaseFrameData{

    int roundMargain = 60;
    int roundHeight = 300;
    int roundRadius = 25;
    int roundLineWidth = 5;
    private String todayYyyyMmDd;
    private String lyricContent;

    public TopPopFrame(String lyricContent) {
        this.lyricContent = lyricContent;
        todayYyyyMmDd = todayYyyyMmDd();
    }

    @Override
    void onDraw(Canvas canvas, Paint paint, float volume, int hueColor) {
        int width = canvas.getWidth();
        paint.setStyle(Paint.Style.FILL);//充满
        paint.setAntiAlias(true);// 设置画笔的锯齿效果
        RectF roundRect1 = new RectF(roundMargain - roundLineWidth,roundMargain - roundLineWidth,width - roundMargain + roundLineWidth,roundHeight + roundMargain + roundLineWidth);
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(roundRect1,roundRadius,roundRadius,paint);
        paint.setColor(hueColor);
        RectF roundRect2 = new RectF(roundMargain,roundMargain,width - roundMargain,roundHeight + roundMargain);
        canvas.drawRoundRect(roundRect2,roundRadius,roundRadius,paint);

        int timeMargain = roundMargain + 50;

        paint.setTextSize(10);
        paint.setColor(Color.BLACK);
        String sTime = todayYyyyMmDd + "  "+ todayMMSS();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(40);
        paint.setColor(Color.BLACK);
        canvas.drawText(sTime,width/2,timeMargain,paint);

        int soundMargain = timeMargain + 80;
        String soundTime = "party 是我家";
        String soundTime2 = "party party 是我家";
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(60);
        canvas.drawText(soundTime,width/2,soundMargain,paint);
        canvas.drawText(soundTime2,width/2,soundMargain + 80,paint);
    }
    public static final SimpleDateFormat YYYYMMDD_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    public static final SimpleDateFormat MMSS_FORMAT = new SimpleDateFormat("mm:ss", Locale.getDefault());
    public static String todayYyyyMmDd() {
        return YYYYMMDD_FORMAT.format(new Date());
    }
    public static String todayMMSS(){
        return MMSS_FORMAT.format(new Date());
    }

}
