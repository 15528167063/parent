package com.hzkc.parent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hzkc.parent.Bean.VipMdllistBean;
import com.hzkc.parent.R;

import java.util.List;


public class VipListAdapter extends RecyclerView.Adapter<VipListAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "DemoAdapter";
    private List<VipMdllistBean> mItems;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    public String childuuid;


    public VipListAdapter(List<VipMdllistBean> items, Context context) {
        this.mItems = items;
        this.context=context;
    }
    public VipListAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final VipMdllistBean mdllist = mItems.get(position);
        holder.itemView.setTag(position);
        holder.tv_name.setText(mdllist.getModel_cname());
        Glide.with(context).load(mdllist.getImg_url()).into(holder.iv_pic);
    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public ImageView iv_pic;
        public View view;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_pic = (ImageView) itemView.findViewById(R.id.iv_pic);
            view = itemView.findViewById(R.id.view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
