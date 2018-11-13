package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2017/7/24.
 */

public class VipBean {
    public String price;
    public String leixin;
    public boolean isxufei;
    public String youhui;

    public VipBean(String leixin,String price, boolean isxufei, String youhui) {
        this.price = price;
        this.isxufei = isxufei;
        this.youhui = youhui;
        this.leixin=leixin;
    }
}
