package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hzkc.parent.R;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.mina.Constants;

import java.util.HashMap;
import java.util.List;

public class TotleAppAdapter extends BaseAdapter {
    List<AppDataBean> planlist;
    private Context context;
    private LayoutInflater inflater = null;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    public  String parentUUID,childUUID;

    // 构造器
    public TotleAppAdapter(List<AppDataBean> planlist, Context context) {
        this.context = context;
        this.planlist = planlist;
        parentUUID=context.getSharedPreferences("info", context.MODE_PRIVATE).getString("parentUUID", "");
        childUUID=context.getSharedPreferences("info", context.MODE_PRIVATE).getString("ChildUUID", "");
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
            convertView = inflater.inflate(R.layout.item_app_list, null);
            holder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select_flag);
            holder.ivAppIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvAppName.setText(planlist.get(position).getAppname());
        String url= Constants.APP_IMAGE_URL+parentUUID+"/"+childUUID+"/"+planlist.get(position).getApppackgename()+".jpg";
        Glide.with(context).load(url).placeholder(R.drawable.soye_mrtb).error(R.drawable.soye_mrtb).dontAnimate().into(holder.ivAppIcon);
        if(getIsSelected().get(position)==null ||  !getIsSelected().get(position)){
            if(getIsSelected().get(position)==null){
//                Toast.makeText(context, "我崩了", Toast.LENGTH_SHORT).show();
            }
            holder.ivSelect.setImageResource(R.drawable.mine_check_no);
        }else{
            holder.ivSelect.setImageResource(R.drawable.mine_check);
        }
        return convertView;
    }
    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        TotleAppAdapter.isSelected = isSelected;
    }
    public static class ViewHolder {
        public TextView tvAppName;
        public ImageView ivSelect;
        public ImageView ivAppIcon;
    }
}
