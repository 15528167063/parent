package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2017/11/30.
 */

public class MessageResult {
    private int news_id;

    private String title;

    private String text;

    private String to_url;

    private String img_url;

    private String send_time;

    public int getNews_id() {
        return news_id;
    }

    public void setNews_id(int news_id) {
        this.news_id = news_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTo_url() {
        return to_url;
    }

    public void setTo_url(String to_url) {
        this.to_url = to_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }
}
