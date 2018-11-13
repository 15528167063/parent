package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.jsondata.CmdCommon;

import java.util.List;


/**
 * Created by lenovo-s on 2016/12/8.
 */

public class MySoliderListviewAdapter extends BaseAdapter{
    private List<ChildsTable> childslist;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public MySoliderListviewAdapter(List<ChildsTable> childslist, Context context) {
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

            convertView = inflater.inflate(R.layout.item_childs_list, null);
            holder.tvchildClass = (TextView) convertView.findViewById(R.id.tv_child_class);
            holder.tvchildName = (TextView) convertView.findViewById(R.id.tv_child_name);
            holder.ivchildIcon = (ImageView) convertView.findViewById(R.id.iv_child_icon);
            // 为view设置标签
            convertView.setTag(holder);
        }else{
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvchildClass.setText("年级："+childslist.get(position).getNianji());
        holder.tvchildName.setText(childslist.get(position).getName());
        String sex = childslist.get(position).getSex();
        if(sex.equals(CmdCommon.FLAG_BOY)){
           holder.ivchildIcon.setImageResource(R.drawable.nanshibing);
        }else{
           holder.ivchildIcon.setImageResource(R.drawable.nvshibing);
        }
        return convertView;
    }
    public static class ViewHolder {
        public TextView tvchildName;
        public TextView tvchildClass;
        public ImageView ivchildIcon;
    }
}
