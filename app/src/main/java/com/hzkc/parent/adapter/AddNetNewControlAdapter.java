package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.Bean.NetInfoBean;
import com.hzkc.parent.R;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/9.
 */

public class AddNetNewControlAdapter extends BaseAdapter {
    // 填充数据的list
    List<NetInfoBean> netlist;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    // 构造器
    public AddNetNewControlAdapter(List<NetInfoBean> netlist, Context context) {
        this.context = context;
        this.netlist = netlist;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return netlist.size();
    }

    @Override
    public Object getItem(int position) {
        return netlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.add_net_list_new, null);
            holder.tvNetName = (TextView) convertView.findViewById(R.id.tv_net_name);
            holder.tvNetUrl = (TextView) convertView.findViewById(R.id.tv_net_url);
            holder.ivSelect = (TextView) convertView.findViewById(R.id.iv_select_flag);
            holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvNetName.setText(netlist.get(position).title);
        holder.tvNetUrl.setText(netlist.get(position).link);
        final ViewHolder finalHolder = holder;
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null ){
                    listener.onFbClick(position,1);
                }
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        public TextView tvNetName;
        public TextView tvNetUrl;
        public TextView ivSelect;
        public ImageView iv_delete;
    }
    public interface  ForbitCilckListener{
        void   onFbClick(int position, int type);
    }
    private  ForbitCilckListener listener;

    public void setListener(ForbitCilckListener listener) {
        this.listener = listener;
    }
}
