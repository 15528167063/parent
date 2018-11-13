package com.hzkc.parent.service;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.Bean.ChildInfo;
import com.hzkc.parent.Bean.NcInfo;
import com.hzkc.parent.Bean.NetControlBean;
import com.hzkc.parent.Bean.QinqPhoneBean;
import com.hzkc.parent.Bean.Viplistbean;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.LoginActivity;
import com.hzkc.parent.activity.SplashActivity;
import com.hzkc.parent.activity.UpgradeActivity;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.AppUseDataEvent;
import com.hzkc.parent.event.AppUseTimeDataEvent;
import com.hzkc.parent.event.ChangeChildDataEvent;
import com.hzkc.parent.event.ChangeNCEvent;
import com.hzkc.parent.event.ChangePswDataEvent;
import com.hzkc.parent.event.ChildLineFlagEvent;
import com.hzkc.parent.event.ChildLocationDataEvent;
import com.hzkc.parent.event.ChildLocationPathDataEvent;
import com.hzkc.parent.event.ChildMessageDataEvent;
import com.hzkc.parent.event.ChildOnlineAndOfflineEvent;
import com.hzkc.parent.event.ChildUpEvent;
import com.hzkc.parent.event.CloseYjspDataEvent;
import com.hzkc.parent.event.DeleteChildEvent;
import com.hzkc.parent.event.InitEvent;
import com.hzkc.parent.event.InitRefreshEvent;
import com.hzkc.parent.event.InstallAppListEvent;
import com.hzkc.parent.event.LoginDataEvent;
import com.hzkc.parent.event.NetControlBackEvent;
import com.hzkc.parent.event.OrderChildDataEvent;
import com.hzkc.parent.event.OrderDeleteChildEvent;
import com.hzkc.parent.event.RegiseterDataEvent;
import com.hzkc.parent.event.RequestAppUseTimeEvent;
import com.hzkc.parent.event.RequestChangePswEvent;
import com.hzkc.parent.event.RequestChildAppListEvent;
import com.hzkc.parent.event.RequestChildLoactionEvent;
import com.hzkc.parent.event.RequestInstallEvent;
import com.hzkc.parent.event.RequestLoginEvent;
import com.hzkc.parent.event.RequestNetControlEvent;
import com.hzkc.parent.event.RequestQingQinEvent;
import com.hzkc.parent.event.RequestRegisterEvent;
import com.hzkc.parent.event.RequestStopAppListEvent;
import com.hzkc.parent.event.RequestWhiteAppListEvent;
import com.hzkc.parent.event.SlbhDataEvent;
import com.hzkc.parent.event.SlbhEvent;
import com.hzkc.parent.event.StopInternetPlanEvent;
import com.hzkc.parent.event.TeamInternetPlanEvent;
import com.hzkc.parent.event.UnInstallAppListEvent;
import com.hzkc.parent.event.UpdateNcEvent;
import com.hzkc.parent.event.WWWControlBackEvent;
import com.hzkc.parent.event.WhiteAppBackEvent;
import com.hzkc.parent.event.WhiteAppListEvent;
import com.hzkc.parent.event.WhitePhoneListEvent;
import com.hzkc.parent.event.YjgkDataEvent;
import com.hzkc.parent.event.YjgkEvent;
import com.hzkc.parent.event.YjspDataEvent;
import com.hzkc.parent.event.YjspEvent;
import com.hzkc.parent.event.YjspTuanEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.entity.AppUseBean;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.entity.NetPlanDataBean;
import com.hzkc.parent.greendao.entity.PhoneData;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.AppUseBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.greendao.gen.LoveTrailDataDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.greendao.gen.PhoneDataDao;
import com.hzkc.parent.jsondata.AddStudentData;
import com.hzkc.parent.jsondata.AddStudentData1;
import com.hzkc.parent.jsondata.AppData;
import com.hzkc.parent.jsondata.AppInstallData;
import com.hzkc.parent.jsondata.AppUsageData;
import com.hzkc.parent.jsondata.ChangePswData;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.jsondata.CmdData;
import com.hzkc.parent.jsondata.ControlAllData;
import com.hzkc.parent.jsondata.EyesData;
import com.hzkc.parent.jsondata.IntallData;
import com.hzkc.parent.jsondata.LocationData;
import com.hzkc.parent.jsondata.LoginData;
import com.hzkc.parent.jsondata.NetPlanData;
import com.hzkc.parent.jsondata.PhoneDatas;
import com.hzkc.parent.jsondata.PowerLevelData;
import com.hzkc.parent.jsondata.RegisterInfos;
import com.hzkc.parent.jsondata.StudentData;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.mina.MinaThread;
import com.hzkc.parent.utils.ActivityCollector;
import com.hzkc.parent.utils.ActivityManager;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MyUtils;
import com.hzkc.parent.utils.SocketThread;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MianService extends Service {
    private SharedPreferences sp;
    private String TAG = "Service";
    private MyBinder myBinder = new MyBinder();
    public static MinaThread minaThread;
    private String parentUUID;
    private static String listData;
    private List<String> appdatas;
    private List<String> netPlandatas;
    private String appchilduuid;
    private String planchildUUID;
    private String listPathData;
    private static String OrderlistData;
    private List<String> netdatas;
    private String netChildUUID;
    private AlertDialog ad;
    private SocketThread socketThread;
    public static long connectTime=0;
    private Timer timer = new Timer(true);//任务
    private TimerTask task = new TimerTask() {
        public void run() {
            Message msg = Message.obtain();
            msg.what = 106;
            myHandler.sendMessage(msg);
        }
    };
    private long powersendTime;
    public Handler handler=new Handler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "服务执行了onStartCommand方法");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册事件
        EventBus.getDefault().register(this);
//        RegiseterBroad();
        sp = getSharedPreferences("info", MODE_PRIVATE);
        parentUUID = sp.getString("parentUUID", "");
        LogUtil.w("onCreate", "服务执行了onCreate方法");

        listData="";
        listPathData="";
        OrderlistData="";
        powersendTime=0;

        // 开始执行后台任务
        if(Constants.IsUseMinaSocket){
            minaThread = new MinaThread(this, myHandler);
            minaThread.start();
        }else{
            socketThread = new SocketThread(this, myHandler);
            socketThread.start();
        }
        //启动定时器
        timer.schedule(task, 0, 10*1000);
    }


    @Override
    public void onDestroy() {
        LogUtil.i(TAG, "服务执行了onDestroy方法");
        /**
         * 取消消息订阅
         * */
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * bindServices的时候才用
     */
    public class MyBinder extends Binder {
        public void startDownload() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行具体的下载任务
                }
            }).start();
        }
    }

    /**
     * 处理从服务器中获取的数据
     */
    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101://处理获取到的数据
                    dealData(msg);
                    break;
                case 102://发送init命令
                    sendInit();
                    break;
                case 103://处理心跳包

                    break;
                case 106://刷新孩子状态
                    refreshChildStat();
                    break;
                case 107://关闭一键锁屏
                    closeYjsp((String)msg.obj);
                    LogUtil.e(TAG, "一键锁屏功能向服务器发送的数据107定时关闭");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 关闭一键锁屏
     * */
    private void closeYjsp(String nowChildUUID) {
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(nowChildUUID)).build().unique();
        if (findChild != null) {
            findChild.setYjspfalg(CmdCommon.CMD_FLAG_CLOSE);
            childDao.update(findChild);
            EventBus.getDefault().post(new CloseYjspDataEvent(), "closeyjsp");
        }
    }

    /**
     * 刷新孩子状态
     * */
    private void refreshChildStat() {

        currenttime=System.currentTimeMillis();
        CmdData initJson = new CmdData();
        String token = sp.getString("token", "");

        if (!TextUtils.isEmpty(token)) {
            //初始化孩子的在线状态
            int time = (int)((currenttime - lasttime) / 1000);
            if(time>20 && time   <180){
                lasttime=System.currentTimeMillis();
                MianService.minaThread.disConnect();
            }

            initJson.setParentUUID(parentUUID);
            initJson.setOrder(CmdCommon.CMD_INIT);
            initJson.setIsOpen(CmdCommon.CMD_INIT_CONNECT);
            LogUtil.e(TAG, "发送init命令向服务器发送的数据305" + new Gson().toJson(initJson));

            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(initJson));
            }else{
                socketThread.sMessage(new Gson().toJson(initJson));
            }
        }
    }

    /**
     * 处理获取的数据
     */
    public long lasttime,currenttime;
    private void dealData(Message msg) {
        String cmdMsg = (String) msg.obj;

        lasttime=System.currentTimeMillis();
        CmdData cmdData =null;
        try{
            cmdData = new Gson().fromJson(cmdMsg, CmdData.class);
        }catch (JsonSyntaxException e){   //JsonSyntaxException
            Log.e("------------->","返回的String数据解析异常");
            if(MianService.minaThread!=null && cmdMsg.toString().length()>1000){
                Log.e("------------->","返回的String数据解析异常disConnect()");
                MianService.minaThread.disConnect();
            }
            return;
        }
        if (cmdData != null) {
            String order = cmdData.getOrder();
//            //离线消息
//            if(order.equals(CmdCommon.CMD_ADD_CHILD) || order.equals(CmdCommon.CMD_BAND_CHILD) || order.equals(CmdCommon.CMD_LOCATION_PATH) ||
//                    order.equals(CmdCommon.CMD_TRACE_APP) || order.equals(CmdCommon.CMD_GET_WHITE_APP_LIST )|| order.equals(CmdCommon.CMD_GET_TUAN_KONG)){   //绑定孩子 添加孩子  应用列表 运动轨迹 应用追踪保存这些指令
//                if(!cmdData.getSRC().equals("1")){
//                    MessageBeanDao messageDao = GreenDaoManager.getInstance().getSession().getMessageBeanDao();
//                    String childuuid=cmdData.getChildUUID();
//                    String messageId=cmdData.getOrder();
//                    String time=cmdData.getTime();
//                    MessageBean messageBean=new MessageBean(null,childuuid,messageId,time);
//                    List<MessageBean> list = messageDao.queryBuilder().where(MessageBeanDao.Properties.B.eq(childuuid)).where(MessageBeanDao.Properties.D.eq(messageId)).build().list();
//                    if(list!=null && list.size()>0){
//                        messageDao.delete(list.get(0));
//                    }
//                    messageDao.insert(messageBean);
//                }
//            }
            if (order.equals(CmdCommon.CMD_INIT)) { //处理初始化数据
                orderInint(cmdData);
            } else if (order.equals(CmdCommon.CMD_REGISTER)) {  //处理注册信息数据
                orderRegister(cmdData);
            } else if (order.equals(CmdCommon.CMD_LOGIN)) {     //处理登录逻辑数据
                orderLogin(cmdData);
            } else if (order.equals(CmdCommon.CMD_ADD_CHILD) || order.equals(CmdCommon.CMD_BAND_CHILD)) {    //处理添加孩子
                orderAddchildMessage(cmdData);
            } else if (order.equals(CmdCommon.CMD_NOTICE_POWER_LEVEL)) {   //处理电量提醒
                orderPowerLevel(cmdData);
            } else if (order.equals(CmdCommon.CMD_ONLINE)) {   //处理上线通知
                orderOnline(cmdData, CmdCommon.CMD_ONLINE);
            } else if (order.equals(CmdCommon.CMD_OFFLINE)) {   //处理下线通知
                orderOnline(cmdData, CmdCommon.CMD_OFFLINE);
            } else if (order.equals(CmdCommon.CMD_CONTROL_NET)) {  //上网管控计划切片数据处理
                orderNetControl();
            } else if (order.equals(CmdCommon.CMD_LOCATION)) {   //定位
                orderLocation(cmdData);
            } else if (order.equals(CmdCommon.CMD_LOCATION_PATH)) {   //轨迹
                try {
                    orderLocationPath(cmdData);
                }catch (Exception e){
                    LogUtil.e("dealData","轨迹");
                    e.printStackTrace();
                }
            } else if (order.equals(CmdCommon.CMD_CONTROL_APP)) { //应用管控黑名单APP列表切片数据处理
                orderControlApp(cmdData);
            } else if (order.equals(CmdCommon.CMD_NOTICE_PLAN)) { //孩子管控计划切片数据处理
                orderNetPlan(cmdData);
            } else if (order.equals(CmdCommon.CMD_GET_INSTALLED_APP)) { //获取应用列表
                try {
                    orderAllAppList(cmdData);
                }catch (Exception e){
                    LogUtil.e("dealData","获取应用列表");
                    e.printStackTrace();
                }
            } else if (order.equals(CmdCommon.CMD_RESET_PWD)) { //修改密码
                orderChangePsw(cmdData);
            } else if(order.equals(CmdCommon.CMD_REQUEST_INTERNET)){ //孩子端申请上网
                orderUsePhone(cmdData);
            } else if(order.equals((CmdCommon.CMD_TRACE_APP))){ //app使用跟踪
                orderUseAppTime(cmdData);
            } else if(order.equals(CmdCommon.CMD_CHILD_ALL_LINE)){//获取所有孩子的在线状态
                orderChildLine(cmdData);
            } else  if(order.equals(CmdCommon.CMD_CHANGE_CHILD_MSG)){//处理修改孩子信息
                orderChangeChild(cmdData);
            } else if(order.equals(CmdCommon.CMD_UNBINDING)){//处理删除孩子信息
                OrderDeleteChild(cmdData);
            } else if(order.equals(CmdCommon.CMD_CONTROL_ALL)){//处理一键管控
                OrderControlAll(cmdData);
            }else if(order.equals(CmdCommon.CMD_LOCK_SCREEN)){//处理一键锁屏
                OrderLock(cmdData);
            }else if(order.equals(CmdCommon.CMD_PROTECT_EYES)){//处理视力保护
                OrderSlbh(cmdData);
            } else if (order.equals(CmdCommon.CMD_GET_WHITE_APP_LIST)) { //32 获取应用列表
                try {

                    orderAllAppList1(cmdData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (order.equals(CmdCommon.CMD_GET_TUAN_KONG)) { //31 获取团控
                OrderControam(cmdData);
            } else if (order.equals(CmdCommon.CMD_UP_NC)) { //39 修改昵称
                UpdataNc(cmdData);
            } else if (order.equals(CmdCommon.CMD_RE_LOGIN)) { //40 重复登陆
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!TextUtils.isEmpty(sp.getString("parentUUID", ""))){
                            LoginOut();
                        }
                    }
                },3000);
            }else if (order.equals(CmdCommon.CMD_INSTALL_APP)) { //41 允许安装app
                InstallApp(cmdData);
            } else if (order.equals(CmdCommon.CMD_OFF_LINE)) { //43 离线指令
//                ClearMessage();
            } else if (order.equals(CmdCommon.CMD_WHITE_PHONE)) { //号码管理
                OrderPhoneList(cmdData);
            }else if (order.equals(CmdCommon.CMD_STOP_APP_LIST)) { //自定义app名单
                handleStopAppList(cmdData);
            }else if (order.equals(CmdCommon.CMD_TEAM_CONTROLL)) { //团控数据
                handleTeamAppList(cmdData);
            }
        }
    }


    /**
     * 请求注册数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestRegiseter")
    public void RequestRegisterEvent(RequestRegisterEvent requestRegisterData) {
        CmdData registerJson = new CmdData();
        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = new Date();
        String nowTime = df.format(nowDate.getTime());// 请求注册数据向服务器发送的数据：{"a":"0","b":"","c":"","d":"1","e":"{\"a\":\"18081190425\",\"b\":\"000000\",\"c\":\"\",\"d\":\"\",\"e\":\"\",\"f\":\"\"}","f":"","g":"2017-09-11"}
        LogUtil.e("TAG","nowTime:"+nowTime);
        registerJson.setOrder(CmdCommon.CMD_REGISTER);
        RegisterInfos registerInfo = new RegisterInfos();
        registerInfo.setA(requestRegisterData.phoneNum);
        registerInfo.setB(requestRegisterData.psw);
        registerInfo.setC(requestRegisterData.nichen);

        registerInfo.setD(requestRegisterData.phonetype);
        registerInfo.setE(requestRegisterData.qudao);
        registerJson.setReturnData(new Gson().toJson(registerInfo));
        registerJson.setTime(nowTime);
        Log.e(TAG, "请求注册数据向服务器发送的数据：" + new Gson().toJson(registerJson));

        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(registerJson));
        }else{
            socketThread.sMessage(new Gson().toJson(registerJson));
        }
    }

    /**
     * 处理注册信息数据
     */
    private void orderRegister(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行register指令");
        String isRegistered = cmdData.getIsOpen();
        LogUtil.e(TAG, "该电话的注册状态数据为：" + isRegistered);
        EventBus.getDefault().post(
                new RegiseterDataEvent(isRegistered,cmdData.getParentUUID()), "RegiseterData");
    }

    /**
     * 请求登录数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestLogin")
    public void RequestLoginEvent(RequestLoginEvent requestLoginData) {
        CmdData loginJson = new CmdData();
        loginJson.setOrder(CmdCommon.CMD_LOGIN);
        LoginData loginData = new LoginData();
        loginData.setTelNo(requestLoginData.phoneNum);
        loginData.setPwd(requestLoginData.psw);
        loginData.setTokenID(requestLoginData.token);
        loginData.setAdress(requestLoginData.locations);
        loginJson.setReturnData(new Gson().toJson(loginData));

        sp.edit().putBoolean("islogin",true).commit();//保存用来  登陆成功需要保存token
        LogUtil.e(TAG, "请求登录数据向服务器发送的数据：" + new Gson().toJson(loginJson));
        //minaThread.sMessage(new Gson().toJson(loginJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(loginJson));
        }else{
            socketThread.sMessage(new Gson().toJson(loginJson));
        }
    }

    /**
     * 处理登录逻辑数据
     */
    private void orderLogin(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行login指令");
        LogUtil.e(TAG, "得到的getReturnData数据为：" + cmdData.getReturnData());
        parentUUID = cmdData.getParentUUID();
        String isRegistered = cmdData.getIsOpen();
        LogUtil.e(TAG, "该电话的登录状态数据为：" + isRegistered);
        EventBus.getDefault().post(new LoginDataEvent(isRegistered, parentUUID,cmdData.getReturnData()), "LoginData");

    }

    /**
     * 发送请求初始化数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "init")
    public void RequestInitEvent(InitEvent init) {
        sp.edit().putBoolean("isinit",true).commit();
        CmdData initAllJson = new CmdData();
        initAllJson.setOrder(CmdCommon.CMD_INIT);
        initAllJson.setIsOpen(CmdCommon.CMD_INIT_ALL);
        initAllJson.setParentUUID(init.parentUUID);
        LogUtil.e(TAG, "发送请求初始化数据initAll:" + new Gson().toJson(initAllJson));
        //minaThread.sMessage(new Gson().toJson(initAllJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(initAllJson));
        }else{
            socketThread.sMessage(new Gson().toJson(initAllJson));
        }
    }

    /**
     * 发送init命令
     */
    private void sendInit() {
        CmdData initJson = new CmdData();
        String token = sp.getString("token", "");
        LogUtil.i(TAG, "sendInit: token" + token);
        if (!TextUtils.isEmpty(token)) {
            //初始化孩子的在线状态
            LogUtil.e(TAG, "向服务器发送init命令102");
            initJson.setParentUUID(parentUUID);
            initJson.setOrder(CmdCommon.CMD_INIT);
            initJson.setIsOpen(CmdCommon.CMD_INIT_CONNECT);
            LogUtil.e(TAG, "发送init命令向服务器发送的数据543" + new Gson().toJson(initJson));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(initJson));
            }else{
                socketThread.sMessage(new Gson().toJson(initJson));
            }
        } else {
            //获取系统时间
            LogUtil.i(TAG, "sendInit: 获取系统时间");
            //initJson.setParentUUID(parentUUID);
            initJson.setOrder(CmdCommon.CMD_INIT);
            initJson.setIsOpen(CmdCommon.CMD_INIT_GETTIME);
            LogUtil.e(TAG, "发送init命令向服务器发送的数据" + new Gson().toJson(initJson));
            //minaThread.sMessage(new Gson().toJson(initJson));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(initJson));
            }else{
                socketThread.sMessage(new Gson().toJson(initJson));
            }
        }
    }

    /**
     * 处理init数据
     * {"a":"2","b":"","c":"02221dfb4daf41f7aa8cd842e442cf93","d":"2","e":"[{\"a\":\"10004779\",\"b\":[{\"a\":\"2\"}],\"c\":[],\"d\":[],\"e\":[{\"a\":\"Yt\",\"b\":\"0\",\"c\":\"\",\"d\":\"\",\"e\":\"tianxin\"}],\"f\":[],\"g\":[{\"a\":\"baidu\"}],\"h\":[],\"i\":\"1\"}]","f":"1","g":"1525771787000"}
     */
    private void orderInint(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行init指令");
        LogUtil.e(TAG, "得到的getReturnData数据为111：" + cmdData.getReturnData());
        //获取系统时间
        if(cmdData.getIsOpen().equals(CmdCommon.CMD_INIT_GETTIME)){
            long sendTime = Long.parseLong(cmdData.getTime());
            LogUtil.e("orderChildLine","sendTime:"+sendTime);
            connectTime = sendTime - System.currentTimeMillis();
            LogUtil.e("orderChildLine","connectTime:"+ connectTime);
            return;
        }
        if (TextUtils.isEmpty(cmdData.getReturnData())) {
            LogUtil.i(TAG, "orderInint22: 没有孩子数据");
            sp.edit().putString("childName", "").putString("ChildUUID", "").putString("childSex", "").putBoolean("yjgk",false).commit();
            return;
        }
        if (cmdData.getReturnData().equals("[]")) {
            LogUtil.i(TAG, "orderInint11: 没有孩子数据");
            sp.edit().putString("childName", "").putString("ChildUUID", "").putString("childSex", "").putBoolean("yjgk",false).commit();
            //刷新界面数据
            ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
            ChildContrlFlagDao childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
            AppDataBeanDao appdao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
            NetPlanDataBeanDao netPlandao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
            childDao.deleteAll();
            childCopntrlDao.deleteAll();
//            appdao.deleteAll();
            netPlandao.deleteAll();
            EventBus.getDefault().post(new InitRefreshEvent(), "InitRefreshData");
            return;
        }

        List<ChildInfo> childList =null;
        try{
            Log.e("--------->",cmdData.getReturnData());//[{"a":"10004779","b":[{"a":"2"}],"c":[],"d":[],"e":[{"a":"Yt","b":"0","c":"","d":"","e":"tianxin"}],"f":[],"g":[{"a":"baidu"}],"h":[],"i":"1"}]
            childList = new Gson().fromJson(cmdData.getReturnData().trim(), new TypeToken<List<ChildInfo>>() {}.getType());
        }catch (JsonSyntaxException e){
            Log.e("--------->","JsonSyntaxException+MianService.java:546");
            return;
        }
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildContrlFlagDao childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        AppDataBeanDao appdao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        NetPlanDataBeanDao netPlandao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
        childDao.deleteAll();
        childCopntrlDao.deleteAll();
//        appdao.deleteAll();
        netPlandao.deleteAll();

        LogUtil.i(TAG, "orderInint: childList.size:" + childList.size());
        if (childList != null && childList.size() > 0){
            for (int i = 0; i < childList.size(); i++) {
                ChildInfo childDatas = childList.get(i);
                LogUtil.e(TAG, "orderInint: " + childList.get(i));
                /**获取孩子uuid*/
                String childUUID = childDatas.getChilduuid();
                LogUtil.i(TAG, "orderInint uuid: " + childUUID);

                /**获取孩子是不是允许被安装应用程序*/
                if(childDatas.getInstallable()!=null){
                    String installable = childDatas.getInstallable();//0是禁止，1允许
                    sp.edit().putString("install"+childUUID,installable).commit();
                }
                /**获取孩子是不是允许被使用状态蓝*/
                if(childDatas.getStudentDatas().get(0)!=null && childDatas.getStudentDatas().get(0).f!=null){
                    String statelable = childDatas.getStudentDatas().get(0).f;//0是禁止，1允许
                    if(TextUtils.isEmpty(statelable)){
                        sp.edit().putString("zhuangtai"+childUUID,"0").commit();
                    }else {
                        sp.edit().putString("zhuangtai"+childUUID,statelable).commit();
                    }
                }
                /**获取孩子基本信息*/
                if (childDatas.getStudentDatas().get(0) != null) {
                    LogUtil.e(TAG, "orderInint data: " + childDatas.getStudentDatas().get(0));
                    StudentData studentData = childDatas.getStudentDatas().get(0);
                    LogUtil.e(TAG, "studentData.getE(): " + studentData.getE());
                    String childSchool = studentData.getSchool();
                    String childNianji = studentData.getNianJi();
                    String childName = studentData.getChildName();
                    String childSex = studentData.getChildSex();
                    String childfrom = studentData.getE();
                    String childztl = studentData.getF();
                    String imageUrl = studentData.getG();
                    ChildsTable child = new ChildsTable(null, childUUID, childName, childSex, childNianji, childSchool,childfrom,childztl,imageUrl);
                    childDao.insert(child);
                    LogUtil.e(TAG, "init: 添加孩子成功");
                    //存储第一个孩子的信息
                    if (i == 0) {
                        sp.edit().putString("childNames", childName).putString("ChildUUIDs", childUUID).putString("childSexs", childSex).putString("childFroms", childfrom).putString("childztls", imageUrl).putString("childImages", imageUrl).commit();
                        if(TextUtils.isEmpty(sp.getString("childName",""))){
                            sp.edit().putString("childName", childName).putString("ChildUUID", childUUID).putString("childSex", childSex).putString("childFrom", childfrom).putString("childztl", imageUrl).putString("childImage", imageUrl).commit();
                        }
                        EventBus.getDefault().post(new ChildMessageDataEvent(childUUID, childName, childSex,childfrom,childztl,imageUrl), "");
                    }
                } else {
                    LogUtil.i(TAG, "orderInint: 22孩子的基本信息为空");
                }
                LogUtil.i(TAG, "orderInint app: " + childDatas.getAppList());
                /**获取孩子的计划*/
                if (childDatas.getNetPlanList() != null&&childDatas.getNetPlanList().size()>0) {
                    List<NetPlanData> netPlanList = childDatas.getNetPlanList();
                    for (int j = 0; j < netPlanList.size(); j++) {
                        NetPlanData netPlanData = netPlanList.get(j);
                        NetPlanDataBean planInfo= new NetPlanDataBean(null,childUUID,netPlanData.getPlanName(),netPlanData.getDays(), netPlanData.getPlanStartTime(),netPlanData.getPlanEndTime(),netPlanData.getIsOpen());
                        netPlandao.insert(planInfo);
                    }
                    LogUtil.i(TAG, "orderInint: 获取孩子的计划插入成功");
                    sp.edit().putString(childUUID+"first", "不是第一次").commit();
                } else{
                    LogUtil.i(TAG, "orderInint: 获取孩子的计划为空");
                    sp.edit().putString(childUUID+"first", "").commit();
                }
                /**获取孩子的控制状态*/
                if(childDatas.getConfigInfo().get(0).getYjgkflag().equals("11")){
                    sp.edit().putBoolean("team"+childUUID,true).commit();
                }else if(childDatas.getConfigInfo().get(0).getYjgkflag().equals("12")){
                    sp.edit().putBoolean("team"+childUUID,true).commit();
                }
                //判断是否有视力保护
                if(childDatas.getEyesData()!=null&&childDatas.getEyesData().size()>0){
                    //判断是否有一键管控状态
                    if(childDatas.getConfigInfo()!=null&&childDatas.getConfigInfo().get(0).getYjgkflag().equals("1")){
                        LogUtil.i(TAG, "管控状态不为空和视力保护不为空");
                        sp.edit().putBoolean("team"+childUUID,false).commit();
                        ChildContrlFlag childFlag = new ChildContrlFlag(null, childUUID, parentUUID, "", ""
                                , childDatas.getConfigInfo().get(0).getYjgkflag()
                                , CmdCommon.CMD_FLAG_CLOSE
                                , childDatas.getEyesData().get(0).getIsOpen()
                                , childDatas.getEyesData().get(0).getJianGeTime()
                                , childDatas.getEyesData().get(0).getSleepTime()
                                , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
                        childCopntrlDao.insert(childFlag);
                        sp.edit().putBoolean("yjgk"+childUUID,true).commit();

                    }else{
                        if(childDatas.getConfigInfo().get(0).getYjgkflag().equals("2")){
                            sp.edit().putBoolean("team"+childUUID,false).commit();
                        }
                        LogUtil.i(TAG, "管控状态为空和视力保护不为空");
                        ChildContrlFlag childFlag = new ChildContrlFlag(null, childUUID, parentUUID, "", ""
                                , CmdCommon.CMD_FLAG_CLOSE
                                , CmdCommon.CMD_FLAG_CLOSE
                                , childDatas.getEyesData().get(0).getIsOpen()
                                , childDatas.getEyesData().get(0).getJianGeTime()
                                , childDatas.getEyesData().get(0).getSleepTime()
                                , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
                        childCopntrlDao.insert(childFlag);
                        sp.edit().putBoolean("yjgk"+childUUID,false).commit();
                    }
                }else{
                    //判断是否有一键管控状态
                    if(childDatas.getConfigInfo()!=null&&childDatas.getConfigInfo().get(0).getYjgkflag().equals("1")){
                        LogUtil.i(TAG, "管控状态不为空和视力保护为空");
                        sp.edit().putBoolean("team"+childUUID,false).commit();
                        ChildContrlFlag childFlag = new ChildContrlFlag(null, childUUID, parentUUID, "", ""
                                , childDatas.getConfigInfo().get(0).getYjgkflag()
                                , CmdCommon.CMD_FLAG_CLOSE
                                , CmdCommon.CMD_FLAG_CLOSE
                                , "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
                        childCopntrlDao.insert(childFlag);
                        sp.edit().putBoolean("yjgk"+childUUID,true).commit();

                    }else{
                        LogUtil.i(TAG, "管控状态和视力保护均为空"+childDatas.getConfigInfo().get(0).getYjgkflag()+sp.getBoolean("team"+childUUID,false));
                        if(childDatas.getConfigInfo().get(0).getYjgkflag().equals("2")){
                            sp.edit().putBoolean("team"+childUUID,false).commit();
                            Log.e("-------teamchildUUID11"+childUUID,sp.getBoolean("team"+childUUID,true)+"");
                        }
                        Log.e("-------teamchildUUID11"+childUUID,sp.getBoolean("team"+childUUID,false)+"");


                        ChildContrlFlag childFlag = new ChildContrlFlag(null, childUUID, parentUUID, "", ""
                                , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE
                                , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
                        childCopntrlDao.insert(childFlag);
                        sp.edit().putBoolean("yjgk"+childUUID,false).commit();
                    }
                }
                /**获取孩子的网址过滤*/
                if(childDatas.getNetControlBean()!=null&&childDatas.getNetControlBean().size()>0){
                    String netIndex="";
                    for (int j = 0; j < childDatas.getNetControlBean().size(); j++) {
                        if(j==0){
                            netIndex=childDatas.getNetControlBean().get(j).getNetUrl();
                        }else{
                            netIndex=netIndex+"#,#"+childDatas.getNetControlBean().get(j).getNetUrl();
                        }
                    }
                    sp.edit().putString(childUUID + "netList", netIndex).commit();
                }
            }
        }

        //刷新界面数据
        EventBus.getDefault().post(new InitRefreshEvent(), "InitRefreshData");

        //初始化孩子的在线状态
        CmdData initChildLineJson = new CmdData();
        initChildLineJson.setOrder(CmdCommon.CMD_INIT);
        initChildLineJson.setIsOpen(CmdCommon.CMD_INIT_CONNECT);
        initChildLineJson.setParentUUID(parentUUID);
        LogUtil.e(TAG, "发送初始化孩子的在线状态向服务器发送的数据" + new Gson().toJson(initChildLineJson));
        //minaThread.sMessage(new Gson().toJson(initChildLineJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(initChildLineJson));
        }else{
            socketThread.sMessage(new Gson().toJson(initChildLineJson));
        }
    }

    /**
     * 处理电量提醒
     */
    private void orderPowerLevel(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行处理电量提醒指令");
        LogUtil.e(TAG, "orderPowerLevel：" + cmdData.getReturnData());
        long millis = System.currentTimeMillis();
        LogUtil.e(TAG, "orderPowerLevel：millis:" + millis);
        LogUtil.e(TAG, "orderPowerLevel：powersendTime:" + powersendTime);
        if(powersendTime!=0){
            if(millis-powersendTime<30*60*1000){
                return;
            }
        }
        // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PowerLevelData returnData = new Gson().fromJson(cmdData.getReturnData(), PowerLevelData.class);
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(cmdData.getChildUUID())).build().unique();

        if(findChild!=null){
            String name = findChild.getName();
            LogUtil.i(TAG, "orderPowerLevel: " + returnData.getPowerLevel());
            PendingIntent pendingIntent2 =PendingIntent.getActivity(this, 0, new Intent(this, SplashActivity.class), 0);
            int level=Integer.parseInt(returnData.getPowerLevel());
            LogUtil.e("mianServices","处理电量提醒:"+level);
            sp.edit().putString("power"+findChild.getChilduuid(),level+"").commit();
            if(level>30){
                // 通过Notification.Builder来创建通知，注意API Level
                // API11之后才支持
                Notification notify2 = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.icon)// 设置状态栏中的小图片,尺寸一般建议在24×24
                        // 这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap)
                        .setTicker("你孩子"+name+"手机的剩余电量为" + returnData.getPowerLevel() + "%")// 设置在status
                        // bar上显示的提示文字
                        .setContentTitle("优成长")// 设置在下拉status
                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                        .setContentText("你孩子"+name+"手机的剩余电量为" + returnData.getPowerLevel() + "%")// TextView中显示的详细内容
                        .setContentIntent(pendingIntent2) // 关联PendingIntent
                        //.setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                        .getNotification(); // 需要注意build()是在API level
                // 16及之后增加的，在API11中可以使用getNotificatin()来代替
                notify2.flags |= Notification.FLAG_AUTO_CANCEL;
                manager.notify(1, notify2);
                powersendTime = System.currentTimeMillis();
            }else{
                // 通过Notification.Builder来创建通知，注意API Level
                // API11之后才支持
                Notification notify2 = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.icon)// 设置状态栏中的小图片,尺寸一般建议在24×24
                        // 这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap)
                        .setTicker("您孩子"+name+"手机的电量不足,请及时联系您的孩子！")// 设置在status
                        // bar上显示的提示文字
                        .setContentTitle("优成长")// 设置在下拉status
                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
                        .setContentText("你孩子"+name+"手机的剩余电量为" + returnData.getPowerLevel() + "%")// TextView中显示的详细内容
                        .setContentIntent(pendingIntent2) // 关联PendingIntent
                        .setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
                        .getNotification(); // 需要注意build()是在API level
                // 16及之后增加的，在API11中可以使用getNotificatin()来代替
                notify2.flags |= Notification.FLAG_AUTO_CANCEL;
                manager.notify(1, notify2);
                powersendTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * 处理添加孩子数据
     */
    private void orderAddchildMessage(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行Addchild指令");
        final String childUUID = cmdData.getChildUUID();
        LogUtil.e(TAG, "得到的Addchild————getReturnData数据为：" + cmdData.getReturnData());
        String childSchool =null;
        String childNianji =null;
        String childName = null;
        String childSex = null;
        String childFrom = null;
        String childimage= null;
        if(cmdData.getOrder().equals(CmdCommon.CMD_ADD_CHILD)){
            AddStudentData studentData = new Gson().fromJson(cmdData.getReturnData(), AddStudentData.class);
            childSchool = studentData.getSchool();////:{"a":"2","b":"10003345","c":"447","d":"24","e":"","f":"","g":"1522224902000"}
            childNianji = studentData.getNianJi();
            childName = studentData.getChildName();
            childSex = studentData.getChildSex();
            childFrom=studentData.getChildFrom();
            /**获取孩子是不是允许被安装应用程序*/
            if(studentData.getI()!=null){
                String installable = studentData.getI();//0是禁止，1允许
                sp.edit().putString("install"+cmdData.getChildUUID(),installable).commit();
            }

        }else {
            AddStudentData1 studentData = new Gson().fromJson(cmdData.getReturnData(), AddStudentData1.class);
            childSchool = studentData.getSchool();
            childNianji ="";
            childName = studentData.getChildName();
            childSex = studentData.getChildSex();
            childFrom=studentData.h;
        }


        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildContrlFlagDao childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(childUUID)).build().unique();
        if (findChild == null) {//添加孩子
            ChildsTable child = new ChildsTable(null, childUUID, childName, childSex, childSchool, childNianji,childFrom,null,null);
            ChildContrlFlag childFlag = new ChildContrlFlag(null, childUUID, parentUUID, "", ""
                    , CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE//{"a":"15528167063","b":"111111","c":"VlzjC4leGucDAJSptkxHgVDG","d":"依依","f":"0","g":"GiONEE_GN5001S","h":"","i":"1"}
                    , CmdCommon.CMD_FLAG_CLOSE, "30", "5", CmdCommon.CMD_FLAG_CLOSE, CmdCommon.CMD_FLAG_CLOSE);
            childCopntrlDao.insert(childFlag);
            childDao.insert(child);
            LogUtil.e(TAG, "orderAddchildMessage得到的getReturnData数据为：" + childUUID);
            EventBus.getDefault().post(new ChildMessageDataEvent(childUUID, childName, childSex,childFrom,null,null), "ChildMessageData");
        }
    }

    /**
     * 处理上线下线通知 1代表上线 2代表离线
     */
    private void orderOnline(CmdData cmdData, String s) {
        LogUtil.e(TAG, "我在服务处理上线下线命令指令");
        String versionInfos = sp.getString("childVersion", "");
        LogUtil.e(TAG, "得到的命令数据为：" + cmdData.getTime());
        if(cmdData.getOrder().equals(CmdCommon.CMD_ONLINE)){
            if(versionInfos.contains(cmdData.getChildUUID())){
                String[] split = versionInfos.split(";");
                for (int i = 0; i < split.length; i++) {
                    if(split[i].contains(cmdData.getChildUUID())){
                        split[i]=cmdData.getChildUUID()+"#"+cmdData.getTime()+";";
                    }
                }
                String childInfos="";
                for (int i = 0; i < split.length; i++) {
                    childInfos+=split[i];
                }
                sp.edit().putString("childVersion",childInfos).commit();
            }else{
                sp.edit().putString("childVersion",versionInfos+cmdData.getChildUUID()+"#"+cmdData.getTime()+";").commit();
            }
        }
        String childUUID = cmdData.getChildUUID();
        EventBus.getDefault().post(
                new ChildOnlineAndOfflineEvent(childUUID, s), "OnOfflineData");
    }

    /**
     * 一键管控功能
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "yjgk")
    public void yjgkEvent(YjgkEvent yjgk) {

        sp.edit().putBoolean("isyjgk",true).commit();
        CmdData yjgkJson = new CmdData();
        yjgkJson.setOrder(CmdCommon.CMD_CONTROL_ALL);
        yjgkJson.setChildUUID(yjgk.childUUID);
        ControlAllData yjgkData = new ControlAllData();
        yjgkData.setIsOpen(yjgk.yjgkState);
        yjgkJson.setReturnData(new Gson().toJson(yjgkData));
        yjgkJson.setParentUUID(yjgk.parentUUID);
        yjgkJson.setIsOpen(yjgk.yjgkState);
        LogUtil.e(TAG, "一键管控功能向服务器发送的数据" + new Gson().toJson(yjgkJson));
        //minaThread.sMessage(new Gson().toJson(yjgkJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(yjgkJson));
        }else{
            socketThread.sMessage(new Gson().toJson(yjgkJson));
        }
    }

    /**
     * 一键锁屏功能
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "yjsp")
    public void yjspEvent(final YjspEvent yjsp) {
        CmdData yjspJson = new CmdData();
        yjspJson.setOrder(CmdCommon.CMD_LOCK_SCREEN);
        yjspJson.setChildUUID(yjsp.childUUID);
        yjspJson.setParentUUID(yjsp.parentUUID);
        yjspJson.setIsOpen(yjsp.yjspState);
        ControlAllData yjspData = new ControlAllData();
        if(yjsp.yjspState.equals(CmdCommon.CMD_FLAG_OPEN)){
            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = Message.obtain();
                    msg.what=107;
                    msg.obj=yjsp.childUUID;
                    myHandler.sendMessage(msg);
                }
            },2*60*1000);
        }
        yjspData.setIsOpen(yjsp.yjspState);
        yjspJson.setReturnData(new Gson().toJson(yjspData));
        LogUtil.e(TAG, "一键锁屏功能向服务器发送的数据" + new Gson().toJson(yjspJson));
        //minaThread.sMessage(new Gson().toJson(yjspJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(yjspJson));
        }else{
            socketThread.sMessage(new Gson().toJson(yjspJson));
        }
    }

    /**
     * 视力保护功能
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "slbh")
    public void slbhEvent(SlbhEvent slbh) {
        CmdData yjspJson = new CmdData();
        yjspJson.setOrder(CmdCommon.CMD_PROTECT_EYES);
        yjspJson.setChildUUID(slbh.childUUID);
        yjspJson.setParentUUID(slbh.parentUUID);
        EyesData eyesData = new EyesData();
        eyesData.setIsOpen(slbh.slbhState);
        eyesData.setJianGeTime(slbh.JianGeTime);
        eyesData.setSleepTime(slbh.SleepTime);
        yjspJson.setReturnData(new Gson().toJson(eyesData));
        yjspJson.setIsOpen(CmdCommon.CMD_DATA_ALL);
        LogUtil.e(TAG, "视力保护功能向服务器发送的数据" + new Gson().toJson(yjspJson));
        //minaThread.sMessage(new Gson().toJson(yjspJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(yjspJson));
        }else{
            socketThread.sMessage(new Gson().toJson(yjspJson));
        }
    }

    /**
     * 删除孩子功能
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "deleteChild")
    public void deleteChildEvent(DeleteChildEvent dc) {
        CmdData deleteChildJson = new CmdData();
        deleteChildJson.setOrder(CmdCommon.CMD_UNBINDING);
        deleteChildJson.setChildUUID(dc.childUUID);
        deleteChildJson.setParentUUID(dc.parentUUID);
        LogUtil.e(TAG, "删除孩子功能向服务器发送的数据" + new Gson().toJson(deleteChildJson));
        //minaThread.sMessage(new Gson().toJson(deleteChildJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(deleteChildJson));
        }else{
            socketThread.sMessage(new Gson().toJson(deleteChildJson));
        }
    }

    /**
     * 请求位置数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestLoaction")
    public void requestLocationEvent(RequestChildLoactionEvent location) {
        CmdData LocaionJson = new CmdData();
        LocaionJson.setOrder(CmdCommon.CMD_LOCATION);
        LocaionJson.setIsOpen(CmdCommon.CMD_FLAG_OPEN);
        LocaionJson.setChildUUID(location.childUUID);
        LocaionJson.setParentUUID(location.parentUUID);
        LogUtil.e(TAG, "获取孩子坐标开启向服务器发送的数据" + new Gson().toJson(LocaionJson));
        //minaThread.sMessage(new Gson().toJson(LocaionJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(LocaionJson));
        }else{
            socketThread.sMessage(new Gson().toJson(LocaionJson));
        }
    }

    /**
     * 处理孩子定位数据
     */
    private void orderLocation(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行Location指令");
        LogUtil.e(TAG, "得到的getReturnData数据为111：" + cmdData.getReturnData());
        LocationData returnData = new Gson().fromJson(cmdData.getReturnData(), LocationData.class);
        EventBus.getDefault().post(
                new ChildLocationDataEvent(returnData), "LoactionData");
    }

    /**
     * 请求孩子定位轨迹数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestLoactionPath")
    public void requestLocationPathEvent(RequestChildLoactionEvent location) {
        CmdData LocaionJson = new CmdData();
        LocaionJson.setOrder(CmdCommon.CMD_LOCATION_PATH);
        LocaionJson.setIsOpen(CmdCommon.CMD_FLAG_TRAIL);
        LocaionJson.setChildUUID(location.childUUID);
        LocaionJson.setParentUUID(location.parentUUID);
        LogUtil.e(TAG, "获取孩子运动轨迹向服务器发送的数据" + new Gson().toJson(LocaionJson));
        //minaThread.sMessage(new Gson().toJson(LocaionJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(LocaionJson));
        }else{
            socketThread.sMessage(new Gson().toJson(LocaionJson));
        }
    }

    /**
     * 处理孩子定位轨迹数据
     */
    private synchronized void orderLocationPath(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行LocationPath指令");
        LogUtil.e(TAG, "得到的getReturnData数据为111：" + cmdData.getReturnData());
        LogUtil.e(TAG, "我在服务进行LocationPath指令222: " + cmdData.getIsOpen());
        String childUUID = cmdData.getChildUUID();
        if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_START)) {
            listPathData = "";
            listPathData = cmdData.getReturnData();
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_CONNECT)) {
            listPathData = listPathData + cmdData.getReturnData();
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_END)) {
            listPathData = listPathData + cmdData.getReturnData();
            List<LocationData> locationDataList = new Gson().fromJson(listPathData, new TypeToken<List<LocationData>>() {
            }.getType());
            int size = locationDataList.size();
            LogUtil.i(TAG, "RequestLocationEvent数据: " + listPathData);
            sp.edit().putString(childUUID + "LocationPath", listPathData).commit();
            EventBus.getDefault().post(
                    new ChildLocationPathDataEvent(locationDataList), "LoactionPathData");
            listPathData = "";
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_ALL)  || cmdData.getIsOpen().equals("0") ) {
            listPathData = cmdData.getReturnData();
            if(TextUtils.isEmpty(listPathData)){
                LogUtil.e(TAG, "处理孩子定位轨迹数据为空");
                EventBus.getDefault().post(new ChildLocationPathDataEvent(null), "LoactionPathData");
                return;
            }
            List<LocationData> locationDataList = new Gson().fromJson(listPathData, new TypeToken<List<LocationData>>() {
            }.getType());
            int size = locationDataList.size();
            LogUtil.e(TAG, "RequestLocationEvent数据: size" + size);
            sp.edit().putString(childUUID + "LocationPath", listPathData).commit();
            String isstate = sp.getString("isstate", "1");
            if(isstate.equals("2")){
                EventBus.getDefault().post(new ChildLocationPathDataEvent(locationDataList), "LoactionPathData2");
                sp.edit().putString("isstate","1").commit();
            }else {
                EventBus.getDefault().post(new ChildLocationPathDataEvent(locationDataList), "LoactionPathData");
            }
            listPathData = "";
        }

    }

    /**
     * 发送白名单APP数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestWhiteApp")
    public void requestWhiteAppListEvent(RequestWhiteAppListEvent event) {
        CmdData whiteAppJson = new CmdData();
        whiteAppJson.setOrder(CmdCommon.CMD_GET_WHITE_APP_LIST);
        whiteAppJson.setChildUUID(event.childUUID);
        whiteAppJson.setIsOpen("5");
        appchilduuid = event.childUUID;
        whiteAppJson.setParentUUID(event.parentUUID);
//        AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
//        List<AppDataBean> list = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(event.childUUID)).build().list();
        List<AppDataBean> list =event.datas;
        List<AppData> mlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            AppData data = new AppData();
            data.setAppPkgName(list.get(i).getApppackgename());
            data.setAppname(list.get(i).getAppname());
            data.setB(list.get(i).getAppwhitelist());
            mlist.add(data);
        }

        String appList = new Gson().toJson(mlist);
        appdatas = MyUtils.spliteData(appList);
        LogUtil.e(TAG, "白名单APP向服务器发送的数据000" +mlist.size());
        if (appdatas.size() == 1) {
            whiteAppJson.setReturnData(appdatas.get(0));
            appdatas.remove(0);
            whiteAppJson.setIsOpen(CmdCommon.CMD_DATA_ALL);
            LogUtil.e(TAG, "白名单APP向服务器发送的数据1111" + new Gson().toJson(whiteAppJson));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(whiteAppJson));
            }else{
                socketThread.sMessage(new Gson().toJson(whiteAppJson));
            }
        } else {
            whiteAppJson.setIsOpen(CmdCommon.CMD_DATA_START);
            whiteAppJson.setReturnData(appdatas.get(0));
            appdatas.remove(0);
            LogUtil.e(TAG, "白名单APP向服务器发送的数据2222" + new Gson().toJson(whiteAppJson));
            //minaThread.sMessage(new Gson().toJson(whiteAppJson));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(whiteAppJson));
            }else{
                socketThread.sMessage(new Gson().toJson(whiteAppJson));
            }
        }
    }

    /**
     * 应用管控黑名单APP列表切片数据处理
     */
    private void orderControlApp(CmdData cmdData) {
        LogUtil.i(TAG, "orderControlApp: 应用管控白名单APP列表切片数据处理");
        EventBus.getDefault().post(new WhiteAppBackEvent(), "whiteAppDataBack1");
        CmdData whiteAppJson = new CmdData();
        whiteAppJson.setOrder(CmdCommon.CMD_CONTROL_APP);
        whiteAppJson.setChildUUID(appchilduuid);
        whiteAppJson.setParentUUID(parentUUID);
        if (appdatas.size() > 0) {
            if (appdatas.size() == 1) {
                whiteAppJson.setIsOpen(CmdCommon.CMD_DATA_END);
                whiteAppJson.setReturnData(appdatas.get(0));
                appdatas.remove(0);
                LogUtil.e(TAG, "白名单APP向服务器发送的数据" + new Gson().toJson(whiteAppJson));
                //minaThread.sMessage(new Gson().toJson(whiteAppJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(whiteAppJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(whiteAppJson));
                }
            } else {
                whiteAppJson.setIsOpen(CmdCommon.CMD_DATA_CONNECT);
                whiteAppJson.setReturnData(appdatas.get(0));
                appdatas.remove(0);
                LogUtil.e(TAG, "白名单APP向服务器发送的数据" + new Gson().toJson(whiteAppJson));
                //minaThread.sMessage(new Gson().toJson(whiteAppJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(whiteAppJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(whiteAppJson));
                }
            }
        } else {
            LogUtil.e(TAG, "白名单APP向服务器发送的切片数据结束 ：");
        }
    }

    /**
     * 管控计划功能
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "stopInternetPlan")
    public void stopInternetPlanEvent(StopInternetPlanEvent sipe) {
        CmdData stopPlanJson = new CmdData();
        stopPlanJson.setOrder(CmdCommon.CMD_NOTICE_PLAN);
        stopPlanJson.setChildUUID(sipe.childUUID);
        planchildUUID = sipe.childUUID;
        stopPlanJson.setParentUUID(sipe.parentUUID);
        //读取数据库
        NetPlanDataBeanDao dao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
        List<NetPlanDataBean> planlist =  dao.queryBuilder()
                .where(NetPlanDataBeanDao.Properties.Childuuid.eq(sipe.childUUID))
                .orderDesc(NetPlanDataBeanDao.Properties._id)
                .build().list();
        List<NetPlanData> mlist=new ArrayList<>();
        for (int i = 0; i < planlist.size(); i++) {
            NetPlanData planData = new NetPlanData(planlist.get(i).getNetplanname(),
                    planlist.get(i).getWeekday(),planlist.get(i).getStartplantime(),
                    planlist.get(i).getEndplantime(),planlist.get(i).getPlanflag());
            mlist.add(planData);
        }
        if (planlist == null) {
            stopPlanJson.setIsOpen(CmdCommon.CMD_DATA_ALL);
            stopPlanJson.setReturnData(new Gson().toJson(""));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(stopPlanJson));
            }else{
                socketThread.sMessage(new Gson().toJson(stopPlanJson));
            }
        } else {
            String appList = new Gson().toJson(mlist);
            netPlandatas = MyUtils.spliteData(appList);
            if (netPlandatas.size() == 1) {
                stopPlanJson.setReturnData(netPlandatas.get(0));
                netPlandatas.remove(0);
                stopPlanJson.setIsOpen(CmdCommon.CMD_DATA_ALL);
                LogUtil.e(TAG, "禁止上网上网管控计划功能向服务器发送的数据0" + new Gson().toJson(stopPlanJson));
                //minaThread.sMessage(new Gson().toJson(stopPlanJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(stopPlanJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(stopPlanJson));
                }
            } else {
                stopPlanJson.setIsOpen(CmdCommon.CMD_DATA_START);
                stopPlanJson.setReturnData(netPlandatas.get(0));
                netPlandatas.remove(0);
                LogUtil.e(TAG, "禁止上网上网管控计划功能向服务器发送的数据1" + new Gson().toJson(stopPlanJson));
                //minaThread.sMessage(new Gson().toJson(stopPlanJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(stopPlanJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(stopPlanJson));
                }
            }
        }
    }
    /**
     * 孩子管控计划切片数据处理
     */
    private void orderNetPlan(CmdData cmdData) {
        CmdData stopPlanJson = new CmdData();
        stopPlanJson.setOrder(CmdCommon.CMD_NOTICE_PLAN);
        stopPlanJson.setChildUUID(planchildUUID);
        stopPlanJson.setParentUUID(parentUUID);
        if (netPlandatas.size() > 0) {
            if (netPlandatas.size() == 1) {
                stopPlanJson.setIsOpen(CmdCommon.CMD_DATA_END);
                stopPlanJson.setReturnData(netPlandatas.get(0));
                netPlandatas.remove(0);
                LogUtil.e(TAG, " 孩子管控计划切片向服务器发送的数据" + new Gson().toJson(stopPlanJson));
                //minaThread.sMessage(new Gson().toJson(stopPlanJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(stopPlanJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(stopPlanJson));
                }
            } else {
                stopPlanJson.setIsOpen(CmdCommon.CMD_DATA_CONNECT);
                stopPlanJson.setReturnData(netPlandatas.get(0));
                netPlandatas.remove(0);
                LogUtil.e(TAG, "孩子管控计划向服务器发送的数据" + new Gson().toJson(stopPlanJson));
                //minaThread.sMessage(new Gson().toJson(stopPlanJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(stopPlanJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(stopPlanJson));
                }
            }
        } else {
            EventBus.getDefault().post(new NetControlBackEvent(), "netcontrolback");
        }

    }

    /**
     * 获取应用列表 应用管控，得到白名单APP列表
     */
    private synchronized void orderAllAppList(CmdData cmdData) {
        if(cmdData.getIsOpen().equals("1")){    //修改app返回成功提示
            EventBus.getDefault().post(new WhiteAppBackEvent(), "whiteAppDataBack1");
            return;
        }
        if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_START)) {
            listData = "";
            listData = cmdData.getReturnData();
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_CONNECT)) {
            if(TextUtils.isEmpty(listData)){
                listData="";
                return;
            }
            listData = listData + cmdData.getReturnData();
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_END)) {
            if(TextUtils.isEmpty(listData)){
                listData="";
                return;
            }
            listData = listData + cmdData.getReturnData();
            List<AppInstallData> whiteAppList = new Gson().fromJson(listData, new TypeToken<List<AppInstallData>>() {
            }.getType());
            listData="";
            LogUtil.e(TAG, "size:" + whiteAppList.size() + "得到的getReturnData数据为：WhiteAppList:" +
                    whiteAppList.get(0).getAppName() + whiteAppList.get(1).getAppName());
            AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
            List<AppDataBean> list = dao.queryBuilder()
                    .where(AppDataBeanDao.Properties.Childuuid.eq(cmdData.getChildUUID()))
                    .build().list();
            if (list.size() > 0) {//修改
                for (int i = 0; i < list.size(); i++) {
                    dao.delete(list.get(i));
                }
            }
            if (whiteAppList.size() > 0) {
                for (int i = 0; i < whiteAppList.size(); i++) {
                    String appname="";
                    if(Constants.IsUseMyDecoder){
                        appname=whiteAppList.get(i).getAppName();
                    }else{
                        appname=MyUtils.decodeString64(whiteAppList.get(i).getAppName()) ;
                    }
                    AppDataBean appDataBean = new AppDataBean(
                            null
                            , cmdData.getChildUUID()
                            , whiteAppList.get(i).getAppPkgName()
                            , appname
                            , whiteAppList.get(i).getAppwhite()
                            ,whiteAppList.get(i).getD()
                    );
                    dao.insert(appDataBean);
                }
            } else {
                LogUtil.e(TAG, "应用管控，得到白名单APP列表获取失败 长度小于0");
            }
            EventBus.getDefault().post(
                    new WhiteAppListEvent(whiteAppList), "whiteAppData");
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_ALL)) {
            List<AppInstallData> whiteAppList = new Gson().fromJson(cmdData.getReturnData(), new TypeToken<List<AppInstallData>>() {}.getType());
            AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
            List<AppDataBean> list = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(cmdData.getChildUUID())).build().list();
            if (list.size() > 0) {//修改
                for (int i = 0; i < list.size(); i++) {
                    dao.delete(list.get(i));
                }
            }
            if (whiteAppList.size() > 0) {
                for (int i = 0; i < whiteAppList.size(); i++) {
                    String appname="";
                    if(Constants.IsUseMyDecoder){
                        appname=whiteAppList.get(i).getAppName();
                    }else{
                        appname=MyUtils.decodeString64(whiteAppList.get(i).getAppName()) ;
                    }
                    AppDataBean appDataBean = new AppDataBean(null, cmdData.getChildUUID(), whiteAppList.get(i).getAppPkgName(), appname, whiteAppList.get(i).getAppwhite(),whiteAppList.get(i).getD());
                    dao.insert(appDataBean);
                }
            } else {
                LogUtil.e(TAG, "应用管控，得到白名单APP列表获取失败 长度小于0");
            }
            listData="";
            EventBus.getDefault().post(new WhiteAppListEvent(whiteAppList), "whiteAppData");
        }
    }

    /**
     * 发送卸载APP数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "UnInstallAppListEvent")
    public void UnInstallAppListEvent(UnInstallAppListEvent event) {
        CmdData whiteAppJson = new CmdData();
        whiteAppJson.setOrder(CmdCommon.CMD_XIEZAI_APP);
        whiteAppJson.setChildUUID(event.childUUID);
        whiteAppJson.setParentUUID(event.parentUUID);
        List<AppData> mlist = new ArrayList<>();
        for (int i = 0; i < event.list.size(); i++) {
            AppData data = new AppData();
            data.setAppPkgName(event.list.get(i).getApppackgename());
            data.setB(event.list.get(i).getAppwhitelist());
            data.setAppname(event.list.get(i).getAppname());
            mlist.add(data);
        }
        String appList = new Gson().toJson(mlist);
        whiteAppJson.setReturnData(appList);
        LogUtil.e(TAG, "卸载APP向服务器发送的数据" +appList);
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(whiteAppJson));
        }else{
            socketThread.sMessage(new Gson().toJson(whiteAppJson));
        }
    }
    /**
     * 获取应用列表 应用管控，得到白名单APP列表
     */
//    private synchronized void orderAllAppList1(CmdData cmdData) {
//        if(cmdData.getIsOpen().equals("1")){    //修改app返回成功提示
//            EventBus.getDefault().post(new WhiteAppBackEvent(), "whiteAppDataBack1");
//            return;
//        }
//
//        boolean isapplist = sp.getBoolean("isapplist", false);
//        if(!isapplist){
//            return;
//        }
//        sp.edit().putBoolean("isapplist",false).commit();
//
//        Log.e("--------从服务器返回应用列表1",cmdData.getReturnData());
//        if (cmdData.getIsOpen().equals("0")) {
//            List<AppInstallData> whiteAppList = new Gson().fromJson(cmdData.getReturnData(), new TypeToken<List<AppInstallData>>() {}.getType());
//            AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
//            List<AppDataBean> list = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(cmdData.getChildUUID())).build().list();
//            if (list.size() > 0) {//修改
//                for (int i = 0; i < list.size(); i++) {
//                    dao.delete(list.get(i));
//                }
//            }
//            if (whiteAppList.size() > 0) {
//                for (int i = 0; i < whiteAppList.size(); i++) {
//                    String appname="";
//                    if(Constants.IsUseMyDecoder){
//                        appname=whiteAppList.get(i).getAppName();
//                    }else{
//                        appname=MyUtils.decodeString64(whiteAppList.get(i).getAppName()) ;
//                    }
//                    AppDataBean appDataBean = new AppDataBean(null, cmdData.getChildUUID(), whiteAppList.get(i).getAppPkgName(), appname , whiteAppList.get(i).getAppwhite(),whiteAppList.get(i).getD());
//                    dao.insert(appDataBean);
//                }
//            } else {
//                LogUtil.e(TAG, "应用管控，得到白名单APP列表获取失败 长度小于0");
//            }
//            boolean issave = sp.getBoolean("issave", false);
//            if(issave){
//                sp.edit().putBoolean("issave",false).commit();
//                return;
//            }
//            listData="";
//            EventBus.getDefault().post(new WhiteAppListEvent(whiteAppList), "whiteAppData");
//        }
//    }


    /**
     * 获取应用列表 应用管控，得到白名单APP列表i==0
     */
    private synchronized void orderAllAppList1(CmdData cmdData) {

        if(cmdData.getIsOpen().equals("0") &&  TextUtils.isEmpty(cmdData.getReturnData())){    //修改app返回成功提示
            EventBus.getDefault().post(new WhiteAppBackEvent(), "whiteAppDataBack1");
            return;
        }
        if(cmdData.getIsOpen().equals("60") &&  TextUtils.isEmpty(cmdData.getReturnData())){    //请求新增数据为空
            EventBus.getDefault().post(new WhiteAppListEvent(null), "whiteAppData");
            return;
        }
        if(cmdData.getIsOpen().equals("40") &&  TextUtils.isEmpty(cmdData.getReturnData())){    //请求数据为空
            EventBus.getDefault().post(new WhiteAppListEvent(null), "whiteAppData");
            return;
        }
        boolean isapplist = sp.getBoolean("isapplist", false);
        if(!isapplist){
            return;
        }
        Log.e("--------从服务器返回应用列表:",cmdData.getReturnData());
        sp.edit().putBoolean("isapplist",false).commit();

        if (cmdData.getIsOpen().equals("499") ||cmdData.getIsOpen().equals("699") || cmdData.getIsOpen().equals("400") ||cmdData.getIsOpen().equals("600") ){//分片处理
            if(cmdData.getIsOpen().equals("499") ||cmdData.getIsOpen().equals("699")){   //分包上传
                sp.edit().putBoolean("isapplist",true).commit();
            }
            if(cmdData.getIsOpen().equals("400") ||cmdData.getIsOpen().equals("600")){   //分包上传结束
                sp.edit().putBoolean("isapplist",false).commit();
            }
            List<AppInstallData> whiteAppList = new Gson().fromJson(cmdData.getReturnData(), new TypeToken<List<AppInstallData>>() {}.getType());
            AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
            List<AppDataBean> list = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(cmdData.getChildUUID())).build().list();
            if (whiteAppList.size() > 0) {  //如果返回的有数据   分为添加和删除
                for (int i = 0; i < whiteAppList.size(); i++) {
                    String appname = "";
                    if (Constants.IsUseMyDecoder) {
                        appname = whiteAppList.get(i).getAppName();
                    } else {
                        appname = MyUtils.decodeString64(whiteAppList.get(i).getAppName());
                    }

                    if(whiteAppList.get(i).getD().equals("6")  ||  whiteAppList.get(i).getD().equals("0")) {  //如果是添加数据
                        AppDataBean appDataBean = new AppDataBean(null, cmdData.getChildUUID(), whiteAppList.get(i).getAppPkgName(), appname, whiteAppList.get(i).getAppwhite(), whiteAppList.get(i).getD());
                        List<String> ss = new ArrayList<>();
                        for (int j = 0; j < list.size(); j++) {
                            String apppackgename = list.get(j).getApppackgename();
                            ss.add(apppackgename);
                        }
                        if (!ss.contains(whiteAppList.get(i).getAppPkgName())) {    //  本地应用数据不为空，不保存本地
                            dao.insert(appDataBean);
                        }
                    }
                    if(whiteAppList.get(i).getD().equals("7")) {    //如果是删除数据 ,查询到本地删除数据
                        AppDataBean unique = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(cmdData.getChildUUID())).where(AppDataBeanDao.Properties.Apppackgename.eq(whiteAppList.get(i).getAppPkgName())).build().unique();
                        if(unique!=null){
                            dao.delete(unique);
                        }
                    }
                }
            } else {
                LogUtil.e(TAG, "返回应用列表位空");
            }
            boolean issave = sp.getBoolean("issave", false);
            if(issave){
                sp.edit().putBoolean("issave",false).commit();
                return;
            }
            listData="";
            EventBus.getDefault().post(new WhiteAppListEvent(whiteAppList), "whiteAppData");
        }




        if (cmdData.getIsOpen().equals("4") ||cmdData.getIsOpen().equals("6")){   //不分片处理
            List<AppInstallData> whiteAppList = new Gson().fromJson(cmdData.getReturnData(), new TypeToken<List<AppInstallData>>() {}.getType());
            AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
            List<AppDataBean> list = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(cmdData.getChildUUID())).build().list();
            if (whiteAppList.size() > 0) {  //如果返回的有数据   分为添加和删除
                for (int i = 0; i < whiteAppList.size(); i++) {
                    String appname = "";
                    if (Constants.IsUseMyDecoder) {
                        appname = whiteAppList.get(i).getAppName();
                    } else {
                        appname = MyUtils.decodeString64(whiteAppList.get(i).getAppName());
                    }

                    if(whiteAppList.get(i).getD().equals("6")  ||  whiteAppList.get(i).getD().equals("0")) {  //如果是添加数据
                        AppDataBean appDataBean = new AppDataBean(null, cmdData.getChildUUID(), whiteAppList.get(i).getAppPkgName(), appname, whiteAppList.get(i).getAppwhite(), whiteAppList.get(i).getD());
                        List<String> ss = new ArrayList<>();
                        for (int j = 0; j < list.size(); j++) {
                            String apppackgename = list.get(j).getApppackgename();
                            ss.add(apppackgename);
                        }
                        if (!ss.contains(whiteAppList.get(i).getAppPkgName())) {    //  本地应用数据不为空，不保存本地
                            dao.insert(appDataBean);
                        }
                    }
                    if(whiteAppList.get(i).getD().equals("7")) {    //如果是删除数据 ,查询到本地删除数据
                        AppDataBean unique = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(cmdData.getChildUUID())).where(AppDataBeanDao.Properties.Apppackgename.eq(whiteAppList.get(i).getAppPkgName())).build().unique();
                        if(unique!=null){
                            dao.delete(unique);
                        }
                    }
                }
            } else {
                LogUtil.e(TAG, "返回应用列表位空");
            }
            boolean issave = sp.getBoolean("issave", false);
            if(issave){
                sp.edit().putBoolean("issave",false).commit();
                return;
            }
            listData="";
            EventBus.getDefault().post(new WhiteAppListEvent(whiteAppList), "whiteAppData");
        }
    }



    /**
     * 请求修改密码 忘记密码
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "changePsw")
    public void RequestChangePswEvent(RequestChangePswEvent requestData) {
        CmdData changePswJson = new CmdData();
        changePswJson.setOrder(CmdCommon.CMD_RESET_PWD);
        ChangePswData pswData = new ChangePswData();
        pswData.setPsw(requestData.pwd);
        pswData.setPhoneNum(requestData.phoneNum);
        changePswJson.setReturnData(new Gson().toJson(pswData));
        LogUtil.e(TAG, "请求修改密码数据向服务器发送的数据：" + new Gson().toJson(changePswJson));
        //minaThread.sMessage(new Gson().toJson(changePswJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(changePswJson));
        }else{
            socketThread.sMessage(new Gson().toJson(changePswJson));
        }
    }

    /**
     * 修改密码
     */
    private void orderChangePsw(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行修改密码指令");
        String isRegistered = cmdData.getIsOpen();
        LogUtil.e(TAG, "该修改密码状态数据为：" + isRegistered);
        EventBus.getDefault().post(
                new ChangePswDataEvent(isRegistered), "ChangePswData");
    }

    /**
     * 孩子端申请上网
     * */
    private void orderUsePhone(CmdData cmdData) {
//        LogUtil.e(TAG, "我在服务进行孩子端申请上网指令");
//        String getchildUUID = cmdData.getChildUUID();
//        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
//        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(getchildUUID)).build().unique();
//        if(findChild!=null){
//            if(Build.VERSION.SDK_INT<20){
//                AlertDialog.Builder alertDialog =null;
//                if(alertDialog ==null){
//                    alertDialog=new AlertDialog.Builder(this);
//                }
//                alertDialog.setTitle("优成长").setMessage("您的孩子"+findChild.getName()+"申请使用手机,请前往设置！");
//                alertDialog.setPositiveButton("取消",
//                        new DialogInterface.OnClickListener()
//                        {
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//
//                            }
//                        });
//                alertDialog.setNegativeButton("确定",
//                        new DialogInterface.OnClickListener()
//                        {
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                Intent intent = new Intent(MyApplication.getContext(), MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
//                if(ad!=null&&ad.isShowing()){
//                    ad.dismiss();
//                }
//                ad=alertDialog.create();
//                ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                ad.setCanceledOnTouchOutside(true);//点击外面区域让dialog消失
//                LogUtil.e("isShowing:"+ ad.isShowing());
//                ad.show();
//                LogUtil.e("isShowing:"+ ad.isShowing());
//
//            }else{
//                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                String name = findChild.getName();
//                PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 0,
//                        new Intent(this, MainActivity.class), 0);
//                // 通过Notification.Builder来创建通知，注意API Level
//                // API11之后才支持
//                Notification notify2 = new Notification.Builder(this)
//                        .setSmallIcon(R.drawable.icon_level)// 设置状态栏中的小图片,尺寸一般建议在24×24
//                        // 这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap)
//                        .setTicker("您的孩子"+name+"申请使用手机,请前往设置！！")// 设置在status
//                        // bar上显示的提示文字
//                        .setContentTitle("优成长")// 设置在下拉status
//                        // bar后Activity，本例子中的NotififyMessage的TextView中显示的标题
//                        .setContentText("您的孩子"+name+"申请使用手机,请前往设置！")// TextView中显示的详细内容
//                        .setContentIntent(pendingIntent2) // 关联PendingIntent
//                        .setDefaults(Notification.DEFAULT_VIBRATE)//设置震动
//                        .setDefaults(Notification.DEFAULT_SOUND)//调用系统默认响铃,
//                        //.setNumber(1) // 在TextView的右方显示的数字，可放大图片看，在最右侧。这个number同时也起到一个序列号的左右，如果多个触发多个通知（同一ID），可以指定显示哪一个。
//                        .getNotification(); // 需要注意build()是在API level
//                // 16及之后增加的，在API11中可以使用getNotificatin()来代替
//                notify2.flags |= Notification.FLAG_AUTO_CANCEL;
//                manager.notify(1, notify2);
//            }
//        }


        LogUtil.e(TAG, "我在服务进行孩子端申请上网指令");
        String getchildUUID = cmdData.getChildUUID();
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(getchildUUID)).build().unique();
        if (findChild!=null){
            Intent intent =new Intent(this,UpgradeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("msg",findChild.getName());
            startActivity(intent);
        }
    }

    /**
     * 请求app使用跟踪数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestAppUseTime")
    public void requestAppUseTimeEvent(RequestAppUseTimeEvent usetime) {
        CmdData appUseTimeJson = new CmdData();
        appUseTimeJson.setOrder(CmdCommon.CMD_TRACE_APP);
        appUseTimeJson.setChildUUID(usetime.childUUID);
        appUseTimeJson.setParentUUID(parentUUID);
        appUseTimeJson.setIsOpen(CmdCommon.CMD_DATA_START);
        LogUtil.e(TAG, "获取孩子app使用跟踪数据向服务器发送的数据" + new Gson().toJson(appUseTimeJson));
        //minaThread.sMessage(new Gson().toJson(appUseTimeJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(appUseTimeJson));
        }else{
            socketThread.sMessage(new Gson().toJson(appUseTimeJson));
        }
    }

    /**
     * 处理app使用跟踪数据
     * */
    private void orderUseAppTime(CmdData cmdData) {
        LogUtil.e(TAG, "本地appuseChilduuid"+sp.getString("ChildUUID", ""));
        LogUtil.e(TAG, "得到的getReturnData数据为111：" + cmdData.getReturnData());
        String appuseChilduuid=sp.getString("ChildUUID", "");
        if(!cmdData.getChildUUID().equals(appuseChilduuid)){
            LogUtil.e(TAG, "得到的getReturnData数据为111：拒绝接收" );
            return;
        }
        if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_START)) {
            OrderlistData = "";
            OrderlistData = cmdData.getReturnData();
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_CONNECT)) {
            OrderlistData = OrderlistData + cmdData.getReturnData();
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_END)) {
            OrderlistData = OrderlistData + cmdData.getReturnData();
            EventBus.getDefault().post(
                    new AppUseTimeDataEvent(OrderlistData), "AppUseTimeData");
            OrderlistData="";
        } else if (cmdData.getIsOpen().equals(CmdCommon.CMD_DATA_ALL)  ||cmdData.getIsOpen().equals("0")) {
            List<AppUsageData> appuseDataList = new Gson().fromJson( cmdData.getReturnData(), new TypeToken<List<AppUsageData>>() {}.getType());
            if(appuseDataList==null || appuseDataList.size()==0){     //如果网络请求没得数据  就加在本地数据naizhao  tixie    xinxia   xinxue

                AppUseBeanDao dao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
                List<AppUseBean> list = dao.queryBuilder().where(AppUseBeanDao.Properties.Childuuid.eq(appuseChilduuid)).build().list();
                if (list.size() > 0) {//删除
                    for (int i = 0; i < list.size(); i++) {
                        dao.delete(list.get(i));
                    }
                }
                EventBus.getDefault().post(new AppUseDataEvent(null), "AppUseTimeData");
                return;
            }
            //有数据时保存数据库
            List<AppUseBean> datas=new ArrayList<>();
            if(appuseDataList.size()>0){
                for (int i = 0; i < appuseDataList.size(); i++) {
                    if(TextUtils.isEmpty(appuseDataList.get(i).a)){
                        continue;
                    }
                    LogUtil.e("保存应用追踪---appuseChilduuid---  ",appuseChilduuid);
                    AppUseBean appuses=new AppUseBean(null,appuseDataList.get(i).c,appuseDataList.get(i).a,appuseDataList.get(i).b,appuseDataList.get(i).d,appuseChilduuid); //保存用的数据
                    datas.add(appuses);
                }
                addDao(datas,appuseChilduuid);
            }
            EventBus.getDefault().post(new AppUseDataEvent(appuseDataList), "AppUseTimeData");
        }
    }
    private void addDao(List<AppUseBean> dataList,String appuseChilduuid) {
        LogUtil.e("保存应用追踪---appuseChilduuid---  ",appuseChilduuid);
        AppUseBeanDao dao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
        List<AppUseBean> list = dao.queryBuilder().where(AppUseBeanDao.Properties.Childuuid.eq(appuseChilduuid)).build().list();
        if (list.size() > 0) {//修改
            for (int i = 0; i < list.size(); i++) {
                dao.delete(list.get(i));
            }
        }
        for (int i = 0; i < dataList.size(); i++) {
            dao.insert(dataList.get(i));
        }
    }
    /**
     * 请求上网管控
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestNetControl")
    public void requestNetControlEvent(RequestNetControlEvent netData) {
        CmdData netDataJson = new CmdData();
        netDataJson.setOrder(CmdCommon.CMD_CONTROL_NET);
        netChildUUID = netData.childUUID;
        netDataJson.setChildUUID(netChildUUID);
        netDataJson.setParentUUID(parentUUID);
        String[] mlist = sp.getString(netChildUUID  + "netList", "").split("#,#");
        List<NetControlBean> list = new ArrayList<>();
        for (int i = 0; i < mlist.length; i++) {
            NetControlBean controlBean = new NetControlBean(mlist[i]);
            list.add(controlBean);
        }
        String appList = new Gson().toJson(list);
        netdatas = MyUtils.spliteData(appList);
        if (netdatas.size() == 1) {
            netDataJson.setReturnData(netdatas.get(0));
            netdatas.remove(0);
            netDataJson.setIsOpen(CmdCommon.CMD_DATA_ALL);
            LogUtil.e(TAG, "请求上网管控向服务器发送的数据" + new Gson().toJson(netDataJson));
            //minaThread.sMessage(new Gson().toJson(netDataJson));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(netDataJson));
            }else{
                socketThread.sMessage(new Gson().toJson(netDataJson));
            }
        } else {
            netDataJson.setIsOpen(CmdCommon.CMD_DATA_START);
            netDataJson.setReturnData(netdatas.get(0));
            netdatas.remove(0);
            LogUtil.e(TAG, "请求上网管控向服务器发送的数据" + new Gson().toJson(netDataJson));
            //minaThread.sMessage(new Gson().toJson(netDataJson));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(netDataJson));
            }else{
                socketThread.sMessage(new Gson().toJson(netDataJson));
            }
        }
    }

    /**
     * 上网管控计划切片数据处理
     * */
    private void orderNetControl() {
        LogUtil.i(TAG, "orderNetControl: 上网管控计划切片数据处理");
        CmdData netUrlJson = new CmdData();
        netUrlJson.setOrder(CmdCommon.CMD_CONTROL_NET);
        netUrlJson.setChildUUID(netChildUUID);
        netUrlJson.setParentUUID(parentUUID);
        if (netdatas.size() > 0) {
            if (netdatas.size() == 1) {
                netUrlJson.setIsOpen(CmdCommon.CMD_DATA_END);
                netUrlJson.setReturnData(netdatas.get(0));
                netdatas.remove(0);
                LogUtil.e(TAG, "上网管控计划向服务器发送的数据" + new Gson().toJson(netUrlJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(netUrlJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(netUrlJson));
                }
            } else {
                netUrlJson.setIsOpen(CmdCommon.CMD_DATA_CONNECT);
                netUrlJson.setReturnData(netdatas.get(0));
                netdatas.remove(0);
                LogUtil.e(TAG, "上网管控计划向服务器发送的数据" + new Gson().toJson(netUrlJson));
                //minaThread.sMessage(new Gson().toJson(netUrlJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(netUrlJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(netUrlJson));
                }
            }
        } else {
            LogUtil.e(TAG, "上网管控计划向服务器发送的切片数据结束 ：");
            EventBus.getDefault().post(new WWWControlBackEvent(), "wwwcontrolback");
        }
    }
    /**
     * 获取所有孩子的在线状态
     * */
    private void orderChildLine(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务处理所有孩子的在线状态指令");
        LogUtil.e(TAG, "得到的命令数据为：" +  cmdData.getReturnData());
        String data = cmdData.getReturnData();
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        List<ChildsTable> list = childDao.queryBuilder().build().list();
        for (int i = 0; i < list.size(); i++) {
            sp.edit().putString(list.get(i).getChilduuid() + "online", CmdCommon.CMD_OFFLINE).commit();
        }
        //更新时间
        long sendTime = Long.parseLong(cmdData.getTime());
        LogUtil.e("orderChildLine","sendTime:"+sendTime);
        connectTime = sendTime - System.currentTimeMillis();
        LogUtil.e("orderChildLine","connectTime:"+ connectTime);


        Viplistbean cmdData1 = new Gson().fromJson(data, Viplistbean.class);
        LogUtil.e("cmdData1","cmdData1:"+ cmdData1.a);          // 孩子上下线信息
        LogUtil.e("cmdData1","cmdData1:"+ cmdData1.b);       //应用vip列表
        LogUtil.e("cmdData1","cmdData1:"+ cmdData1.c);      //是不是会员

        if(!TextUtils.isEmpty(cmdData1.b)){
            sp.edit().putString("viplist",cmdData1.b).commit();
        }
        if(!TextUtils.isEmpty(cmdData1.c)){
            if(cmdData1.c.equals("0")){
                sp.edit().putBoolean("isVip",false).commit();
            }else {
                sp.edit().putBoolean("isVip",true).commit();
            }
        }

        String returnData = cmdData1.a;
        if(!TextUtils.isEmpty(returnData)){
            String[] split = returnData.split("#");
            LogUtil.e("orderChildLine","size:"+split.length);
            for (int i = 0; i < split.length; i++) {
                String[] childata = split[i].split(",");
                if(childata[1].equals("1")){
                    sp.edit().putString(childata[0] + "online", CmdCommon.CMD_ONLINE).commit();
                }else{
                    sp.edit().putString(childata[0] + "online", CmdCommon.CMD_OFFLINE).commit();
                }
            }
        }else{
            for (int i = 0; i < list.size(); i++) {
                sp.edit().putString(list.get(i).getChilduuid() + "online", CmdCommon.CMD_OFFLINE).commit();
            }
        }
        EventBus.getDefault().post(new ChildLineFlagEvent(), "childLineData");
    }
    /**
     * 请求修改孩子信息
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "changeChildData")
    public void RequestChangeChildDataEvent(ChangeChildDataEvent requestData) {
        CmdData changeChildJson = new CmdData();
        changeChildJson.setOrder(CmdCommon.CMD_CHANGE_CHILD_MSG);
        changeChildJson.setParentUUID(parentUUID);
        changeChildJson.setChildUUID(requestData.Childuuid);
        Log.e("-------state--",requestData.state);
        StudentData pswData = new StudentData(requestData.childname,requestData.childsex,"","","ycz",requestData.state,"");
        changeChildJson.setReturnData(new Gson().toJson(pswData));
        LogUtil.e(TAG, "请求修改孩子信息向服务器发送的数据：" + new Gson().toJson(changeChildJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(changeChildJson));
        }else{
            socketThread.sMessage(new Gson().toJson(changeChildJson));
        }
    }

    /**
     * 处理修改孩子信息
     * */
    private void orderChangeChild(CmdData cmdData) {
        LogUtil.e(TAG, "我在服务进行处理修改孩子信息指令");
        String isRegistered = cmdData.getIsOpen();
        LogUtil.e(TAG, "处理修改孩子信息的状态为：" + isRegistered);
        EventBus.getDefault().post(new OrderChildDataEvent(isRegistered), "orederChildData");
    }
    /**
     * 处理删除孩子信息
     * */
    private void OrderDeleteChild(CmdData cmdData) {
        LogUtil.e(TAG, "处理删除孩子信息");
        String isRegistered = cmdData.getIsOpen();
        LogUtil.e(TAG, "处理删除孩子信息：" + isRegistered);
        EventBus.getDefault().post(
                new OrderDeleteChildEvent(isRegistered), "orederdeleteChild");
    }
    /**
     * 请求孩子端升级
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestChildUp")
    private void OrderChildUp(ChildUpEvent requestData) {
        CmdData childUpJson = new CmdData();
        childUpJson.setOrder(CmdCommon.CMD_CHILD_UP);
        childUpJson.setParentUUID(parentUUID);
        childUpJson.setChildUUID(requestData.ChildUUID);
        LogUtil.e(TAG, "请求孩子端升级向服务器发送的数据：" + new Gson().toJson(childUpJson));
        //minaThread.sMessage(new Gson().toJson(childUpJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(childUpJson));
        }else{
            socketThread.sMessage(new Gson().toJson(childUpJson));
        }
    }

    /**
     * 处理一键管控反馈
     * */
    private void OrderControlAll(CmdData cmdData) {
        EventBus.getDefault().post(new YjgkDataEvent(cmdData.getIsOpen()), "yjgkdata");
    }


    /**t
     * 处理团控
     * */
    private void OrderControam(CmdData cmdData) {
        String childUUID = cmdData.getChildUUID();
        EventBus.getDefault().post(new YjspTuanEvent(cmdData.getIsOpen(),childUUID), "yjgkteam");
    }
    /**t
     * 处理团控
     * */
    private void UpdataNc(CmdData cmdData) {
        String childUUID = cmdData.getChildUUID();
        EventBus.getDefault().post(new UpdateNcEvent(cmdData.getIsOpen()), "UpdateNc");
    }
    /**
     * 处理一键锁屏反馈
     * */
    private void OrderLock(CmdData cmdData) {
        EventBus.getDefault().post(new YjspDataEvent(cmdData.getIsOpen()), "yjspata");
    }

    /**
     * 处理视力保护反馈
     * */
    private void OrderSlbh(CmdData cmdData) {
        EventBus.getDefault().post(new SlbhDataEvent(cmdData.getIsOpen()), "slbhata");
    }
    /**t
     * 处理是不是允许安装app
     * */
    private void InstallApp(CmdData cmdData) {
        EventBus.getDefault().post(new InstallAppListEvent(cmdData.getIsOpen()), "AppInstallEnble");

    }

    /**
     * 发送信息给用户
     * */
    private void sendUserHello() throws JSONException {
        String url = "https://a1.easemob.com/ehand/ycz/messages";
        String s="{\"target_type\" : \"users\",\"target\" : [\""+parentUUID+"\"],\"msg\" : {\"type\" : \"txt\",\"msg\" : \"欢迎使用优成长\"},\"from\" : \"119\"}";
        JSONObject jsonObject = new JSONObject(s);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST,url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.e("发送信息给用户", "sendUserHello:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("发送信息给用户", "sendUserHello:" + error);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+sp.getString("imtoken",""));
                return headers;
            }
        };
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(jsonRequest);
    }

    /**
     * 请求孩子应用列表(所有)
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestchildApp")
    private void OrderChildAppList(RequestChildAppListEvent requestData) {
        CmdData childUpJson = new CmdData();
        childUpJson.setOrder(CmdCommon.CMD_GET_WHITE_APP_LIST);
        childUpJson.setIsOpen("4");   //请求列表 回复  f=0     修改app  回复  f==1
        childUpJson.setParentUUID(parentUUID);
        childUpJson.setChildUUID(requestData.childUUID);
        LogUtil.e(TAG, "请求孩子应用列表向服务器发送的数据：" + new Gson().toJson(childUpJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(childUpJson));
        }else{
            socketThread.sMessage(new Gson().toJson(childUpJson));
        }
    }
    /**
     * 请求孩子应用列表（增加删除）
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestChangechildApp")
    private void OrderChildAppLists(RequestChildAppListEvent requestData) {
        CmdData childUpJson = new CmdData();
        childUpJson.setOrder(CmdCommon.CMD_GET_WHITE_APP_LIST);
        childUpJson.setIsOpen("6");   //请求列表 回复  f=0     修改app  回复  f==1
        childUpJson.setParentUUID(parentUUID);
        childUpJson.setChildUUID(requestData.childUUID);
        LogUtil.e(TAG, "请求孩子增删应用列表数据：" + new Gson().toJson(childUpJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(childUpJson));
        }else{
            socketThread.sMessage(new Gson().toJson(childUpJson));
        }
    }

    /**
     * 修改昵称
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "changeNc")
    private void ChangeNc(ChangeNCEvent name) {
        CmdData childUpJson = new CmdData();
        childUpJson.setOrder(CmdCommon.CMD_UP_NC);
        childUpJson.setParentUUID(parentUUID);

        NcInfo ncInfo=new NcInfo();
        ncInfo.setA(name.nc);
        childUpJson.setReturnData(new Gson().toJson(ncInfo));

        LogUtil.e(TAG, "修改昵称数据：" + new Gson().toJson(childUpJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(childUpJson));
        }else{
            socketThread.sMessage(new Gson().toJson(childUpJson));
        }
    }
    /**
     * 重复登陆
     */
    private void LoginOut() {
        Toast.makeText(MyApplication.getContext(),"您的账号已经在其他手机上登陆",Toast.LENGTH_SHORT).show();
        toLogin();
    }
    public void toLogin(){
        sp.edit().putString("childName", "")
                .putString("ChildUUID", "")
                .putString("childSex", "")
                .putString("childztl", "")
                .putString("childImage", "")
                .putString("token", "")
                .putString("tokens", "")
                .putString("loginImFlag", "")
                .putString("childVersion", "")
                .putString("loginImFlag","")
                .putString("headimage","")
                .putString("parentUUID","")
                .putString("nc", "").commit();
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildContrlFlagDao childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        AppDataBeanDao appdao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        NetPlanDataBeanDao netPlanDao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
        LoveTrailDataDao lovePlanDao = GreenDaoManager.getInstance().getSession().getLoveTrailDataDao();
        AppUseBeanDao appUseBeanDao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
        PhoneDataDao phoneDataDao = GreenDaoManager.getInstance().getSession().getPhoneDataDao();
        appUseBeanDao.deleteAll();
        lovePlanDao.deleteAll();
        netPlanDao.deleteAll();
        phoneDataDao.deleteAll();
        childDao.deleteAll();
        childCopntrlDao.deleteAll();
        appdao.deleteAll();

        Intent intent5 = new Intent(ActivityManager.getInstance().getCurrentActivity(), LoginActivity.class);
        intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent5);
        ActivityCollector.finishAll();
    }
    /**
     * 请求是不是允许安装app
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestInstall")
    private void InstallAppList(RequestInstallEvent requestData) {
        CmdData childUpJson = new CmdData();
        IntallData install=new IntallData();
        install.setIsOpen(requestData.state);

        childUpJson.setOrder(CmdCommon.CMD_INSTALL_APP);
        childUpJson.setIsOpen(requestData.state);
        childUpJson.setParentUUID(parentUUID);
        childUpJson.setChildUUID(requestData.childUUID);
        childUpJson.setReturnData(new Gson().toJson(install));

        LogUtil.e(TAG, "孩子端是不是允许安装app：" + new Gson().toJson(childUpJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(childUpJson));
        }else{
            socketThread.sMessage(new Gson().toJson(childUpJson));
        }
    }

    /**
     * 请求孩子亲情号码
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestWhitePhone")
    private void OrderPhoneList(RequestQingQinEvent requestData) {
        CmdData childUpJson = new CmdData();
        childUpJson.setOrder(CmdCommon.CMD_WHITE_PHONE);
        childUpJson.setParentUUID(parentUUID);
        childUpJson.setChildUUID(requestData.childUUID);
        if(requestData.list==null){
            childUpJson.setIsOpen("4");   //请求列表 回复  f=0
        }else {
            childUpJson.setIsOpen("5");   //请求添加修改
            List<PhoneData> list =requestData.list;
            List<QinqPhoneBean> mlist = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                QinqPhoneBean data = new QinqPhoneBean(list.get(i).getName(),list.get(i).getPhone(),list.get(i).getState());
                mlist.add(data);
            }
            if(mlist.size()>0){
                String appList = new Gson().toJson(mlist);
                childUpJson.setReturnData(appList);
            }
        }
        Log.e(TAG, "请求号码亲情名单：" + new Gson().toJson(childUpJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(childUpJson));
        }else{
            socketThread.sMessage(new Gson().toJson(childUpJson));
        }
    }

    /**
     * 服务器返回亲情号码
     */
    private synchronized void OrderPhoneList(CmdData cmdData) {
        if(cmdData.getIsOpen().equals("2")){    //修改app返回成功提示
            EventBus.getDefault().post(new WhiteAppBackEvent(), "whitephoness");
            return;
        }
        Log.e("--------服务器返回黑白名单号码：",cmdData.getReturnData());//[{"a":"哈哈","b":"15528167","c":"1"}]
        if (cmdData.getIsOpen().equals("0")) {
            List<PhoneDatas> whiteAppList = new Gson().fromJson(cmdData.getReturnData(), new TypeToken<List<PhoneDatas>>() {}.getType());
            PhoneDataDao dao = GreenDaoManager.getInstance().getSession().getPhoneDataDao();
            List<PhoneData> list = dao.queryBuilder().where(PhoneDataDao.Properties.Childuuid.eq(cmdData.getChildUUID())).build().list();
            if(whiteAppList==null){
                dao.deleteAll();
                EventBus.getDefault().post(new WhitePhoneListEvent(whiteAppList), "whitePhoneData");
                return;
            }
            if (whiteAppList.size() >= 0) {
                if (list.size() > 0) {//修改
                    for (int i = 0; i < list.size(); i++) {
                        dao.delete(list.get(i));
                    }
                }
                for (int i = 0; i < whiteAppList.size(); i++) {
                    PhoneData appDataBean = new PhoneData(null, cmdData.getChildUUID(), whiteAppList.get(i).getA(),  whiteAppList.get(i).getB() , whiteAppList.get(i).getC() );
                    dao.insert(appDataBean);
                }
            } else {
                LogUtil.e(TAG, "手机号码获取失败");
            }
            EventBus.getDefault().post(new WhitePhoneListEvent(whiteAppList), "whitePhoneData");
        }
    }
    /**
     * 修改号码黑白名单
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "updatePhonedata")
    public void updatePhonedata(RequestWhiteAppListEvent event) {
        CmdData whiteAppJson = new CmdData();
        whiteAppJson.setOrder(CmdCommon.CMD_WHITE_PHONE);
        whiteAppJson.setChildUUID(event.childUUID);
        whiteAppJson.setIsOpen("5");
        appchilduuid = event.childUUID;
        whiteAppJson.setParentUUID(event.parentUUID);
        PhoneDataDao dao = GreenDaoManager.getInstance().getSession().getPhoneDataDao();
        List<PhoneData> list = dao.queryBuilder().where(PhoneDataDao.Properties.Childuuid.eq(event.childUUID)).build().list();
        List<PhoneDatas> mlist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PhoneDatas data = new PhoneDatas();
            data.setA(list.get(i).getName());
            data.setB(list.get(i).getPhone());
            data.setC(list.get(i).getState());
            mlist.add(data);
        }

        String appList = new Gson().toJson(mlist);
        appdatas = MyUtils.spliteData(appList);
        LogUtil.e(TAG, "发送号码黑白名单11" +mlist.size());
        if (appdatas.size() == 1) {
            whiteAppJson.setReturnData(appdatas.get(0));
            appdatas.remove(0);
            whiteAppJson.setIsOpen(CmdCommon.CMD_DATA_ALL);

            LogUtil.e(TAG, "发送号码黑白名单22" + new Gson().toJson(whiteAppJson));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(whiteAppJson));
            }else{
                socketThread.sMessage(new Gson().toJson(whiteAppJson));
            }
        } else {
            whiteAppJson.setIsOpen(CmdCommon.CMD_DATA_START);
            whiteAppJson.setReturnData(appdatas.get(0));
            appdatas.remove(0);
            LogUtil.e(TAG, "发送号码黑白名单222" + new Gson().toJson(whiteAppJson));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(whiteAppJson));
            }else{
                socketThread.sMessage(new Gson().toJson(whiteAppJson));
            }
        }
    }

    /**
     * 发送禁用名单APP数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestStopAppList")
    public void StopAppListEvent(RequestStopAppListEvent event) {
        CmdData whiteAppJson = new CmdData();
        whiteAppJson.setOrder(CmdCommon.CMD_STOP_APP_LIST);
        whiteAppJson.setChildUUID(event.childUUID);
        whiteAppJson.setIsOpen("4");
        appchilduuid = event.childUUID;
        whiteAppJson.setParentUUID(event.parentUUID);
        LogUtil.e(TAG, "禁用名单APP向服务器发送的数据2" + new Gson().toJson(whiteAppJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(whiteAppJson));
        }else{
            socketThread.sMessage(new Gson().toJson(whiteAppJson));
        }
    }
    /**
     * 处理禁用名单APP数据
     * */
    private void handleStopAppList(CmdData cmdData) {
        LogUtil.e(TAG, "服务返回禁用名单APP："+ cmdData.getReturnData());
        if (cmdData.getIsOpen().equals("0") ) {
            EventBus.getDefault().post(new RequestStopAppListEvent(null,null,null, cmdData.getReturnData()), "appStopMinewData");
        }else if(cmdData.getIsOpen().equals("1")){
            EventBus.getDefault().post(new RequestStopAppListEvent(null,null,null, cmdData.getIsOpen()), "appSubmitMinewData");
        }
    }

    /**
     * 提交禁用名单APP数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestSubmitAppList")
    public void requestWhiteAppListEvent(RequestStopAppListEvent event) {
        CmdData whiteAppJson = new CmdData();
        whiteAppJson.setOrder(CmdCommon.CMD_STOP_APP_LIST);
        whiteAppJson.setChildUUID(event.childUUID);
        whiteAppJson.setIsOpen("5");
        appchilduuid = event.childUUID;
        whiteAppJson.setParentUUID(event.parentUUID);
        List<AppData> mlist = new ArrayList<>();
        if(event.list==null || event.list.size()==0){
            whiteAppJson.setReturnData("");
            LogUtil.e(TAG, "禁用名单APP向服务器修改的数据0" +new Gson().toJson(whiteAppJson));
        }else {
            mlist=event.list;
            String appList = new Gson().toJson(mlist);
            appdatas = MyUtils.spliteData(appList);
            if (appdatas.size() == 1) {
                whiteAppJson.setReturnData(appdatas.get(0));
                appdatas.remove(0);
                whiteAppJson.setIsOpen(CmdCommon.CMD_DATA_ALL);
                LogUtil.e(TAG, "禁用名单APP向服务器修改的数据1" + new Gson().toJson(whiteAppJson));
            } else {
                whiteAppJson.setIsOpen(CmdCommon.CMD_DATA_START);
                whiteAppJson.setReturnData(appdatas.get(0));
                appdatas.remove(0);
                LogUtil.e(TAG, "禁用名单APP向服务器修改的数据2" + new Gson().toJson(whiteAppJson));
            }
        }
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(whiteAppJson));
        }else{
            socketThread.sMessage(new Gson().toJson(whiteAppJson));
        }
    }

    /**
     * 修改 添加 删除团控
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "TeamInternetPlan")
    public void TeamInternetPlanEvent(TeamInternetPlanEvent sipe) {
        CmdData stopPlanJson = new CmdData();
        stopPlanJson.setOrder(CmdCommon.CMD_TEAM_CONTROLL);
        stopPlanJson.setChildUUID(sipe.childUUID);
        planchildUUID = sipe.childUUID;
        stopPlanJson.setParentUUID(sipe.parentUUID);
        List<NetPlanData> mlist =sipe.planlist;
        if (mlist == null) {
            stopPlanJson.setIsOpen(CmdCommon.CMD_DATA_ALL);
            stopPlanJson.setReturnData(new Gson().toJson(""));
            if(Constants.IsUseMinaSocket){
                minaThread.sMessage(new Gson().toJson(stopPlanJson));
            }else{
                socketThread.sMessage(new Gson().toJson(stopPlanJson));
            }
        } else {
            String appList = new Gson().toJson(mlist);
            netPlandatas = MyUtils.spliteData(appList);
            if (netPlandatas.size() == 1) {
                stopPlanJson.setReturnData(netPlandatas.get(0));
                netPlandatas.remove(0);
                stopPlanJson.setIsOpen(CmdCommon.CMD_DATA_ALL);
                LogUtil.e(TAG, "团控发送的数据" + new Gson().toJson(stopPlanJson));
                //minaThread.sMessage(new Gson().toJson(stopPlanJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(stopPlanJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(stopPlanJson));
                }
            } else {
                stopPlanJson.setIsOpen(CmdCommon.CMD_DATA_START);
                stopPlanJson.setReturnData(netPlandatas.get(0));
                netPlandatas.remove(0);
                LogUtil.e(TAG, "团控发送的数据" + new Gson().toJson(stopPlanJson));
                //minaThread.sMessage(new Gson().toJson(stopPlanJson));
                if(Constants.IsUseMinaSocket){
                    minaThread.sMessage(new Gson().toJson(stopPlanJson));
                }else{
                    socketThread.sMessage(new Gson().toJson(stopPlanJson));
                }
            }
        }
    }

    /**
     * 发出团控数据数据
     */

    @Subscriber(mode = ThreadMode.ASYNC, tag = "requestTeamAppList")
    public void StopAppListEvent(TeamInternetPlanEvent event) {
        CmdData whiteAppJson = new CmdData();
        whiteAppJson.setOrder(CmdCommon.CMD_TEAM_CONTROLL);
        whiteAppJson.setIsOpen("4");
        appchilduuid = event.childUUID;
        whiteAppJson.setParentUUID(event.parentUUID);
        LogUtil.e(TAG, "手机端请求团控数据" + new Gson().toJson(whiteAppJson));
        if(Constants.IsUseMinaSocket){
            minaThread.sMessage(new Gson().toJson(whiteAppJson));
        }else{
            socketThread.sMessage(new Gson().toJson(whiteAppJson));
        }
    }

    /**
     * 处理团控数据数据
     */
    private void handleTeamAppList(CmdData cmdData) {
        LogUtil.e(TAG, "服务器返回团控数据："+ cmdData.getReturnData());
        if (cmdData.getIsOpen().equals("0") ) {   //请求团控的数据
            EventBus.getDefault().post(new TeamInternetPlanEvent(null,null, cmdData.getReturnData(),null), "TeamControllData");
        }else if(cmdData.getIsOpen().equals("1")){   //修改团控的回包
            EventBus.getDefault().post(new TeamInternetPlanEvent(null,null,null,null), "TeamControllSubmit");
        }
    }


//    /**
//     * 离线消息
//     */
//    public void OfflinListEvent() {
//        CmdData whiteAppJson = new CmdData();
//        whiteAppJson.setOrder(CmdCommon.CMD_OFF_LINE);
//        parentUUID = sp.getString("parentUUID", "");
//        whiteAppJson.setParentUUID(parentUUID);
//        final MessageBeanDao dao = GreenDaoManager.getInstance().getSession().getMessageBeanDao();
//        List<MessageBean> list = dao.queryBuilder().build().list();
//        List<MessageBean> mlist = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            MessageBean data = new MessageBean();
//            if(!TextUtils.isEmpty(list.get(i).getB())){
//                int b1 = Integer.parseInt(list.get(i).getB());
//                int b2=b1-10000000;
//                data.setB(b2+"");
//                data.setD(list.get(i).getD());
//                data.setG(list.get(i).getG());
//                mlist.add(data);
//            }
//        }
//        String appList = new Gson().toJson(mlist);
//        appdatas = MyUtils.spliteData(appList);
//        whiteAppJson.setReturnData(appdatas.get(0));
//        String  ss=appdatas.get(0);
//        LogUtil.e(TAG, "parentUUID:"+parentUUID+"发送离线消息1111:" + appdatas.get(0));
//        appdatas.remove(0);
//        if(TextUtils.isEmpty(ss)){
//            return;
//        }
//        LogUtil.e(TAG, "parentUUID:"+parentUUID+"发送离线消息2222" + new Gson().toJson(whiteAppJson));
//
//        if(TextUtils.isEmpty(parentUUID)){
//            return;
//        }
//        LogUtil.e(TAG, "parentUUID:"+parentUUID+"发送离线消息3333" + new Gson().toJson(whiteAppJson));
//        if(Constants.IsUseMinaSocket){
//            minaThread.sMessage(new Gson().toJson(whiteAppJson));
//        }else{
//            socketThread.sMessage(new Gson().toJson(whiteAppJson));
//        }
//    }
//    /**
//     * 清空离线消息
//     */
//    private void ClearMessage() {
//        final MessageBeanDao dao = GreenDaoManager.getInstance().getSession().getMessageBeanDao();
//        if(dao!=null){
//            dao.deleteAll();
//        }
//    }

}
