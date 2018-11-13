package com.hzkc.parent.solider.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.UpDownMsgInfo;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.adapter.MyFragmentPagerAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.ChildUpEvent;
import com.hzkc.parent.fragment.information.InformationFragment;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.service.MianService;
import com.hzkc.parent.solider.fragment.InternetsFragment;
import com.hzkc.parent.solider.fragment.MinesFragment;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.view.NoPreloadViewPager;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainSoldActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    private NoPreloadViewPager vpMain;
    private RadioGroup rbMain;
    private RadioButton rbTab1Internet;
    private RadioButton rbTab2Find;
    private RadioButton rbTab3Mine;

    private LinearLayout llGuide;
    private ImageView ivGuide;
    private long exitTime = 0;
    private static final String TAG = "MainActivity";
    private boolean initFlag;
    private UpDownMsgInfo info1;
    private String childVersion;
    private String userid;
    private String phone;

    /**
     * 未读消息
     */
    private TextView unReadMsg;
    private int currentTabIndex;
    //==========环信变量start==========
    /**
     * 是否显示错误提示的dialog,下线，被移除等。
     */
    private boolean isExceptionDialogShow = false;
    /**
     * 展示错误的dialog
     */
    private AlertDialog.Builder exceptionBuilder;
    /**
     * 用户登录到另一个设备
     */
    public boolean isConflict = false;
    /**
     * 用户帐户被删除
     */
    private boolean isCurrentAccountRemoved = false;
    /**
     * 邀请信息
     */
    private LocalBroadcastManager broadcastManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blues));
        Volley.newRequestQueue(this);
        initFlag=true;
        userid = sp.getString("parentUUID", "");
        phone = sp.getString("phoneNum", "");
        //初始化控件
        initView();
        //RadioGroup选中状态改变监听
        radioGroupChange();
        //孩子端的版本情况
        //checkChildVersion();
        //发送通讯录
        //sendConnect();
        //获取权限
        if(!AppUtils.isCameraCanUse()){
            requestPermissions();
        }
    }
    /**
     * 发送通讯录
     * */
    private void sendConnect() {
        String url2 = Constants.FIND_URL_API_ADDUSER+"userid="+userid+"&phone="+phone+"&txid="+phone.substring(phone.length()-2);
        LogUtil.e(url2);
        //通讯数据库中添加数据
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("通讯数据库", "Response:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("通讯数据库", "error:" + error);
            }
        });
        //设置超时时间
        stringRequest2.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(stringRequest2);

//        StringRequest postsr = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                Log.i(TAG, "onResponse: 成功了");
//                Toast.makeText(MainSoldActivity.this, "volleyPostStringMonth请求成功：" + s, Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Log.i(TAG, "onErrorResponse: 失败了");
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("type", "news");
//                map.put("nums", "20");
//                return map;
//            }
//        };

    }

    /**
     * 初始化控件
     */
    private void initView() {
        vpMain = (NoPreloadViewPager) findViewById(R.id.vp_main);
        rbMain = (RadioGroup) findViewById(R.id.rg_main);
        rbTab1Internet = (RadioButton) findViewById(R.id.rb_tab1_internet);
        rbTab2Find = (RadioButton) findViewById(R.id.rb_tab2_find);
        rbTab3Mine = (RadioButton) findViewById(R.id.rb_tab3_mine);
        unReadMsg = (TextView) findViewById(R.id.im_main_unread_msg);
        llGuide = (LinearLayout) findViewById(R.id.ll_guide);
        ivGuide = (ImageView) findViewById(R.id.iv_guide);



        ivGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llGuide.setVisibility(View.GONE);
                sp.edit().putString("guide","guide").commit();
            }
        });
        //检测引导界面
        checkGuide();
        childVersion = sp.getString("childVersion", "");
        Log.e(TAG, "initView: "+childVersion);
        //初始化fragment
        InternetsFragment InternetsFragment = new InternetsFragment();
//        FindFragment findFragment = new FindFragment();
        MinesFragment minesFragment = new MinesFragment();
        InformationFragment informationFragment=new InformationFragment();
        //初始化fragment集合
        List<Fragment> listFragment = new ArrayList();
        listFragment.add(InternetsFragment);
//        listFragment.add(findFragment);
        listFragment.add(informationFragment);
        listFragment.add(minesFragment);
        vpMain.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), listFragment));
        vpMain.setCurrentItem(0);
    }
    /**
     * 检测引导界面
     * */
    private void checkGuide() {
        String guide = sp.getString("guide", "");
//        if(TextUtils.isEmpty(guide)){
//           llGuide.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * RadioGroup选中状态改变监听
     */
    private void radioGroupChange() {
        rbMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_tab1_internet:
                        /**
                         * setCurrentItem第二个参数控制页面切换动画
                         * true:打开/false:关闭
                         */
                        currentTabIndex = 0;
                        vpMain.setCurrentItem(0, false);
                        break;
//                    case R.id.rb_tab2_find:
//                        currentTabIndex = 1;
//                        vpMain.setCurrentItem(1, false);
//                        break;
                    case R.id.rb_tab3_news:
                        currentTabIndex = 1;
                        vpMain.setCurrentItem(1, false);
                        break;
                    case R.id.rb_tab3_mine:
                        currentTabIndex = 2;
                        vpMain.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
    /**
     *判断是否有smartbar
     * */
    protected boolean hasSmartBar() {
        try {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod(
                    "hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {

        }
        // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }

    /**
     * 孩子端的版本情况
     * */
    private void checkChildVersion() {
        //孩子端的版本情况
        String url1 = Constants.FIND_URL_API_CHILD_UP;
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.e("孩子端的版本情况", "updateDatas:" + response);
                        info1 = new Gson().fromJson(response, UpDownMsgInfo.class);
                        LogUtil.e("孩子端的版本情况", "childVersion:" + childVersion);
                        if(TextUtils.isEmpty(childVersion)){
                            return;
                        }
                        String[] split = childVersion.split(";");
                        for (int i = 0; i < split.length; i++) {
                            if(!split[i].contains(info1.getVersion())){
                                showChildUpdataDialog();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.e("孩子端的版本情况", "updateDatas:" + error);
            }
        });
        MyApplication.getRequestQueue().add(stringRequest1);
    }
    /**
     * 孩子端升级对话框
     * */
    protected void showChildUpdataDialog() {
        AlertDialog.Builder builer = new AlertDialog.Builder(this) ;
        builer.setTitle("孩子端发现新版本");
        builer.setMessage(info1.getDescription());
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String[] split = childVersion.split(";");
                for (int i = 0; i < split.length; i++) {
                    if(!split[i].contains(info1.getVersion())){
                        String childuuid= split[i].split("#")[0];
                        LogUtil.e("孩子端升级对话框","发现版本升级"+childuuid);
                        EventBus.getDefault().post(new ChildUpEvent(childuuid), "requestChildUp");
                    }
                }
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }
    //================================== 开始配置环信 ==================================


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    /**
     * 退出前的操作
     * */
    private void tuichu() {
        sp.edit().putString("childName", "")
                .putString("ChildUUID", "")
                .putString("childSex", "")
                .putString("token", "")
                .putString("loginImFlag", "")
                .putString("childVersion", "")
                .putString("loginImFlag","")
                .putString("headimage","")    //清空本地头像，昵称，版本，推送id值，加密token,
                .putString("nc", "").commit();
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildContrlFlagDao childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        AppDataBeanDao appdao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        NetPlanDataBeanDao netPlanDao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
        netPlanDao.deleteAll();
        childDao.deleteAll();
        childCopntrlDao.deleteAll();
        appdao.deleteAll();
        if(MianService.minaThread!=null){
            MianService.minaThread.disConnect();
        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exceptionBuilder != null) {
            exceptionBuilder.create().dismiss();
            exceptionBuilder = null;
            isExceptionDialogShow = false;
        }
        //注销DemoHelper监听广播
//        broadcastManager.unregisterReceiver(broadcastReceiver);
    }

    //================================== 结束配置环信 ==================================

    /**
     * 获取权限
     * */
    private void requestPermissions() {
        //要获取的权限
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SYSTEM_ALERT_WINDOW};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Have permissions, do the thing!
            LogUtil.e("已获取权限");
        } else {
            EasyPermissions.requestPermissions(this, "应用运行必要的权限", 0, perms);
        }
    }
    /**
     *
     * 失败
     * */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }
    /**
     * 成功
     * */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
