package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/8/20.
 */
public class VipHistoryNewData {
    private String pay_status;

    private String pay_src;

    private String trade_no;

    private String price;

    private String goods_name;

    private String pay_time;

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getPay_src() {
        return pay_src;
    }

    public void setPay_src(String pay_src) {
        this.pay_src = pay_src;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    public VipHistoryNewData(String pay_status, String pay_src, String trade_no, String price, String goods_name, String pay_time) {
        this.pay_status = pay_status;
        this.pay_src = pay_src;
        this.trade_no = trade_no;
        this.price = price;
        this.goods_name = goods_name;
        this.pay_time = pay_time;
    }
}
