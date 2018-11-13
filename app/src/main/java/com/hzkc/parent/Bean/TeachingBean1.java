package com.hzkc.parent.Bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.rv.ViewHolder;
import com.hzkc.parent.adapter.rv.mul.IMulTypeHelper;

import java.io.ByteArrayOutputStream;


public class TeachingBean1 implements IMulTypeHelper {

    public String src;
    public String weburl;
    public String time;
    public String pic;
    public String title;
    public String category;
    public String content;
    public String url;

    public TeachingBean1( String src, String weburl, String time, String pic, String title, String content) {
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
        return R.layout.item_teach_type1;
    }

    @Override
    public void onBind(ViewHolder holder) {
        holder.setText(R.id.tv_title, title);
        holder.setText(R.id.tv_time,"发布日期 "+ time);
        if(pic.startsWith("data:image/")){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            stringtoBitmap(pic).compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes=baos.toByteArray();
            Glide.with(holder.itemView.getContext()).load(bytes).into((ImageView) holder.getView(R.id.iv_img));
        }else {
            Glide.with(holder.itemView.getContext()).load(pic).into((ImageView) holder.getView(R.id.iv_img));
        }
    }

    public Bitmap stringtoBitmap(String string){
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}
