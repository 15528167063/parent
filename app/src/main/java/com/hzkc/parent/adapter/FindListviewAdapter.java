package com.hzkc.parent.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.Bean.FindArticleBean;
import com.hzkc.parent.R;
import com.hzkc.parent.utils.LogUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lenovo-s on 2016/12/8.
 */

public class FindListviewAdapter extends BaseAdapter {
    private List<FindArticleBean> findList;
    private static final String TAG = "FindListviewAdapter";
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public FindListviewAdapter(List<FindArticleBean> findList, Context context) {
        this.findList=findList;
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return findList.size();
    }

    @Override
    public Object getItem(int position) {
        return findList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogUtil.e(TAG,"getView " + position + " " + convertView);
        ViewHolder holder=null;
        if(convertView==null){
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_find, null);
            holder.ivImg = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.tvArticleTitle = (TextView) convertView.findViewById(R.id.tv_article_title);
            holder.tvArticleTime = (TextView) convertView.findViewById(R.id.tv_article_time);
            // 为view设置标签
            convertView.setTag(holder);
        }else{
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        Picasso.with(context).load(findList.get(position).getImgUrl()).into(holder.ivImg);
        holder.tvArticleTitle.setText(findList.get(position).getName());
        holder.tvArticleTime.setText(findList.get(position).getTime());
        return convertView;
    }
    public static class ViewHolder {
        public ImageView ivImg;
        public TextView tvArticleTitle;
        public TextView tvArticleTime;
    }
}
