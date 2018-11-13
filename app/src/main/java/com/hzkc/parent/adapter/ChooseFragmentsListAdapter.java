package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hzkc.parent.R;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.utils.GlideCircleTransform;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo-s on 2016/12/8.
 */

public class ChooseFragmentsListAdapter extends BaseAdapter {
    List<ChildsTable> planlist;
    private Context context;
    private LayoutInflater inflater = null;
    private static HashMap<Integer, Boolean> isSelected;

    // 构造器
    public ChooseFragmentsListAdapter(List<ChildsTable> list, Context contex,String childid) {
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
            convertView = inflater.inflate(R.layout.item_choose_list, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.ivSelect = (ImageView) convertView.findViewById(R.id.iv_select_flag);
            holder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(planlist.get(position).getName());
        Glide.with(context).load(planlist.get(position).getImageurl()).transform(new GlideCircleTransform(context)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head__01).into( holder.iv_head);
        if(getIsSelected().get(position)==null ||  !getIsSelected().get(position)){
            holder.ivSelect.setImageResource(R.drawable.choose_no);
        }else{
            holder.ivSelect.setImageResource(R.drawable.choose_yes);
            if(listener!=null){
                listener.chooseListener(position);
            }
        }
        return convertView;
    }
    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ChooseFragmentsListAdapter.isSelected = isSelected;
    }
    public static class ViewHolder {
        public TextView tv_name;
        public ImageView ivSelect;
        public ImageView iv_head;
    }


    public interface  ChooseListener{
        void  chooseListener(int position);
    }
    private ChooseListener listener;

    public void setListener(ChooseListener listener) {
        this.listener = listener;
    }
}
