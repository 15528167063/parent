package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.jsondata.AppData;

import java.util.List;


public class StopAppListAdapter extends BaseAdapter {
    private List<AppData> planlist;
    private Context context;
    private LayoutInflater inflater = null;

    // 构造器
    public StopAppListAdapter(List<AppData> planlist, Context context) {
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
            convertView = inflater.inflate(R.layout.item_app_stop, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_app_name);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select_flag);
            holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppData appData=planlist.get(position);
        holder.tv_name.setText(appData.getAppname());
        if(appData.c.equals("0")){
            holder.tv_state.setText("白名单");
        }else {
            holder.tv_state.setText("黑名单");
        }
        holder.ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planlist.remove(position);
                if(planlist.size()==0){
                    if(nodatdalistener!=null){
                        nodatdalistener.setNoData();
                    }
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    public static class ViewHolder {
        public TextView tv_name;
        public TextView tv_state;
        public ImageView ivSelect;
    }
    public interface  Nodatda{
        void setNoData();
    }
    private Nodatda nodatdalistener;

    public void setNodatdalistener(Nodatda nodatdalistener) {
        this.nodatdalistener = nodatdalistener;
    }
}
