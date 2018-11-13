package com.hzkc.parent.Bean;

/**
 * Created by lenovo-s on 2017/5/11.
 */

public class YzCodeMsgInfo1 {

    /**
     * code : 1
     * msg : 无数据
     * result : null
     */

    private String code;
    private String msg;
    private Student result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Student getResult() {
        return result;
    }

    public void setResult(Student result) {
        this.result = result;
    }
    public  class Student{
        public String  mobtel;
        public String  schoolId;
        public String  Name;
        public String  type;
        public String  gen;

    }
}
