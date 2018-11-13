package com.hzkc.parent.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.greendao.entity.NetPlanDataBean;
import com.hzkc.parent.utils.LogUtil;

import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by lenovo-s on 2016/11/9.
 */

public class MyListviewAdapter extends BaseAdapter {
    private Context context;
    List<NetPlanDataBean> planlist;
    private LayoutInflater inflater = null;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    //判断checkbox是不是需要显示
    public boolean CbFlags = false;   //ture要显示


    // 构造器
    public MyListviewAdapter(List<NetPlanDataBean> planlist, Context context) {
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
            convertView = inflater.inflate(R.layout.item_stop_plan_new, null);
            holder.tvPlanTime = (TextView) convertView.findViewById(R.id.tv_plan_time);
            holder.tvPlanName = (TextView) convertView.findViewById(R.id.tv_plan_name);
            holder.tvPlanDay = (TextView) convertView.findViewById(R.id.tv_plan_day);
            holder.ivPlanFlag = (ImageView) convertView.findViewById(R.id.iv_plan_falg);
            holder.tv_1 = (TextView) convertView.findViewById(R.id.tv_1);
            holder.tv_2 = (TextView) convertView.findViewById(R.id.tv_2);
            holder.tv_3 = (TextView) convertView.findViewById(R.id.tv_3);
            holder.tv_4 = (TextView) convertView.findViewById(R.id.tv_4);
            holder.tv_5 = (TextView) convertView.findViewById(R.id.tv_5);
            holder.tv_6 = (TextView) convertView.findViewById(R.id.tv_6);
            holder.tv_7 = (TextView) convertView.findViewById(R.id.tv_7);
            holder.tv_8 = (TextView) convertView.findViewById(R.id.tv_8);
            holder.lin_scro = (HorizontalScrollView) convertView.findViewById(R.id.lin_scro);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            holder.re_cb = (RelativeLayout) convertView.findViewById(R.id.re_cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (CbFlags) {
            holder.re_cb.setVisibility(View.VISIBLE);
        } else {
            holder.re_cb.setVisibility(View.GONE);
        }
        String planflag = planlist.get(position).getPlanflag();
        if(planflag.equals("1")){
            holder.ivPlanFlag.setImageResource(R.drawable.plan_on);
        }else {
            holder.ivPlanFlag.setImageResource(R.drawable.plan_off);
        }
        // 设置list中TextView的显示
        holder.tvPlanTime.setText("时间："+planlist.get(position).startplantime+"-"+planlist.get(position).endplantime);
        String weekday = planlist.get(position).getWeekday();
        LogUtil.i(TAG, "getView: "+weekday);
        String planDay="";
        if(weekday.contains("1")){
            planDay=planDay+"一";
            holder.tv_1.setVisibility(View.VISIBLE);
        }
        if(weekday.contains("2")){
            planDay=planDay+"二";
            holder.tv_2.setVisibility(View.VISIBLE);
        }
        if(weekday.contains("3")){
            planDay=planDay+"三";
            holder.tv_3.setVisibility(View.VISIBLE);
        }
        if(weekday.contains("4")){
            planDay=planDay+"四";
            holder.tv_4.setVisibility(View.VISIBLE);
        }
        if(weekday.contains("5")){
            planDay=planDay+"五";
            holder.tv_5.setVisibility(View.VISIBLE);
        }
        if(weekday.contains("6")){
            planDay=planDay+"六";
            holder.tv_6.setVisibility(View.VISIBLE);
        }
        if(weekday.contains("7")){
            planDay=planDay+"日";
            holder.tv_7.setVisibility(View.VISIBLE);
        }
        if(weekday.contains("1234567")){
            holder.lin_scro.setVisibility(View.GONE);
            holder.tv_8.setVisibility(View.VISIBLE);
        }
        holder.ivPlanFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean sptishi = context.getSharedPreferences("info", context.MODE_PRIVATE).getBoolean("gkjhtishi",false);
                if( planlist.get(position).getPlanflag()=="0" && !sptishi ){
                    showTiShiDialog(position);
                    return;
                }
                if(listener!=null){
                    listener.ivClick(position);
                }
            }
        });

        holder.tvPlanName.setText("标签："+planlist.get(position).getNetplanname());
        holder.tvPlanDay.setText("周期：");
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MyListviewAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tvPlanTime;
        public TextView tvPlanName;
        public TextView tvPlanDay;
        public TextView tv_1;
        public TextView tv_2;
        public TextView tv_3;
        public TextView tv_4;
        public TextView tv_5;
        public TextView tv_6;
        public TextView tv_7;
        public TextView tv_8;
        public HorizontalScrollView lin_scro;
        public ImageView ivPlanFlag;
        public CheckBox cb;
        public RelativeLayout re_cb;
}

    public PlanFlagChangeListener listener;
    public interface PlanFlagChangeListener{
        public void ivClick(int position);
    }
    public void changPlanFlag(PlanFlagChangeListener listener){
        this.listener=listener;
    }



    /**
     * (提示一键管控和视力保护和管控计划不能一起开)
     * */
    public CheckBox cb_box;
    public TextView tv_content,tvFinish1;
    public AlertDialog showDialog;
    public void showTiShiDialog(final int position){
        AlertDialog.Builder builder=new AlertDialog.Builder(context,R.style.MyAlertDialog);
        //builder.setCancelable(false);
        View dialogView =  View.inflate(context, R.layout.dialog_internet_tishi, null);
        tvFinish1 = (TextView) dialogView.findViewById(R.id.tv_finish);
        tv_content = (TextView) dialogView.findViewById(R.id.tv_content);
        cb_box = (CheckBox) dialogView.findViewById(R.id.cb_box);
        tvFinish1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog.dismiss();
                if(cb_box.isChecked()){
                    context.getSharedPreferences("info", context.MODE_PRIVATE).edit().putBoolean("gkjhtishi",true).commit();
                }
                if(listener!=null){
                    listener.ivClick(position);
                }
            }
        });
        builder.setView(dialogView);
        showDialog = builder.show();
    }
}
