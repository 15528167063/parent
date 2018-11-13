package com.hzkc.parent;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hzkc.parent.Bean.AdRoot;
import com.hzkc.parent.Bean.UpDownMsgInfo;
import com.hzkc.parent.Bean.VipRootBean;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.adapter.ChooseFragmentsListAdapter;
import com.hzkc.parent.adapter.MyFragmentPagerAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.ChildUpEvent;
import com.hzkc.parent.event.RequestAppUseTimeEvent;
import com.hzkc.parent.fragment.InternetFragment;
import com.hzkc.parent.fragment.MineFragment;
import com.hzkc.parent.fragment.information.InformationFragment;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.AppUtils;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.NoPreloadViewPager;

import org.simple.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

import static com.hzkc.parent.R.id.left_drawer;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {
    private NoPreloadViewPager vpMain;
    private RadioGroup rbMain;
    private RadioButton rbTab1Internet;
    private RadioButton rbTab2Find;
    private RadioButton rbTab3Mine;

    private LinearLayout llGuide;
    private ImageView ivGuide,iv_middle;
    private ImageView iv_btn1;
    private ImageView iv_btn2;
    private ImageView iv_btn3;
    private ImageView iv_btn4;
    private RelativeLayout re_guide1;
    private RelativeLayout re_guide2;
    private RelativeLayout re_guide3;
    private RelativeLayout re_guide4;
    private long exitTime = 0;
    private static final String TAG = "MainActivity";
    private boolean initFlag;
    private UpDownMsgInfo info1;
    private String childVersion;
    private String userid,childuuid;
    private String phone;
    private static LinearLayout leftDrawer;
    /**
     * 未读消息
     */
    private int currentTabIndex;
    //==========环信变量start==========
    /**
     * 是否显示错误提示的dialog,下线，被移除等。
     */
    private boolean isExceptionDialogShow = false;
    /**
     * 展示错误的dialog
     */
    private android.app.AlertDialog.Builder exceptionBuilder;
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
    public  VipRootBean vipRoot; //保存一个vip信息
    private Handler han=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT);  //改为透明
            } catch (Exception e) {}
        }
        setContentView(R.layout.activity_main_new);
//        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));

        Volley.newRequestQueue(this);
        initFlag=true;
        userid = sp.getString("parentUUID", "");
        phone = sp.getString("phoneNum", "");
        childuuid = sp.getString("ChildUUID", "");
        //初始化控件
        initView();
        //注册监听用户是否成功注册到友盟推送
        //RadioGroup选中状态改变监听

        initData();
        radioGroupChange();
        //孩子端的版本情况
        //checkChildVersion();
        //发送通讯录
        //sendConnect();
        //获取权限
        if(!AppUtils.isCameraCanUse()){
            requestPermissions();
        }
        getAdvertertion();//获取首页广告保存本地
        boolean isfirstLogin = sp.getBoolean("isfirstLogin", true);//显示提示界面(只限第一次进入)
        if(isfirstLogin){
            re_guide1.setVisibility(View.VISIBLE);
            sp.edit().putBoolean("isfirstLogin", false).commit();
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
    }


    /**
     * 初始化控件
     */
    private static DrawerLayout drawerLayout;
    private static ListView lvMine;
    private  InternetFragment internetFragment;
    private void initView() {
        vpMain = (NoPreloadViewPager) findViewById(R.id.vp_main);
        rbMain = (RadioGroup) findViewById(R.id.rg_main);
        rbTab1Internet = (RadioButton) findViewById(R.id.rb_tab1_internet);
        rbTab2Find = (RadioButton) findViewById(R.id.rb_tab2_find);
        rbTab3Mine = (RadioButton) findViewById(R.id.rb_tab3_mine);
        llGuide = (LinearLayout) findViewById(R.id.ll_guide);
        ivGuide = (ImageView) findViewById(R.id.iv_guide);
        iv_middle = (ImageView) findViewById(R.id.iv_middle);
        lvMine = (ListView) findViewById(R.id.lv_mine);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (LinearLayout) findViewById(left_drawer);
        iv_btn1 = (ImageView) findViewById(R.id.iv_btn1);
        iv_btn2 = (ImageView) findViewById(R.id.iv_btn2);
        iv_btn3 = (ImageView) findViewById(R.id.iv_btn3);
        iv_btn4 = (ImageView) findViewById(R.id.iv_btn4);
        re_guide1 = (RelativeLayout) findViewById(R.id.re_guide1);
        re_guide2 = (RelativeLayout) findViewById(R.id.re_guide2);
        re_guide3 = (RelativeLayout) findViewById(R.id.re_guide3);
        re_guide4 = (RelativeLayout) findViewById(R.id.re_guide4);

        ivGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llGuide.setVisibility(View.GONE);
                sp.edit().putString("guide","guide").commit();
            }
        });
        iv_btn1.setOnClickListener(this);
        iv_btn2.setOnClickListener(this);
        iv_btn3.setOnClickListener(this);
        iv_btn4.setOnClickListener(this);

        //检测引导界面
        checkGuide();
        childVersion = sp.getString("childVersion", "");
        LogUtil.e(TAG, "initView: "+childVersion);
        //初始化fragment
        internetFragment = new InternetFragment();
//        FindFragment findFragment = new FindFragment();
        MineFragment mineFragment = new MineFragment();
        InformationFragment informationFragment=new InformationFragment();
        //初始化fragment集合
        List<Fragment> listFragment = new ArrayList();
//        listFragment.add(findFragment);
        listFragment.add(informationFragment);
        listFragment.add(internetFragment);
        listFragment.add(mineFragment);
        vpMain.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), listFragment));
        vpMain.setCurrentItem(1);
    }

    private List<ChildsTable> list;
    private ChooseFragmentsListAdapter adapter;
    public void initData() {
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();       //获取保存的小孩列表信息
        list = childDao.queryBuilder().build().list();
        if (list == null || list.size() == 0) {
            return;
        }
        adapter=new ChooseFragmentsListAdapter(list,this,childuuid);
        adapter.setListener(new ChooseFragmentsListAdapter.ChooseListener() {
            @Override
            public void chooseListener(int position) {
                chooseChild();
                drawerLayout.closeDrawers();
            }
        });
        lvMine.setAdapter(adapter);
        lvMine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChooseFragmentsListAdapter.ViewHolder holder = (ChooseFragmentsListAdapter.ViewHolder) view.getTag();
                Boolean flag = ChooseFragmentsListAdapter.getIsSelected().get((int)id);
                for (int i = 0; i < list.size(); i++) {
                    ChooseFragmentsListAdapter.getIsSelected().put(i, false);
                }
                ChooseFragmentsListAdapter.getIsSelected().put((int)id, true);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public  void SetChildInfor() {
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        list = childDao.queryBuilder().build().list();
        adapter.notifyDataSetChanged();
    }
    /**
     * 检测引导界面
     * */
    private void checkGuide() {
        String guide = sp.getString("guide", "");
    }
    /**
     * 打开侧滑菜单栏
     * */

    public static void open(){
        drawerLayout.openDrawer(leftDrawer);
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
                        iv_middle.setImageResource(R.drawable.sy_hh_off);
                        break;
//                    case R.id.rb_tab2_find:
//                        currentTabIndex = 1;
//                        vpMain.setCurrentItem(1, false);
//                        break;
                    case R.id.rb_tab3_news:
                        currentTabIndex = 1;
                        vpMain.setCurrentItem(1, false);
                        iv_middle.setImageResource(R.drawable.sy_hh_on);

                        break;
                    case R.id.rb_tab3_mine:
                        currentTabIndex = 2;
                        vpMain.setCurrentItem(2, false);
                        iv_middle.setImageResource(R.drawable.sy_hh_off);
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




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exceptionBuilder != null) {
            exceptionBuilder.create().dismiss();
            exceptionBuilder = null;
            isExceptionDialogShow = false;
        }
    }

    //================================== 结束配置环信 ==================================

    /**
     * 获取权限
     * */
    private void requestPermissions() {
        //要获取的权限
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.READ_CONTACTS};
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

    public Handler handler=new Handler();
    private void chooseChild() {
        ChildsTable student = null;
        HashMap<Integer, Boolean> map = adapter.getIsSelected();
        if (!(map + "").contains("true")) {
            ToastUtils.showToast(MyApplication.getContext(), "请选择孩子");
            return;
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (map.get(i) == null) {
                    return;
                }
                if (map.get(i)) {
                    student = (ChildsTable) adapter.getItem(i);
                    sp.edit().putString("ChildUUID", student.getChilduuid()).putString("childName", student.getName()).putString("childSex", student.getSex())
                    .putString("childFrom", student.getChildfrom()).putString("childztl", student.getChildfrom()).putString("childImage", student.getImageurl()).commit();
                    internetFragment.onResume();
                    final ChildsTable finalStudent = student;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            internetFragment.getDataFromDb(true);
                            EventBus.getDefault().post(new RequestAppUseTimeEvent(finalStudent.getChilduuid()),"requestAppUseTime");
                        }
                    },300);
                }
            }
        }
    }

    /**
     * 首页广告
     */
    private void getAdvertertion() {
        String url=Constants.PHP_URL+"v"+ AppUtils.getVerName()+"/bannerHome";
        Log.e("家长端广告信息", "url:" + url);
        StringRequest postsr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("家长端广告信息", "response:" + response);
                AdRoot adRoot =null;
                try{
                    adRoot= new Gson().fromJson(response, AdRoot.class);
                }catch (Exception e){
                    return;
                }
                if(!TextUtils.isEmpty(adRoot.getData().imgurl)){
                    sp.edit().putString("adversterurl", adRoot.getData().imgurl).commit();
                }else {
                    sp.edit().putString("adversterurl","").commit();
                }
                if(!TextUtils.isEmpty(adRoot.getData().tourl)){
                    sp.edit().putString("tourl", adRoot.getData().tourl).commit();
                }else {
                    sp.edit().putString("tourl","").commit();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("家长端广告信息", "error:" + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //创建一个集合，放的是keyvalue的key是参数名与value是参数值
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };
        postsr.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getRequestQueue().add(postsr);
    }


    public VipRootBean getVipRoot() {
        return vipRoot;
    }

    public void setVipRoot(VipRootBean vipRoot) {
        this.vipRoot = vipRoot;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_btn1:
                re_guide1.setVisibility(View.GONE);
                re_guide2.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_btn2:
                re_guide2.setVisibility(View.GONE);
                re_guide3.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_btn3:
                re_guide3.setVisibility(View.GONE);
                re_guide4.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_btn4:
                re_guide4.setVisibility(View.GONE);
                break;

        }

    }
}
