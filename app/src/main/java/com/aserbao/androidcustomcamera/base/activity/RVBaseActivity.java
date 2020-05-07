package com.aserbao.androidcustomcamera.base.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.adapter.CommonAdapter;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;
import com.aserbao.androidcustomcamera.base.utils.APermissionUtils;
import com.aserbao.androidcustomcamera.base.viewHolder.IBaseRecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RVBaseActivity extends AppCompatActivity implements IBaseRecyclerItemClickListener {
    public final String TAG = this.getClass().getCanonicalName();
    @BindView(R.id.base_rv)
    public RecyclerView mBaseRv;
    public CommonAdapter mCommonAdapter;
    public LinearLayoutManager mLinearLayoutManager;

    public List<BaseRecyclerBean> mBaseRecyclerBeen = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        initGetData();
        initView();
    }

    protected abstract void initGetData();


    protected void initView() {
        mCommonAdapter = new CommonAdapter(this, this, mBaseRecyclerBeen,this);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBaseRv.setLayoutManager(mLinearLayoutManager);
        mBaseRv.setAdapter(mCommonAdapter);
        APermissionUtils.checkPermission(this);
    }
}
