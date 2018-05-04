package com.aserbao.androidcustomcamera.blocks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.activity.BaseActivity;
import com.aserbao.androidcustomcamera.base.adapter.CommonAdapter;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;
import com.aserbao.androidcustomcamera.blocks.mediacodec.MediaCodecActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlocksActivity extends BaseActivity {

    @BindView(R.id.blocks_rv)
    RecyclerView mBlocksRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocks);
    }
    @Override
    public int setLayoutId() {
        return R.layout.activity_blocks;
    }

    @Override
    protected void trimView() {
        mBlocksRv.setLayoutManager(mLinearLayoutManager);
        mBlocksRv.setAdapter(mCommonAdapter);
    }

    @Override
    public List<ClassBean> initData() {
        List<ClassBean> mClasses = new ArrayList<>();
        mClasses.add(new ClassBean("MediaCodec", MediaCodecActivity.class));
        return mClasses;
    }

}
