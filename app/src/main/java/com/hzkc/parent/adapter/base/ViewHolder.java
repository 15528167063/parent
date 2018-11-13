package com.hzkc.parent.adapter.base;

import android.util.SparseArray;
import android.view.View;

/**
 * 类名：ViewHolder
 * 功能：
 * 作者：Yong.Wang
 * 日期：2016-11-29
 */

public class ViewHolder {

    public SparseArray<View> views = new SparseArray<View>();

    /**
     * 指定resId和类型即可获取到相应的view
     *
     * @param convertView
     * @param resId
     * @param <T>
     * @return
     */
    public <T extends View> T obtainView(View convertView, int resId) {
        View v = views.get(resId);
        if (null == v) {
            v = convertView.findViewById(resId);
            views.put(resId, v);
        }
        return (T) v;
    }
}
