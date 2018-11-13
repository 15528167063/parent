package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.greendao.entity.PhoneData;

import java.util.HashMap;
import java.util.List;

public class WhitePhoneAdapter extends BaseAdapter {
    List<PhoneData> planlist;
    private Context context;
    private LayoutInflater inflater = null;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    // 构造器
    public WhitePhoneAdapter(List<PhoneData> planlist, Context context) {
        this.context = context;
        this.planlist = planlist;
        try {
            inflater = LayoutInflater.from(context);
            isSelected = new HashMap<Integer, Boolean>();
            initData();
        } catch (Exception e) {
            return;
        };
    }

        // 初始化isSelected的数据
    private void initData() {
        for (int i = 0; i < planlist.size(); i++) {
            getIsSelected().put(i, false);
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
            convertView = inflater.inflate(R.layout.item_phone_list, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select_flag);
            holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(planlist.get(position).getName());
        holder.tv_number.setText(planlist.get(position).getPhone());
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
        WhitePhoneAdapter.isSelected = isSelected;
    }
    public static class ViewHolder {
        public TextView tv_name;
        public TextView tv_number;
        public ImageView ivSelect;
    }
}
