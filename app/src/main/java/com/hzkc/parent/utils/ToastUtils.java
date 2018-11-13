package com.hzkc.parent.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/12.
 */

public class ToastUtils {
    private static Toast mToast=null;
    public static void showToast(Context context,String msg){
        if(mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
