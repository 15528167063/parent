package com.hzkc.parent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hzkc.parent.Bean.MessageResult;
import com.hzkc.parent.R;
import java.util.List;

public class MyMessageAdapter extends RecyclerView.Adapter<com.hzkc.parent.adapter.MyMessageAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "DemoAdapter";
    private List<MessageResult> mItems;
    private com.hzkc.parent.adapter.MyMessageAdapter.OnItemClickListener mOnItemClickListener;
    private Context context;
    public String childuuid;


    public MyMessageAdapter(List<MessageResult> items, Context context) {
        this.mItems = items;
        this.context=context;
    }
    public com.hzkc.parent.adapter.MyMessageAdapter setOnItemClickListener(com.hzkc.parent.adapter.MyMessageAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }
    @Override
    public com.hzkc.parent.adapter.MyMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_list, parent, false);
        v.setOnClickListener(this);
        return new com.hzkc.parent.adapter.MyMessageAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final com.hzkc.parent.adapter.MyMessageAdapter.ViewHolder holder, final int position) {
        final MessageResult mdllist = mItems.get(position);
        holder.itemView.setTag(position);
        if(!TextUtils.isEmpty(mdllist.getImg_url())){
            holder.lin_01.setVisibility(View.VISIBLE);
            holder.lin_02.setVisibility(View.GONE);
            holder.tv_name1.setText(mdllist.getTitle());
            holder.tv_content1.setText(mdllist.getText());
            String substring ="";
            if(!TextUtils.isEmpty(mdllist.getSend_time())){
                 substring = mdllist.getSend_time().substring(0, mdllist.getSend_time().length() - 3);
            }
            holder.tv_time1.setText(substring);
            Glide.with(context).load(mdllist.getImg_url()).into(holder.iv_pic);
        }else {
            holder.lin_01.setVisibility(View.GONE);
            holder.lin_02.setVisibility(View.VISIBLE);
            holder.tv_name2.setText(mdllist.getTitle());
            holder.tv_content2.setText(mdllist.getText());
            String substring ="";
            if(!TextUtils.isEmpty(mdllist.getSend_time())){
                substring = mdllist.getSend_time().substring(0, mdllist.getSend_time().length() - 3);
            }
            holder.tv_time2.setText(substring);
        }
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
        public LinearLayout lin_01,lin_02;
        public TextView tv_name1;
        public TextView tv_name2;
        public TextView tv_time1;
        public TextView tv_time2;
        public TextView tv_content1;
        public TextView tv_content2;
        public ImageView iv_pic;
        public View view;
        public ViewHolder(View itemView) {
            super(itemView);
            lin_01 = (LinearLayout) itemView.findViewById(R.id.lin_01);
            lin_02 = (LinearLayout) itemView.findViewById(R.id.lin_02);
            tv_name1 = (TextView) itemView.findViewById(R.id.tv_name1);
            tv_name2 = (TextView) itemView.findViewById(R.id.tv_name2);
            tv_time1 = (TextView) itemView.findViewById(R.id.tv_time1);
            tv_time2 = (TextView) itemView.findViewById(R.id.tv_time2);
            tv_content1 = (TextView) itemView.findViewById(R.id.tv_content1);
            tv_content2 = (TextView) itemView.findViewById(R.id.tv_content2);
            iv_pic = (ImageView) itemView.findViewById(R.id.iv_pic);
            view = itemView.findViewById(R.id.view);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
