package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/28.
 */

public class Root {
    private boolean hasNext;

    private String retcode;

    private String appCode;

    private String dataType;

    private String pageToken;

    private List<Data> data ;

    public void setHasNext(boolean hasNext){
        this.hasNext = hasNext;
    }
    public boolean getHasNext(){
        return this.hasNext;
    }
    public void setRetcode(String retcode){
        this.retcode = retcode;
    }
    public String getRetcode(){
        return this.retcode;
    }
    public void setAppCode(String appCode){
        this.appCode = appCode;
    }
    public String getAppCode(){
        return this.appCode;
    }
    public void setDataType(String dataType){
        this.dataType = dataType;
    }
    public String getDataType(){
        return this.dataType;
    }
    public void setPageToken(String pageToken){
        this.pageToken = pageToken;
    }
    public String getPageToken(){
        return this.pageToken;
    }
    public void setData(List<Data> data){
        this.data = data;
    }
    public List<Data> getData(){
        return this.data;
    }

}