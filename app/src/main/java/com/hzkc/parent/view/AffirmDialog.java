package com.hzkc.parent.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.hzkc.parent.R;

/**
 * 含有两个按钮的提示弹出框
 */
public class AffirmDialog extends Dialog {
    private int tag;
    TextView title;
    TextView cancel;
    TextView ensure;
    public AffirmDialog(Context context) {
        super(context, R.style.customDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_affirm, null);
        title=(TextView)view.findViewById(R.id.affirm_title);
        cancel=(TextView)view.findViewById(R.id.affirm_cancel);
        ensure=(TextView)view.findViewById(R.id.affirm_confirm);
        this.setContentView(view);
    }

    /**
     * 设置监听 <ul>
     * <li>取消，affirm_cancel</li>
     * <li>确认，affirm_confirm</li>
     * </ul>
     *
     * @param listener 监听事件
     */
    public void setAffirmClickListener(View.OnClickListener listener) {
        if (listener != null) {
            cancel.setOnClickListener(listener);
            ensure.setOnClickListener(listener);
        }
    }


    /**
     * 设置标志位
     */
    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    /**
     * 设置标题文字
     *
     * @param title
     */
    public void setTitleText(String title) {
        if (!TextUtils.isEmpty(title)) {
            this.title.setText(title);
        }
    }


    /**
     * 设置取消按钮文字
     *
     * @param cancel
     */
    public void setCancelText(String cancel) {
        if (!TextUtils.isEmpty(cancel)) {
            this.cancel.setText(cancel);
        }
    }

    /**
     * 设置确认按钮文字
     */
    public void setEnsureText(String ensure) {
        if (!TextUtils.isEmpty(ensure)) {
            this.ensure.setText(ensure);
        }
    }


}
