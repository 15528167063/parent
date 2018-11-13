package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/20.
 */

public class VipDataBean {
    private VipInforBean vipinfo ;

    private List<VipPriceBean> price ;

    private List<VipMdllistBean> mdllist ;

    public void setVipinfo(VipInforBean vipinfo){
        this.vipinfo = vipinfo;
    }
    public VipInforBean getVipinfo(){
        return this.vipinfo;
    }
    public void setPrice(List<VipPriceBean> price){
        this.price = price;
    }
    public List<VipPriceBean> getPrice(){
        return this.price;
    }
    public void setMdllist(List<VipMdllistBean> mdllist){
        this.mdllist = mdllist;
    }
    public List<VipMdllistBean> getMdllist(){
        return this.mdllist;
    }
}
