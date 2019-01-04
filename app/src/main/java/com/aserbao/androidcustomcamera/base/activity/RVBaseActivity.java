package com.aserbao.androidcustomcamera.base.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.adapter.CommonAdapter;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;
import com.aserbao.androidcustomcamera.base.utils.APermissionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RVBaseActivity extends AppCompatActivity {

    @BindView(R.id.base_rv)
    RecyclerView mBaseRv;
    public CommonAdapter mCommonAdapter;
    public LinearLayoutManager mLinearLayoutManager;

    public List<ClassBean> mClassBeans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        mClassBeans = new ArrayList<>();
        initView();
    }


    protected void initView() {
        mCommonAdapter = new CommonAdapter(this, this, initData());
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBaseRv.setLayoutManager(mLinearLayoutManager);
        mBaseRv.setAdapter(mCommonAdapter);
        APermissionUtils.checkPermission(this);
    }

    public abstract List<ClassBean> initData();
}
