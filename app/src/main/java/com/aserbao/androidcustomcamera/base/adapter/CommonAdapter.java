package com.aserbao.androidcustomcamera.base.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.beans.ClassBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * description:
 * Created by aserbao on 2018/5/4.
 */

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.CommonViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private List<ClassBean> mClassBeen = new ArrayList<>();

    public CommonAdapter(Context context, Activity activity, List<ClassBean> classBeen) {
        mContext = context;
        mActivity = activity;
        mClassBeen = classBeen;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.common_item, parent, false);
        return new CommonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        if(mClassBeen != null && position < mClassBeen.size()) {
            final ClassBean classBean = mClassBeen.get(position);
            holder.mBtnItemCommon.setText(classBean.getName());
            holder.mBtnItemCommon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.startActivity(new Intent(mActivity, classBean.getClazz()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if (mClassBeen.size() > 0) {
            ret = mClassBeen.size();
        }
        return ret;
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_item_common)
        Button mBtnItemCommon;
        public CommonViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
