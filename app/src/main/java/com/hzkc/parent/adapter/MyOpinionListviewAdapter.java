package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hzkc.parent.Bean.OpinionBackBean;
import com.hzkc.parent.R;

import java.util.List;

/**
 * Created by lenovo-s on 2016/12/8.
 */

public class MyOpinionListviewAdapter extends BaseAdapter{
    private List<OpinionBackBean> opinionList;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public MyOpinionListviewAdapter(List<OpinionBackBean> opinionList, Context context) {
        this.opinionList=opinionList;
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return opinionList.size();
    }

    @Override
    public Object getItem(int position) {
        return opinionList.get(position);
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
            convertView = inflater.inflate(R.layout.item_iponion_list, null);
            holder.tvUser = (TextView) convertView.findViewById(R.id.tv_user);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            // 为view设置标签
            convertView.setTag(holder);
        }else{
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvUser.setText(opinionList.get(position).getUser());
        holder.tvTime.setText(opinionList.get(position).getTime());
        holder.tvContent.setText(opinionList.get(position).getContent());
        return convertView;
    }
    public static class ViewHolder {
        public TextView tvUser;
        public TextView tvTime;
        public TextView tvContent;
    }
}
