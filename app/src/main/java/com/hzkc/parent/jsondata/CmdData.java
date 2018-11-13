package com.hzkc.parent.jsondata;

import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.service.MianService;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.SocketThread;


// 服务器向PC端发送指令时，告知PC请求是否成功，IMEI，Order是指相应的指令，Data是向PC端传输的数据
// 注意，Data是json格式的，不同的Order对应了不同的Data，因此需要定义不同的Data对象，但都继承BaseData
// 这样，PC端在收到回传的json数据后，先转换ServerJson，然后根据Order去转换Data
// 如果Order是 GPS，我就把Data转成GpsData对象
public class CmdData {

    private String a;// 命令来源 父亲端还是孩子端

    private String b;// 学生端UUID

    private String c;// 家长端UUID

    private String d;// 命令

    private String e;// 命令带的数据

    private String f;//命令开关

    private String g;// 命令发送时间
    private String v;// banb版本号

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public CmdData() {
        this.a = CmdCommon.SRC_PARENT;
        this.b = "";
        this.c = "";
        this.d = "";
        this.e = "";
        this.f = "";

        if(AppUtils.isApkDebugable()){
            this.v ="1.0.0;"+MyApplication.getChannelName(MyApplication.getContext());  //调试模式固定版本号
        }else {
            this.v =AppUtils.getVerName()+";"+MyApplication.getChannelName(MyApplication.getContext());  //获取当前版本号
        }
        if(Constants.IsUseMinaSocket){
            if(MianService.connectTime==0){
                this.g = 0 + "";
            }else{
                this.g = (MianService.connectTime+ System.currentTimeMillis())+"";
            }
        }else{
            this.g = SocketThread.t1+ "";
        }
    }


    public String getIsOpen() {
        return this.f;
    }

    public void setIsOpen(String ss) {
        this.f = ss;
    }

    public String getChildUUID() {
        return b;
    }

    public void setChildUUID(String ss) {
        b = ss;
    }

    public void setParentUUID(String ss) {
        c = ss;
    }

    public String getParentUUID() {
        return c;
    }

    public String getSRC() {
        return a;
    }

    public void setSRC(String sRC) {
        a = sRC;
    }

    public String getOrder() {
        return d;
    }

    public void setOrder(String order) {
        d = order;
    }

    public String getReturnData() {
        return e;
    }

    public void setReturnData(String returnData) {
        e = returnData;
    }

    public String getTime() {
        return g;
    }

    public void setTime(String time) {
        g = time;
    }


}
