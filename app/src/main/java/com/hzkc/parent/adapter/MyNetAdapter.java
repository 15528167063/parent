package com.hzkc.parent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkc.parent.Bean.NetInfoBean;
import com.hzkc.parent.R;

import java.util.List;

public class MyNetAdapter extends RecyclerView.Adapter<MyNetAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = "DemoAdapter";
    private List<NetInfoBean> netlist;
    private MyNetAdapter.OnItemClickListener mOnItemClickListener;
    private Context context;
    public String childuuid;


    public MyNetAdapter(List<NetInfoBean> items, Context context) {
        this.netlist = items;
        this.context=context;
    }
    public MyNetAdapter setOnItemClickListener(MyNetAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }
    @Override
    public MyNetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_net_list_new, parent, false);
        v.setOnClickListener(this);
        return new MyNetAdapter.ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final MyNetAdapter.ViewHolder holder, final int position) {
        final NetInfoBean mdllist = netlist.get(position);
        holder.itemView.setTag(position);
        holder.tvNetName.setText(netlist.get(position).title);
        holder.tvNetUrl.setText(netlist.get(position).link);
        holder.tv_time.setText(netlist.get(position).browse_time);
        if(!TextUtils.isEmpty(netlist.get(position).num)){
            holder.tv_numb.setText("浏览次数："+netlist.get(position).num+"");
        }
        holder.ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivSelect.setVisibility(View.GONE);
                holder.ivSelect_on.setVisibility(View.VISIBLE);
                if(listener!=null ){
                    listener.onFbClick(position,1);
                }
            }
        });
        holder.ivSelect_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivSelect.setVisibility(View.VISIBLE);
                holder.ivSelect_on.setVisibility(View.GONE);
                if(listener!=null ){
                    listener.onFbClick(position,0);
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return netlist.size();
    }
    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNetName;
        public TextView tvNetUrl;
        public TextView ivSelect;
        public TextView ivSelect_on;
        public TextView tv_numb;
        public TextView tv_time;
        public LinearLayout view;
        public ViewHolder(View itemView) {
            super(itemView);
            view = (LinearLayout)itemView.findViewById(R.id.view);
            tvNetName = (TextView) itemView.findViewById(R.id.tv_net_name);
            tvNetUrl = (TextView) itemView.findViewById(R.id.tv_net_url);
            ivSelect = (TextView) itemView.findViewById(R.id.iv_select_flag);
            tv_numb = (TextView) itemView.findViewById(R.id.tv_numb);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            ivSelect_on = (TextView) itemView.findViewById(R.id.iv_select_flag_om);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public interface  ForbitCilckListener{
        void   onFbClick(int position, int type);
    }
    private MyNetNewControlAdapter.ForbitCilckListener listener;

    public void setListener(MyNetNewControlAdapter.ForbitCilckListener listener) {
        this.listener = listener;
    }
}
