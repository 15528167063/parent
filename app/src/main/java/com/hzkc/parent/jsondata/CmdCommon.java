package com.hzkc.parent.jsondata;

public class CmdCommon {


    public static final String HEARTBEAT_ID = "00000000";


    //相机二维码字符串前缀标识
    public static final String QR_ID = "parent";

    //来源父母端
    public static final String SRC_PARENT = "0";
    //来源孩子端
    public static final String SRC_KID = "1";
    //来源服务器端转发
    public static final String SRC_SERVER = "2";

    //命令功能开启状态未知
    public static final String CMD_FLAG_UNKNOW = "-1";
    //命令功能状态为打开
    public static final String CMD_FLAG_OPEN = "1";

    public static final String CMD_FLAG_TRAIL = "4";
    //命令功能状态为关闭
    public static final String CMD_FLAG_CLOSE = "2";

    //应用为白名单
    public static final String CMD_FLAG_WHITE = "1";
    //应用为黑名单
    public static final String CMD_FLAG_BLACK = "2";
    //应用为特权名单
    public static final String CMD_FLAG_Te = "3";
    //电话为白名单
    public static final String CMD_PHONE_WHITE = "0";
    //电话为黑名单
    public static final String CMD_PHONE_BLACK = "1";
    //init请求孩子全部数据
    public static final String CMD_INIT_ALL = "1";
    //init只是请求连接
    public static final String CMD_INIT_CONNECT = "2";
    //获取时间
    public static final String CMD_INIT_GETTIME = "3";

    //数据分片
    public static final int DATA_SPLITE_BYTES=1*512;
    //数据拼接开始
    public static final String CMD_DATA_START = "2";
    //数据拼接中间部分
    public static final String CMD_DATA_CONNECT = "3";
    //数据拼接开始
    public static final String CMD_DATA_END = "4";
    //数据完整
    public static final String CMD_DATA_ALL = "5";

    //成功标志
    public static final String CMD_FLAG_SUCCESS = "1";
    //失败标识
    public static final String CMD_FLAG_FAILURE = "2";
    //无效的命令
    public static final String CMD_INVALID = "-1";
    //表示男孩
    public static final String FLAG_BOY = "1";
    //表示女孩
    public static final String FLAG_GIRL = "0";
    //心跳包命令
    public static final String CMD_HEARTBEAT = "0";

    //注册命令  家长端第一次注册发送命令
    //学生端如果没有分配到唯一ID，第一次连接发送注册命令
    public static final String CMD_REGISTER = "1";

    // 初始化命令
    //家长端注册成功之后，以后第一次连接都发送该命令
    //学生端如果已经分配到唯一ID之后以后第一次连接都发送该命令
    public static final String CMD_INIT = "2";

    // 定位命令
    public static final String CMD_LOCATION = "3";

    // 一键管控
    public static final String CMD_CONTROL_ALL = "4";

    // 锁屏
    public static final String CMD_LOCK_SCREEN = "5";

    //刷新通知
    public static final String CMD_REFRESH = "6";

    //上线通知
    public static final String CMD_ONLINE = "7";

    //下线通知
    public static final String CMD_OFFLINE = "8";

    //上网管控计划
    public static final String CMD_CONTROL_NET = "9";

    //视力保护
    public static final String CMD_PROTECT_EYES = "10";

    //运动轨迹
    public static final String CMD_LOCATION_PATH = "11";

    //应用管控
    public static final String CMD_CONTROL_APP = "12";

    //呼喊
    public static final String CMD_CALL_YOU = "13";

    //计划提醒
    public static final String CMD_NOTICE_PLAN = "14";

    //睡觉时间段管控
    public static final String CMD_CONTROL_SLEEP = "15";

    //电量提醒
    public static final String CMD_NOTICE_POWER_LEVEL = "16";

    //app使用跟踪
    public static final String CMD_TRACE_APP = "17";

    //命令状态反馈命令
    public static final String CMD_CMD_RESULT = "18";

    //支付命令
    public static final String CMD_PAY = "19";

    //忘记密码
    public static final String CMD_FORGET_PWD = "20";

    //修改重置密码
    public static final String CMD_RESET_PWD = "21";

    //取消绑定(删除孩子)
    public static final String CMD_UNBINDING = "22";

    //登录命令
    public static final String CMD_LOGIN = "23";

    //新增孩子
    //如果服务器收到孩子端注册信息,服务器向家长端发送新增孩子命令
    public static final String CMD_ADD_CHILD = "24";

    //孩子端申请上网
    public static final String CMD_REQUEST_INTERNET = "25";

    //上传孩子端手机安装app列表
    public static final String CMD_GET_INSTALLED_APP = "26";

    //修改孩子基本信息命令
    public static final String CMD_CHANGE_CHILD_MSG = "27";     

    //请求所有孩子的上线下线状态
    public static final String CMD_CHILD_ALL_LINE = "28";

    //孩子升级
    public static final String CMD_CHILD_UP = "29";

    //请求applist
    public static final String CMD_GET_CHILD_APP_LIST = "30";

    //请求applist
    public static final String CMD_GET_WHITE_APP_LIST = "32";
    //请求团控
    public static final String CMD_GET_TUAN_KONG = "31";
    //口令绑定
    public static final String CMD_BAND_CHILD = "33";
    //修改昵称
    public static final String CMD_UP_NC = "39";
    //修改昵称
    public static final String CMD_RE_LOGIN = "40";
    //是不是允许安装app
    public static final String CMD_INSTALL_APP = "41";
    //卸载安装app
    public static final String CMD_XIEZAI_APP = "42";
    //发送离线消息
    public static final String CMD_OFF_LINE= "43";
    //发送手机号黑白名单
    public static final String CMD_WHITE_PHONE= "44";
    //发送团控消息
    public static final String CMD_TEAM_CONTROLL= "45";
    //请求禁用名单
    public static final String CMD_STOP_APP_LIST = "46";

    //{"a":"1","b":"442","c":"","d":"33","e":"{\"a\":\"15882142346\",\"b\":\"123456\",\"c\":\"Waen4tastFADAF1ya/73kXFF\",\"d\":\"又是我\",\"e\":\"1\",\"f\":\"\"}","f":"1","g":"1506408107890"}
}
