package com.hzkc.parent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkc.parent.Bean.VipPriceBean;
import com.hzkc.parent.R;

import java.util.List;


public class VipListNewAdapter extends RecyclerView.Adapter<VipListNewAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "DemoAdapter";
    private List<VipPriceBean> mItems;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    public String childuuid;


    public VipListNewAdapter(List<VipPriceBean> items, Context context) {
        this.mItems = items;
        this.context=context;
        initdata(items);
    }

    private void initdata(List<VipPriceBean> items) {
        if(items!=null && items.size()>0){
            for (int i = 0; i < items.size(); i++) {
                if(i==0){
                    items.get(i).setState(true);
                    return;
                }
                items.get(i).setState(false);
            }
        }
    }

    public VipListNewAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vip_list, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.lin_all.setTag(position);
        final VipPriceBean mdllist = mItems.get(position);
        if(mdllist.isState()){
            holder.lin_all.setBackgroundResource(R.drawable.shape_vip_bg1);
        }else {
            holder.lin_all.setBackgroundResource(R.drawable.shape_vip_bg);
        }
        holder.lin_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mItems.size(); i++) {
                    if(i==position){
                        mItems.get(i).setState(true);
                    }else {
                        mItems.get(i).setState(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
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
        public TextView tv_sj;
        public TextView tv_yj;
        public TextView tv_yh;
        public ImageView iv_pic;
        public LinearLayout lin_all;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_sj = (TextView) itemView.findViewById(R.id.tv_price_1);
            tv_yj = (TextView) itemView.findViewById(R.id.tv_price_2);
            tv_yh = (TextView) itemView.findViewById(R.id.tv_price_3);
            iv_pic = (ImageView) itemView.findViewById(R.id.iv_head);
            lin_all = (LinearLayout) itemView.findViewById(R.id.lin_all);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
