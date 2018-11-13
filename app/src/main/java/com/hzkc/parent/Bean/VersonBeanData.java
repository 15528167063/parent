package com.hzkc.parent.Bean;

/**
 * Created by Administrator on 2018/8/14.
 */

public class VersonBeanData {
    private int version_id;

    private int cper_id;

    private String version_code;

    private String apkname;

    private String version_info;

    private String create_time;

    private int is_force;

    private String apk_size;

    private String port_src;

    public void setVersion_id(int version_id){
        this.version_id = version_id;
    }
    public int getVersion_id(){
        return this.version_id;
    }
    public void setCper_id(int cper_id){
        this.cper_id = cper_id;
    }
    public int getCper_id(){
        return this.cper_id;
    }
    public void setVersion_code(String version_code){
        this.version_code = version_code;
    }
    public String getVersion_code(){
        return this.version_code;
    }
    public void setApkname(String apkname){
        this.apkname = apkname;
    }
    public String getApkname(){
        return this.apkname;
    }
    public void setVersion_info(String version_info){
        this.version_info = version_info;
    }
    public String getVersion_info(){
        return this.version_info;
    }
    public void setCreate_time(String create_time){
        this.create_time = create_time;
    }
    public String getCreate_time(){
        return this.create_time;
    }
    public void setIs_force(int is_force){
        this.is_force = is_force;
    }
    public int getIs_force(){
        return this.is_force;
    }
    public void setApk_size(String apk_size){
        this.apk_size = apk_size;
    }
    public String getApk_size(){
        return this.apk_size;
    }
    public void setPort_src(String port_src){
        this.port_src = port_src;
    }
    public String getPort_src(){
        return this.port_src;
    }
}
