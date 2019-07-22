package com.aserbao.androidcustomcamera.whole.pickvideo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.aserbao.androidcustomcamera.R;
import com.aserbao.androidcustomcamera.whole.pickvideo.beans.Directory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Woo
 * Date: 2018/2/27
 * Time: 13:43
 */

public class FolderListHelper {
    private PopupWindow mPopupWindow;
    private View mContentView;
    private RecyclerView rv_folder;
    private FolderListAdapter mAdapter;

    public void initFolderListView(Context ctx) {
        if (mPopupWindow == null) {
            mContentView = LayoutInflater.from(ctx).inflate(R.layout.vw_layout_folder_list, null);
            rv_folder = (RecyclerView) mContentView.findViewById(R.id.rv_folder);
            mAdapter = new FolderListAdapter(ctx, new ArrayList<Directory>());
            rv_folder.setAdapter(mAdapter);
            rv_folder.setLayoutManager(new LinearLayoutManager(ctx));
            mContentView.setFocusable(true);
            mContentView.setFocusableInTouchMode(true);

            mPopupWindow = new PopupWindow(mContentView);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setTouchable(true);
        }
    }

    public void setFolderListListener(FolderListAdapter.FolderListListener listener) {
        mAdapter.setListener(listener);
    }

    public void fillData(List<Directory> list) {
        mAdapter.refresh(list);
    }

    public void toggle(View anchor) {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            mContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            mPopupWindow.showAsDropDown(anchor,
                    (anchor.getMeasuredWidth() - mContentView.getMeasuredWidth()) / 2,
                    0);
            mPopupWindow.update(anchor, mContentView.getMeasuredWidth(),
                    mContentView.getMeasuredHeight());
        }
    }
}
