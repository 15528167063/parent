package com.hzkc.parent.fragment.information;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.base.BaseListAdapter;
import com.hzkc.parent.adapter.base.ViewHolder;

/**
 * Created by Administrator on 2016/12/6 0006.
 */

public class ActivityAdapter extends BaseListAdapter<ImageBean> {

    public ActivityAdapter(Context mContext) {
        super(mContext);
    }
    @Override
    public int itemLayoutRes() {
        return R.layout.item_activity_list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent, ViewHolder holder) {
        ImageView iv = holder.obtainView(convertView, R.id.iv);
        TextView tv = holder.obtainView(convertView, R.id.tv);
        ImageBean item = getItem(position);
        if(item.state==1){
            tv.setText("进行中");
        }else {
            tv.setText("已结束");
        }
        Glide.with(mContext).load(item.ico_id).into(iv);
        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
