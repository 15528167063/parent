package com.hzkc.parent.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.hzkc.parent.R;

/**
 * 用户登陆选择角色
 *
 * @author lwj
 * @date 2017/5/22 10:47
 */

public class IdentitySelectedDialog extends Dialog implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    RadioButton teacherBtn;
    RadioButton parentBtn;
    ImageView closeImg;
    Context context;

    private onIdentitySelectListener selectListener;

    public void setSelectListener(onIdentitySelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public IdentitySelectedDialog(Context context) {
        super(context, R.style.customDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_identity, null);
        teacherBtn = (RadioButton) view.findViewById(R.id.select_identity_teacher);
        parentBtn = (RadioButton) view.findViewById(R.id.select_identity_parent);
        closeImg = (ImageView) view.findViewById(R.id.select_identity_close);
        teacherBtn.setOnCheckedChangeListener(this);
        parentBtn.setOnCheckedChangeListener(this);
        closeImg.setOnClickListener(this);
        setContentView(view);
        setCancelable(false);
        initIdentity();
    }

    public void initIdentity() {
        //判断用户身份
        String teacherFlg = "y";
        String parentFlg = "y";
        if (TextUtils.isEmpty(teacherFlg) || !TextUtils.equals(teacherFlg, "y")) {
            //没有教师身份
            teacherBtn.setVisibility(View.GONE);
        } else {
            //有教师身份
            teacherBtn.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(parentFlg) || !TextUtils.equals(parentFlg, "y")) {
            //没有家长身份
            parentBtn.setVisibility(View.GONE);
        } else {
            //有家长身份
            parentBtn.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_identity_close:
                if (selectListener != null) {
                    selectListener.identityClick(2);
                }
                dismiss();
                break;
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton radioButton, boolean isChecked) {
        if (isChecked) {
            switch (radioButton.getId()) {
                case R.id.select_identity_teacher:
                    //我是家长
                    if (selectListener != null) {
                        selectListener.identityClick(0);
                    }
                    this.dismiss();
                    break;
                case R.id.select_identity_parent:
                    //我是武警
                    if (selectListener != null) {
                        selectListener.identityClick(1);
                    }
                    this.dismiss();
                    break;
            }
        }
    }


    /**
     * 是否显示底部关闭按钮，默认不显示
     *
     * @param show
     */
    public void setShowOrHideCloseIcon(boolean show) {
        if (show)
            closeImg.setVisibility(View.VISIBLE);
        else
            closeImg.setVisibility(View.GONE);
    }

    /**
     * 选择身份回掉
     */
    public interface onIdentitySelectListener {
        /**
         * 选中的身份
         *
         * @param index 0教师，1家长，2管理员,3商户
         */
        void identityClick(int index);
    }


}
