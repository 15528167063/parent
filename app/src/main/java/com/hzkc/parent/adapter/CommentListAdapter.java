package com.hzkc.parent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hzkc.parent.Bean.CommentBean;
import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;

import java.util.List;

/**
 * Created by lenovo-s on 2016/12/8.
 */

public class CommentListAdapter extends BaseAdapter{
    private List<CommentBean> commentList;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    public CommentListAdapter(List<CommentBean> commentList, Context context) {
        this.commentList=commentList;
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_comment_list, null);
            holder.ivHfIcon = (SimpleDraweeView) convertView.findViewById(R.id.iv_hf_icon);
            holder.tvHfName = (TextView) convertView.findViewById(R.id.tv_hf_name);
            holder.tvHfTime = (TextView) convertView.findViewById(R.id.tv_hf_time);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
            // 为view设置标签
            convertView.setTag(holder);
        }else{
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvContent.setText(commentList.get(position).getText());
        holder.tvHfName.setText(commentList.get(position).getHfnc());
        holder.tvHfTime.setText(commentList.get(position).getTime());
        holder.ivHfIcon.setImageURI(Constants.FIND_URL_TX+commentList.get(position).getTxid()+".jpg");
        return convertView;
    }
    public static class ViewHolder {
        private SimpleDraweeView ivHfIcon;
        private TextView tvHfName;
        private TextView tvHfTime;
        private TextView tvContent;
    }
}
