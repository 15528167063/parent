package com.hzkc.parent.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hzkc.parent.Bean.VipPriceBean;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.MyNewMemeberActivity;
import com.hzkc.parent.utils.PayUtils;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/9.
 */

public class MyNewVipAdapter extends BaseAdapter {
    private Context context;
    private List<VipPriceBean>datas;
    private LayoutInflater inflater = null;
    public String price;
    public MyNewMemeberActivity activity;
    public MyNewVipAdapter(List<VipPriceBean>datas, Context context, MyNewMemeberActivity activity) {
        this.context = context;
        this.datas = datas;
        this.activity=activity;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
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
            convertView = inflater.inflate(R.layout.item_vip, null);
            holder.line1 = (LinearLayout) convertView.findViewById(R.id.line1);
            holder.line2 = (LinearLayout) convertView.findViewById(R.id.line2);
            holder.tv_xufei= (TextView) convertView.findViewById(R.id.tv_xufei);
            holder.tv_vip= (TextView) convertView.findViewById(R.id.tv_vip);
            holder.tv_price= (TextView) convertView.findViewById(R.id.tv_price);
            holder.tv_youhui= (TextView) convertView.findViewById(R.id.tv_youhui);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final VipPriceBean vipBean = datas.get(position);
        holder.tv_xufei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuyPop(vipBean);

            }
        });
        if(position==0){
            holder.line1.setVisibility(View.VISIBLE);
        }else {
            holder.line1.setVisibility(View.GONE);
        }
        holder.tv_vip.setText(datas.get(position).getGoods_name());
        holder.tv_price.setText(datas.get(position).getOriginal_price());
        holder.tv_youhui.setText(datas.get(position).getPrice());
        holder.tv_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
        holder.tv_price.getPaint().setAntiAlias(true);
        return convertView;
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public static class ViewHolder {
        public LinearLayout line1;
        public LinearLayout line2;
        public TextView tv_xufei;
        public TextView tv_vip;
        public TextView tv_price;
        public TextView tv_youhui;
    }

    public PopupWindow window;
    public void showBuyPop(final VipPriceBean vipBean ) {
        LayoutInflater inflater = (LayoutInflater)context. getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.layout_pop_pay, null);
        window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        TextView priceTv1 = (TextView) view.findViewById(R.id.totalPriceTv);
        priceTv1.setText(vipBean.getPrice());
        final View wechatImg = view.findViewById(R.id.wechatImg);
        final View alipayImg = view.findViewById(R.id.alipayImg);
        view.findViewById(R.id.closeImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        view.findViewById(R.id.wechatPayLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wechatImg.setVisibility(View.VISIBLE);
                alipayImg.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.alipayLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wechatImg.setVisibility(View.GONE);
                alipayImg.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.payTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                String userid= context.getSharedPreferences("info", context.MODE_PRIVATE).getString("parentUUID", "");
//                if(TextUtils.isEmpty(userid)){
//                    userid="447";
//                }
                if (wechatImg.getVisibility() == View.VISIBLE) {
                    PayUtils.payWechat(vipBean.getGoods_id(), userid,"0");
                } else if (alipayImg.getVisibility() == View.VISIBLE) {
                    PayUtils.payali(vipBean.getGoods_id(), userid,activity);
                }
            }
        });
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        backgroundAlpha(activity, 0.5f);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(activity, 1f);
            }
        });
    }
}
