package com.hzkc.parent.fragment.txappmanage.adapter;

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

/**
 * Created by lenovo-s on 2016/11/9.
 */

public class TeQuanAppWhiteAdapter extends BaseAdapter {
    // 填充数据的list
    List<AppDataBean> planlist;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public String parentUUID,childUUID;

    // 构造器
    public TeQuanAppWhiteAdapter(List<AppDataBean> planlist, Context context,String parentUUID,String childUUID) {
        this.context = context;
        this.planlist = planlist;
        this.parentUUID=parentUUID;
        this.childUUID=childUUID;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initData();
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
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_app_list1, null);
            holder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select_flag);
            holder.ivAppIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvAppName.setText(planlist.get(position).getAppname());
        // 根据isSelected来设置checkbox的选中状况
        //LogUtil.i(TAG, "checkboxFlag: "+getIsSelected().get(position));
//        if(getIsSelected().get(position)){
//            holder.ivSelect.setImageResource(R.drawable.white_list_check_y);
//        }else{
//            holder.ivSelect.setImageResource(R.drawable.white_list_check_n);
//        }
        if(getIsSelected().get(position)==null ||  !getIsSelected().get(position)){
            holder.ivSelect.setImageResource(R.drawable.white_list_check_n);
        }else{
            holder.ivSelect.setImageResource(R.drawable.white_list_check_y);
        }
        String url= Constants.APP_IMAGE_URL+parentUUID+"/"+childUUID+"/"+planlist.get(position).getApppackgename()+".jpg";
        Glide.with(context).load(url).placeholder(R.drawable.soye_mrtb).error(R.drawable.soye_mrtb).into( holder.ivAppIcon);
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        TeQuanAppWhiteAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        public TextView tvAppName;
        public ImageView ivSelect;
        public ImageView ivAppIcon;
    }

}
