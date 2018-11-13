package com.hzkc.parent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hzkc.parent.R;

/**
 * Created by Administrator on 2017/7/21.
 */

public class LocationImageView extends LinearLayout {
    private View rootView;
    private ImageView iv;
    public LocationImageView(Context context) {
        super(context);
        init();
    }


    public LocationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LocationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        rootView = View.inflate(getContext(), R.layout.item_image,this);
        iv=(ImageView) rootView.findViewById(R.id.iv_img);
    }


    public  void setImage(Boolean isboy) {
        if(isboy){
            iv.setImageResource(R.drawable.child_detail_icon_boy);
        }else {
            iv.setImageResource(R.drawable.child_detail_icon_girl);
        }
    }


}
