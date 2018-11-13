package com.hzkc.parent.Bean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/5.
 */

public class VipRoot {
    private String viplvl;

    private String vipendtime;

    private List<VipList> list ;
    private List<Mdllist> mdllist ;

    public List<Mdllist> getMdllist() {
        return mdllist;
    }
    public void setMdllist(List<Mdllist> mdllist) {
        this.mdllist = mdllist;
    }

    public void setViplvl(String viplvl){
        this.viplvl = viplvl;
    }
    public String getViplvl(){
        return this.viplvl;
    }
    public void setVipendtime(String vipendtime){
        this.vipendtime = vipendtime;
    }
    public String getVipendtime(){
        return this.vipendtime;
    }
    public void setList(List<VipList> list){
        this.list = list;
    }
    public List<VipList> getList(){
        return this.list;
    }
}
