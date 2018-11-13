package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/11/9.
 * 支付信息
 */

public class PayData {

    /**
     * 支付订单号
     */
    public String a;
    /*
    充值时间
     */
    public String b;
    /*
    充值金额
     */
    public String c;

    public PayData()
    {
        a="";
        b="";
        c="";
    }
    public String  getOrderNo()
    {
        return a;
    }
    public void setOrderNo(String s)
    {
        a=s;
    }

    public String getPayStartTime()
    {
        return b;
    }
    public void setPayStartTime(String s)
    {
        b=s;
    }
    public String getPayMoney()
    {
        return c;
    }
    public void setPayMoney(String s)
    {
        c=s;
    }


}
