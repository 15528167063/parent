package com.hzkc.parent.Bean;

import java.io.Serializable;

/**
 * Created by wangyong on 2017/3/29.
 */

public class WechatRootBean implements Serializable {
    private String code;

    private String message;

    private WechatResultBean data ;

    public void setCode(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setResult(WechatResultBean result){
        this.data = result;
    }
    public WechatResultBean getResult(){
        return this.data;
    }
}
