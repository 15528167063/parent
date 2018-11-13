package com.hzkc.parent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hzkc.parent.R;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.view.CircleImageView;

import java.util.List;


public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> implements View.OnClickListener {
    public static final int VIEW_TYPE_IMAGE = 0;
    public static final int VIEW_TYPE_TEXT = 1;
    private static final String TAG = "DemoAdapter";
    private List<ChildsTable> mItems;
    private int mType = VIEW_TYPE_IMAGE;
    private OnItemClickListener mOnItemClickListener;
    private Context context;
    public String childuuid;


    public DemoAdapter(String childuuid, List<ChildsTable> items, Context context) {
        this(childuuid,items, VIEW_TYPE_IMAGE);
        this.context=context;
    }

    public DemoAdapter(String childuuid, List<ChildsTable> items, int type) {
        this.mItems = items;
        mType = type;
        this.childuuid=childuuid;
    }

    public DemoAdapter setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    @Override
    public int getItemViewType(int position) {
        return mType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        ImageView viewById;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_demo, parent, false);

        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ChildsTable item = mItems.get(position);
        holder.itemView.setTag(position);
        holder.tv_name.setText(item.getName());
        //判断男孩还是女孩
        if(item.getSex().equals("1")){
            Glide.with(context).load("").error(R.drawable.child_detail_icon_boy).into(holder.iv);
        }else{
            Glide.with(context).load("").error(R.drawable.child_detail_icon_girl).into(holder.iv);
        }
        //判断是不是当前选中小孩
        if(childuuid.equals(item.getChilduuid())){
            holder.tv_chooseed.setBackgroundResource(R.drawable.btn_blue);
            holder.tv_chooseed.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            holder.tv_chooseed.setBackgroundResource(R.drawable.btn_white);
            holder.tv_chooseed.setTextColor(context.getResources().getColor(R.color.blue_color));
        }

        holder.tv_chooseed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通知保存小孩信息
                Intent intent = new Intent("sp");
                intent.putExtra("student",item);
                context.sendBroadcast(intent);
                //更新选择按钮状态
                childuuid=item.getChilduuid();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(final View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_chooseed,tv_unchoosed,tv_name;
        public CircleImageView iv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_chooseed = (TextView) itemView.findViewById(R.id.tv_choose1);
            tv_unchoosed = (TextView) itemView.findViewById(R.id.tv_choose2);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv = (CircleImageView) itemView.findViewById(R.id.iv_head);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
