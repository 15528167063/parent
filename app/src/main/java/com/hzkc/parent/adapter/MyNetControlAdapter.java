package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo-s on 2016/11/9.
 */

public class MyNetControlAdapter extends BaseAdapter {
    // 填充数据的list
    List<String> netlist;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    // 构造器
    public MyNetControlAdapter(List<String> netlist, Context context) {
        this.context = context;
        this.netlist = netlist;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initData();
    }

    // 初始化isSelected的数据
    private void initData() {
        for (int i = 0; i < netlist.size(); i++) {
            getIsSelected().put(i, false);
        }
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
            convertView = inflater.inflate(R.layout.item_net_list, null);
            holder.tvNetName = (TextView) convertView.findViewById(R.id.tv_net_name);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select_flag);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvNetName.setText(netlist.get(position));
        // 根据isSelected来设置checkbox的选中状况
        //LogUtil.i(TAG, "checkboxFlag: "+getIsSelected().get(position));
        if(getIsSelected().get(position)){
            holder.ivSelect.setImageResource(R.drawable.mine_check);
        }else{
            holder.ivSelect.setImageResource(R.drawable.mine_check_no);
        }
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MyNetControlAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tvNetName;
        public ImageView ivSelect;
    }

}
