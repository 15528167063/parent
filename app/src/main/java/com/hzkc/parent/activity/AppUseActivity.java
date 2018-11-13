package com.hzkc.parent.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hzkc.parent.Bean.UseAppDataBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyUseAppAdapter;
import com.hzkc.parent.event.AppUseDataEvent;
import com.hzkc.parent.event.RequestAppUseTimeEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppUseBean;
import com.hzkc.parent.greendao.gen.AppUseBeanDao;
import com.hzkc.parent.jsondata.AppUsageData;
import com.hzkc.parent.utils.AppUseTimeThread;
import com.hzkc.parent.utils.LogUtil;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppUseActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private ListView lvAppUse;
    private LinearLayout llLoad;
    private List<UseAppDataBean> applist;
    private List<UseAppDataBean> datas=new ArrayList<>();
    private List<String> dataApks=new ArrayList<>();
    private MyUseAppAdapter adapter;
    private String childUUID,parentUUID;
    private LinearLayout flKong,lin_hasdata;
    private boolean regFlag;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 301:
                    applist.clear();
                    List<UseAppDataBean> dataList = (List<UseAppDataBean>) msg.obj;
                    LogUtil.e(TAG, "handleMessage: 22"+dataList.size());
                    if(dataList.size()==0){
                        flKong.setVisibility(View.VISIBLE);
                    }else if(dataList.size()==1){
                        flKong.setVisibility(View.GONE);
                        applist.addAll(dataList);
                    }else{
                        flKong.setVisibility(View.GONE);
                        datas.clear();
                        dataApks.clear();
                        for (int i = 0; i < dataList.size(); i++) {
                            if(!dataApks.contains(dataList.get(i).getPackegname())){   //没有
                                dataApks.add(dataList.get(i).getPackegname());
                                datas.add(new UseAppDataBean(dataList.get(i).getAppname(),dataList.get(i).getUseTime(),dataList.get(i).getTimePercent(),dataList.get(i).getPackegname(),1));
                            }else {
                                int a=0;
                                for (int j = 0; j <dataApks.size() ; j++) {
                                    if(dataApks.get(j).equals(dataList.get(i).getPackegname())){
                                        a=j;
                                    }
                                }
                                UseAppDataBean useAppDataBean = datas.get(a);
                                String useTime = useAppDataBean.getUseTime();
                                datas.remove(a);
                                int i1 = Integer.parseInt(useTime)+Integer.parseInt(dataList.get(i).getUseTime());
                                int number = useAppDataBean.getNumber()+1;
                                UseAppDataBean useAppDataBeans=new UseAppDataBean(useAppDataBean.getAppname(),i1+"","",useAppDataBean.getPackegname(),number);
                                datas.add(a,useAppDataBeans);
                            }
                        }


                        /**
                         *把list根据时间大排序排序
                         * */
                        try {
                            Collections.sort(datas, new Comparator<UseAppDataBean>(){

                                /*
                                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                                 * 返回负数表示：o1 小于o2，
                                 * 返回0 表示：o1和o2相等，
                                 * 返回正数表示：o1大于o2。
                                 */
                                public int compare(UseAppDataBean o1, UseAppDataBean o2) {
                                    if(Integer.parseInt(o1.getUseTime()) < Integer.parseInt(o2.getUseTime())){
                                        return 1;
                                    }
                                    if(Integer.parseInt(o1.getUseTime()) == Integer.parseInt(o2.getUseTime())){
                                        return 0;
                                    }
                                    return -1;
                                }
                            });
                        }catch (Exception e){
                            LogUtil.e("AppUseActivity","111111");
                            e.printStackTrace();
                        }
                        applist.addAll(datas);
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 106:
                    if (!regFlag) {
                        llLoad.setVisibility(View.GONE);
//                        ToastUtils.showToast(AppUseActivity.this,"获取孩子应用使用数据失败，请稍后再试");
                        getDataFromDb();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_use);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initData();
        freshUseTime();
        regFlag=false;
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                handler.sendMessage(msg);
            }
        };
        handler.postDelayed(runnable, 30000);
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        lvAppUse = (ListView) findViewById(R.id.lv_app_use);
        llLoad = (LinearLayout) findViewById(R.id.ll_load);

        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);
        flKong.setVisibility(View.GONE);

        childUUID = getIntent().getStringExtra("ChildUUID");
        tvTopTitle.setText("应用统计");
        ivFinish.setVisibility(View.VISIBLE);
        llLoad.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
    }

    private void initData() {
        parentUUID = sp.getString("parentUUID", "");
        applist=new ArrayList<>();
        adapter = new MyUseAppAdapter(applist, AppUseActivity.this,parentUUID,childUUID);
        lvAppUse.setAdapter(adapter);
    }
    /**
     * 联网更新数据
     * */
    private void freshUseTime() {
        EventBus.getDefault().post(new RequestAppUseTimeEvent(childUUID),"requestAppUseTime");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            default:
                break;
        }
    }
    /**
     * 处理App使用数据{"a":"2","b":"10004835","c":"447","d":"17","e":"[{\"a\":\"com.gionee.amisystem\",\"b\":\"804247\"},{\"a\":\"com.qihoo.appstore\",\"b\":\"42241\"},{\"a\":\"com.gionee.gnservice\",\"b\":\"1062\"},{\"a\":\"com.tencent.mtt\",\"b\":\"611626\"},{\"a\":\"com.hzkc.parent\",\"b\":\"4087\"}]","f":"5","g":"1529474762000"}
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "AppUseTimeData")
    public void AppUseDataEvent(AppUseDataEvent dataEvent) {
        LogUtil.e(TAG, "RequestLocationEvent: app使用的东西");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llLoad.setVisibility(View.GONE);
            }
        });
        regFlag=true;
        handler.removeCallbacks(runnable);
        if(dataEvent.datas==null){
            getDataFromDb();
            return;
        }
        List<AppUsageData> appuseDataList =dataEvent.datas;
        AppUseTimeThread td = new AppUseTimeThread(appuseDataList.size()-1, this, appuseDataList,childUUID);
        td.reflushPart(new AppUseTimeThread.RefleshListener() {

            @Override
            public void sendData(List<UseAppDataBean> mlist) {
                LogUtil.i(TAG, "sendData() returned: " + mlist.size());
                Message msg = Message.obtain();
                msg.obj=mlist;
                msg.what=301;
                handler.sendMessage(msg);
            }
        });
        new Thread(td).start();
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        regFlag=true;
        super.onDestroy();
    }

    /**
     *从本地获取应用追踪数据
     */
    private void getDataFromDb() {
        AppUseBeanDao dao = GreenDaoManager.getInstance().getSession().getAppUseBeanDao();
        List<AppUseBean> list = dao.queryBuilder().where(AppUseBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        if(list==null || list.size()==0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    flKong.setVisibility(View.VISIBLE);
                }
            });
            return;
        }
        List<AppUsageData> datas=new ArrayList<>();
        if(list!=null &&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                AppUsageData data=new AppUsageData(list.get(i).getA(),list.get(i).getB(),list.get(i).getC(),list.get(i).getD());
                datas.add(data);
            }
        }
        List<AppUsageData> appuseDataList =datas;
        AppUseTimeThread td = new AppUseTimeThread(appuseDataList.size()-1, this, appuseDataList,childUUID);
        td.reflushPart(new AppUseTimeThread.RefleshListener() {

            @Override
            public void sendData(List<UseAppDataBean> mlist) {
                LogUtil.i(TAG, "sendData() returned: " + mlist.size());
                Message msg = Message.obtain();
                msg.obj=mlist;
                msg.what=301;
                handler.sendMessage(msg);
            }
        });
        new Thread(td).start();
    }
}
