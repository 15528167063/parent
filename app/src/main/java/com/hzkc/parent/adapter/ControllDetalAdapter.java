package com.hzkc.parent.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.greendao.entity.ChildTable;

import java.util.List;


public class ControllDetalAdapter extends BaseAdapter {
    private List<ChildTable> planlist;
    private Context context;
    private LayoutInflater inflater = null;

    // 构造器
    public ControllDetalAdapter(List<ChildTable> planlist, Context context) {
        this.context = context;
        this.planlist = planlist;
        inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.item_controll_detail, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_sex = (TextView) convertView.findViewById(R.id.tv_sex);
            holder.tv_place = (TextView) convertView.findViewById(R.id.tv_place);
            holder.tv_team = (TextView) convertView.findViewById(R.id.tv_team);
            holder.tv_controll = (TextView) convertView.findViewById(R.id.tv_controll);
            holder.tv_regist = (TextView) convertView.findViewById(R.id.tv_regist);
            holder.lv_title = (LinearLayout) convertView.findViewById(R.id.lv_title);
            holder.tv_lasttime = (TextView) convertView.findViewById(R.id.tv_lasttime);
            holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(position==0){
            holder.lv_title.setVisibility(View.VISIBLE);
        }else {
            holder.lv_title.setVisibility(View.GONE);
        }
        ChildTable childTable=planlist.get(position);
        holder.tv_name.setText(childTable.getName());
        if(childTable.getSex().equals("1")){
            holder.tv_sex.setText("男");
        }else{
            holder.tv_sex.setText("女");
        }

        holder.tv_place.setText(childTable.getPlace());
        holder.tv_team.setText(childTable.getTeam());

        if(childTable.getState().equals("1")){
            holder.tv_controll.setText("受控");
            holder.tv_controll.setTextColor(context.getResources().getColor(R.color.blue));
        }else if(childTable.getState().equals("2")){
            holder.tv_controll.setText("脱管");
            holder.tv_controll.setTextColor(context.getResources().getColor(R.color.red));
        }else if(childTable.getState().equals("0")){
            holder.tv_controll.setText("不受控");
            holder.tv_controll.setTextColor(context.getResources().getColor(R.color.orange));
        }else if(TextUtils.isEmpty(childTable.getState())){
            holder.tv_controll.setText("不受控");
            holder.tv_controll.setTextColor(context.getResources().getColor(R.color.orange));
        }

        holder.tv_regist.setText(childTable.getRegist().substring(0,10));
        holder.tv_lasttime.setText(childTable.getLasttime());
        holder.tv_location.setText(childTable.getLocation());
        return convertView;
    }
    public static class ViewHolder {
        public TextView tv_name;
        public TextView tv_sex;
        public TextView tv_place;
        public TextView tv_team;
        public TextView tv_controll;
        public TextView tv_regist;
        public TextView tv_lasttime;
        public TextView tv_location;
        public LinearLayout lv_title;



    }
}
