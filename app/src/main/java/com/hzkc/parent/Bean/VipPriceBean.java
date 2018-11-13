package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/8/20.
 */

public class VipPriceBean {
    private String goods_id;

    private String goods_name;

    private String vip_time;

    public String getVip_time() {
        return vip_time;
    }

    public void setVip_time(String vip_time) {
        this.vip_time = vip_time;
    }

    private String original_price;
    private String price;
    private boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setGoods_id(String goods_id){
        this.goods_id = goods_id;
    }
    public String getGoods_id(){
        return this.goods_id;
    }
    public void setGoods_name(String goods_name){
        this.goods_name = goods_name;
    }
    public String getGoods_name(){
        return this.goods_name;
    }
    public void setOriginal_price(String original_price){
        this.original_price = original_price;
    }
    public String getOriginal_price(){
        return this.original_price;
    }
    public void setPrice(String price){
        this.price = price;
    }
    public String getPrice(){
        return this.price;
    }

    public VipPriceBean(String goods_id, String goods_name, String original_price, String price, boolean state) {
        this.goods_id = goods_id;
        this.goods_name = goods_name;
        this.original_price = original_price;
        this.price = price;
        this.state = state;
    }
}
