package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2018/6/14.
 */

public class TeamRoot {
    private String code;

    private String message;

    private List<TeamResult> result ;

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
    public void setResult(List<TeamResult> result){
        this.result = result;
    }
    public List<TeamResult> getResult(){
        return this.result;
    }
}
