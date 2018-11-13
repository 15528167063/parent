package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/8/20.
 */
public class VipHistoryData {
    private int pay_id;

    private String pay_src;

    private String trade_no;

    private String cash_fee;

    private String goods_name;

    private String pay_time;

    public void setPay_id(int pay_id){
        this.pay_id = pay_id;
    }
    public int getPay_id(){
        return this.pay_id;
    }
    public void setPay_src(String pay_src){
        this.pay_src = pay_src;
    }
    public String getPay_src(){
        return this.pay_src;
    }
    public void setTrade_no(String trade_no){
        this.trade_no = trade_no;
    }
    public String getTrade_no(){
        return this.trade_no;
    }
    public void setCash_fee(String cash_fee){
        this.cash_fee = cash_fee;
    }
    public String getCash_fee(){
        return this.cash_fee;
    }
    public void setGoods_name(String goods_name){
        this.goods_name = goods_name;
    }
    public String getGoods_name(){
        return this.goods_name;
    }
    public void setPay_time(String pay_time){
        this.pay_time = pay_time;
    }
    public String getPay_time(){
        return this.pay_time;
    }

}
