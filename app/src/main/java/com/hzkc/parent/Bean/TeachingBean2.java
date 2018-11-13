package com.hzkc.parent.Bean;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.rv.ViewHolder;
import com.hzkc.parent.adapter.rv.mul.IMulTypeHelper;

import java.util.ArrayList;
import java.util.List;


public class TeachingBean2 implements IMulTypeHelper {
    public String src;
    public String weburl;
    public String time;
    public List<String> pic;
    public String title;
    public String category;
    public String content;
    public String url;
    public String infor_id;

    public String getInfor_id() {
        return infor_id;
    }

    public void setInfor_id(String infor_id) {
        this.infor_id = infor_id;
    }
    public TeachingBean2(String infor_id, String weburl, String time, List<String> pic, String title, String content) {
        this.infor_id = infor_id;
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

    public List<String> getPic() {
        return pic;
    }

    public void setPic(List<String> pic) {
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
        return R.layout.item_teach_type2;
    }

    @Override
    public void onBind(ViewHolder holder) {
        holder.setText(R.id.tv_title, title);
        holder.setText(R.id.tv_number, "3000");
        holder.setText(R.id.tv_from, "第三方来源");
        holder.setText(R.id.tv_time,time);
        final List<PhotoInfo> imageDatas = new ArrayList<>();
        for (int i = 0; i < pic.size(); i++) {
            imageDatas.add(new PhotoInfo(pic.get(i)));
        }
        holder.setmultiImagView(R.id.multiImagView, imageDatas);

    }
}
