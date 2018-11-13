package com.hzkc.parent.Bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.rv.ViewHolder;
import com.hzkc.parent.adapter.rv.mul.IMulTypeHelper;
import com.hzkc.parent.mina.Constants;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class OtherBean1 implements IMulTypeHelper {

    public String src;
    public String weburl;
    public String time;
    public List<String> pic;
    public String title;
    public String category;
    public String content;
    public String url;
    public String infor_id;
    public String create_time;
    public int view;

    public String getInfor_id() {
        return infor_id;
    }

    public void setInfor_id(String infor_id) {
        this.infor_id = infor_id;
    }
    public OtherBean1(String infor_id, String weburl, String time, List<String> pic, String title, String content,String create_time,int view) {
        this.infor_id = infor_id;
        this.weburl = weburl;
        this.time = time;
        this.pic = pic;
        this.title = title;
        this.content = content;
        this.create_time = create_time;
        this.view=view;
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
        return R.layout.item_teach_type1;
    }

    @Override
    public void onBind(ViewHolder holder) {
        holder.setText(R.id.tv_title, title);
        holder.setText(R.id.tv_number, view+"");
        holder.setText(R.id.tv_from, "第三方来源");
        if(time.length()>=10){
            time=time.substring(0,10);
        }
        holder.setText(R.id.tv_time,create_time);
        if(pic.get(0).startsWith("data:image/")){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            stringtoBitmap(pic.get(0)).compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bytes=baos.toByteArray();
            Glide.with(holder.itemView.getContext()).load(bytes).into((ImageView) holder.getView(R.id.iv_img));
        }else {
            String ss=Constants.FIND_IMAGE_URL+pic.get(0);
            Log.e("-------pic.get(0)----",pic.get(0));
            Glide.with(holder.itemView.getContext()).load(pic.get(0)).into((ImageView) holder.getView(R.id.iv_img));
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
