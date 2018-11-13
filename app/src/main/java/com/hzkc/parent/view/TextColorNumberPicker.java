package com.hzkc.parent.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;


import com.hzkc.parent.R;

import java.lang.reflect.Field;

/**
 * 自定义NumberPicker，更改字体颜色、分割线样式
 * Created by Administrator on 2017/3/13.
 */

public class TextColorNumberPicker extends NumberPicker {
    public TextColorNumberPicker(Context context) {
        super(context);
    }

    public TextColorNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextColorNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    public void updateView(View view) {
        if (view instanceof EditText) {
            //这里修改显示字体的属性，主要修改颜色
            ((EditText) view).setTextColor(Color.parseColor("#666666"));
            ((EditText) view).setTextSize(14);
        }
        //点击数字不弹出键盘
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    }


    public void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(ContextCompat.getColor(getContext(), R.color.color_divider)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        // 分割线高度
        for (Field pf2 : pickerFields) {
            if (pf2.getName().equals("mSelectionDividerHeight")) {
                pf2.setAccessible(true);
                try {
                    int result = 1;
                    pf2.set(picker, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }


}
