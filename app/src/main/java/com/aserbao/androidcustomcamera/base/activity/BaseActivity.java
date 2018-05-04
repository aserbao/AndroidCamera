package com.aserbao.androidcustomcamera.base.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.adapter.CommonAdapter;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;

import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    public CommonAdapter mCommonAdapter;
    public LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        ButterKnife.bind(this);
        initData();
        initView();
        trimView();
    }

    protected void initView() {
        mCommonAdapter = new CommonAdapter(this, this, initData());
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    public abstract int setLayoutId();
    public abstract List<ClassBean> initData();
    protected abstract void trimView();
}
