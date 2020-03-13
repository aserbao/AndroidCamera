package com.aserbao.androidcustomcamera.whole.videoPlayer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.MyApplication;
import com.aserbao.androidcustomcamera.base.utils.DisplayUtil;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;
import com.aserbao.androidcustomcamera.base.utils.StatusBarUtil;
import com.aserbao.androidcustomcamera.whole.editVideo.VideoEditActivity;
import com.aserbao.androidcustomcamera.whole.jiaozivideo.JZVideoPlayer;
import com.aserbao.androidcustomcamera.whole.jiaozivideo.PublicVideoJZVideo;
import com.aserbao.androidcustomcamera.whole.selCover.SelCoverTimeActivity;


import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.COMR_FROM_SEL_COVER_TIME_ACTIVITY;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.COMR_FROM_VIDEO_EDIT_TIME_ACTIVITY;


public class VideoPlayerActivity2 extends AppCompatActivity  {
    private static final String TAG = VideoPlayerActivity2.class.getSimpleName();
    @BindView(R.id.public_video_jz_video)
    PublicVideoJZVideo mPublicVideoJZVideo;



    @BindView(R.id.pop_video_loading_fl)
    FrameLayout mPopVideoLoadingFl;

    private RelativeLayout rlVideo;
    private String  videoFilePath = "/storage/emulated/0/ych/123.mp4", mOnLineVideoFilePath;
    private Context mContext;

    public static void launch(Activity activity, String outputPath) {
        Intent intent = new Intent(activity, VideoPlayerActivity2.class);
        intent.putExtra(StaticFinalValues.VIDEOFILEPATH, outputPath);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//状态栏半透明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_player2);
        ButterKnife.bind(this);
        mContext = this;
        initData();
        StatusBarUtil.transparencyBar(this);
    }

    private void initData() {
        videoFilePath = getIntent().getStringExtra(StaticFinalValues.VIDEOFILEPATH);
    }



    private void playVideo() {
        mPublicVideoJZVideo.setUp(videoFilePath, JZVideoPlayer.SCREEN_LAYOUT_NORMAL, "");
        mPublicVideoJZVideo.startVideo();
    }


    @Override
    protected void onResume() {
        super.onResume();
        playVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 当屏幕发生切换时调用
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
            setSystemUiHide();// 隐藏最上面那一栏
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);// 设置为全屏


            // 强制移除半屏状态
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
            setSystemUiShow();// 显示最上面那一栏
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dipToPx(this, 240));


            // 强制移除全屏状态
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    // 设置VideoView的大小
    private void setVideoViewScale(int width, int height) {
        rlVideo = (RelativeLayout) findViewById(R.id.video_layout);
        ViewGroup.LayoutParams params = rlVideo.getLayoutParams();
        params.width = width;
        params.height = height;
        rlVideo.setLayoutParams(params);
    }

    // 隐藏SystemUi
    private void setSystemUiHide() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    // 显示SystemUi
    private void setSystemUiShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mPopVideoLoadingFl!= null && mPopVideoLoadingFl.getVisibility() == View.VISIBLE) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @OnClick({R.id.video_player2_edit_video_tv,  R.id.video_player2_sel_cover, R.id.back_iv,  R.id.video_player_tv_storage, R.id.video_player_tv_public})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.video_player2_edit_video_tv:
                Intent intent = new Intent(MyApplication.getContext(), VideoEditActivity.class);
                intent.putExtra(StaticFinalValues.VIDEOFILEPATH,videoFilePath);
                startActivityForResult(intent,COMR_FROM_VIDEO_EDIT_TIME_ACTIVITY);
                break;
            case R.id.video_player2_sel_cover:
                Intent intent2 = new Intent(VideoPlayerActivity2.this, SelCoverTimeActivity.class);
                intent2.putExtra(StaticFinalValues.VIDEOFILEPATH, videoFilePath);
                startActivityForResult(intent2, COMR_FROM_SEL_COVER_TIME_ACTIVITY);
                break;
            case R.id.back_iv:
                onBackPressed();
                break;

            case R.id.video_player_tv_storage:
                break;
            case R.id.video_player_tv_public:
                storeToPhoto(videoFilePath);
                break;
        }
    }

    // 返回事件
    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (mPopVideoLoadingFl!= null && mPopVideoLoadingFl.getVisibility() != View.VISIBLE) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode){
            case StaticFinalValues.COMR_FROM_VIDEO_EDIT_TIME_ACTIVITY:
                videoFilePath = data.getStringExtra(StaticFinalValues.VIDEOFILEPATH);
                playVideo();
                break;
            case StaticFinalValues.COMR_FROM_SEL_COVER_TIME_ACTIVITY:
                videoFilePath = data.getStringExtra(StaticFinalValues.VIDEOFILEPATH);
                int selTime = data.getIntExtra(StaticFinalValues.CUT_TIME, 0);
                Toast.makeText(mContext, String.valueOf(selTime), Toast.LENGTH_SHORT).show();
                playVideo();
                break;
        }
    }

    private void storeToPhoto(String path) {
        ContentResolver localContentResolver = this.getContentResolver();
        /*String path = task.getPath();
        String filename = task.getFilename();*/
        ContentValues localContentValues = getVideoContentValues(this, new File(path), System.currentTimeMillis());
        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
        Toast.makeText(mContext, "保存到相册成功，路径为"+ path, Toast.LENGTH_SHORT).show();
    }


    public static ContentValues getVideoContentValues(Context paramContext, File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/3gp");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }
}
