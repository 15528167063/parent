package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/8/20.
 */

public class VipRootBean {
    private int code;

    private String msg;

    private VipDataBean data;

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
    public void setData(VipDataBean data){
        this.data = data;
    }
    public VipDataBean getData(){
        return this.data;
    }
}
