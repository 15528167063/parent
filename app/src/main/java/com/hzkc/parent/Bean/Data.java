package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2017/11/28.
 */
public class Data {
    private String posterId;

    private String tags;

    private int publishDate;

    private String commentCount;

    private List<String> imageUrls ;

    private String id;

    private String posterScreenName;

    private String title;

    private String url;

    private String publishDateStr;

    private String content;

    public void setPosterId(String posterId){
        this.posterId = posterId;
    }
    public String getPosterId(){
        return this.posterId;
    }
    public void setTags(String tags){
        this.tags = tags;
    }
    public String getTags(){
        return this.tags;
    }
    public void setPublishDate(int publishDate){
        this.publishDate = publishDate;
    }
    public int getPublishDate(){
        return this.publishDate;
    }
    public void setCommentCount(String commentCount){
        this.commentCount = commentCount;
    }
    public String getCommentCount(){
        return this.commentCount;
    }
    public void setString(List<String> imageUrls){
        this.imageUrls = imageUrls;
    }
    public List<String> getString(){
        return this.imageUrls;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }
    public void setPosterScreenName(String posterScreenName){
        this.posterScreenName = posterScreenName;
    }
    public String getPosterScreenName(){
        return this.posterScreenName;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }
    public void setPublishDateStr(String publishDateStr){
        this.publishDateStr = publishDateStr;
    }
    public String getPublishDateStr(){
        return this.publishDateStr;
    }
    public void setContent(String content){
        this.content = content;
    }
    public String getContent(){
        return this.content;
    }

}