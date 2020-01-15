package com.aserbao.androidcustomcamera.base.viewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * 功能:
 *
 * @author aserbao
 * @date : On 2019-08-16 15:39
 * @project:AserbaosAndroid
 * @package:com.aserbao.aserbaosandroid.base.viewHolder
 */
public class BaseClickViewHolder extends RecyclerView.ViewHolder {

    public static final int COME_FROM_RV_ITEM = 0;

    public BaseClickViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void setDataSource(final int position, final IBaseRecyclerItemClickListener mIBaseRecyclerItemClickListener){
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempFlag = getTempFlag(v, position);
                if (mIBaseRecyclerItemClickListener != null) {
                    mIBaseRecyclerItemClickListener.itemClickBack(v, tempFlag,false, COME_FROM_RV_ITEM);
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int tempFlag = getTempFlag(v, position);
                if (mIBaseRecyclerItemClickListener != null) {
                    mIBaseRecyclerItemClickListener.itemClickBack(v, tempFlag,true, COME_FROM_RV_ITEM);
                }
                return true;
            }
        });
    }

    private int getTempFlag(View v, int position) {
        int tempFlag = 0;
        Object viewTag = v.getTag();
        if (viewTag instanceof Integer) {
            int tag = (int) viewTag;
            if (tag >= 0) {
                tempFlag = tag;
            } else {
                tempFlag = position;
            }
        } else {
            tempFlag = position;
        }
        return tempFlag;
    }

}
