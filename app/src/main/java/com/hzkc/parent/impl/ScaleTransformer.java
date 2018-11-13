package com.hzkc.parent.impl;

import android.view.View;

import com.hzkc.parent.view.GalleryLayoutManager;


/**
 * Created by chensuilun on 2016/12/16.
 */
public class ScaleTransformer implements GalleryLayoutManager.ItemTransformer {

    private static final String TAG = "CurveTransformer";


    @Override
    public void transformItem(GalleryLayoutManager layoutManager, View item, float fraction) {
        item.setPivotX(item.getWidth() / 2.f);
        item.setPivotY(item.getHeight()/2.0f);
        float scale = 1 - 0.2f * Math.abs(fraction);
        float scale1 = 1 - 0.15f * Math.abs(fraction);
        item.setScaleX(scale1);
        item.setScaleY(scale);
    }
}
