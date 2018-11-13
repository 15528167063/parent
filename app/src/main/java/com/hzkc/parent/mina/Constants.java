package com.hzkc.parent.mina;

import android.os.Environment;

import java.io.File;

public class Constants {



    public static boolean IsUseMyDecoder =true;
    public static boolean IsUseMinaSocket =true;
    public static boolean isUseMob=false;

    public static final String IMPsw="hj2017";
    //    本地服务器
//    public static final  int PORT=22039;
//    public static final String IP="192.168.2.113";
       //泸州服务器
//    public static final  int PORT=23304  ;
//    public static final String IP="118.122.48.24";
    //    阿里服务器
    //    public static final  int PORT=22039;
    //    public static final String IP="119.23.38.125";

    //PHP服务器
    public static final String FIND_URL="http://47.92.214.106:8100/";

    //PHP服务器(新版)
//    public static final String PHP_URL="http://119.23.38.125:8085/";
    //泸州PHP服务器
//    public static final String PHP_URL="http://119.23.38.125:8086/";
    //资讯H5列表
//    public static final String PHP_INFOR="http://119.23.38.125:8086/h5/";

    public static final  int PORT=8003;
    public static final String IP="47.92.214.106";
    public static final String PHP_URL="http://47.92.214.106:8100/";
    public static final String PHP_INFOR="http://47.92.214.106:8100/h5/";


    //  public static final String IP="192.168.0.113";
    public static final String FIND_URL_INIT=FIND_URL+"pyq/index.php?id=";
    public static final String FIND_URL_IMG=FIND_URL+"pyq/uploadimg/";
    public static final String FIND_URL_TX=FIND_URL+"pyq/uploadimg/tx/";
    public static final String FIND_URL_DZ=FIND_URL+"pyq/dz.php?";
    public static final String FIND_URL_COMMENTLIST=FIND_URL+"pyq/readpl.php?";
    public static final String FIND_URL_SEND_COMMENT=FIND_URL+"pyq/pl.php?";
    public static final String FIND_URL_SEND_PYQ=FIND_URL+"pyq/w.php?";
    public static final String FIND_URL_COMMENT_TZ=FIND_URL+"pyq/tz.php?";
    public static final String FIND_URL_SEND_IMG=FIND_URL+"pyq/up.php?";
    public static final String FIND_URL_SEND_NEW_MSG=FIND_URL+"pyq/smg.php?";
    public static final String APKID="03091866737";
    public static final String FIND_URL_API=FIND_URL+"V3.2.8/";
    public static final String FIND_IMAGE_URL=Constants.PHP_URL+"ycz_app/Public/images/title/";
    public static final String VIPMODL_IMAGE_URL=Constants.FIND_URL+"images/vipmdl/";
    public static final String FIND_URL_API_UP=FIND_URL_API+"up.php?jh=j";
    public static final String FIND_URL_API_CHILD_UP=FIND_URL_API+"up.php?jh=h";
    public static final String FIND_URL_API_DOWNLOAD=FIND_URL_API+"download.php?jh=j&apkid="+APKID;//http://www.ycz365.com/api/download.php?jh=j&apkid=03091866737
    public static final String FIND_URL_API_FIRST=FIND_URL_API+"init.php?jh=j&ai=android&apkid="+APKID;
    public static final String FIND_URL_API_OPEN=FIND_URL_API+"qd.php?jh=j&apkid="+APKID;
    public static final String FIND_URL_API_ADDUSER=FIND_URL_API+"Tadduser.php?";
    public static final String FIND_URL_API_CHANGE_NC=FIND_URL_API+"nc.php?";
    public static final String FIND_URL_API_GET_NC=FIND_URL_API+"getnc.php?";
    public static final String FIND_URL_API_REG=FIND_URL_API+"reg.php?apkid="+APKID;
    public static final String FIND_URL_API_CHILD_DOWNLOAD=FIND_URL_API+"download?down_src=child&apkid="+APKID;
    public static final String FIND_URL_API_INFOR_TITLE=FIND_URL+"pyq/inforlabel.php";
    public static final String REG_SEND_YZCODE="http://www.lzxxt.cn:8089/web_api/MobileSystem/GetRegCodeNew?";
    public static final String CHECK_USER_ISXXT="http://www.lzxxt.cn:8089/web_api/MobileSystem/ValidateUser?";
    public static final String type = "Android-ParentInfo";
    public static final String sessionId = "lzxxt2016";
    public static final String WEIXIN_APPID = "wx92c80606ca83e5e2";
    public static final String PARTNER = "";
    public static final String SELLER = "";
    public static final String RSA_PRIVATE = "";
    //vip列表id
    public static final  String VIP_ONE_CONTROLL="4";
    public static final  String VIP_EYE_PROTECT="10";
    public static final  String VIP_CONTROLL_PLAN="14";
    public static final  String VIP_SPORY_LINE="11";
    public static final  String VIP_APP_MANAGE="32";
    public static final  String VIP_APP_QQHM="44";
    public static final  String VIP_APP_ZHIZHONG="17";
    public static final  String VIP_WEB_CHOOSE="9";
    public static final  String APP_IMAGE_URL="http://119.23.38.125/icon/";
    //18628272829
     /*缓存目录*/
    public static final String SD_CACHE = Environment.getExternalStorageDirectory() + File.separator + "parent" + File.separator + "teacher";
    public static final String SD_IMAGE_CACHE = SD_CACHE + File.separator + "img";
    static {
        File imageFile = new File(SD_IMAGE_CACHE);
        if (!imageFile.exists()) {
            imageFile.mkdirs();
        }
    }
}
