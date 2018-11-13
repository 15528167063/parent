package com.hzkc.parent.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/12/5.
 */

public class EnlargeTextView extends TextView {

    public EnlargeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float orgTextSize;
    private float textSize;
    private float dSize;

    /** speed值控制缩放速度 **/
    private int speed = 5;
    /** scale值控制缩放最大时的倍数 **/
    private float scale = 2.0f;

    @Override
    protected void onDraw(Canvas canvas) {
        if (orgTextSize == 0) {
            /** 这个值保存原字体大小 **/
            orgTextSize = getTextSize();
            /** 这个值用于控制缩放 **/
            textSize = orgTextSize;
        }
        super.onDraw(canvas);
        /** 以下三个if else 条件分别指明了 放大，缩小，停止缩放三个过程 **/
        if (dSize == 0 && Math.ceil(textSize) < Math.ceil(orgTextSize * scale)) {
            dSize = speed;
        } else if (dSize == speed && Math.abs(Math.ceil(textSize) - Math.ceil(orgTextSize * scale)) <= speed) {
            dSize = -speed;
        } else if (dSize == -speed && Math.abs(Math.ceil(textSize) - Math.ceil(orgTextSize)) <= speed) {
            dSize = 0;
        }
        textSize += dSize;
        //getTextSize()返回的是px(像素)值，setTextSize()需要的是sp值，因此这里通过TypedValue处理
        //若不处理会发现字体放大时基准线下移，字体上部的位置保持不变，不是按照中心放大的，
        //当然也有可能发生其他问题，都是单位未转换导致的
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        /** 当dSzie为0时，说明字体已经完成了一次放大并还原，之后不再重新调用onDraw**/
        if (dSize != 0) {
            /** 通过invalidate()调用onDraw()强制刷新**/
            invalidate();
        }
    }
    /**
     * TextView的setText(CharSequence text)是一个final的方法，不能被重写，
     * 但它实际上调用的是setText(CharSequence text, BufferType type)这个方法
     * 以下的重置textSize和dSize是为了在点击较快时使每个字体都从头经历一次放大缩小过程
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        textSize = orgTextSize;
        dSize=0;
    }
}
