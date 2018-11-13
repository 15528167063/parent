package com.hzkc.parent.Bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wangyong on 2017/3/29.
 */

public class WechatPayBean implements Serializable {

    /**
     * charge : {"package":"Sign=WXPay","appid":"wx36708b4c3dd7510f","sign":"C8C5D9C899A4A3E05A282A28637FC966","partnerid":"1451107702","prepayid":"wx201703291536210fa646b2b10153475748","noncestr":"7406tkXhDRfc4HHKYj5Ed1aAgDeZ7bAW","timestamp":"1490772981"}
     * trade_no : 846989389501300736
     *  {"appid":"wx426b3015555a46be","partnerid":"1900009851","prepayid":"wx20171211095119901db6e76a0446917426","package":"Sign=WXPay","noncestr":"FVrmd6idpx64Vucw","timestamp":1512957079,"sign":"F21178142413751F06B0DE8DCDEDC189"}
     */

    private ChargeBean charge;
    private String trade_no;

    public ChargeBean getCharge() {
        return charge;
    }

    public void setCharge(ChargeBean charge) {
        this.charge = charge;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public static class ChargeBean implements Serializable{
        /**
         * package : Sign=WXPay
         * appid : wx36708b4c3dd7510f
         * sign : C8C5D9C899A4A3E05A282A28637FC966
         * partnerid : 1451107702
         * prepayid : wx201703291536210fa646b2b10153475748
         * noncestr : 7406tkXhDRfc4HHKYj5Ed1aAgDeZ7bAW
         * timestamp : 1490772981
         */

        @SerializedName("package")
        private String packageX;
        private String appid;
        private String sign;
        private String partnerid;
        private String prepayid;
        private String noncestr;
        private String timestamp;

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
