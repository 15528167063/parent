package com.hzkc.parent.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.hzkc.parent.appcation.MyApplication;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/12/9.
 */

public class AdddataUtils {
    /**
     * 加密
     * //crc= 62359;
     //Base64编码json数据   eyJhIjoiMCIsImIiOiIiLCJjIjoiIiwiZCI6IjIiLCJlIjoiIiwiZiI6IjMiLCJnIjoiMCJ9
     //模拟toke=1605474C863C8BC7E4D57866087708D1
     //json与toke混合e16yJ05hI47jo4CiM86CI3CsI8BmIC7iOE4iID5iL78CJ66jI08jo77iI08iwD1iZCI6IjIiLCJlIjoiIiwiZiI6IjMiLCJnIjoiMCJ9
     */
    public static String head="$$$$";
    public static String footer="####";
    public static String getmidata( String s) {
        String enToStr = Base64.encodeToString(s.getBytes(), Base64.DEFAULT); //base64加密
        enToStr = enToStr.replaceAll("[\\s*\t\n\r]", "");            //base64加密去掉其中的换行符号
        String token=MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).getString("tokens","");   //取出本地保存的token
        int index = CRC16.calcCrc16(AdddataUtils.getAdddata(enToStr,token).trim().getBytes());   //crc效验码
        String format = String.format("%05d", index);   //不足5位补齐5位  前面至0
        String  adddata= AdddataUtils.getAdddata(enToStr,token).trim()+format;    //混合
        MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putString("adddata",adddata).commit();
        String str1 = null;
        try {
            str1 = String.format("%08d", adddata.getBytes("utf-8").length); //加长度
            if(TextUtils.isEmpty(token)){
                str1="1"+str1.substring(1,str1.length());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String str  = head+str1+adddata +footer;
//        LogUtil.e("--Adddata加密--",str);
        return  str;
    }

    /**
     * 混合json和toke组合报文
     */
    public static String getAdddata(String s1, String s2) {
        if(TextUtils.isEmpty(s2)){
            return s1;
        }
        if (s1.length() >= s2.length()) {
            if (s2.length() % 2 == 0) {
                StringBuffer ss = new StringBuffer();
                for (int i = 0; i < s2.length() - 1; i = i + 2) {

                    String a = s2.substring(i, i + 2);
                    String b = "";
                    if (i == 0) {
                        b = s1.substring(i, i + 1);
                    } else {
                        b = s1.substring(i - 1, i + 1);
                    }
                    ss = ss.append(b + a);
                }

                ss = ss.append(s1.substring(s2.length() - 1, s1.length()));
                return ss.toString();
            } else {
                StringBuffer ss = new StringBuffer();
                for (int i = 0; i < s2.length(); i = i + 2) {
                    String a = "";
                    if (i + 2 > s2.length()) {
                        a = s2.substring(i, s2.length());
                    } else {
                        a = s2.substring(i, i + 2);
                    }
                    String b = "";
                    if (i == 0) {
                        b = s1.substring(i, i + 1);
                    } else {
                        b = s1.substring(i - 1, i + 1);
                    }
                    ss = ss.append(b + a);
                }
                ss = ss.append(s1.substring(s2.length(), s1.length()));
                return ss.toString();
            }
        }
        return null;
    }

    /**
     * 组合报文获取json
     */
    public static String getJsondata( String s1,String token) {
        //getJsondata("a11aa11aaa","1111")
        StringBuffer ss = new StringBuffer();
        StringBuffer tokens = new StringBuffer();
        if(token.length()%2==0){
            for (int i = 0; i <=token.length()/2 ; i++) {
                if(i==0){
                    ss.append(s1.substring(0, 1));
                    tokens.append(s1.substring(1, 3));
                }else {
                    String substring = s1.substring(4 * i - 1, 4 * i + 1);
                    String substring1 = s1.substring(4 * i +1 , 4 * i +3);
                    ss.append(substring);
                    tokens.append(substring1);
                }
            }

            LogUtil.e("------tokens---",tokens.toString().substring(0,32));
            if( MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).getBoolean("islogin",false)){
                LogUtil.e("------tokens--islogin-",tokens.toString().substring(0,32));
                MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putString("tokens",tokens.toString().substring(0,32)).commit();
                MyApplication.getContext().getSharedPreferences("info", MyApplication.getContext().MODE_PRIVATE).edit().putBoolean("islogin",false).commit();
            }
            ss.append(s1.substring(token.length()*2+1,s1.length()));
        }else {
            for (int i = 0; i <=token.length()/2+1 ; i++) {
                if(i==0){
                    ss.append(s1.substring(0, 1));
                }else {
                    String substring ="";
                    if(i ==token.length()/2+1){
                        substring = s1.substring(4 * i - 1, 4 * i );
                    }else {

                        substring = s1.substring(4 * i - 1, 4 * i + 1);
                    }
                    ss.append(substring);
                }
            }
            ss.append(s1.substring(token.length()*2+1,s1.length()));
        }
        return ss.toString();
    }

    /**
     * 解码
     */
    //A66A00000110e16yJ05hI47jo4CiM86CI3CsI8BmIC7iOE4iID5iL78CJ66jI08jo77iI08iwD1iZCI6IjIiLCJlIjoiIiwiZiI6IjMiLCJnIjoiMCJ9623596AA6
    //e13yJcfhI1fjo45iMabiI53sI47mIb2iO91iIb9iL32CJ0bjI38jo15iIadiw75iZCI6IjIiLCJlIjoiIiwiZiI6IjMiLCJnIjoiMTUxMzMwNjI0MjAwMCJ937799
    //e21yJ74hI1ejod5iM4diI7dsI44mI7diO8eiIfbiL2eCJf1jI35jobciI55iwd7iZCI6IjIiLCJlIjoiIiwiZiI6IjMiLCJnIjoiMTUxMzMwNjU3OTAwMCJ925142
    public static String getdata( String s) {
        Log.e("----",s);
//        if(s.startsWith("A66A") && s.endsWith("6AA6")){
        String substring="";
        if(s.endsWith("####")){
             substring = s.substring(0, s.length()-9);
        }else {
             substring = s.substring(0, s.length()-5);
        }

        String jsondata = getJsondata(substring, "074a47ff418340be88240f0da78d01a8");
        String decode = new String(Base64.decode(jsondata, Base64.DEFAULT));
        return decode;

    }
}