package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/6/29.{"code":"200","message":"OK","result":[{"isactivity":"Y","describe":"分享有礼","ischannel":"N"}]}
 */

public class ShareBean {
    public String code;
    public String msg;
    public Result data;

    public class Result {
        public String isActivity;
        public String describe;
        public String ischannel;
    }
}
