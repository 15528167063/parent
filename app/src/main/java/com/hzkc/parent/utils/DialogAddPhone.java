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


/**
 * 选择图片
 * Created by lwj .
 * Data 2016/2/26 12:50.
 */
public class DialogAddPhone extends Dialog implements View.OnClickListener {
    public EditText et_nc,et_phone;
    private TextView bt_comfirm;
    private View view;
    /**
     * 监听
     */
    private DialogChangeWord.onDialogChangeNcListener imgListener;

    public void setImgListener(DialogChangeWord.onDialogChangeNcListener imgListener) {
        this.imgListener = imgListener;
    }

    public DialogAddPhone(Context context) {
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
        view = getLayoutInflater().inflate(R.layout.dialog_add_phone, null);
        et_nc = (EditText) view.findViewById(R.id.et_my_nc);
        et_phone = (EditText) view.findViewById(R.id.et_my_phone);
        bt_comfirm = (TextView) view.findViewById(R.id.bt_comfirm);
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
    public String getEt_nc() {
        return et_nc.getText().toString();
    }
    public String getEt_phone() {
        return et_phone.getText().toString();
    }

}
