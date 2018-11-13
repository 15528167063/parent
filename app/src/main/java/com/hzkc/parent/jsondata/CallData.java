package com.hzkc.parent.jsondata;

/**
 * Created by lenovo on 2016/11/9.
 */

public class CallData {
    /*
    消息类型，文字还是音频
     */
    public  String a;
    /*
    是否呼喊
     */
    public String b;
    /*
    文本消息
     */
    public String c;
    /*
    语音文件
     */
    public String d;
    public CallData()
    {
        a="";
        b="";
        c="";
        d="";
    }
    public String getMsgType()
    {
        return a;
    }
    public void setMsgType(String s)
    {
        a=s;
    }
    public  String getIsCall()
    {
        return b;
    }
    public void setIsCall(String s)
    {
        b=s;
    }
    public  String getMsgContent()
    {
        return c;
    }
    public void setMsgContent(String s)
    {
        c=s;
    }
    public String getMsgVoice()
    {
        return d;
    }
    public void  setMsgVoice(String s)
    {
        d=s;
    }

}
