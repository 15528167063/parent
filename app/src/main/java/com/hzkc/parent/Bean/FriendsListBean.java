package com.hzkc.parent.Bean;

/**
 * Created by lenovo-s on 2017/2/22.
 */

public class FriendsListBean {

    /**
     * id : 当前说说ID号
     * text : 说说内容
     * nc : 发布说说人昵称
     * ncid : 发布人ID
     * ncpic : 发布人头像编号
     * time : 发布时间
     * dzbz : 是否有点赞标志（1为有，0为没有，以下标志相同）
     * plbz : 是否有评论标志
     * picbz : 说说是否有图片标志
     * picdz : 说说图片地址
     * dzlist : 点赞人昵称列表
     * plnr : 回复json列表（采用base64编码，编码解开后为json数组，需要再次解析）
     *          解析后json参数说明：hfnc：回复人昵称。hfid：回复人ID。text：回复内容。
     *          （json里面得数据再次采用了base64编码，需要解码）
     * pltj : 评论数量
     * dztj : 点赞数量
     */

    private String id;
    private String text;
    private String nc;
    private String ncid;
    private String ncpic;
    private String time;
    private String dzbz;
    private String plbz;
    private String picbz;
    private String picdz;
    private String dzlist;
    private String plnr;
    private String pltj;
    private String dztj;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNc() {
        return nc;
    }

    public void setNc(String nc) {
        this.nc = nc;
    }

    public String getNcid() {
        return ncid;
    }

    public void setNcid(String ncid) {
        this.ncid = ncid;
    }

    public String getNcpic() {
        return ncpic;
    }

    public void setNcpic(String ncpic) {
        this.ncpic = ncpic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDzbz() {
        return dzbz;
    }

    public void setDzbz(String dzbz) {
        this.dzbz = dzbz;
    }

    public String getPlbz() {
        return plbz;
    }

    public void setPlbz(String plbz) {
        this.plbz = plbz;
    }

    public String getPicbz() {
        return picbz;
    }

    public void setPicbz(String picbz) {
        this.picbz = picbz;
    }

    public String getPicdz() {
        return picdz;
    }

    public void setPicdz(String picdz) {
        this.picdz = picdz;
    }

    public String getDzlist() {
        return dzlist;
    }

    public void setDzlist(String dzlist) {
        this.dzlist = dzlist;
    }

    public String getPlnr() {
        return plnr;
    }

    public void setPlnr(String plnr) {
        this.plnr = plnr;
    }

    public String getPltj() {
        return pltj;
    }

    public void setPltj(String pltj) {
        this.pltj = pltj;
    }

    public String getDztj() {
        return dztj;
    }

    public void setDztj(String dztj) {
        this.dztj = dztj;
    }

    public FriendsListBean() {
        this.id = "123";
        this.text = "说说内容";
        this.nc = "昵称";
        this.ncid = "发布人ID";
        this.ncpic = "发布人头像编号";
        this.time = "发布时间";
        this.dzbz = "是否有点赞标志（1为有，0为没有，以下标志相同）";
        this.plbz = "是否有点赞标志（1为有，0为没有，以下标志相同）";
        this.picbz = "是否有评论标志";
        this.picdz = "说说是否有图片标志";
        this.dzlist = "点赞人昵称列表";
        this.plnr = "回复json列表";
        this.pltj = "评论数量";
        this.dztj = "点赞数量";
    }
}
