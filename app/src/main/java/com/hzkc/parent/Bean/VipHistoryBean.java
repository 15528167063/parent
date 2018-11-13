package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/20.
 */

public class VipHistoryBean {
    private int code;

    private String msg;

    private List<VipHistoryNewData> data ;

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
    public void setData(List<VipHistoryNewData> data){
        this.data = data;
    }
    public List<VipHistoryNewData> getData(){
        return this.data;
    }
}
