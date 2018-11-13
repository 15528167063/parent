package com.hzkc.parent.js;

import android.app.Activity;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;

import com.hzkc.parent.appcation.MyApplication;

/**
 * js注入
 *
 * @author lwj
 * @date 2018/1/8 13:50
 */

public class JSWebInterface {
    private Activity mActivity;
    private String parentUUID ;
    private String infor_id ;

    public JSWebInterface(Activity mActivity, String infor_id, String parentUUID) {
        this.mActivity = mActivity;
        this.infor_id=infor_id;
        this.parentUUID=parentUUID;
    }

    @JavascriptInterface
    public void exitWeb() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }


    @JavascriptInterface
    public String getParentUid() {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE);
        String parentUUID = sp.getString("parentUUID", "");
        return parentUUID+"";
    }
    @JavascriptInterface
    public String getInfor_id() {
        return infor_id;
    }
    @JavascriptInterface
    public String getParentUUID() {
        return parentUUID;
    }


}
