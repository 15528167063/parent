package com.hzkc.parent.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hzkc.parent.R;
public class DialogChangeWord extends Dialog implements View.OnClickListener {
    private EditText et_nc;
    private TextView bt_comfirm;
    private TextView tv_my_nc;
    private View view;
    /**
     * 监听
     */
    private onDialogChangeNcListener imgListener;

    public void setImgListener(onDialogChangeNcListener imgListener) {
        this.imgListener = imgListener;
    }

    public DialogChangeWord(Context context) {
        super(context, R.style.customDialog);
        init();
    }

    /**
     * 初始化控件
     */
    private void init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        view = getLayoutInflater().inflate(R.layout.dialog_change_nc, null);
        et_nc = (EditText) view.findViewById(R.id.et_my_nc);
        bt_comfirm = (TextView) view.findViewById(R.id.bt_comfirm);
        tv_my_nc = (TextView) view.findViewById(R.id.tv_my_nc);
        bt_comfirm.setOnClickListener(this);

        setContentView(view);

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_comfirm:
                dismiss();
                if (imgListener != null) {
                    imgListener.ChangeNcClick();
                }
                break;
        }
    }
    /**
     * 选择图片监听
     */
    public interface onDialogChangeNcListener {
        void ChangeNcClick();
    }
    public void setTv_my_nc(String tv_my_nc) {
        this.tv_my_nc.setText("当前昵称为："+tv_my_nc);
    }
    public String getEt_nc() {
        return et_nc.getText().toString();
    }

}
