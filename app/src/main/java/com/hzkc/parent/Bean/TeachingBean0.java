package com.hzkc.parent.Bean;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.rv.ViewHolder;
import com.hzkc.parent.adapter.rv.mul.IMulTypeHelper;


public class TeachingBean0 implements IMulTypeHelper {
    public String src;
    public String weburl;
    public String time;
    public String pic;
    public String title;
    public String category;
    public String content;
    public String url;

    public TeachingBean0(String src, String weburl, String time, String pic, String title, String content) {
        this.src = src;
        this.weburl = weburl;
        this.time = time;
        this.pic = pic;
        this.title = title;
        this.content = content;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int getItemLayoutId() {
        return R.layout.item_teach_type0;
    }

    @Override
    public void onBind(ViewHolder holder) {
        holder.setText(R.id.tv_title, title);
        holder.setText(R.id.tv_time,"发布日期 "+ time);
    }
}
