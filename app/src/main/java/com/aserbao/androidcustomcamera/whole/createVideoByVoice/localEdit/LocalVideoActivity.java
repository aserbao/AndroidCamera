package com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.MyApplication;
import com.aserbao.androidcustomcamera.base.utils.DisplayUtil;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.adapter.ThumbAdapter;
import com.aserbao.androidcustomcamera.whole.videoPlayer.VideoPlayerActivity2;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import VideoHandle.CmdList;
import VideoHandle.EpEditor;
import VideoHandle.OnEditorListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.ISSAVEVIDEOTEMPEXIST;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.VIDEOTEMP;

public class LocalVideoActivity extends AppCompatActivity implements MediaPlayerWrapper.IMediaCallback {
    private static final int SAVE_BITMAP = 1;
    private static final int SUBMIT = 2;
    private static final int ClEAR_BITMAP = 3;
    private static final int CLIPPER_GONE = 4;
    private static final int CLIPPER_FAILURE = 5;
    private static final String TAG = "Atest";
    @BindView(R.id.local_back_iv)
    ImageView mLocalBackIv;
    @BindView(R.id.local_rotate_iv)
    ImageView mLocalRotateIv;
    @BindView(R.id.local_video_next_tv)
    TextView mLocalVideoNextTv;
    @BindView(R.id.local_title)
    RelativeLayout mLocalTitle;
    @BindView(R.id.local_video_view)
    VideoPreviewView mLocalVideoView;
    @BindView(R.id.local_sel_time_tv)
    TextView mLocalSelTimeTv;
    @BindView(R.id.local_recycler_view)
    RecyclerView mLocalRecyclerView;
    @BindView(R.id.local_thumb_view)
    ThumbnailView mLocalThumbView;
    @BindView(R.id.local_frame_layout)
    FrameLayout mLocalFrameLayout;
    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;
    @BindView(R.id.tv_hint)
    TextView mTvHint;
    @BindView(R.id.pop_video_loading_fl)
    FrameLayout mPopVideoLoadingFl;
    private String mInputVideoPath = "/storage/emulated/0/aserbaoCamera/321.mp4";
    private String mOutVideoPath;
    private int rotate;
    public ThumbAdapter mThumbAdapter;
    public LinearLayoutManager mLinearLayoutManager;
    public float mStartTime = 0;
    public float mEndTime;
    public int mRecyclerWidth;
    public int mTotolWidth;
    public int mThumbSelTime = 30;//选择器选中的时间间隔
    public String mVideoRotation;
    private int mInitRotation;//视频初始旋转角度，竖屏为90，横屏为0
    private boolean isFailure = false;
    public long mLastTime;
    private long lastTime;
    private boolean isLocalPortrait = false;
    public String mSavevideotemp;
    private boolean isClickRotate = false;//是否点击了旋转按钮
    public AsyncTask<Void, Void, Boolean> mAsyncTask;
    private int mRotate = 0;
    private String DIR;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mPopVideoLoadingFl != null && mPopVideoLoadingFl.getVisibility() == View.VISIBLE){
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video);
        ButterKnife.bind(this);
        mContext = this;
        initData();
        initView();
        initListener();
    }

    public int mHorizontalScrollOffset;

    private void initListener() {
        mLocalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //获取当前RecyclerView的滑动偏移距离2340
                mTotolWidth = mLocalRecyclerView.computeHorizontalScrollRange();
                mHorizontalScrollOffset = mLocalRecyclerView.computeHorizontalScrollOffset();
                float mThumbLeftPosition = mLocalThumbView.getLeftInterval() + mHorizontalScrollOffset;
                float v = mThumbLeftPosition / (float) mTotolWidth;
                mStartTime = mVideoDuration * v;
                mEndTime = (int) mStartTime + mThumbSelTime * 1000;

                if (mLocalRecyclerView.computeHorizontalScrollExtent() + mHorizontalScrollOffset == mTotolWidth) {
                    float right = mLocalThumbView.getRightInterval();
                    float width = mLocalThumbView.getTotalWidth();
                    if (right == width) {
                        mEndTime = mVideoDuration;
                        mStartTime = mEndTime - mThumbSelTime * 1000;
                    }
                }
                Log.e(TAG, "OnScrollBorder: mStartTime:" + mStartTime + "mEndTime:" + mEndTime);
            }
        });
        mLocalThumbView.setOnScrollBorderListener(new ThumbnailView.OnScrollBorderListener() {
            @Override
            public void OnScrollBorder(float start, float end) {
                mTotolWidth = mLocalRecyclerView.computeHorizontalScrollRange();
                float left = mLocalThumbView.getLeftInterval();
                float mThumbLeftPosition = left + mHorizontalScrollOffset;
                float v = mThumbLeftPosition / (float) mTotolWidth;
                mStartTime = mVideoDuration * v;
                float right = mLocalThumbView.getRightInterval();
                mThumbSelTime = (int) ((right - left) * 30 / MyApplication.screenWidth);
                float width = mLocalThumbView.getTotalWidth();
                if (right == width) {
                    mThumbSelTime = (mVideoDuration - (int) mStartTime) / 1000;
                }
                if (mThumbSelTime > 30) {
                    mThumbSelTime = 30;
                }
                mLocalSelTimeTv.setText("已选取" + mThumbSelTime + "秒");
                mEndTime = mStartTime + mThumbSelTime * 1000;
                Log.e(TAG, "OnScrollBorder: mStartTime:" + mStartTime + "mEndTime:" + mEndTime);
            }

            @Override
            public void onScrollStateChange() {
                Log.e(TAG, "OnScrollBorder: startTime" + mStartTime + "             endTime ===             " + mEndTime);
            }
        });

    }

    private boolean isThreadStart = false;
    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (isPlaying) {
                isThreadStart = true;
                int videoDuration = mLocalVideoView.getVideoDuration();
                if (mStartTime > videoDuration || mEndTime < videoDuration) {
                    mLocalVideoView.seekTo((int) mStartTime / 1000);
                    mLocalVideoView.start();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void initView() {
        mThumbAdapter = new ThumbAdapter(this);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mLocalRecyclerView.setLayoutManager(mLinearLayoutManager);
        mLocalRecyclerView.setAdapter(mThumbAdapter);
        mLocalThumbView.setMinInterval(MyApplication.screenWidth / 6);
    }

    private void initData() {
        mInputVideoPath = getIntent().getBundleExtra(StaticFinalValues.BUNDLE).getString(StaticFinalValues.VIDEOFILEPATH);
        initThumbs();//获取缩略图
        ArrayList<String> srcList = new ArrayList<>();
        srcList.add(mInputVideoPath);
        mLocalVideoView.setVideoPath(srcList);
        mLocalVideoView.setIMediaCallback(this);
        initSetParam();
    }

    /**
     *
     */
    private void initSetParam() {
        /*if(mVideoRotation.equals("90") && mVideoWidth > mVideoHeight || mVideoRotation.equals("0") && mVideoWidth < mVideoHeight){//本地相机视频竖屏//自定义相机视频
        }*/
        //todo:自定义相机录制视频的方向不对，长宽是对的，系统相机视频只可以获取正确是角度，不能通过长宽进行判断
        if (mVideoRotation.equals("0") && mVideoWidth > mVideoHeight) {//本地视频横屏
            Log.e(TAG, "initSetParam: " );
//            mInitRotation = 90;
//            mLocalVideoView.setRotation(mInitRotation);
        } else if (mVideoRotation.equals("90") && mVideoWidth > mVideoHeight) {//本地视频竖屏
            mInitRotation = 90;
            isLocalPortrait = true;
            setPortraitParam();
        }else if(mVideoRotation.equals("0") && mVideoWidth < mVideoHeight){ //保存视频竖屏
            setPortraitParam();
        }else if(mVideoRotation.equals("180") && mVideoWidth > mVideoHeight){//本地视频横屏
            Log.e(TAG, "initSetParam: " );
        } else{
            mInitRotation = 90;
            setPortraitParam();
        }
    }

    @NonNull
    private void setPortraitParam() {
        ViewGroup.LayoutParams layoutParams1 = mLocalVideoView.getLayoutParams();
        layoutParams1.width = 630;
        layoutParams1.height = 1120;
        mLocalVideoView.setLayoutParams(layoutParams1);
        mLocalVideoView.requestLayout();
    }
    @NonNull
    private void setLandScapeParam() {
        ViewGroup.LayoutParams layoutParams1 = mLocalVideoView.getLayoutParams();
        layoutParams1.width = 1120;
        layoutParams1.height = 630;
        mLocalVideoView.setLayoutParams(layoutParams1);
        mLocalVideoView.requestLayout();
    }

    

    @OnClick({R.id.local_back_iv, R.id.local_rotate_iv, R.id.local_video_next_tv})
    public void onViewClicked(View view) {
        if (System.currentTimeMillis() - lastTime < 500 || mPopVideoLoadingFl != null && mPopVideoLoadingFl.getVisibility() == View.VISIBLE) {
            return;
        }
        lastTime = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.local_back_iv:
                onBackPressed();
                break;
            case R.id.local_rotate_iv:
                isClickRotate = true;
                mRotate = rotate;
                if (rotate < 270) {
                    rotate = rotate + 90;
                } else {
                    rotate = 0;
                }
                int rotation = mInitRotation + rotate;
                if(rotation == 90 || rotation == 270){
                    setLandScapeParam();
                }else{
                    setPortraitParam();
                }
//                mLocalVideoView.setRotation(rotation);
               /* ObjectAnimator animator = ObjectAnimator.ofFloat(mLocalVideoView, "rotation", mRotate, rotation);
                animator.setDuration(500);
                animator.start();*/
                if (mInitRotation == 90) {
                    mLocalVideoView.setRotate(rotate);
                } else {
                    mLocalVideoView.setRotate(rotate);
                }
                break;
            case R.id.local_video_next_tv:
                CmdList cmd = new CmdList();
                cmd.append("-y");
                cmd.append("-ss").append(String.valueOf((int) mStartTime / 1000)).append("-t").append(String.valueOf(mThumbSelTime)).append("-accurate_seek");
                cmd.append("-i").append(mInputVideoPath);
                if (isLocalPortrait) {
                    if (!isClickRotate && rotate == 0 || rotate == 90) {
                        rotate = 180;
                    } else {
                        isLocalPortrait = false;
                        if (rotate == 0) {
                            rotate = 270;
                        } else {
                            rotate = rotate - 90;
                        }
                    }
                }
                switch (rotate) {
                    case 0:
                        cmd.append("-vcodec");
                        cmd.append("copy");
                        cmd.append("-acodec");
                        cmd.append("copy");
                        break;
                    case 270:
                        cmd.append("-filter_complex");
                        cmd.append("transpose=2");
                        cmd.append("-preset");
                        cmd.append("ultrafast");
                        break;
                    case 180:
                        cmd.append("-filter_complex");
                        cmd.append("vflip,hflip");
                        cmd.append("-preset");
                        cmd.append("ultrafast");
                        break;
                    case 90:
                        cmd.append("-filter_complex");
                        cmd.append("transpose=1");
                        cmd.append("-preset");
                        cmd.append("ultrafast");
                        break;
                }

                File file = new File(ISSAVEVIDEOTEMPEXIST);
                if (!file.exists()){
                    file.mkdir();
                }
                mOutVideoPath = ISSAVEVIDEOTEMPEXIST + System.currentTimeMillis() + ".mp4";
                if (!new File(VIDEOTEMP).exists()) {
                    new File(VIDEOTEMP).mkdirs();
                }
                cmd.append(mOutVideoPath);
                mLocalVideoView.pause();
                exec(cmd);
                break;
        }
    }

    public void translateVideo() {
        CmdList cmd = new CmdList();
        cmd.append("-y");
        cmd.append("-i");
        cmd.append(mOutVideoPath);
        cmd.append("-filter_complex");
        cmd.append("vflip,hflip");
        cmd.append("-preset");
        cmd.append("ultrafast");
        File file = new File(ISSAVEVIDEOTEMPEXIST);
        if (!file.exists()){
            file.mkdir();
        }
        mSavevideotemp = ISSAVEVIDEOTEMPEXIST+ System.currentTimeMillis() + ".mp4";
        cmd.append(mSavevideotemp);
        isLocalPortrait = false;
        exec(cmd);
    }

    public void exec(CmdList cmdList) {
        mPopVideoLoadingFl.setVisibility(View.VISIBLE);
//        progressDialog = DialogManager.showProgressDialog(mContext);
        String[] cmds = cmdList.toArray(new String[cmdList.size()]);
        StringBuffer stringBuffer = new StringBuffer();
        for (String ss : cmds) {
            stringBuffer.append(ss).append(" ");
            Log.e("EpMediaF", "cmd:" + ss + "     stringBuffer :  " + stringBuffer.toString());
        }
        EpEditor.execCmd(stringBuffer.toString(), 0, new OnEditorListener() {
            @Override
            public void onSuccess() {
                isFailure = false;
                if (!isLocalPortrait) {
                    if (!TextUtils.isEmpty(mSavevideotemp)) {
                        if (new File(mOutVideoPath).exists()) {
                            new File(mOutVideoPath).delete();
                        }
                        VideoPlayerActivity2.launch(LocalVideoActivity.this,mSavevideotemp);
                    } else {
                        VideoPlayerActivity2.launch(LocalVideoActivity.this,mOutVideoPath);
                    }
                   myHandler.sendEmptyMessage(CLIPPER_GONE);
                } else {
                    translateVideo();
                }
            }

            @Override
            public void onFailure() {
                isFailure = true;
                myHandler.sendEmptyMessage(CLIPPER_GONE);
            }

            @Override
            public void onProgress(float v) {
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (resumed) {
            mLocalVideoView.start();
        }
        resumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        isDestroy = true;
        mLocalVideoView.onDestroy();
        for (int i = 0; i < mThumbBitmap.size(); i++) {
            mThumbBitmap.get(i).recycle();
        }
        mThumbBitmap = null;
        System.gc();
        mAsyncTask.cancel(true);
        mAsyncTask = null;
    }

    @Override
    public void onBackPressed() {
        if (mPopVideoLoadingFl != null && mPopVideoLoadingFl.getVisibility() == View.GONE) {
            super.onBackPressed();
        }
    }

    private boolean resumed;
    private boolean isDestroy;
    private boolean isPlaying = false;
    static final int VIDEO_PREPARE = 0;
    static final int VIDEO_START = 1;
    static final int VIDEO_UPDATE = 2;
    static final int VIDEO_PAUSE = 3;
    static final int VIDEO_CUT_FINISH = 4;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_PREPARE:
                    Executors.newSingleThreadExecutor().execute(update);
                    break;
                case VIDEO_START:
                    isPlaying = true;
                    break;
                case VIDEO_UPDATE:
                  /*  int curDuration = mVideoView.getCurDuration();
                    if (curDuration > startPoint + clipDur) {
                        mVideoView.seekTo(startPoint);
                        mVideoView.start();
                    }*/
                    break;
                case VIDEO_PAUSE:
                    isPlaying = false;
                    break;
                case VIDEO_CUT_FINISH:
                    finish();
                    //TODO　已经渲染完毕了　
                    break;
            }
        }
    };
    private Runnable update = new Runnable() {
        @Override
        public void run() {
            while (!isDestroy) {
                if (!isPlaying) {
                    try {
                        Thread.currentThread().sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                mHandler.sendEmptyMessage(VIDEO_UPDATE);
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onVideoPrepare() {
        mHandler.sendEmptyMessage(VIDEO_PREPARE);
    }

    @Override
    public void onVideoStart() {
        mHandler.sendEmptyMessage(VIDEO_START);
    }

    @Override
    public void onVideoPause() {
        mHandler.sendEmptyMessage(VIDEO_PAUSE);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mLocalVideoView.seekTo(0);
        mLocalVideoView.start();
    }

    @Override
    public void onVideoChanged(VideoInfo info) {
    }

    public int mVideoHeight, mVideoWidth, mVideoDuration;
    private Context mContext;

    private void initThumbs() {
        final MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(mContext, Uri.parse(mInputVideoPath));
        mVideoRotation = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        mVideoWidth = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        mVideoHeight = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        mVideoDuration = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        if(mVideoDuration /1000 > 30){
            mThumbSelTime = 30;
        }else {
            mThumbSelTime = mVideoDuration / 1000;
        }
        mEndTime = (mVideoDuration + 100) / 1000;
        if (mEndTime < 30) {
            mLocalSelTimeTv.setText("已选取" + mEndTime + "秒");
        }
        final int frame;
        final int frameTime;
        if (mVideoDuration >= 29900 && mVideoDuration < 30300) {
            frame = 10;
            frameTime = mVideoDuration / frame * 1000;
        } else {
            frameTime = 3000 * 1000;
            frame = mVideoDuration * 1000 / frameTime;
        }
        mAsyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                myHandler.sendEmptyMessage(ClEAR_BITMAP);
                for (int x = 0; x < frame; x++) {
                    Bitmap bitmap = mediaMetadata.getFrameAtTime(frameTime * x, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    Message msg = myHandler.obtainMessage();
                    msg.what = SAVE_BITMAP;
                    msg.obj = bitmap;
                    msg.arg1 = x;
                    Log.e(TAG, "doInBackground: " + x);
                    myHandler.sendMessage(msg);
                }
                mediaMetadata.release();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                myHandler.sendEmptyMessage(SUBMIT);
            }
        };
        mAsyncTask.execute();
    }

    private List<Bitmap> mThumbBitmap = new ArrayList<>();

    private static class MyHandler extends Handler {
        private WeakReference<LocalVideoActivity> mWeakReference;

        public MyHandler(LocalVideoActivity localVideoActivity) {
            mWeakReference = new WeakReference<LocalVideoActivity>(localVideoActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            final LocalVideoActivity localVideoActivity = mWeakReference.get();
            if (localVideoActivity != null) {
                switch (msg.what) {
                    case CLIPPER_FAILURE:
                        Toast.makeText(localVideoActivity.mContext, "视频编译失败，请换个视频试试", Toast.LENGTH_LONG).show();
                    case CLIPPER_GONE:
                        localVideoActivity.mPopVideoLoadingFl.setVisibility(View.GONE);
                        break;
                    case ClEAR_BITMAP:
                        localVideoActivity.mThumbBitmap.clear();
                        break;
                    case SAVE_BITMAP:
                        if (localVideoActivity.mThumbBitmap != null) {
                            localVideoActivity.mThumbBitmap.add(msg.arg1, (Bitmap) msg.obj);
                        }
                        break;
                    case SUBMIT:
                        localVideoActivity.mThumbAdapter.addThumb(localVideoActivity.mThumbBitmap);
                        localVideoActivity.mThumbAdapter.setLoadSuccessCallBack(new ThumbAdapter.LoadSuccessCallBack() {
                            @Override
                            public void callback() {
                                //获取recyclerView在屏幕中的长度1080
                                localVideoActivity.mRecyclerWidth = localVideoActivity.mLocalRecyclerView.computeHorizontalScrollExtent();
                                //获取recyclerView所有item的长度3420
                                localVideoActivity.mTotolWidth = localVideoActivity.mLocalRecyclerView.computeHorizontalScrollRange();
                                int i = localVideoActivity.mLocalRecyclerView.computeHorizontalScrollRange();
                                if (i < MyApplication.screenWidth) {
                                    if (i > MyApplication.screenWidth / 6) {
                                        localVideoActivity.mLocalThumbView.setWidth(i + DisplayUtil.dp2px(localVideoActivity,1));
                                    } else {
                                        localVideoActivity.mLocalThumbView.setWidth(MyApplication.screenWidth / 6 - DisplayUtil.dp2px(localVideoActivity,10));
                                    }
                                }
                                Log.e(TAG, "callback: " + i);
                            }
                        });
                        break;
                }
            }
        }
    }


    private Handler myHandler = new MyHandler(this);

}
