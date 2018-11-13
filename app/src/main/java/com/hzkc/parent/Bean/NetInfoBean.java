package com.hzkc.parent.Bean;

public class NetInfoBean {
    public String title;
    public String link;
    public String browse_time;
    public String num;


    public NetInfoBean(String urlname, String url) {
        this.title = urlname;
        this.link = url;
    }
}
