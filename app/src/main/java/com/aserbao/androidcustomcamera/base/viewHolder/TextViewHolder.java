package com.aserbao.androidcustomcamera.base.viewHolder;

import android.view.View;
import android.widget.TextView;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.base.beans.BaseRecyclerBean;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextViewHolder extends BaseClickViewHolder {
        @BindView(R.id.base_recycler_view_item_tv)
        public TextView mBaseRecyclerViewItemTv;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setDataSource(BaseRecyclerBean classBean, int position, IBaseRecyclerItemClickListener mIBaseRecyclerItemClickListener){
            super.setDataSource(position,mIBaseRecyclerItemClickListener);
            int tag = classBean.getTag();
            String name = classBean.getName();
            if (tag >= 0) {
                itemView.setTag(tag);
                name = name + String.valueOf(tag);
            } else {
                name = name + String.valueOf(position);
            }
            mBaseRecyclerViewItemTv.setText(name);
        }

}