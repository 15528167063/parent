package com.hzkc.parent.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hzkc.parent.Bean.ImageInf;
import com.hzkc.parent.R;
import com.hzkc.parent.utils.ImageScan;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/7/26.
 */

public class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ImageInf> mDatas;
    private Context mContext;
    private static final int TYPE_IMAGE_ITEM = 1;
    private static final int TYPE_BUTTON_ADD = 2;
    private OnAddImageClickListener onAddImageClickListener;
    private int ImageCount = 9;

    public ImageListAdapter(ArrayList<ImageInf> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        //ToastUtils.showToast(mContext, mDatas.size() + "总共");
    }

    public void setDatas(ArrayList<ImageInf> datas) {
        this.mDatas = datas;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDatas.size()) {
            return TYPE_BUTTON_ADD;
        } else if (position < mDatas.size()) {
            return TYPE_IMAGE_ITEM;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE_ITEM) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.compose_image_item, parent, false);
            return new ImageListHolder(v);
        } else if (viewType == TYPE_BUTTON_ADD) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.compose_pic_add, parent, false);
            return new ImageAddHolder(v);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if (mDatas != null && mDatas.size() > 0) {
            return mDatas.size() + 1;//增加最后的增加照片按钮
        } else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ImageListHolder) {
            Uri uri = Uri.parse("file:///" + mDatas.get(position).getImageFile().getAbsolutePath());
            ImageScan.showThumb(mContext, uri, ((ImageListHolder) holder).image);
            ((ImageListHolder) holder).close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatas.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mDatas.size() + 1);
                }
            });
        } else if (holder instanceof ImageAddHolder) {
            if (position == ImageCount) {
                ((ImageAddHolder) holder).mView.setVisibility(View.GONE);
            } else {
                ((ImageAddHolder) holder).mView.setVisibility(View.VISIBLE);
            }
        }
    }


    public class ImageListHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView image;
        ImageView close;

        public ImageListHolder(View itemView) {
            super(itemView);
            image=(SimpleDraweeView)itemView.findViewById(R.id.image);
            close=(ImageView)itemView.findViewById(R.id.close);
        }
    }

    public class ImageAddHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView add;
        private View mView;

        public ImageAddHolder(View itemView) {
            super(itemView);
            mView = itemView;
            add=(ImageView)itemView.findViewById(R.id.add);
            add.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add:
                    onAddImageClickListener.OnAddImageClick();
                    break;
                default:
                    break;
            }
        }
    }

    public interface OnAddImageClickListener {
        void OnAddImageClick();
    }

    public void setOnAddImageClickListener(OnAddImageClickListener onAddImageClickListener) {
        this.onAddImageClickListener = onAddImageClickListener;
    }
}
