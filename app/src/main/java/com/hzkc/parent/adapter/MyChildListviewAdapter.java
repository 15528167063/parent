package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hzkc.parent.R;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.utils.GlideCircleTransform;

import java.util.List;

/**
 * Created by lenovo-s on 2016/12/8.
 */

public class MyChildListviewAdapter extends BaseAdapter{
    private List<ChildsTable> childslist;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public MyChildListviewAdapter(List<ChildsTable> childslist, Context context) {
        this.childslist=childslist;
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return childslist.size();
    }

    @Override
    public Object getItem(int position) {
        return childslist.get(position);
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

            convertView = inflater.inflate(R.layout.item_child_list, null);
            holder.tvchildName = (TextView) convertView.findViewById(R.id.tv_child_name);
            holder.tvchildSex = (TextView) convertView.findViewById(R.id.tv_child_sex);
            holder.ivchildIcon = (ImageView) convertView.findViewById(R.id.iv_child_icon);
            // 为view设置标签
            convertView.setTag(holder);
        }else{
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvchildName.setText(childslist.get(position).getName());
        String sex = childslist.get(position).getSex();
        Glide.with(context).load(childslist.get(position).getImageurl()).transform(new GlideCircleTransform(context)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head__01).into(holder.ivchildIcon);
        if(sex.equals(CmdCommon.FLAG_BOY)){
            holder.tvchildSex.setText("男");
        }else{
            holder.tvchildSex.setText("女");
        }
        return convertView;
    }
    public static class ViewHolder {
        public TextView tvchildName;
        public TextView tvchildSex;
        public ImageView ivchildIcon;
    }
}
