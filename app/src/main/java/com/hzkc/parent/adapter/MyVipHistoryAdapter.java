package com.hzkc.parent.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzkc.parent.Bean.VipHistoryNewData;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.OrderHistoryActivity;

import java.util.List;

/**
 * Created by lenovo-s on 2016/11/9.
 */

public class MyVipHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<VipHistoryNewData>datas;
    private LayoutInflater inflater = null;
    public OrderHistoryActivity activity;
    public MyVipHistoryAdapter(List<VipHistoryNewData>datas, Context context, OrderHistoryActivity activity) {
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
            convertView = inflater.inflate(R.layout.item_vip_history, null);
            holder.line1 = (RelativeLayout) convertView.findViewById(R.id.re_1);
            holder.tv_vip_type= (TextView) convertView.findViewById(R.id.tv_vip_type);
            holder.tv_vip_way= (TextView) convertView.findViewById(R.id.tv_vip_way);
            holder.tv_vip_num= (TextView) convertView.findViewById(R.id.tv_vip_num);
            holder.tv_vip_time= (TextView) convertView.findViewById(R.id.tv_vip_time);
            holder.tv_state= (TextView) convertView.findViewById(R.id.tv_state);
            holder.tv_vip_hao= (TextView) convertView.findViewById(R.id.tv_vip_hao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        VipHistoryNewData vipHisData = datas.get(position);
        if(vipHisData.getPay_status().equals("支付成功")){
            holder.tv_state.setText("支付成功");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.textblack));
        }else {
            holder.tv_state.setText("支付失败");
            holder.tv_state.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.tv_vip_type.setText(vipHisData.getGoods_name());
        holder.tv_vip_way.setText("付款方式 : "+vipHisData.getPay_src());
        holder.tv_vip_num.setText("￥"+vipHisData.getPrice());
        holder.tv_vip_time.setText("创建日期 : "+vipHisData.getPay_time());
        holder.tv_vip_hao.setText("订单编号 : "+vipHisData.getTrade_no());

        return convertView;
    }
    public static class ViewHolder {
        public RelativeLayout line1;
        public TextView tv_vip_type;
        public TextView tv_vip_way;
        public TextView tv_vip_num;
        public TextView tv_vip_time;
        public TextView tv_vip_hao;
        public TextView tv_state;
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
}
