package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkc.parent.Bean.LoveTrailBean;
import com.hzkc.parent.R;

import java.util.List;

/**
 * Created by lenovo-s on 2016/12/8.
 */

public class MyLoveTrailListviewAdapter extends BaseAdapter{
    private List<LoveTrailBean> mlist;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public MyLoveTrailListviewAdapter(List<LoveTrailBean> mlist, Context context) {
        this.mlist=mlist;
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_love_trail_new, null);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_request_time);
//            holder.tvMyTime = (TextView) convertView.findViewById(R.id.tv_my_time);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tvStopTime = (TextView) convertView.findViewById(R.id.tv_stop_time);
            holder.re_1 = (RelativeLayout) convertView.findViewById(R.id.rv_1);
            holder.re_2 = (RelativeLayout) convertView.findViewById(R.id.rv_2);
            // 为view设置标签
            convertView.setTag(holder);
        }else{
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        if(position==0){
            holder.re_1.setVisibility(View.VISIBLE);
            holder.re_2.setVisibility(View.GONE);
        }else {
            holder.re_1.setVisibility(View.GONE);
            holder.re_2.setVisibility(View.VISIBLE);
        }
        holder.tvTime.setText(mlist.get(position).getRequestTime());
//        holder.tvMyTime.setText(mlist.get(position).getRequestTime2());
        holder.tvAddress.setText(mlist.get(position).getAddress());
        String substring ="";
        if(mlist.get(position).getStopTime().length()>4){
            substring = mlist.get(position).getStopTime().substring(0, 4);
        }
        holder.tvStopTime.setText(substring);
        return convertView;
    }
    public static class ViewHolder {
        public TextView tvTime;
//        public TextView tvMyTime;
        public TextView tvAddress;
        public TextView tvStopTime;
        public RelativeLayout re_1;
        public RelativeLayout re_2;
    }
}
