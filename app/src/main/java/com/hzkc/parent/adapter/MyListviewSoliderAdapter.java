package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.hzkc.parent.R;
import com.hzkc.parent.jsondata.NetPlanData;
import com.hzkc.parent.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by lenovo-s on 2016/11/9.
 */

public class MyListviewSoliderAdapter extends BaseAdapter {
    private Context context;
    ArrayList<NetPlanData> planlist;
    private LayoutInflater inflater = null;
    private static HashMap<Integer, Boolean> isSelected;
    public boolean CbFlags = false;   //ture要显示


    // 构造器
    public MyListviewSoliderAdapter(ArrayList<NetPlanData> planlist, Context context) {
        this.context = context;
        this.planlist = planlist;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }
    // 初始化isSelected的数据
    private void initDate() {
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
            convertView = inflater.inflate(R.layout.item_stop_plans, null);
            holder.tvPlanTime = (TextView) convertView.findViewById(R.id.tv_plan_time);
            holder.tvPlanName = (TextView) convertView.findViewById(R.id.tv_plan_name);
            holder.tvPlanName = (TextView) convertView.findViewById(R.id.tv_plan_name);
            holder.ivPlanFlag = (ImageView) convertView.findViewById(R.id.iv_plan_falg);
            holder.TvPlanDay = (TextView) convertView.findViewById(R.id.tv_plan_day);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (CbFlags) {
            holder.cb.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.GONE);
        }
        String planflag = planlist.get(position).e;
        if(planflag.equals("1")){
            holder.ivPlanFlag.setImageResource(R.drawable.clock_on);
        }else {
            holder.ivPlanFlag.setImageResource(R.drawable.clock_off);
        }
        // 设置list中TextView的显示
        holder.tvPlanTime.setText("管控时间:"+planlist.get(position).c+"-"+planlist.get(position).d);
        String weekday = planlist.get(position).b;
        LogUtil.i(TAG, "getView: "+weekday);
        String planDay="";
        if(weekday.contains("1")){
            planDay=planDay+"一";
        }
        if(weekday.contains("2")){
            planDay=planDay+"二";
        }
        if(weekday.contains("3")){
            planDay=planDay+"三";
        }
        if(weekday.contains("4")){
            planDay=planDay+"四";
        }
        if(weekday.contains("5")){
            planDay=planDay+"五";
        }
        if(weekday.contains("6")){
            planDay=planDay+"六";
        }
        if(weekday.contains("7")){
            planDay=planDay+"日";
        }
        holder.ivPlanFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.ivClick(position);
                }
            }
        });
        holder.tvPlanName.setText("标签："+planlist.get(position).a);
        holder.TvPlanDay.setText("管控周期："+planDay);
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MyListviewSoliderAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tvPlanTime;
        public TextView tvPlanName;
        public TextView TvPlanDay;
        public ImageView ivPlanFlag;
        public CheckBox cb;
    }

    public PlanFlagChangeListener listener;
    public interface PlanFlagChangeListener{
        public void ivClick(int position);
    }
    public void changPlanFlag(PlanFlagChangeListener listener){
        this.listener=listener;
    }
}
