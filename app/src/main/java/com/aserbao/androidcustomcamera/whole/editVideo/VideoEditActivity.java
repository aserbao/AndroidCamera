package com.aserbao.androidcustomcamera.whole.editVideo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.utils.StaticFinalValues;
import com.aserbao.androidcustomcamera.base.utils.StatusBarUtil;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.MediaPlayerWrapper;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.VideoInfo;
import com.aserbao.androidcustomcamera.whole.createVideoByVoice.localEdit.VideoPreviewView;
import com.aserbao.androidcustomcamera.whole.editVideo.fragment.FilterDialogFragment;
import com.aserbao.androidcustomcamera.whole.editVideo.mediacodec.VideoClipper;
import com.aserbao.androidcustomcamera.whole.editVideo.view.BaseImageView;
import com.aserbao.androidcustomcamera.whole.editVideo.view.BubbleInputDialog;
import com.aserbao.androidcustomcamera.whole.editVideo.view.BubbleTextView;
import com.aserbao.androidcustomcamera.whole.editVideo.view.DynamicImageView;
import com.aserbao.androidcustomcamera.whole.editVideo.view.PopBubbleEditView;
import com.aserbao.androidcustomcamera.whole.editVideo.view.PopBubbleView;
import com.aserbao.androidcustomcamera.whole.editVideo.view.PopPasterView;
import com.aserbao.androidcustomcamera.whole.editVideo.view.StickInfoImageView;
import com.aserbao.androidcustomcamera.whole.editVideo.view.StickerView;
import com.aserbao.androidcustomcamera.whole.editVideo.view.VideoEditView;
import com.aserbao.androidcustomcamera.whole.record.filters.GifDecoder;
import com.aserbao.androidcustomcamera.whole.record.other.MagicFilterType;
import com.aserbao.androidcustomcamera.whole.record.ui.SlideGpuFilterGroup;
import com.aserbao.androidcustomcamera.whole.videoPlayer.VideoPlayerActivity2;
import com.aserbao.androidcustomcamera.whole.videoPlayer.VideoViewPlayerActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.BUNDLE;
import static com.aserbao.androidcustomcamera.base.utils.StaticFinalValues.STORAGE_TEMP_VIDEO_PATH;


public class VideoEditActivity extends FragmentActivity implements PopBubbleView.BubbleSelectListener, PopPasterView.PasterSelectListener, MediaPlayerWrapper.IMediaCallback, SlideGpuFilterGroup.OnFilterChangeListener, View.OnTouchListener, VideoEditView.OnSelectTimeChangeListener {
    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;
    @BindView(R.id.tv_hint)
    TextView mTvHint;
    @BindView(R.id.pop_video_loading_fl)
    FrameLayout mPopVideoLoadingFl;
    private String TAG = VideoEditActivity.class.getSimpleName();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.ll_edit_seekbar)
    VideoEditView videoEditView;
    @BindView(R.id.ll_select_bar)
    LinearLayout llSelectBar;
    @BindView(R.id.rl_content_root)
    FrameLayout mContentRootView;
    @BindView(R.id.bigicon_play)
    ImageView bigiconPlay;
    @BindView(R.id.video_preview)
    VideoPreviewView mVideoView;
    @BindView(R.id.ll_add_filter)
    TextView mLlAddFilterTv;
    @BindView(R.id.pop_video_percent_tv)
    TextView mPopVideoPercentTv;
    //当前处于编辑状态的贴纸
    private StickerView mCurrentView;
    //当前处于编辑状态的气泡
    private BubbleTextView mCurrentEditTextView;
    //存储贴纸列表
    private ArrayList<BaseImageView> mViews = new ArrayList<>();
    //气泡输入框
    private BubbleInputDialog mBubbleInputDialog;

    private ArrayList<StickInfoImageView> stickerViews = new ArrayList<>();
    private ArrayList<DynamicImageView> dynamicImageViews = new ArrayList<>();

    private PopBubbleView popBubbleView;
    private PopPasterView popPasterView;

    private int[] bubbleArray = new int[]{
            R.drawable.bubbleone, R.drawable.bubbletwo, R.drawable.bubblethree, R.drawable.bubblefour, R.drawable.bubblefive, R.drawable.bubblesix, R.drawable.bubbleseven, R.drawable.bubbleeight
    };
    private String mVideoPath = "/storage/emulated/0/ych/1234.mp4";
    public int mVideoHeight, mVideoWidth, mVideoDuration; //mIsNotComeLocal 1表示拍摄,mIsAnswer 1表示回答者
    private Context mContext;
    private boolean hasSelectStickerView;
    private float mPixel = 1.778f;
    private long lastTime = 0;
    private boolean isVideoPause = false;
    private PopBubbleEditView popBubbleEditView;
    private long currentTime;
    private boolean isPlayVideo;
    public String mVideoRotation = "90";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);
        ButterKnife.bind(this);
        mContext = this;

        mBubbleInputDialog = new BubbleInputDialog(this);
        mBubbleInputDialog.setCompleteCallBack(new BubbleInputDialog.CompleteCallBack() {
            @Override
            public void onComplete(View bubbleTextView, String str) {
                ((BubbleTextView) bubbleTextView).setText(str);
            }
        });
        initData();
        initListener();
        mThumbBitmap.clear();
        StatusBarUtil.transparencyBar(this);
    }

    private void initData() {
        mVideoPath = getIntent().getStringExtra(StaticFinalValues.VIDEOFILEPATH);
        initThumbs();
        ArrayList<String> srcList = new ArrayList<>();
        srcList.add(mVideoPath);
        mVideoView.setVideoPath(srcList);
        initSetParam();
    }
    private void initListener(){
        mVideoView.setOnFilterChangeListener(this);
        mVideoView.setIMediaCallback(this);
        videoEditView.setOnSelectTimeChangeListener(this);
    }

    private void initSetParam() {
        ViewGroup.LayoutParams layoutParams = mContentRootView.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = mVideoView.getLayoutParams();
        if (!TextUtils.isEmpty(mVideoRotation) && mVideoRotation.equals("0") && mVideoWidth > mVideoHeight ||  mVideoRotation.equals("180") && mVideoWidth > mVideoHeight) {//本地视频横屏
            layoutParams.width = 1120;
            layoutParams.height = 630;
            layoutParams1.width = 1120;
            layoutParams1.height = 630;
        } else {
//            layoutParams.width = (int) (mVideoWidth * StaticFinalValues.VIDEO_WIDTH_HEIGHT);
            layoutParams.width = 630;
            layoutParams.height = 1120;
            layoutParams1.width = 630;
            layoutParams1.height = 1120;
        }
        mContentRootView.setLayoutParams(layoutParams);
        mVideoView.setLayoutParams(layoutParams1);

    }

    int mFilterSel = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mPopVideoLoadingFl != null &&  mPopVideoLoadingFl.getVisibility() == View.VISIBLE){
            Log.e(TAG, "dispatchTouchEvent: ");
            return true;
        }else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @OnClick({R.id.rl_content_root, R.id.iv_back, R.id.ll_add_sticker, R.id.ll_add_subtitle, R.id.edit_video_next_tv, R.id.ll_play_video, R.id.ll_add_filter})
    public void onViewClicked(View view) {
        if (System.currentTimeMillis() - lastTime < 500 || mPopVideoLoadingFl != null && mPopVideoLoadingFl.getVisibility() == View.VISIBLE) {
            return;
        }
        lastTime = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.rl_content_root:
                if (mCurrentEditTextView != null) {
                    mCurrentEditTextView.setInEdit(false);
                }
                if (mCurrentView != null) {
                    mCurrentView.setInEdit(false);
                }
                break;
            case R.id.ll_add_filter:
                FilterDialogFragment dialogFragment = new FilterDialogFragment();
                Bundle bundle = new Bundle();
                dialogFragment.setArguments(bundle);
                dialogFragment.setResultListener(new FilterDialogFragment.ResultListener() {
                    @Override
                    public void result(int making, int mFilterType, int mBeauty, boolean isDismiss) {
                        mFilterSel = mFilterType;
                        filterType = StaticFinalValues.types[mFilterType];
                        mVideoView.setFilter(mFilterSel - 1);
                    }
                });
                dialogFragment.show(getFragmentManager(), "filter");
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_add_sticker:
                if (popPasterView == null) {
                    popPasterView = new PopPasterView(VideoEditActivity.this);
                    popPasterView.setPasterSelectListener(VideoEditActivity.this);
                }
                if (isPlayVideo) {
                    videoPlay();
                }
                popPasterView.show();
                break;
            case R.id.ll_add_subtitle:
                if (popBubbleView == null) {
                    popBubbleView = new PopBubbleView(VideoEditActivity.this);
                    popBubbleView.setBubbleSelectListener(VideoEditActivity.this);
                }
                if (isPlayVideo) {
                    videoPlay();
                }
                popBubbleView.show();
                break;
            case R.id.edit_video_next_tv:
                videoEditView.recoverView();
                if (mCurrentEditTextView != null) {
                    mCurrentEditTextView.setInEdit(false);
                }
                if (mCurrentView != null) {
                    mCurrentView.setInEdit(false);
                }
                mVideoView.pause();
                VideoClipper clipper = new VideoClipper();
                clipper.setInputVideoPath(mVideoPath);
                final String outputPath = STORAGE_TEMP_VIDEO_PATH;
                clipper.setFilterType(filterType);
//                clipper.setFilterType(MagicFilterType.HUDSON);
                clipper.setOutputVideoPath(outputPath);
                clipper.setOnVideoCutFinishListener(new VideoClipper.OnVideoCutFinishListener() {
                    @Override
                    public void onFinish() {
//                            VideoViewPlayerActivity.launch(VideoEditActivity.this,outputPath);
                        VideoPlayerActivity2.launch(VideoEditActivity.this,outputPath);
                            mHandler.sendEmptyMessage(VIDEO_CUT_FINISH);
                    }

                    @Override
                    public void onProgress(float percent) {
                        Message message = new Message();
                        message.what = CLIP_VIDEO_PERCENT;
                        message.obj = percent;
                        mHandler.sendMessage(message);
                    }
                });
                try {
                    int clipDur = mVideoView.getVideoDuration() * 1000;
                    clipper.clipVideo(0, clipDur, mViews, getResources());

//                    progressDialog = new PopupManager(mContext).showLoading();
                    mPopVideoLoadingFl.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_play_video:
                videoPlay();
                break;
        }
    }

    private void videoPlay() {
        Log.e(TAG,"currentTime:"+currentTime+",mVideoDuration:"+mVideoDuration);
        if(currentTime >= mVideoDuration){
            return;
        }
        for (StickInfoImageView stickInfoImageView : stickerViews) {  //清空gif图
            mContentRootView.removeView(stickInfoImageView);
        }
        stickerViews.clear();
        for (BaseImageView baseImageView : mViews) {
            baseImageView.setVisibility(View.GONE);
//            addGifView(baseImageView);
        }
        videoEditView.videoPlay(mViews);
    }


    //添加表情
    private void addStickerView(int resourceId, int gifId) {
        if(mViews.size() >= 40){
            Toast.makeText(VideoEditActivity.this, "字幕和贴纸的数量不能超过40个", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((mVideoDuration - currentTime) / 1000 < 2) {
            Toast.makeText(VideoEditActivity.this, "当前时间不足以添加贴纸", Toast.LENGTH_SHORT).show();
            return;
        }
        hasSelectStickerView = true;
        final StickerView stickerView = new StickerView(this);
//        stickerView.setImageResource(R.drawable.ic_cat);
        stickerView.setParentSize(mContentRootView.getMeasuredWidth(), mContentRootView.getMeasuredHeight());
        GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(getResources().openRawResource(gifId));
        List<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < gifDecoder.getFrameCount(); i++) {
            bitmaps.add(gifDecoder.getFrame(i));
        }
        stickerView.setBitmaps(bitmaps);
//        stickerView.setImageResource(resourceId);
        stickerView.setBitmap(bitmaps.get(bitmaps.size() / 2));
        stickerView.setGif(true);
        stickerView.setGifId(gifId);
        stickerView.setOperationListener(new StickerView.OperationListener() {
            @Override
            public void onDeleteClick() {
                mViews.remove(stickerView);
                mContentRootView.removeView(stickerView);
            }

            @Override
            public void onEdit(StickerView stickerView) {
                Log.e(TAG, "StickerView onEdit");
                hasSelectStickerView = true;
                if (mCurrentEditTextView != null) {
                    mCurrentEditTextView.setInEdit(false);
                }
                mCurrentView.setInEdit(false);
                mCurrentView = stickerView;
                mCurrentView.setInEdit(true);

                if (mViews != null && mViews.size() > 0) {
                    int position;
                    position = mViews.indexOf(mCurrentView);
                    if (position != -1) {
                        mViews.get(position).setRotateDegree(mCurrentView.getRotateDegree());
                        mViews.get(position).setViewHeight(mCurrentView.getViewHeight());
                        mViews.get(position).setViewWidth(mCurrentView.getViewWidth());
                        mViews.get(position).setX(mCurrentView.getX());
                        mViews.get(position).setY(mCurrentView.getY());
                    }
                }

                videoEditView.recoverView(mViews, stickerView, true);
                if (isPlayVideo) {   //如果已经处于播放状态，则暂停播放
                    videoEditView.videoPlay(mViews);
                }
            }

            @Override
            public void onTop(StickerView stickerView) {
                Log.e(TAG, "StickerView onTop");
                int position = mViews.indexOf(stickerView);
                if (position == mViews.size() - 1) {
                    return;
                }
                StickerView stickerTemp = (StickerView) mViews.remove(position);
                mViews.add(mViews.size(), stickerTemp);
            }


        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mContentRootView.addView(stickerView, lp);
        Log.e(TAG, " 初始位置,X=" + stickerView.getPosX() + stickerView.getBitmap().getWidth() / 2);
        Log.e(TAG, " 初始位置,Y=" + stickerView.getPosY() + stickerView.getBitmap().getHeight() / 2);
        stickerView.setX(stickerView.getPosX() + stickerView.getBitmap().getWidth() / 2);
        stickerView.setY(stickerView.getPosY() + stickerView.getBitmap().getHeight() / 2);

        stickerView.setStartTime(currentTime);
        long endTime = currentTime + 2000;
        if (endTime > mVideoDuration) {
            endTime = mVideoDuration;
        }
        stickerView.setEndTime(endTime);
        stickerView.setTimeStamp(System.currentTimeMillis());
        mViews.add(stickerView);
        setCurrentEdit(stickerView);
        videoEditView.recoverView(mViews, stickerView, false);
    }


    //添加气泡
    private void addBubble(int index) {
        if(mViews.size() >= 40){
            Toast.makeText(VideoEditActivity.this, "字幕和贴纸的数量不能超过40个", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((mVideoDuration - currentTime) / 1000 < 2) {
            Toast.makeText(VideoEditActivity.this, "当前时间不足以添加贴纸", Toast.LENGTH_SHORT).show();
            return;
        }
        hasSelectStickerView = false;
        final BubbleTextView bubbleTextView = new BubbleTextView(this,
                Color.BLACK, 0, index);
//        bubbleTextView.setImageResource(R.drawable.bubble_7_rb);
        bubbleTextView.setParentSize(mContentRootView.getMeasuredWidth(), mContentRootView.getMeasuredHeight());
        bubbleTextView.setImageResource(bubbleArray[index]);
        bubbleTextView.setGif(false);
        bubbleTextView.setOperationListener(new BubbleTextView.OperationListener() {
            @Override
            public void onDeleteClick() {
                Log.e(TAG, "BubbleTextView onDeleteClick");
                mViews.remove(bubbleTextView);
                mContentRootView.removeView(bubbleTextView);
            }

            @Override
            public void onEdit(BubbleTextView bubbleTextView) {
                Log.e(TAG, "BubbleTextView onEdit");
                hasSelectStickerView = false;
                if (mCurrentView != null) {
                    mCurrentView.setInEdit(false);
                }
                mCurrentEditTextView.setInEdit(false);
                mCurrentEditTextView = bubbleTextView;
                mCurrentEditTextView.setInEdit(true);

                if (mViews != null && mViews.size() > 0) {
                    int position;
                    position = mViews.indexOf(mCurrentEditTextView);
                    if (position != -1) {
                        mViews.get(position).setRotateDegree(mCurrentEditTextView.getRotateDegree());
                        mViews.get(position).setViewHeight(mCurrentEditTextView.getViewHeight());
                        mViews.get(position).setViewWidth(mCurrentEditTextView.getViewWidth());
                        mViews.get(position).setX(mCurrentEditTextView.getX());
                        mViews.get(position).setY(mCurrentEditTextView.getY());
                    }
                }

                videoEditView.recoverView(mViews, bubbleTextView, true);

                if (isPlayVideo) {   //如果已经处于播放状态，则暂停播放
                    videoEditView.videoPlay(mViews);
                }
            }

            @Override
            public void onClick(BubbleTextView bubbleTextView) {
//                mBubbleInputDialog.setBubbleTextView(bubbleTextView);
//                mBubbleInputDialog.show();
                if (popBubbleEditView == null) {
                    popBubbleEditView = new PopBubbleEditView(VideoEditActivity.this);
                    popBubbleEditView.setOnTextSendListener(new PopBubbleEditView.OnTextSendListener() {
                        @Override
                        public void onTextSend(String text) {
                            mCurrentEditTextView.setText(text);
                        }
                    });
                }
                popBubbleEditView.show(bubbleTextView.getmStr());
            }

            @Override
            public void onTop(BubbleTextView bubbleTextView) {
                int position = mViews.indexOf(bubbleTextView);
                if (position == mViews.size() - 1) {
                    return;
                }
                BubbleTextView textView = (BubbleTextView) mViews.remove(position);
                mViews.add(mViews.size(), textView);
            }

        });
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mContentRootView.addView(bubbleTextView, lp);

        bubbleTextView.setStartTime(currentTime);
        long endTime = currentTime + 2000;
        if (endTime > mVideoDuration) {
            endTime = mVideoDuration;
        }
        bubbleTextView.setEndTime(endTime);
        bubbleTextView.setTimeStamp(System.currentTimeMillis());
        mViews.add(bubbleTextView);
        setCurrentEdit(bubbleTextView);
        videoEditView.recoverView(mViews, bubbleTextView, false);
    }

    /**
     * 设置当前处于编辑模式的贴纸
     */
    private void setCurrentEdit(StickerView stickerView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        if (mCurrentEditTextView != null) {
            mCurrentEditTextView.setInEdit(false);
        }
        mCurrentView = stickerView;
        stickerView.setInEdit(true);
    }

    /**
     * 设置当前处于编辑模式的气泡
     */
    private void setCurrentEdit(BubbleTextView bubbleTextView) {
        if (mCurrentView != null) {
            mCurrentView.setInEdit(false);
        }
        if (mCurrentEditTextView != null) {
            mCurrentEditTextView.setInEdit(false);
        }
        mCurrentEditTextView = bubbleTextView;
        mCurrentEditTextView.setInEdit(true);
    }
    //字幕选择接口回调
    @Override
    public void bubbleSelect(int bubbleIndex) {
        addBubble(bubbleIndex);
    }

    @Override
    public void pasterSelect(int resourceId, int gifId) {
        addStickerView(resourceId, gifId);
    }

    //视频播放接口
    private boolean isDestroy;
    private boolean isPlaying = false;
    static final int VIDEO_PREPARE = 0;
    static final int VIDEO_START = 1;
    static final int VIDEO_UPDATE = 2;
    static final int VIDEO_PAUSE = 3;
    static final int VIDEO_CUT_FINISH = 4;
    static final int CLIP_VIDEO_PERCENT = 5;
    static final int AUTO_PAUSE = 6;
    private MagicFilterType filterType = MagicFilterType.NONE;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CLIP_VIDEO_PERCENT:
                    float aFloat = (float) msg.obj;
                    mPopVideoPercentTv.setText(String.valueOf((int) (aFloat * 100)) + "%");
                    break;
                case VIDEO_PREPARE:
                    Executors.newSingleThreadExecutor().execute(update);
                    mHandler.sendEmptyMessageDelayed(AUTO_PAUSE,50);
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
                    mPopVideoPercentTv.setText("0%");
                    mPopVideoLoadingFl.setVisibility(View.GONE);
                    //TODO　已经渲染完毕了　
                    break;
                case AUTO_PAUSE:
                    mVideoView.pause();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }

        if (isPlayVideo) {
            videoPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        isDestroy = true;
        mVideoView.onDestroy();
        if (mThumbBitmap != null) {
            for (int i = 0; i < mThumbBitmap.size(); i++) {
                mThumbBitmap.get(i).recycle();
            }
            mThumbBitmap = null;
        }
        System.gc();
    }

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
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

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
        /*mVideoView.seekTo(0);
        mVideoView.start();*/
    }

    @Override
    public void onVideoChanged(VideoInfo info) {

    }

    @Override
    public void onFilterChange(MagicFilterType type) {
        this.filterType = type;
    }

    /**
     * 初始化缩略图
     */
    private void initThumbs() {
        final MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(mContext, Uri.parse(mVideoPath));
        try {
            mVideoRotation = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            mVideoWidth = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            mVideoHeight = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            finish();
        }
        mPixel = (float) mVideoHeight / (float) mVideoWidth;
        mVideoDuration = Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        Log.e(TAG, "mVideoDuration:" + mVideoDuration);
        videoEditView.setTotalTime(mVideoDuration + 100);
        final int frame = mVideoDuration / (2 * 1000);
        Log.e(TAG, "frame:" + frame);
        final int frameTime;
        if (frame > 0) {
            frameTime = mVideoDuration / frame * 1000;
        } else {
            frameTime = 1 * 1000;
        }
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                for (int x = 0; x < frame; x++) {
                    Bitmap bitmap = mediaMetadata.getFrameAtTime(frameTime * x, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    Message msg = myHandler.obtainMessage();
                    msg.obj = bitmap;
                    msg.arg1 = x;
                    myHandler.sendMessage(msg);
                }
                mediaMetadata.release();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (mThumbBitmap != null) {
                    videoEditView.addImageView(mThumbBitmap);
                }
            }
        }.execute();
    }

    private List<Bitmap> mThumbBitmap = new ArrayList<>();
    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mThumbBitmap != null) {
                mThumbBitmap.add(msg.arg1, (Bitmap) msg.obj);
            }
        }
    };


    @Override
    public void selectTimeChange(long startTime, long endTime) {
        if (mViews == null || mViews.size() == 0) {
            return;
        }
        int position;
        if (hasSelectStickerView) {
            position = mViews.indexOf(mCurrentView);
        } else {
            position = mViews.indexOf(mCurrentEditTextView);
        }
        if (position != -1) {
            mViews.get(position).setStartTime(startTime);
            mViews.get(position).setEndTime(endTime);
        }

    }


    @Override
    public void playChange(boolean isPlayVideo) {
        Log.e(TAG, "播放状态变化");
        this.isPlayVideo = isPlayVideo;
        if (isPlayVideo) {
            if (mCurrentView != null) {
                mCurrentView.setInEdit(false);
            }
            if (mCurrentEditTextView != null) {
                mCurrentEditTextView.setInEdit(false);
            }
        } else {
            for (StickInfoImageView stickInfoImageView : stickerViews) {  //清空动态贴纸
                mContentRootView.removeView(stickInfoImageView);
            }
            stickerViews.clear();
        }
        try {
            if (isPlayVideo) {
//                    mVideoView.seekTo(0);
                mVideoView.start();
            } else {
                mVideoView.pause();
            }
        } catch (Exception e) {
            Log.e(TAG, "异常:" + e);
        }
    }

    @Override
    public void videoProgressUpdate(long currentTime, boolean isVideoPlaying) {
        this.currentTime = currentTime;
        if (!isVideoPlaying) {
            try {
                Log.e(TAG, "currentTime:" + currentTime);
                mVideoView.seekTo((int) currentTime);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "异常:" + e);
            }
        }else {
            Log.e(TAG, "播放中currentTime:" + currentTime);
        }
        for (int i = 0; i < mViews.size(); i++) {              ////遍历显示静态图
            BaseImageView baseImageView = mViews.get(i);
            long startTime = baseImageView.getStartTime();
            long endTime = baseImageView.getEndTime();
            if (currentTime >= startTime && currentTime <= endTime) {
                if (baseImageView.isGif()) {
                    if (currentTime != 0) {
                        int frameIndex = baseImageView.getFrameIndex();
                        ((StickerView) baseImageView).changeBitmap(baseImageView.getBitmaps().get(frameIndex));
                        mViews.get(i).setFrameIndex(frameIndex + 1);
                    }
                    baseImageView.setVisibility(View.VISIBLE);
                } else {
                    baseImageView.setVisibility(View.VISIBLE);
                }
            } else {
                baseImageView.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void onBackPressed() {
        setResult(StaticFinalValues.COMR_FROM_VIDEO_EDIT_TIME_ACTIVITY,getIntent());
        super.onBackPressed();
    }
}
