package com.aserbao.androidcustomcamera.whole.selCover;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;
import com.aserbao.androidcustomcamera.base.utils.StatusBarUtil;
import com.aserbao.androidcustomcamera.whole.selCover.view.ThumbnailSelTimeView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.COMR_FROM_SEL_COVER_TIME_ACTIVITY;

public class SelCoverTimeActivity extends AppCompatActivity {

    private static final int SEL_TIME = 0;
    private static final int SUBMIT = 1;
    private static final int SAVE_BITMAP = 2;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.cut_time_finish_tv)
    TextView mCutTimeFinishTv;
    @BindView(R.id.rl_title)
    RelativeLayout mRlTitle;
    @BindView(R.id.cut_recycler_view)
    RecyclerView mCutRecyclerView;
    @BindView(R.id.thumb_sel_time_view)
    ThumbnailSelTimeView mThumbSelTimeView;
    @BindView(R.id.sel_cover_video_view)
    VideoView mSelCoverVideoView;
    @BindView(R.id.sel_cover_tv)
    TextView mSelCoverTv;
    @BindView(R.id.sel_cover_frame_layout)
    FrameLayout mSelCoverFrameLayout;
    private List<Bitmap> mBitmapList = new ArrayList<>();
    private String mVideoPath = "/storage/emulated/0/ych/321.mp4";
    public SelCoverAdapter mSelCoverAdapter;
    private float mSelStartTime = 0.5f;
    private boolean mIsSelTime;//是否点了完成按钮
    public String mVideoRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_cover_time);
        ButterKnife.bind(this);
        mVideoPath = getIntent().getStringExtra(StaticFinalValues.VIDEOFILEPATH);
        initThumbs();
        initSetParam();
        initView();
        initListener();
        StatusBarUtil.transparencyBar(this);
    }

    private void initListener() {
        mThumbSelTimeView.setOnScrollBorderListener(new ThumbnailSelTimeView.OnScrollBorderListener() {
            @Override
            public void OnScrollBorder(float start, float end) {
            }

            @Override
            public void onScrollStateChange() {
                myHandler.removeMessages(SEL_TIME);
                float rectLeft = mThumbSelTimeView.getRectLeft();
                mSelStartTime = (mVideoDuration * rectLeft) / 1000;
                Log.e("Atest", "onScrollStateChange: " +mSelStartTime );
                mSelCoverVideoView.seekTo((int) mSelStartTime);
                myHandler.sendEmptyMessage(SEL_TIME);
            }
        });
    }

    private void initSetParam() {
        ViewGroup.LayoutParams layoutParams = mSelCoverVideoView.getLayoutParams();
        if(mVideoRotation.equals("0") && mVideoWidth > mVideoHeight) {//本地视频横屏 0表示竖屏
            layoutParams.width = 1120;
            layoutParams.height = 630;
        }else{
            layoutParams.width = 630;
            layoutParams.height = 1120;
        }

        mSelCoverVideoView.setLayoutParams(layoutParams);
        mSelCoverVideoView.setVideoPath(mVideoPath);
        mSelCoverVideoView.start();
        mSelCoverVideoView.getDuration();
    }

    private void initView() {
        mSelCoverAdapter = new SelCoverAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        mCutRecyclerView.setLayoutManager(linearLayoutManager);
        mCutRecyclerView.setAdapter(mSelCoverAdapter);
    }

    public int mVideoHeight, mVideoWidth, mVideoDuration;

    private void initThumbs() {
        final MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(this, Uri.parse(mVideoPath));
        mVideoRotation = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        mVideoWidth = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        mVideoHeight = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        mVideoDuration = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        final int frame = 10;
        final int frameTime = mVideoDuration / frame * 1000;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                for (int x = 0; x < frame; x++) {
                    Bitmap bitmap = mediaMetadata.getFrameAtTime(frameTime * x, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    Message msg = myHandler.obtainMessage();
                    msg.what = SAVE_BITMAP;
                    msg.obj = bitmap;
                    msg.arg1 = x;
                    myHandler.sendMessage(msg);
                }
                mediaMetadata.release();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                myHandler.sendEmptyMessage(SUBMIT);
            }
        }.execute();
    }

    private Handler myHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private WeakReference<SelCoverTimeActivity> mActivityWeakReference;

        public MyHandler(SelCoverTimeActivity  activityWeakReference) {
            mActivityWeakReference = new WeakReference<SelCoverTimeActivity>(activityWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            SelCoverTimeActivity activity = mActivityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case SEL_TIME:
                        activity.mSelCoverVideoView.seekTo((int) activity.mSelStartTime * 1000 );
                        activity.mSelCoverVideoView.start();
                        sendEmptyMessageDelayed(SEL_TIME,1000);
                        break;
                    case SAVE_BITMAP:
                        activity.mBitmapList.add(msg.arg1, (Bitmap) msg.obj);
                        break;
                    case SUBMIT:
                        activity.mSelCoverAdapter.addBitmapList(activity.mBitmapList);
                        sendEmptyMessageDelayed(SEL_TIME,1000);
                        break;
                }
            }
        }
    }


    @OnClick({R.id.iv_back, R.id.cut_time_finish_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.cut_time_finish_tv:
                mIsSelTime = true;
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if(mIsSelTime){
            if(mSelStartTime < 0.5f){
                mSelStartTime = 0.5f;
            }
            intent.putExtra(StaticFinalValues.CUT_TIME,mSelStartTime);
        }else{
            intent.putExtra(StaticFinalValues.CUT_TIME,0.5f);
        }
        setResult(COMR_FROM_SEL_COVER_TIME_ACTIVITY,intent);
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase)
        {
            @Override
            public Object getSystemService(String name)
            {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }
}
