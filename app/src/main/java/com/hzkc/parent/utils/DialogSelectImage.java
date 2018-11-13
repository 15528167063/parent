package com.hzkc.parent.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hzkc.parent.R;


/**
 * 选择图片
 * Created by lwj .
 * Data 2016/2/26 12:50.
 */
public class DialogSelectImage extends Dialog implements View.OnClickListener {
    /**
     * 相机
     */
    public final static int MERCHANT_DETAIL_CAMERA = 0x1;
    /**
     * 相册
     */
    public final static int MERCHANT_DETAIL_PHOTOGRAPH = 0x2;
    /**
     * 取消
     */
    private TextView cancel;
    /**
     * 拍照
     */
    private TextView camera;
    /**
     * 本地图片
     */
    private TextView photograph;
    private View view;
    /**
     * 监听
     */
    private onDialogSelectImgListener imgListener;

    public void setImgListener(onDialogSelectImgListener imgListener) {
        this.imgListener = imgListener;
    }

    public DialogSelectImage(Context context) {
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

        view = getLayoutInflater().inflate(R.layout.dialog_select_img, null);
        cancel = (TextView) view.findViewById(R.id.dialog_select_img_cancel);
        camera = (TextView) view.findViewById(R.id.dialog_select_img_camera);
        photograph = (TextView) view.findViewById(R.id.dialog_select_img_photograph);
        cancel.setOnClickListener(this);
        camera.setOnClickListener(this);
        photograph.setOnClickListener(this);

        setContentView(view);

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_select_img_cancel:
                dismiss();
                if (imgListener != null) {
                    imgListener.imgClick(3);
                }
                break;
            case R.id.dialog_select_img_camera:
                //拍照
                dismiss();
                if (imgListener != null) {
                    imgListener.imgClick(1);
                }
                break;
            case R.id.dialog_select_img_photograph:
                //本地图片
                dismiss();
                if (imgListener != null) {
                    imgListener.imgClick(2);
                }
                break;
        }
    }

    /**
     * 选择图片监听
     */
    public interface onDialogSelectImgListener {
        /**
         * 选择图片item监听
         *
         * @param index 1，拍照，2，相册
         */
        void imgClick(int index);
    }

}
