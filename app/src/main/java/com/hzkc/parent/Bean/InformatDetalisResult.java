package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/8/15.
 */

public class InformatDetalisResult {
    private int infor_id;

    private String keyword;

    private String description;

    private String content;

    private String partyurl;

    private int view;

    private String create_time;

    public void setInfor_id(int infor_id){
        this.infor_id = infor_id;
    }
    public int getInfor_id(){
        return this.infor_id;
    }
    public void setKeyword(String keyword){
        this.keyword = keyword;
    }
    public String getKeyword(){
        return this.keyword;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getDescription(){
        return this.description;
    }
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return this.content;
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
