package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hzkc.parent.Bean.UseAppDataBean;
import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/9.
 */

public class MyUseAppAdapter extends BaseAdapter {
    // 填充数据的list
    private List<UseAppDataBean> applist;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public String parentUUID,childUUID;
    // 构造器
    // 构造器
    public MyUseAppAdapter(List<UseAppDataBean> planlist, Context context,String parentUUID,String childUUID) {
        this.context = context;
        this.applist = planlist;
        this.parentUUID=parentUUID;
        this.childUUID=childUUID;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return applist.size();
    }

    @Override
    public Object getItem(int position) {
        return applist.get(position);
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
            convertView = inflater.inflate(R.layout.item_app_use_list, null);
            holder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
            holder.tv_open = (TextView) convertView.findViewById(R.id.tv_open);
            holder.tvAppTime = (TextView) convertView.findViewById(R.id.tv_app_time);
            holder.pbTime = (ProgressBar) convertView.findViewById(R.id.pb_time);
            holder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvAppName.setText(applist.get(position).getAppname());
        if(applist.get(position).getNumber()!=0){
            holder.tv_open.setText("打开"+applist.get(position).getNumber()+"次");
        }
        int usetime = Integer.parseInt(applist.get(position).getUseTime());
        if(usetime/60>0){
            if(usetime/(60*60)>0){
                holder.tvAppTime.setText("已使用"+usetime/3600+"小时"+(usetime%3600)/60+"分"+usetime%60+"秒");
            }else{
                holder.tvAppTime.setText("已使用"+usetime/60+"分"+usetime%60+"秒");
            }
        }else {
            holder.tvAppTime.setText("已使用"+usetime+"秒");
        }
        double time =usetime *1.0f/ (24*60*60);
        holder.pbTime.setProgress((int)(time*100));

        String url= Constants.APP_IMAGE_URL+parentUUID+"/"+childUUID+"/"+applist.get(position).getPackegname()+".jpg";
        Glide.with(context).load(url).placeholder(R.drawable.soye_mrtb).error(R.drawable.soye_mrtb).into( holder.iv_app_icon);
        return convertView;
    }


    public static class ViewHolder {
        public TextView tvAppName;
        public TextView tvAppTime;
        public TextView tv_open;
        public ProgressBar pbTime;
        public ImageView iv_app_icon;
    }

}
