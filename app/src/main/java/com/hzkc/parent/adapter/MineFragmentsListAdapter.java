package com.hzkc.parent.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.hzkc.parent.Bean.MineItemBean;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.TouchMineActivity;

import java.util.List;

/**
 * Created by lenovo-s on 2016/12/8.
 */

public class MineFragmentsListAdapter extends BaseAdapter {
    private List<MineItemBean> minelist;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    public MineFragmentsListAdapter(List<MineItemBean> minelist, Context context) {
        this.minelist = minelist;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return minelist.size();
    }

    @Override
    public Object getItem(int position) {
        return minelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_mine_list, null);
            holder.tvItemName = (TextView) convertView.findViewById(R.id.tv_mine_name);
            holder.ivItemIcon = (ImageView) convertView.findViewById(R.id.iv_mine_icon);
            holder.lin = (LinearLayout) convertView.findViewById(R.id.lin);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        if(position==0){
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent0 = new Intent(context, TouchMineActivity.class);
                    context.startActivity(intent0);
                }
            });
        }
        holder.tvItemName.setText(minelist.get(position).getTvName());
        holder.ivItemIcon.setImageResource(minelist.get(position).getIvIcon());
        return convertView;
    }

    public static class ViewHolder {
        public TextView tvItemName;
        public ImageView ivItemIcon;
        public LinearLayout lin;
    }
}
