package com.aserbao.androidcustomcamera.whole.editVideo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.DisplayUtil;
import com.aserbao.androidcustomcamera.whole.editVideo.beans.BubblePropertyModel;

/**
 * Created by Abner on 15/6/7.
 * QQ 230877476
 * Email nimengbo@gmail.com
 */
public class BubbleTextView extends BaseImageView {

    private static final String TAG = BubbleTextView.class.getSimpleName();


    private Bitmap deleteBitmap;
    private Bitmap flipVBitmap;
    private Bitmap topBitmap;
    private Bitmap resizeBitmap;
    private Bitmap mBitmap;
    private Bitmap originBitmap;
    private Rect dst_delete;
    private Rect dst_resize;
    private Rect dst_flipV;
    private Rect dst_top;


    private int deleteBitmapWidth;
    private int deleteBitmapHeight;
    private int resizeBitmapWidth;
    private int resizeBitmapHeight;
    private int flipVBitmapWidth;
    private int flipVBitmapHeight;

    //置顶
    private int topBitmapWidth;
    private int topBitmapHeight;
    private Paint localPaint;
    private int mScreenwidth, mScreenHeight;
    private static final float BITMAP_SCALE = 0.7f;
    private PointF mid = new PointF();
    private OperationListener operationListener;
    private float lastRotateDegree;

    //是否是第二根手指放下
    private boolean isPointerDown = false;
    //手指移动距离必须超过这个数值
    private final float pointerLimitDis = 20f;
    private final float pointerZoomCoeff = 0.09f;

    private final float moveLimitDis = 0.5f;
    /**
     * 对角线的长度
     */
    private float lastLength;
    private boolean isInResize = false;

//    private Matrix matrix = new Matrix();
    /**
     * 是否在四条线内部
     */
    private boolean isInSide;

    private float lastX, lastY;
    /**
     * 是否在编辑模式
     */
    private boolean isInEdit = true;

    private float MIN_SCALE = 0.5f;

    private float MAX_SCALE = 1.5f;

    private double halfDiagonalLength;

    private float oringinWidth = 0;

    private DisplayMetrics dm;

    /**
     * 文字部分
     */
    private final String defaultStr;
    //显示的字符串
    private String mStr = "";

    //字号默认16sp
    private final float mDefultSize = 14;
    private float mFontSize = 10;
    //最大最小字号
    private final float mMaxFontSize = 25;
    private final float mMinFontSize = 18;

    //字离旁边的距离
    private final float mDefaultMargin = 20;
    private float mMargin = 20;

    //绘制文字的画笔
    private TextPaint mFontPaint;

    //绘制背景图片的话题
    private Paint paint;

    private Canvas canvasText;

    private Paint.FontMetrics fm;
    //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
    private float baseline;

    boolean isInit = true;

    //双指缩放时的初始距离
    private float oldDis;

    //是否按下
    private boolean isDown = false;
    //是否移动
    private boolean isMove = false;
    //是否抬起手
    private boolean isUp = false;
    //是否在顶部
    private boolean isTop = true;

    private boolean isInBitmap;

    private final int fontColor;

    private boolean isInputEdit;

    private final long bubbleId;
    private int selectIndex;

    private int resourceId;
    private boolean isInRomate = false;
    int direction = 0;
    private boolean isFaceBottom = true;//边角是否在底部
    private boolean isFaceRight = false;//边角是否在右边
    protected Matrix rotateMatrix = new Matrix();
    private long mTouchDownTime;


    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = Color.BLACK;
        bubbleId = 0;
        init();
    }

    public BubbleTextView(Context context) {
        super(context);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = Color.BLACK;
        bubbleId = 0;
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = Color.BLACK;
        bubbleId = 0;
        init();
    }

    /**
     * @param context
     * @param fontColor
     * @param bubbleId  some fuck id
     */
    public BubbleTextView(Context context, int fontColor, long bubbleId) {
        super(context);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = fontColor;
        this.bubbleId = bubbleId;
        init();
    }

    /**
     * @param context
     * @param fontColor
     * @param bubbleId  some fuck id
     */
    public BubbleTextView(Context context, int fontColor, long bubbleId, int selectIndex) {
        super(context);
        defaultStr = getContext().getString(R.string.double_click_input_text);
        this.fontColor = fontColor;
        this.bubbleId = bubbleId;
        this.selectIndex = selectIndex;
        Log.e(TAG, "selectIndex:" + selectIndex);
        init();
    }


    private void init() {
        dm = getResources().getDisplayMetrics();
        dst_delete = new Rect();
        dst_resize = new Rect();
        dst_flipV = new Rect();
        dst_top = new Rect();
        localPaint = new Paint();
        localPaint.setColor(getResources().getColor(R.color.white));
        localPaint.setAntiAlias(true);
        localPaint.setDither(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0f);
        mScreenwidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        mFontSize = mDefultSize;
        mFontPaint = new TextPaint();
        mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, dm));
        mFontPaint.setColor(fontColor);
        mFontPaint.setTextAlign(Paint.Align.CENTER);
        mFontPaint.setAntiAlias(true);
        mFontPaint.setAlpha(255);
        paint = new Paint();
        paint.setAlpha(204);
        fm = mFontPaint.getFontMetrics();

        baseline = fm.descent - fm.ascent;
        isInit = true;
        mStr = defaultStr;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            float[] arrayOfFloat = new float[9];
            matrix.getValues(arrayOfFloat);
            float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
            float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
            float f5 = 0.0F * arrayOfFloat[0] + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f6 = 0.0F * arrayOfFloat[3] + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
            float f7 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
            float f8 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];

            canvas.save();
            //先往文字上绘图
            mBitmap = rotateToDegrees(originBitmap.copy(Bitmap.Config.ARGB_8888, true));
            canvasText.setBitmap(mBitmap);
            canvasText.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            float left = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, dm);
            float scalex = arrayOfFloat[Matrix.MSCALE_X];
            float skewy = arrayOfFloat[Matrix.MSKEW_Y];
            float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);
            float size = rScale * 0.75f * mDefultSize;
            if (size > mMaxFontSize) {
                mFontSize = mMaxFontSize;
            } else if (size < mMinFontSize) {
                mFontSize = mMinFontSize;
            } else {
                mFontSize = size;
            }
            float parts = 2;
            float leftMarginCount = 4;
            float marginLeft = mBitmap.getWidth() / 2;

            if (selectIndex == 0) {
                parts = 2.8f;
                mFontSize = 16;
                if (!isFaceBottom) {  //1
                    parts = 1.4f;
                }
            }
            if (selectIndex == 1) {
                parts = 2.8f;
                mFontSize = 16;
                if (!isFaceBottom) {
                    parts = 1.3f;
                }
            }

            if (selectIndex == 2) {
                parts = 2f;
                mFontSize = 16;
                if (!isFaceBottom) {
                    parts = 1.4f;
                }
                leftMarginCount = 7;
                if(isFaceRight){
                    marginLeft *= 0.9f;
                }else {
                    marginLeft *= 1.1f;
                }
            }

            if (selectIndex == 3) {
                parts = 2f;
                mFontSize = 16;
                if (!isFaceBottom) {
                    parts = 1.5f;
                }
            }

            if (selectIndex == 4) {
                parts = 2f;
                mFontSize = 16;
                if (!isFaceBottom) {
                    parts = 1.4f;
                }
            }

            if (selectIndex == 5) {
                parts = 2.14f;
                mFontSize = 15;
                leftMarginCount = 5.8f;
                if (!isFaceBottom) {
                    parts = 1.2f;
                }
                if(isFaceRight){
                    marginLeft *= 1.1f;
                }else {
                    marginLeft *= 0.9f;
                }
            }
            if (selectIndex == 6) {
                mFontSize = 13;
                if (!isFaceBottom) {
                    parts = 1.5f;
                } else {
                    parts = 1.7f;
                }
            }

            if (selectIndex == 7) {
                parts = 2f;
                mFontSize = 16;
                if (!isFaceBottom) {
                    parts = 1.4f;
                }
                if(isFaceRight){
                    marginLeft *= 1f;
                }else {
                    marginLeft *= 1.05f;
                }
            }
            mFontPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, dm));
//            String[] texts = autoSplit(mStr, mFontPaint, mBitmap.getWidth() - left * 3);
            String[] texts = autoSplit(mStr, mFontPaint, mBitmap.getWidth() - left * leftMarginCount);
            float height = (texts.length * (baseline + fm.leading) + baseline);
            float top = (mBitmap.getHeight() - height) / parts;
            //基于底线开始画的
            top += baseline;

            for (String text : texts) {
                if (TextUtils.isEmpty(text)) {
                    continue;
                }
                canvasText.drawText(text, marginLeft, top, mFontPaint);  //坐标以控件左上角为原点
                top += baseline + fm.leading; //添加字体行间距

            }
//            rotateMatrix.set(matrix);
//            if (!isMove) {
//                switch (direction % 4) {
//                    case 0:
//                        break;
//                    case 1:
//                        rotateMatrix.postRotate(90, X, Y);
//                        break;
//                    case 2:
//                        rotateMatrix.postRotate(180, X, Y);
//                        break;
//                    case 3:
//                        rotateMatrix.postRotate(270, X, Y);
//                        break;
//                }
//            }
            canvas.drawBitmap(mBitmap, matrix, paint);

            //删除在右上角
            dst_delete.left = (int) (f3 - deleteBitmapWidth / 2);
            dst_delete.right = (int) (f3 + deleteBitmapWidth / 2);
            dst_delete.top = (int) (f4 - deleteBitmapHeight / 2);
            dst_delete.bottom = (int) (f4 + deleteBitmapHeight / 2);
            //拉伸等操作在右下角
            dst_resize.left = (int) (f7 - resizeBitmapWidth / 2);
            dst_resize.right = (int) (f7 + resizeBitmapWidth / 2);
            dst_resize.top = (int) (f8 - resizeBitmapHeight / 2);
            dst_resize.bottom = (int) (f8 + resizeBitmapHeight / 2);
            //置顶在左上角
            dst_top.left = (int) (f1 - topBitmapWidth / 2);
            dst_top.right = (int) (f1 + topBitmapWidth / 2);
            dst_top.top = (int) (f2 - topBitmapHeight / 2);
            dst_top.bottom = (int) (f2 + topBitmapHeight / 2);
            //水平镜像在右下角
            dst_flipV.left = (int) (f5 - topBitmapWidth / 2);
            dst_flipV.right = (int) (f5 + topBitmapWidth / 2);
            dst_flipV.top = (int) (f6 - topBitmapHeight / 2);
            dst_flipV.bottom = (int) (f6 + topBitmapHeight / 2);

            //dst_delete dst_resize dst_top dst_flipV
            leftBottomX = dst_delete.centerX();
            leftBottomX = leftBottomX > dst_resize.centerX() ? dst_resize.centerX() : leftBottomX;
            leftBottomX = leftBottomX > dst_top.centerX() ? dst_top.centerX() : leftBottomX;
            leftBottomX = leftBottomX > dst_flipV.centerX() ? dst_flipV.centerX() : leftBottomX;

            leftBottomY = dst_flipV.centerY();
            leftBottomY = leftBottomY < dst_resize.centerY() ? dst_resize.centerY() : leftBottomY;
            leftBottomY = leftBottomY < dst_top.centerY() ? dst_top.centerY() : leftBottomY;
            leftBottomY = leftBottomY < dst_flipV.centerY() ? dst_flipV.centerY() : leftBottomY;

            float distanceX = Math.abs(dst_delete.centerX() - dst_top.centerX());
            float distanceY = Math.abs(dst_delete.centerY() - dst_top.centerY());
            viewWidth = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            float distanceHeightX = Math.abs(dst_resize.centerX() - dst_delete.centerX());
            float distanceHeightY = Math.abs(dst_resize.centerY() - dst_delete.centerY());
            viewHeight = (float) Math.sqrt(distanceHeightX * distanceHeightX + distanceHeightY * distanceHeightY);

            if (isInEdit) {
                canvas.drawLine(f1, f2, f3, f4, localPaint);
                canvas.drawLine(f3, f4, f7, f8, localPaint);
                canvas.drawLine(f5, f6, f7, f8, localPaint);
                canvas.drawLine(f5, f6, f1, f2, localPaint);


                canvas.drawBitmap(deleteBitmap, null, dst_delete, null);
                canvas.drawBitmap(resizeBitmap, null, dst_resize, null);
//                canvas.drawBitmap(flipVBitmap, null, dst_flipV, null);
                canvas.drawBitmap(topBitmap, null, dst_top, null);
            }

            canvas.restore();
        }
    }

    /**
     * 图片旋转
     *
     * @param tmpBitmap
     * @param
     * @return
     */
    public Bitmap rotateToDegrees(Bitmap tmpBitmap) {
        Matrix matrix = new Matrix();
        matrix.reset();
//        matrix.setRotate(degrees);
        switch (direction % 4) {
            case 0:
                isFaceBottom = true;
                isFaceRight = false;
                break;
            case 1:
                isFaceBottom = true;
                isFaceRight = true;
                matrix.postScale(-1.0F, 1.0F);
                break;
            case 2:
                isFaceBottom = false;
                isFaceRight = true;
                matrix.postScale(-1.0F, -1.0F);
                break;
            case 3:
                isFaceBottom = false;
                isFaceRight = false;
                matrix.postScale(1.0F, -1.0F);
                break;
        }

        return tmpBitmap =
                Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(), tmpBitmap.getHeight(), matrix,
                        true);
    }


    public void setText(String text) {
//        if (TextUtils.isEmpty(text)) {
//            mStr = defaultStr;
//            mFontSize = mDefultSize;
//            mMargin = mDefaultMargin;
//        } else {
        mStr = text;
//        }
        invalidate();
    }


    public void setParentSize(int mScreenwidth, int mScreenHeight) {
//        this.mScreenwidth = mScreenwidth;
//        this.mScreenHeight = mScreenHeight;
        this.mScreenwidth = mScreenwidth > mScreenHeight ? mScreenHeight : mScreenwidth;
        this.mScreenHeight = mScreenwidth > mScreenHeight ? mScreenwidth : mScreenHeight;
    }

    @Override
    public void setImageResource(int resId) {
        this.resourceId = resId;
        matrix.reset();
        //使用拷贝 不然会对资源文件进行引用而修改
        setBitmap(BitmapFactory.decodeResource(getResources(), resId));
    }

    public void setImageResource(int resId, BubblePropertyModel model) {
        matrix.reset();
        //使用拷贝 不然会对资源文件进行引用而修改
        setBitmap(BitmapFactory.decodeResource(getResources(), resId), model);
    }

    public void setBitmap(Bitmap bitmap, BubblePropertyModel model) {
        mFontSize = mDefultSize;
        originBitmap = bitmap;
        mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasText = new Canvas(mBitmap);
        setDiagonalLength();
        initBitmaps();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        oringinWidth = w;

        mStr = model.getText();
        float scale = model.getScaling() * mScreenwidth / mBitmap.getWidth();
        if (scale > MAX_SCALE) {
            scale = MAX_SCALE;
        } else if (scale < MIN_SCALE) {
            scale = MIN_SCALE;
        }
        float degree = (float) Math.toDegrees(model.getDegree());
        matrix.postRotate(-degree, w >> 1, h >> 1);
        matrix.postScale(scale, scale, w >> 1, h >> 1);
        float midX = model.getxLocation() * mScreenwidth;
        float midY = model.getyLocation() * mScreenwidth;
        float offset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, dm);
        midX = midX - (w * scale) / 2 - offset;
        midY = midY - (h * scale) / 2 - offset;
        matrix.postTranslate(midX, midY);
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        mFontSize = mDefultSize;
        originBitmap = bitmap;
        mBitmap = originBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvasText = new Canvas(mBitmap);
        setDiagonalLength();
        initBitmaps();
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        oringinWidth = w;
        float topbarHeight = DisplayUtil.dipToPx(getContext(), 50);
        float initScale = 1.3f * (MIN_SCALE + MAX_SCALE) / 2;
        matrix.postScale(initScale, initScale, w / 2, h / 2);
        //Y坐标为 （顶部操作栏+正方形图）/2
        matrix.postTranslate(mScreenwidth / 2 - w / 2, (mScreenwidth) / 2 - h / 2);
        invalidate();
    }

    private void setDiagonalLength() {
        halfDiagonalLength = Math.hypot(mBitmap.getWidth(), mBitmap.getHeight()) / 2;
    }

    private void initBitmaps() {

        float minWidth = mScreenwidth / 8;
        if (mBitmap.getWidth() < minWidth) {
            MIN_SCALE = 1f;
        } else {
            MIN_SCALE = 1.0f * minWidth / mBitmap.getWidth();
        }

        if (mBitmap.getWidth() > mScreenwidth) {
            MAX_SCALE = 1;
        } else {
            MAX_SCALE = 1.0f * mScreenwidth / mBitmap.getWidth();
        }
        topBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camera_scaling);
        deleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camera_delete);
        flipVBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camera_scaling);
        resizeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camera_rotate);

        deleteBitmapWidth = (int) (deleteBitmap.getWidth() * BITMAP_SCALE);
        deleteBitmapHeight = (int) (deleteBitmap.getHeight() * BITMAP_SCALE);

        resizeBitmapWidth = (int) (resizeBitmap.getWidth() * BITMAP_SCALE);
        resizeBitmapHeight = (int) (resizeBitmap.getHeight() * BITMAP_SCALE);

        flipVBitmapWidth = (int) (flipVBitmap.getWidth() * BITMAP_SCALE);
        flipVBitmapHeight = (int) (flipVBitmap.getHeight() * BITMAP_SCALE);

        topBitmapWidth = (int) (topBitmap.getWidth() * BITMAP_SCALE);
        topBitmapHeight = (int) (topBitmap.getHeight() * BITMAP_SCALE);

    }

    private long preClicktime;

    private final long doubleClickTimeLimit = 200;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        boolean handled = true;
        isInBitmap = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownTime = System.currentTimeMillis();
                if (isInButton(event, dst_delete)) {
                    if (operationListener != null) {
                        operationListener.onDeleteClick();
                    }
                    isDown = false;
                    isInRomate = false;
                } else if (isInResize(event)) {
                    isInResize = true;
                    lastRotateDegree = rotationToStartPoint(event);
                    midPointToStartPoint(event);
                    lastLength = diagonalLength(event);
                    isDown = false;
                    isInRomate = false;
                } else if (isInButton(event, dst_flipV)) {
                    PointF localPointF = new PointF();
                    midDiagonalPoint(localPointF);
                    matrix.postScale(-1.0F, 1.0F, localPointF.x, localPointF.y);
                    isDown = false;
                    isInRomate = false;
                    invalidate();
                } else if (isInButton(event, dst_top)) {
                    if (operationListener != null) {
                        operationListener.onTop(this);
                    }
                    isDown = false;
                    direction++;
                    X = (dst_top.left + dst_resize.right) / 2;
                    Y = (dst_top.top + dst_resize.bottom) / 2;
//                    matrix.postRotate(lastRotateDegree, X, Y);
                    isInRomate = true;
                    invalidate();

                } else if (isInBitmap(event)) {
                    isInSide = true;
                    lastX = event.getX(0);
                    lastY = event.getY(0);
                    isDown = true;
                    isMove = false;
                    isPointerDown = false;
                    isUp = false;
                    isInRomate = false;
                    isInBitmap = true;
                    isInputEdit = true;

                } else {
                    isInRomate = false;
                    handled = false;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (spacing(event) > pointerLimitDis) {
                    oldDis = spacing(event);
                    isPointerDown = true;
                    midPointToStartPoint(event);
                } else {
                    isPointerDown = false;
                }
                isInSide = false;
                isInResize = false;
                isInRomate = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(System.currentTimeMillis() - mTouchDownTime > 200) {
                    isInputEdit = false;
                }
                //双指缩放
                if (isPointerDown) {
                    float scale;
                    float disNew = spacing(event);
                    if (disNew == 0 || disNew < pointerLimitDis) {
                        scale = 1;
                    } else {
                        scale = disNew / oldDis;
                        //缩放缓慢
                        scale = (scale - 1) * pointerZoomCoeff + 1;
                    }
                    float scaleTemp = (scale * Math.abs(dst_flipV.left - dst_resize.left)) / oringinWidth;
                    if (((scaleTemp <= MIN_SCALE)) && scale < 1 ||
                            (scaleTemp >= MAX_SCALE) && scale > 1) {
                        scale = 1;
                    } else {
                        lastLength = diagonalLength(event);
                    }
//                    matrix.postScale(scale, scale, mid.x, mid.y);
                    matrix.postScale(scale, scale, X, Y);
                    isInRomate = false;
                    invalidate();
                } else if (isInResize) {
                    matrix.postRotate((rotationToStartPoint(event) - lastRotateDegree) * 2, mid.x, mid.y);
                    lastRotateDegree = rotationToStartPoint(event);

                    float scale = diagonalLength(event) / lastLength;

                    if (((diagonalLength(event) / halfDiagonalLength <= MIN_SCALE)) && scale < 1 ||
                            (diagonalLength(event) / halfDiagonalLength >= MAX_SCALE) && scale > 1) {
                        scale = 1;
                        if (!isInResize(event)) {
                            isInResize = false;
                        }
                    } else {
                        lastLength = diagonalLength(event);
                    }
//                    matrix.postScale(scale, scale, mid.x, mid.y);
                    X = (dst_top.left + dst_resize.right) / 2;
                    Y = (dst_top.top + dst_resize.bottom) / 2;

                    matrix.postScale(scale, scale, X, Y);
                    isInRomate = false;
                    invalidate();
                } else if (isInSide) {
                    //TODO 移动区域判断 不能超出屏幕
                    float x = event.getX(0);
                    float y = event.getY(0);
                    //判断手指抖动距离 加上isMove判断 只要移动过 都是true
                    if (!isMove && Math.abs(x - lastX) < moveLimitDis
                            && Math.abs(y - lastY) < moveLimitDis) {
                        isMove = false;
                    } else {
                        isMove = true;
                    }
                    matrix.postTranslate(x - lastX, y - lastY);
                    lastX = x;
                    lastY = y;
                    isInRomate = false;
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isInResize = false;
                isInSide = false;
                isPointerDown = false;
                isUp = true;

                if (isInputEdit) {
                    isInputEdit = false;
                    if (isInEdit && operationListener != null) {
                        operationListener.onClick(this);
                    }
                }


                X = (dst_top.left + dst_resize.right) / 2;
                Y = (dst_top.top + dst_resize.bottom) / 2;
                rotateDegree = lastRotateDegree;
                Log.e(TAG, "leftBottomX:" + leftBottomX);
                Log.e(TAG, "leftBottomY:" + leftBottomY);
                Log.e(TAG, "viewWidth:" + viewWidth);
                Log.e(TAG, "viewHeight:" + viewHeight);
//                Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
//                Log.e(TAG, "bitmapWidth:" + bitmap.getWidth());
//                Log.e(TAG, "bitmapHeight:" + bitmap.getHeight());
                break;

        }
        if (handled && operationListener != null) {
            operationListener.onEdit(this);
        }
//        //判断是不是做了点击动作 必须在编辑状态 且在图片内 并且是双击
//        if (isDoubleClick && isDown && !isPointerDown && !isMove && isUp && isInBitmap && isInEdit && operationListener != null) {
//            operationListener.onClick(this);
//        }
        return handled;
    }

    public BubblePropertyModel calculate(BubblePropertyModel model) {
        float[] v = new float[9];
        matrix.getValues(v);
        // translation is simple
        float tx = v[Matrix.MTRANS_X];
        float ty = v[Matrix.MTRANS_Y];
        Log.d(TAG, "tx : " + tx + " ty : " + ty);
        // calculate real scale
        float scalex = v[Matrix.MSCALE_X];
        float skewy = v[Matrix.MSKEW_Y];
        float rScale = (float) Math.sqrt(scalex * scalex + skewy * skewy);
        Log.d(TAG, "rScale : " + rScale);
        // calculate the degree of rotation
        float rAngle = Math.round(Math.atan2(v[Matrix.MSKEW_X], v[Matrix.MSCALE_X]) * (180 / Math.PI));
        Log.d(TAG, "rAngle : " + rAngle);

        float minX = (dst_top.centerX() + dst_resize.centerX()) / 2;
        float minY = (dst_top.centerY() + dst_resize.centerY()) / 2;

        Log.d(TAG, "midX : " + minX + " midY : " + minY);

        model.setDegree((float) Math.toRadians(rAngle));
        model.setBubbleId(bubbleId);
        //TODO 占屏幕百分比
        float precentWidth = (mBitmap.getWidth() * rScale) / mScreenwidth;
        model.setScaling(precentWidth);
        Log.d(TAG, " x " + (minX / mScreenwidth) + " y " + (minY / mScreenwidth));
        model.setxLocation(minX / mScreenwidth);
        model.setyLocation(minY / mScreenwidth);
        model.setText(mStr);
        return model;
    }


    /**
     * 是否在四条线内部
     *
     * @return
     */
    private boolean isInBitmap(MotionEvent event) {
        float[] arrayOfFloat1 = new float[9];
        this.matrix.getValues(arrayOfFloat1);
        //左上角
        float f1 = 0.0F * arrayOfFloat1[0] + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f2 = 0.0F * arrayOfFloat1[3] + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //右上角
        float f3 = arrayOfFloat1[0] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[1] + arrayOfFloat1[2];
        float f4 = arrayOfFloat1[3] * this.mBitmap.getWidth() + 0.0F * arrayOfFloat1[4] + arrayOfFloat1[5];
        //左下角
        float f5 = 0.0F * arrayOfFloat1[0] + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
        float f6 = 0.0F * arrayOfFloat1[3] + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];
        //右下角
        float f7 = arrayOfFloat1[0] * this.mBitmap.getWidth() + arrayOfFloat1[1] * this.mBitmap.getHeight() + arrayOfFloat1[2];
        float f8 = arrayOfFloat1[3] * this.mBitmap.getWidth() + arrayOfFloat1[4] * this.mBitmap.getHeight() + arrayOfFloat1[5];

        float[] arrayOfFloat2 = new float[4];
        float[] arrayOfFloat3 = new float[4];
        //确定X方向的范围
        arrayOfFloat2[0] = f1;//左上的左
        arrayOfFloat2[1] = f3;//右上的右
        arrayOfFloat2[2] = f7;//右下的右
        arrayOfFloat2[3] = f5;//左下的左
        //确定Y方向的范围
        arrayOfFloat3[0] = f2;//左上的上
        arrayOfFloat3[1] = f4;//右上的上
        arrayOfFloat3[2] = f8;
        arrayOfFloat3[3] = f6;
        return pointInRect(arrayOfFloat2, arrayOfFloat3, event.getX(0), event.getY(0));
    }

    /**
     * 判断点是否在一个矩形内部
     *
     * @param xRange
     * @param yRange
     * @param x
     * @param y
     * @return
     */
    private boolean pointInRect(float[] xRange, float[] yRange, float x, float y) {
        //四条边的长度
        double a1 = Math.hypot(xRange[0] - xRange[1], yRange[0] - yRange[1]);
        double a2 = Math.hypot(xRange[1] - xRange[2], yRange[1] - yRange[2]);
        double a3 = Math.hypot(xRange[3] - xRange[2], yRange[3] - yRange[2]);
        double a4 = Math.hypot(xRange[0] - xRange[3], yRange[0] - yRange[3]);
        //待检测点到四个点的距离
        double b1 = Math.hypot(x - xRange[0], y - yRange[0]);
        double b2 = Math.hypot(x - xRange[1], y - yRange[1]);
        double b3 = Math.hypot(x - xRange[2], y - yRange[2]);
        double b4 = Math.hypot(x - xRange[3], y - yRange[3]);

        double u1 = (a1 + b1 + b2) / 2;
        double u2 = (a2 + b2 + b3) / 2;
        double u3 = (a3 + b3 + b4) / 2;
        double u4 = (a4 + b4 + b1) / 2;

        //矩形的面积
        double s = a1 * a2;
        double ss = Math.sqrt(u1 * (u1 - a1) * (u1 - b1) * (u1 - b2))
                + Math.sqrt(u2 * (u2 - a2) * (u2 - b2) * (u2 - b3))
                + Math.sqrt(u3 * (u3 - a3) * (u3 - b3) * (u3 - b4))
                + Math.sqrt(u4 * (u4 - a4) * (u4 - b4) * (u4 - b1));
        double distance = Math.abs(s - ss);
        Log.e(TAG, "pointInRect: " + distance );
        return distance < 0.5;


    }


    private boolean isInButton(MotionEvent event, Rect rect) {
        int left = rect.left;
        int right = rect.right;
        int top = rect.top;
        int bottom = rect.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    private boolean isInResize(MotionEvent event) {
        int left = -20 + this.dst_resize.left;
        int top = -20 + this.dst_resize.top;
        int right = 20 + this.dst_resize.right;
        int bottom = 20 + this.dst_resize.bottom;
        return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;
    }

    private void midPointToStartPoint(MotionEvent event) {
        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float f1 = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = f1 + event.getX(0);
        float f4 = f2 + event.getY(0);
        mid.set(f3 / 2, f4 / 2);
    }

    private void midDiagonalPoint(PointF paramPointF) {
        float[] arrayOfFloat = new float[9];
        this.matrix.getValues(arrayOfFloat);
        float f1 = 0.0F * arrayOfFloat[0] + 0.0F * arrayOfFloat[1] + arrayOfFloat[2];
        float f2 = 0.0F * arrayOfFloat[3] + 0.0F * arrayOfFloat[4] + arrayOfFloat[5];
        float f3 = arrayOfFloat[0] * this.mBitmap.getWidth() + arrayOfFloat[1] * this.mBitmap.getHeight() + arrayOfFloat[2];
        float f4 = arrayOfFloat[3] * this.mBitmap.getWidth() + arrayOfFloat[4] * this.mBitmap.getHeight() + arrayOfFloat[5];
        float f5 = f1 + f3;
        float f6 = f2 + f4;
        paramPointF.set(f5 / 2.0F, f6 / 2.0F);
    }


    /**
     * 在滑动过车中X,Y是不会改变的，这里减Y，减X，其实是相当于把X,Y当做原点
     *
     * @param event
     * @return
     */
    private float rotationToStartPoint(MotionEvent event) {

        float[] arrayOfFloat = new float[9];
        matrix.getValues(arrayOfFloat);
        float x = 0.0f * arrayOfFloat[0] + 0.0f * arrayOfFloat[1] + arrayOfFloat[2];
        float y = 0.0f * arrayOfFloat[3] + 0.0f * arrayOfFloat[4] + arrayOfFloat[5];
        double arc = Math.atan2(event.getY(0) - y, event.getX(0) - x);
        return (float) Math.toDegrees(arc);
    }

    /**
     * 触摸点到矩形中点的距离
     *
     * @param event
     * @return
     */
    private float diagonalLength(MotionEvent event) {
        float diagonalLength = (float) Math.hypot(event.getX(0) - mid.x, event.getY(0) - mid.y);
        return diagonalLength;
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    public interface OperationListener {
        void onDeleteClick();

        void onEdit(BubbleTextView bubbleTextView);

        void onClick(BubbleTextView bubbleTextView);

        void onTop(BubbleTextView bubbleTextView);
    }

    public void setOperationListener(OperationListener operationListener) {
        this.operationListener = operationListener;
    }

    public void setInEdit(boolean isInEdit) {
        this.isInEdit = isInEdit;
        invalidate();
    }

    /**
     * 自动分割文本
     *
     * @param content 需要分割的文本
     * @param p       画笔，用来根据字体测量文本的宽度
     * @param width   指定的宽度
     * @return 一个字符串数组，保存每行的文本
     */
    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth <= width) {
            return new String[]{content};
        }

        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        String[] lineTexts = new String[lines];
        while (start < length) {
            if (p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if (end == length) { //不足一行的文本
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }
        return lineTexts;
    }

    public String getmStr() {
        return mStr;
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        return bitmap;
    }

}
