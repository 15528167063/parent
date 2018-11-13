package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/15.
 */

public class InformatResultbean {
    private int infor_id;

    private String title;

    private List<String> title_img ;

    private String partyurl;

    private int view;

    private String create_time;

    public void setInfor_id(int infor_id){
        this.infor_id = infor_id;
    }
    public int getInfor_id(){
        return this.infor_id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setString(List<String> title_img){
        this.title_img = title_img;
    }
    public List<String> getString(){
        return this.title_img;
    }
    public void setPartyurl(String partyurl){
        this.partyurl = partyurl;
    }
    public String getPartyurl(){
        return this.partyurl;
    }
    public void setView(int view){
        this.view = view;
    }
    public int getView(){
        return this.view;
    }
    public void setCreate_time(String create_time){
        this.create_time = create_time;
    }
    public String getCreate_time(){
        return this.create_time;
    }

}
