package com.hzkc.parent.utils;


public class SystemShared {
    /**
     * 友盟推送传递数据
     */
    public static final String PUSH_EXTRA_BUNDLE = "launchBundle";
    /**
     * 注册友盟推送广播提交token到后台
     */
    public static final String PUSH_REGISTER_RECEIVER = "push_register_receiver";

    public static String UM_REGISTER_SUCCESS = TransformUtil.encodeStringToHex("um_register_success");
}
