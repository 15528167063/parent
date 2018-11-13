package com.hzkc.parent.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名：BaseListAdapter
 * 功能：
 * 作者：Yong.Wang
 * 日期：2016-11-29
 */

public abstract class BaseListAdapter<T> extends BaseAdapter {
    protected List<T> list;
    protected Context mContext;

    public BaseListAdapter(Context mContext) {
        this.mContext = mContext;
        this.list = new ArrayList<>();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(itemLayoutRes(), parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return getView(position, convertView, parent, viewHolder);
    }

    /**
     * 此方法需要子类实现，需要返回item布局的resource id
     */
    public abstract int itemLayoutRes();

    /**
     * 使用该getView方法替换原来的getView方法，需要子类实现
     */
    public abstract View getView(int position, View convertView, ViewGroup parent, ViewHolder holder);

    public void setList(List<T> list) {
        if (list == null) {
            return;
        }
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addList(List<T> list) {
        if (list == null || list.size() <= 0) {
            return;
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        if (list == null) {
            return;
        }
        this.list.add(item);
        notifyDataSetChanged();
    }

    public List<T> getList(){
        return list;
    }

}
