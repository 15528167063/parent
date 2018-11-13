package com.hzkc.parent.Bean;

/**
 * 从服务器获取的好友数据
 * Created by Administrator on 2017/3/16.
 */

public class NewFriend {
    /**
     * id
     */
    private String userid;
    /**
     * 昵称
     */
    private String name;
    /**
     * 头像，需要拼接
     */
    private String txid;

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
