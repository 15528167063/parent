package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/8/14.
 */

public class VersonBean {
    private int code;

    private String msg;

    private VersonBeanData data;

    public void setCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return this.msg;
    }
    public void setData(VersonBeanData data){
        this.data = data;
    }
    public VersonBeanData getData(){
        return this.data;
    }
}
