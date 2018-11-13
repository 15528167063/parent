package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/6/14.
 */

public class TeamResult {
    private String name;

    private String loginname;

    private String regtime;
    private String sex;
    private String state;//1团控中  0托管  2不受控

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setLoginname(String loginname){
        this.loginname = loginname;
    }
    public String getLoginname(){
        return this.loginname;
    }
    public void setRegtime(String regtime){
        this.regtime = regtime;
    }
    public String getRegtime(){
        return this.regtime;
    }
}
