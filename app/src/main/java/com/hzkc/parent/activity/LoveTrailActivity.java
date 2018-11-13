package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.Bean.LoveTrailBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyLoveTrailListviewAdapter;
import com.hzkc.parent.event.ChildLocationPathDataEvent;
import com.hzkc.parent.event.RequestChildLoactionEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.entity.LoveTrailData;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.greendao.gen.LoveTrailDataDao;
import com.hzkc.parent.jsondata.LocationData;
import com.hzkc.parent.utils.GlideCircleTransform;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.MyThread2;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoveTrailActivity extends BaseActivity implements View.OnClickListener{
    private TextView tv_numb;
    private TextView tv_lastadress;
    private TextView tvTopTitle;
    private TextView tvName;
    private TextView tvTime;
    private ImageView ivFinish,iv_zj;
    private ImageView ivIcon;
    private ListView lvLoveTrail;
    private String childUUID;
    private String parentUUID;
    private List<LoveTrailBean> mlist;
    private MyLoveTrailListviewAdapter adapter;
    private SimpleDateFormat formatter;
    private boolean regFlag;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 301:
                    mlist.clear();
                    List<LoveTrailBean> dataList = (List<LoveTrailBean>) msg.obj;
                    if(dataList.size()==0){
//                        toast("暂无数据");
                        getLocialDao();
                        return;
                    }else{
                        mlist.addAll(dataList);
                    }
                    if(mlist!=null && mlist.size()>0){
                        tv_numb.setText(mlist.size()+"");
                        tv_lastadress.setText(mlist.get(0).getAddress());
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case 106:
//                    if (!regFlag) {
//                        llDownload.setVisibility(View.GONE);
//                        toast("网络连接不通畅,请稍后再试");
//                    }
                    if (!regFlag) {
                        llDownload.setVisibility(View.GONE);
                        getLocialDao();
                    }
                    break;
                default:
                    break;
            }
        }
    };



    private Thread thread;
    private String childName;
    private String childSex;
    private LinearLayout llDownload;
    private Runnable runnable;
//[{"a":"30.558472974513037,104.07155509492686","b":"四川省成都市武侯区吉庆四路14号靠近成都银行(世纪城支行)","c":"1531379407064"},{"a":"30.558485377072,104.07184642996368","b":"四川省成都市武侯区吉庆四路14号靠近重庆银行(成都分行)","c":"1531379647247"}]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_love_trail_new);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        initView();
        initData();
        freshChildLocation();
        regFlag = false;
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                handler.sendMessage(msg);
            }
        };
        handler.postDelayed(runnable, 10000);
    }
    /**
     * 吐司的一个小方法
     */
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showToast(LoveTrailActivity.this, str);
            }
        });
    }
    private ChildsTableDao childDao;
    private ChildContrlFlagDao childCopntrlDao;
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tv_numb = (TextView) findViewById(R.id.tv_gj_num);
        tv_lastadress = (TextView) findViewById(R.id.tv_last_wz);
        tvName = (TextView) findViewById(R.id.tv_love_child_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        iv_zj = (ImageView) findViewById(R.id.iv_zj);
        ivIcon = (ImageView) findViewById(R.id.iv_child_icon);
        llDownload = (LinearLayout) findViewById(R.id.ll_download);
        lvLoveTrail = (ListView) findViewById(R.id.lv_love_trail);
        childUUID = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        childName = sp.getString("childName", "");
        childSex = sp.getString("childSex", "");

        tvName.setText(childName);


        childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        childCopntrlDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(childUUID)).build().unique();
        if (findChild != null) {
            if (findChild.getImageurl() != null) {        //头像初始化
                Glide.with(this).load(findChild.getImageurl()).transform(new GlideCircleTransform(this)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.mine_head__01).into(ivIcon);
            }
        }
        tvTopTitle.setText("运动轨迹");
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date();
        String time = df.format(date);
        //tvTime.setText(time+" 到过的地方");
        tvTime.setText("最近"+"到过的地方");

        ivFinish.setVisibility(View.VISIBLE);
        llDownload.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        iv_zj.setOnClickListener(this);

        mlist=new ArrayList<>();
        //初始化Formatter的转换格式。
    }

    private void initData() {
        if(mlist!=null && mlist.size()>0){
            tv_numb.setText(mlist.size()+"");
            tv_lastadress.setText(mlist.get(0).getAddress());
        }
        adapter = new MyLoveTrailListviewAdapter(mlist, this);
        lvLoveTrail.setAdapter(adapter);
    }
    /**
     * 联网更新数据
     * */
    private void freshChildLocation() {
        EventBus.getDefault().post(new RequestChildLoactionEvent(childUUID,parentUUID),"requestLoactionPath");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.iv_zj:
                Intent intent3 = new Intent(LoveTrailActivity.this, LocationHistoryActivity.class);
                intent3.putExtra("ChildUUID", childUUID);
                startActivity(intent3);
                break;
            default:
                break;
        }
    }
    /**
     * 处理位置数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "LoactionPathData")
    public void RequestLocationEvent(ChildLocationPathDataEvent dataEvent) {
        regFlag = true;
        handler.removeCallbacks(runnable);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llDownload.setVisibility(View.GONE);
            }
        });
        String getData = sp.getString(childUUID + "LocationPath", "");
        if(dataEvent.locationDataList==null ){
            Toast.makeText(this, "暂无运动轨迹数据", Toast.LENGTH_SHORT).show();
            return;
        }
        List<LocationData> locationDataLists = new Gson().fromJson(getData, new TypeToken<List<LocationData>>() {
        }.getType());
        List<LocationData> locationDataList=new ArrayList<>();
        //有数据时保存数据库
        if(locationDataLists.size()>0){
            List<LoveTrailData> datas=new ArrayList<>();
            for (int i = 0; i < locationDataLists.size(); i++) {
                if(TextUtils.isEmpty(locationDataLists.get(i).a)){
                    continue;
                }
                LoveTrailData loveTrailData=new LoveTrailData(null,locationDataLists.get(i).a,locationDataLists.get(i).b,locationDataLists.get(i).c,childUUID); //保存用的数据
                LocationData locationData=new LocationData(locationDataLists.get(i).a,locationDataLists.get(i).b,locationDataLists.get(i).c);   //显示用的数据
                datas.add(loveTrailData);
                locationDataList.add(locationData);
            }
            List<LocationData> locationDataList11 = locationDataList;
            addDao(datas);
        }

        MyThread2 td = new MyThread2(locationDataList.size()-1, this, locationDataList);
        td.reflushPart(new MyThread2.RefleshListener() {
            @Override
            public void sendData(List<LoveTrailBean> mlist) {
                LogUtil.i(TAG, "sendData() returned: " + mlist.size());
                Message msg = Message.obtain();
                msg.obj=mlist;
                msg.what=301;
                handler.sendMessage(msg);
            }
        });
        thread = new Thread(td);
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(thread!=null){
            thread.interrupt();
        }
        EventBus.getDefault().unregister(this);
    }
    //加入數據庫
    private void addDao(List<LoveTrailData> dataList) {
        LoveTrailDataDao dao = GreenDaoManager.getInstance().getSession().getLoveTrailDataDao();
        List<LoveTrailData> list = dao.queryBuilder().where(LoveTrailDataDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (list.size() > 0) {//修改
            for (int i = 0; i < list.size(); i++) {
                dao.delete(list.get(i));
            }
        }
        for (int i = 0; i < dataList.size(); i++) {
            dao.insert(dataList.get(i));
        }
    }
    //本地數據庫获取
    private void   getLocialDao() {
        LoveTrailDataDao dao = GreenDaoManager.getInstance().getSession().getLoveTrailDataDao();
        List<LoveTrailData> list = dao.queryBuilder().where(LoveTrailDataDao.Properties.Childuuid.eq(childUUID)).build().list();
        if(list==null || list.size()==0){
            Toast.makeText(LoveTrailActivity.this,"暂无运动轨迹数据",Toast.LENGTH_SHORT).show();
            return;
        }
        List<LocationData> datas=new ArrayList<>();
        if(list!=null &&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                LocationData data=new LocationData(list.get(i).getA(),list.get(i).getB(),list.get(i).getC());
                datas.add(data);
            }
        }
        MyThread2 td = new MyThread2(datas.size()-1, this, datas);
        td.reflushPart(new MyThread2.RefleshListener() {
            @Override
            public void sendData(List<LoveTrailBean> mlist) {
                LogUtil.i(TAG, "sendData() returned: " + mlist.size());
                Message msg = Message.obtain();
                msg.obj=mlist;
                msg.what=301;
                handler.sendMessage(msg);
            }
        });
        thread = new Thread(td);
        thread.start();
    }
}
