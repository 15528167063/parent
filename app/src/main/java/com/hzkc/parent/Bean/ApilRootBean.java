package com.hzkc.parent.Bean;

import java.io.Serializable;

/**
 * Created by wangyong on 2017/3/29.
 */

public class ApilRootBean implements Serializable {
    private int code;

    private String msg;

    private String data ;

    public void setCode(int code){
        this.code = code;
    }
    public Integer getCode(){
        return this.code;
    }
    public void setMessage(String message){
        this.msg = message;
    }
    public String getMessage(){
        return this.msg;
    }
    public void setResult(String result){
        this.data = result;
    }
    public String getResult(){
        return this.data;
    }
}
