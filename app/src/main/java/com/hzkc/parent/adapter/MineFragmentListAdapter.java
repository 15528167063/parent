package com.hzkc.parent.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzkc.parent.Bean.MineItemBean;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.MyMemeberActivity;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.DensityUtil;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.List;

/**
 * Created by lenovo-s on 2016/12/8.
 */

public class MineFragmentListAdapter extends BaseAdapter {
    private List<MineItemBean> minelist;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;
    private Boolean   ishare=false,istuiguan=false;
    private String   parentUUID,des;
    public MineFragmentListAdapter(List<MineItemBean> minelist, Context context,boolean ishare,boolean istuiguan,String parentUUID ,String des) {
        this.minelist = minelist;
        this.context = context;
        this.ishare=ishare;
        this.istuiguan=istuiguan;
        this.parentUUID=parentUUID;
        this.des=des;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return minelist.size();
    }

    @Override
    public Object getItem(int position) {
        return minelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_mine_list, null);
            holder.tvItemName = (TextView) convertView.findViewById(R.id.tv_mine_name);
            holder.ivItemIcon = (ImageView) convertView.findViewById(R.id.iv_mine_icon);
            holder.lin = (LinearLayout) convertView.findViewById(R.id.lin);
            holder.tv_share = (TextView) convertView.findViewById(R.id.tv_share);
            holder.tv_erweima = (TextView) convertView.findViewById(R.id.tv_erweima);
            holder.lin_1 =  convertView.findViewById(R.id.lin_1);
            holder.lin_2 =  convertView.findViewById(R.id.lin_2);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        if(position==0){

            holder.tv_share.setVisibility(View.GONE);
            holder.lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent0 = new Intent(context, MyMemeberActivity.class);
                    context.startActivity(intent0);
                }
            });
        }
        if(position==1){
            if(ishare){
                holder.tv_share.setVisibility(View.VISIBLE);
                holder.tv_share.setText(des);
            }else {
                holder.tv_share.setVisibility(View.GONE);
            }

        }
        holder.tvItemName.setText(minelist.get(position).getTvName());
        holder.ivItemIcon.setImageResource(minelist.get(position).getIvIcon());
        holder.tv_erweima.setVisibility(View.GONE);
        if(!istuiguan  && position==3){
            holder.lin.setVisibility(View.GONE);
        }
        if(istuiguan  && position==3){
            holder.lin.setVisibility(View.VISIBLE);
            holder.tv_erweima.setVisibility(View.VISIBLE);
            holder.tv_erweima.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPoP();
                }
            });
        }
        if(position==1 || position==3|| position==5){
            holder.lin_2.setVisibility(View.VISIBLE);
            holder.lin_1.setVisibility(View.GONE);
        }
        return convertView;
    }

    public static class ViewHolder {
        public TextView tvItemName;
        public ImageView ivItemIcon;
        public LinearLayout lin;
        public TextView  tv_share;
        public TextView  tv_erweima;
        public View  lin_1;
        public View  lin_2;
    }

    public Bitmap mBitmap = null;
    private void showPoP() {
        final AlertDialog ad=new AlertDialog.Builder(context).create();
        ad.show();
        Window window = ad.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 有白色背景，加这句代码
        View view= LayoutInflater.from(context).inflate(R.layout.weweimadialog,null);
        ImageView iv_img = (ImageView) view.findViewById(R.id.iv_img);
        String url = Constants.FIND_URL+"YczH5/index.php?code=" + parentUUID;
        mBitmap = CodeUtils.createImage(url,  DensityUtil.dip2px(context,200),  DensityUtil.dip2px(context,200), BitmapFactory.decodeResource(context.getResources(), R.drawable.my_icon));
        iv_img.setImageBitmap(mBitmap);
        window.setContentView(view);

    }

}
