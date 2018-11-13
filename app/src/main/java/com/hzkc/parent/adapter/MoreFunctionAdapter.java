package com.hzkc.parent.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.Bean.InternetItemBean;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;

import java.util.List;


/**
 * Created by lenovo-s on 2016/10/20.
 */

public class MoreFunctionAdapter extends RecyclerView.Adapter<MoreFunctionAdapter.MyViewHolder>implements View.OnClickListener{
    private Activity mActivity;
    private List<InternetItemBean> mList;
    private OnRecyclerViewItemClickListener mListener=null;
    public String viplist;
    public MoreFunctionAdapter(Activity mActivity, List<InternetItemBean> mList) {
        this.mActivity=mActivity;
        this.mList=mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view=LayoutInflater.from(mActivity).inflate(R.layout.item_more_recyclerview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        holder.tv.setText(mList.get(position).getTvName());
        holder.tv2.setText(mList.get(position).getTv2Name());
        if(position==0||position==1){
            holder.tv2.setVisibility(View.VISIBLE);
        }
        holder.iv.setImageResource(mList.get(position).getIvIcon());
        //将数据保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        viplist = mActivity.getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).getString("viplist", "");
        if(viplist.contains(Constants.VIP_APP_ZHIZHONG)){
            if(position==0){
                holder.iv1.setVisibility(View.VISIBLE);
            }
        }
        if(viplist.contains(Constants.VIP_WEB_CHOOSE)){
            if(position==1){
                holder.iv1.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }

    @Override
    public void onClick(View v) {
        if(mListener!=null){
            //注意这里使用getTag方法获取数据
            mListener.onItemClick(v,(int)v.getTag());
        }
    }
    /**
     * 最后暴露给外面的调用者
     * */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener mListener) {
        this.mListener = mListener;
    }

    /**
     * 初始化MyViewHolder
     * */
    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv;
        TextView tv2;
        ImageView iv;
        ImageView iv1;
        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.tv_internet_name);
            tv2 = (TextView) view.findViewById(R.id.tv_internet_flag);
            iv = (ImageView) view.findViewById(R.id.iv_internet_icon);
            iv1 = (ImageView) view.findViewById(R.id.iv_vip);
        }
    }

    /**
     * 定义点击接口
     * */
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
}


