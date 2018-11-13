package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2017/12/13.
 */

public class AlipayResult {
    public AlipayRoot alipay_trade_app_pay_response;

    public String sign;

    public String sign_type;
    public class AlipayRoot{
        public String code;

        public String msg;

        public String app_id;

        public String auth_app_id;

        public String charset;

        public String timestamp;

        public String total_amount;

        public String trade_no;

        public String seller_id;

        public String out_trade_no;

    }
}
