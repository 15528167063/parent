package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/15.
 */

public class InformatRootbean {
    private int code;
    private String msg;
    private List<InformatResultbean> data ;

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
    public void setData(List<InformatResultbean> data){
        this.data = data;
    }
    public List<InformatResultbean> getData(){
        return this.data;

    }
}
