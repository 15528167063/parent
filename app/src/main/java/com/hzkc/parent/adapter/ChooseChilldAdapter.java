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

import java.util.HashMap;
import java.util.List;

public class ChooseChilldAdapter extends BaseAdapter {
    List<ChildsTable> planlist;
    private Context context;
    private LayoutInflater inflater = null;
    private static HashMap<Integer, Boolean> isSelected;

    // 构造器
    public ChooseChilldAdapter(List<ChildsTable> list, Context contex, String childid) {
        this.context = contex;
        this.planlist = list;
        try {
            inflater = LayoutInflater.from(context);
            isSelected = new HashMap<Integer, Boolean>();
            initData(childid);
        } catch (Exception e) {
            return;
        };
    }

    // 初始化isSelected的数据
    private void initData(String childid) {
        for (int i = 0; i < planlist.size(); i++) {
            if(planlist.get(i).getChilduuid().equals(childid)){
                getIsSelected().put(i, true);
            }else {
                getIsSelected().put(i, false);
            }
        }
    }

    @Override
    public int getCount() {
        return planlist.size();
    }

    @Override
    public Object getItem(int position) {
        return planlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_childe_list, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select_flag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(planlist.get(position).getName());
        if(getIsSelected().get(position)==null ||  !getIsSelected().get(position)){
            holder.ivSelect.setImageResource(R.drawable.white_list_check_n);
        }else{
            holder.ivSelect.setImageResource(R.drawable.white_list_check_y);
        }
        return convertView;
    }
    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ChooseChilldAdapter.isSelected = isSelected;
    }
    public static class ViewHolder {
        public TextView tv_name;
        public ImageView ivSelect;
    }
}
