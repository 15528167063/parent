package com.hzkc.parent.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hzkc.parent.R;

import java.util.List;


/**
 * Created by Administrator on 2016/7/11.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mDatas;
    private ViewHolder viewHolder;
    public ImageAdapter(Context context, List<String> mDatas){
        this.mContext = context;
        this.mDatas = mDatas;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image,parent,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;



    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(this.mDatas.size()==1){
            ViewGroup.LayoutParams params = holder.simple_image.getLayoutParams();
            params.width = (int) mContext.getResources().getDimension(R.dimen.width_140dp);
            params.height = (int) mContext.getResources().getDimension(R.dimen.height_140dp);
        }
        holder.simple_image.setImageURI(Uri.parse(mDatas.get(position)));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setData(List<String> data) {
        this.mDatas = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView simple_image;
        public ViewHolder(View itemView) {
            super(itemView);
            simple_image= (SimpleDraweeView) itemView.findViewById(R.id.simple_image);
        }
    }
}
