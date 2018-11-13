package com.hzkc.parent.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.R;
import com.hzkc.parent.event.ChildLocationPathDataEvent;
import com.hzkc.parent.event.RequestChildLoactionEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.jsondata.LocationData;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.LocationImageView;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class LocationHistoryActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private MapView bmapView;
    private BaiduMap baiduMap;
    private Marker moveMarker;
    private LinearLayout llWrite;
    private static final String TAG = "ChildLocationActivity";
    private String childUUID;
    private String parentUUID;
    private RelativeLayout re_chakna;
    private boolean regFlag;
    private LocationImageView   ivOvey;
    private ChildsTableDao childDao;
    private boolean isboy=false;
    private Runnable runnable;
    private OverlayOptions ooPolyline,option1;
    private  List<LatLng>  pointstwo = new ArrayList<>();

    private Handler hd=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 99:
                    llWrite.setVisibility(View.GONE);
                    break;
                case 106:
                    if (!regFlag) {
                        llWrite.setVisibility(View.GONE);
                        ToastUtils.showToast(LocationHistoryActivity.this,"定位失败，请稍后再试");
                    }
                    break;
                case 107:
                    llWrite.setVisibility(View.GONE);
                    ToastUtils.showToast(LocationHistoryActivity.this,"定位孩子失败，请稍后再试");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_locatline);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        initView();
        freshChildLocation();
        regFlag=false;
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                hd.sendMessage(msg);
            }
        };
        hd.postDelayed(runnable, 5000);
    }




    /**
     * 联网更新数据
     * */
    private void freshChildLocation() {
        sp.edit().putString("isstate","2").commit();
        EventBus.getDefault().post(new RequestChildLoactionEvent(childUUID,parentUUID),"requestLoactionPath");
        EventBus.getDefault().register(this);
    }

    /**
     * 处理位置数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "LoactionPathData2")
    public void RequestLocationEvent(ChildLocationPathDataEvent dataEvent) {
        hd.removeCallbacks(runnable);
        String getData = sp.getString(childUUID + "LocationPath", "");
        List<LocationData> locationDataList = new Gson().fromJson(getData, new TypeToken<List<LocationData>>() {}.getType());
        if(TextUtils.isEmpty(locationDataList.get(locationDataList.size()-1).c)){
            regFlag = true;
            Message msg = Message.obtain();
            msg.what = 107;
            hd.sendMessage(msg);
        }
        for (int i = 0; i <locationDataList.size() ; i++) {
            LogUtil.e("---a---->",locationDataList.get(i).a+"--b--"+locationDataList.get(i).b+"--c--"+locationDataList.get(i).c);
            String str=locationDataList.get(i).a;
            if(!TextUtils.isEmpty(str)){
                double v_1 = Double.parseDouble(str.substring(0, str.indexOf(",")));   //获取精度
                double v_2 = Double.parseDouble(str.substring(str.indexOf(",")+1, str.length())); //获取纬度
                pointstwo.add(new LatLng(v_1,v_2));
            }
        }
        openLocation(locationDataList.get(locationDataList.size()-1));
    }








    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);
        llWrite = (LinearLayout) findViewById(R.id.ll_load);
        bmapView = (MapView) findViewById(R.id.bmapView);
        re_chakna=(RelativeLayout)findViewById(R.id.re_chakan) ;
        childUUID = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(childUUID)).build().unique();
        if (findChild != null) {
            if (findChild.getSex().equals(CmdCommon.FLAG_BOY)) {
                isboy=true;
            } else if (findChild.getSex().equals(CmdCommon.FLAG_GIRL)) {
                isboy=false;
            }
        }

        baiduMap = bmapView.getMap();
        tvTopTitle.setText("今日足迹");
        //设置百度地图的缩放级别，范围为3~19，数值越大越精确
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));
        llWrite.setVisibility(View.VISIBLE);
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        re_chakna.setOnClickListener(this);
    }


    private boolean isStop=true;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
//            case R.id.iv_right:
//                if(isStop){
//                    iv_right.setImageResource(R.drawable.location_2);
//                    isStop=false;
//                    freshChildLocation();
//                }else {
//                    iv_right.setImageResource(R.drawable.location_1);
//                    isStop=true;
//                    baiduMap.clear();
//                }
//                break;
            case R.id.re_chakan:
                Intent intent3 = new Intent(LocationHistoryActivity.this, LoveTrailActivity.class);
                intent3.putExtra("ChildUUID", childUUID);
                startActivity(intent3);
                break;
            default:
                break;
        }
    }



    public void openLocation(LocationData location){
        String locationDetailAdress = location.getTime();

        regFlag=true;
        Message obtain = Message.obtain();
        obtain.obj=locationDetailAdress;
        obtain.what=99;
        hd.sendMessage(obtain);

        if(moveMarker!=null){
            moveMarker.remove();
        }
        LogUtil.i(TAG, "address:" + location.getTime() + " latitude:" + location.getLatitude() + " longitude:" + location.getLongitude() + "-----------------------");
        for (int i = 0; i <pointstwo.size() ; i++) {
            Log.e("pointtwo("+i+")",pointstwo.get(i).toString());
        }
        LatLng ll=pointstwo.get(pointstwo.size()-1);
        MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);



        //地图上轨迹起点标注
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.locationhistory_start);
        OverlayOptions option = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(bitmap).position(pointstwo.get(0)).zIndex(9);
        moveMarker = (Marker)baiduMap.addOverlay(option);

        //连线
        if(pointstwo.size()>=2){
            ooPolyline = new PolylineOptions().width(16).color(Color.BLUE).points(pointstwo);
            baiduMap.addOverlay(ooPolyline);
        }
        //地图上轨迹终点标注
        ivOvey=new LocationImageView(LocationHistoryActivity.this);
        ivOvey.setImage(isboy);
        BitmapDescriptor bitmap1 = BitmapDescriptorFactory.fromView(ivOvey);
        OverlayOptions option1 = new MarkerOptions().flat(true).anchor( 0.5f,1).icon(bitmap1)   //0.5f表示横坐标中心位置，1表示坐标完全在坐标点上面
                .position(pointstwo.get(pointstwo.size()-1))
                .zIndex(11);
        moveMarker = (Marker)baiduMap.addOverlay(option1);

    }



    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        regFlag=true;
        super.onDestroy();
    }
}
