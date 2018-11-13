package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/8/15.
 */

public class InformatDetalisbean {
    private int code;
    private String msg;
    private InformatDetalisResult data ;

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
    public void setData(InformatDetalisResult data){
        this.data = data;
    }
    public InformatDetalisResult getData(){
        return this.data;

    }
}
