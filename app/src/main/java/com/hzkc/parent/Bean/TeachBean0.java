package com.hzkc.parent.Bean;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.rv.ViewHolder;
import com.hzkc.parent.adapter.rv.mul.IMulTypeHelper;

import java.util.List;


public class TeachBean0 implements IMulTypeHelper {
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
    public TeachBean0(String posterId, List<String> imageUrls, String title, String url, String publishDateStr) {
        this.posterId = posterId;
        this.imageUrls = imageUrls;
        this.title = title;
        this.url = url;
        this.publishDateStr = publishDateStr;
    }
    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(int publishDate) {
        this.publishDate = publishDate;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosterScreenName() {
        return posterScreenName;
    }

    public void setPosterScreenName(String posterScreenName) {
        this.posterScreenName = posterScreenName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishDateStr() {
        return publishDateStr;
    }

    public void setPublishDateStr(String publishDateStr) {
        this.publishDateStr = publishDateStr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.item_teach_type0;
    }

    @Override
    public void onBind(ViewHolder holder) {
        holder.setText(R.id.tv_title, title);
        holder.setText(R.id.tv_time,"发布日期 "+ publishDateStr);
    }
}
