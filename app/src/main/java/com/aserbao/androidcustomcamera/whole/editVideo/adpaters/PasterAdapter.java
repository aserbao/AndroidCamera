package com.aserbao.androidcustomcamera.whole.editVideo.adpaters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aserbao.androidcustomcamera.R;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * <pre>
 *     author : Administrator (Jacket)
 *     e-mail : 378315764@qq.com
 *     time   : 2018/01/31
 *     desc   :
 *     version: 3.2
 * </pre>
 */

public class PasterAdapter extends RecyclerView.Adapter<PasterAdapter.ViewHolder> {

    private String TAG = PagerAdapter.class.getSimpleName();
    private Context context;
    private int[] imgList;

    private int[] imagesGif = new int[]{
            R.raw.aini, R.raw.dengliao, R.raw.baituole, R.raw.burangwo, R.raw.bufuhanzhe, R.raw.nizabushagntian, R.raw.zan, R.raw.buyue, R.raw.nizaidouwo, R.raw.gandepiaoliang, R.raw.xiase
    };

    public PasterAdapter(Context context, int[] imgList) {
        this.context = context;
        this.imgList = imgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_paster, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.pasterview.setImageResource(imgList[position]);
    }

    @Override
    public int getItemCount() {
        return imgList == null ? 0 : imgList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pasterview)
        ImageView pasterview;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.pasterview})
        public void onViewClicked(View view) {
            switch (view.getId()) {
                case R.id.pasterview:
                    if (pasterItemSelectListener != null) {
                        pasterItemSelectListener.pasterItemSelect(imgList[getLayoutPosition()], imagesGif[getLayoutPosition()]);
                    }
                    break;
            }
        }
    }

    public interface PasterItemSelectListener {
        void pasterItemSelect(int resourseId, int gifId);
    }

    PasterItemSelectListener pasterItemSelectListener;

    public void setPasterItemSelectListener(PasterItemSelectListener pasterItemSelectListener) {
        this.pasterItemSelectListener = pasterItemSelectListener;
    }
}
