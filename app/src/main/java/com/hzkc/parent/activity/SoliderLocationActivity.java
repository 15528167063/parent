package com.hzkc.parent.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.hzkc.parent.R;
import com.hzkc.parent.event.ChildLocationDataEvent;
import com.hzkc.parent.event.RequestChildLoactionEvent;
import com.hzkc.parent.jsondata.LocationData;
import com.hzkc.parent.utils.LogUtil;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;



public class SoliderLocationActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView tvDetailAddress;
    private ImageView ivFinish;
    private ImageView ivRefresh;
    private MapView bmapView;
    private BaiduMap baiduMap;
    private Marker moveMarker;
    private LinearLayout llWrite,noWrite;
    private static final String TAG = "ChildLocationActivity";
    private String childUUID;
    private String parentUUID;
    private boolean regFlag;
    private Handler hd=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 99:
                    llWrite.setVisibility(View.GONE);
                    tvDetailAddress.setText((String) msg.obj);
                    break;
                case 106:
                    if (!regFlag) {
                        llWrite.setVisibility(View.GONE);
                        noWrite.setVisibility(View.VISIBLE);
//                        ToastUtils.showToast(ChildLocationActivity.this,"定位失败，请稍后再试");
                    }
                    ivRefresh.setEnabled(true);
                    break;
                case 107:
                    llWrite.setVisibility(View.GONE);
                    noWrite.setVisibility(View.VISIBLE);
                    ivRefresh.setEnabled(true);
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
        setContentView(R.layout.activity_child_location);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blues));
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
        hd.postDelayed(runnable, 30000);
    }
    /**
     * 联网更新数据
     * */
    private void freshChildLocation() {
        EventBus.getDefault().post(new RequestChildLoactionEvent(childUUID,parentUUID),"requestLoaction");
        EventBus.getDefault().register(this);
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvDetailAddress= (TextView) findViewById(R.id.tv_detail_address);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        llWrite = (LinearLayout) findViewById(R.id.ll_load);
        noWrite  = (LinearLayout) findViewById(R.id.no_load);
        ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        bmapView = (MapView) findViewById(R.id.bmapView);

        childUUID = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");

        baiduMap = bmapView.getMap();
        String currentname = sp.getString("childName","士兵");
        tvTopTitle.setText(currentname+"的位置");
        //设置百度地图的缩放级别，范围为3~19，数值越大越精确
        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(
                new MapStatus.Builder().zoom(19).build()));

        llWrite.setVisibility(View.VISIBLE);
        ivFinish.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.GONE);
        ivFinish.setOnClickListener(this);
        ivRefresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.iv_refresh://刷新孩子
                regFlag=false;                          // {"a":"2","b":"","c":"","d":"23","e":"","f":"34","g":"1504943388468"}  登录
                llWrite.setVisibility(View.VISIBLE);/// {"a":"2","b":"","c":"","d":"28","e":"","f":"1","g":"1504943276000"}    上线下线
                EventBus.getDefault().post(new RequestChildLoactionEvent(childUUID,parentUUID),"requestLoaction");
                ivRefresh.setEnabled(false);
                hd.postDelayed(runnable, 5000);
                break;
            default:
                break;
        }
    }

    public void openLocation(LocationData location){
        String locationDetailAdress = location.getTime();
        LogUtil.i(TAG, "openLocation: "+locationDetailAdress);
        regFlag=true;

        Message obtain = Message.obtain();
        obtain.obj=locationDetailAdress;
        obtain.what=99;
        hd.sendMessage(obtain);

        if(moveMarker!=null){
            moveMarker.remove();
        }

        LatLng ll=new LatLng(Double.valueOf(location.getLatitude()),Double.valueOf(location.getLongitude()));
        LogUtil.i(TAG, "address:" + location.getTime() + " latitude:" + location.getLatitude()
                + " longitude:" + location.getLongitude() + "-----------------------");
        MapStatusUpdate update= MapStatusUpdateFactory.newLatLng(ll);
        baiduMap.animateMapStatus(update);
        //定义Maker坐标点
        //LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().flat(true).anchor(0.5f, 0.5f).icon(bitmap)
                .position(ll)
                .zIndex(9);
        //在地图上添加Marker，并显示
        moveMarker = (Marker)baiduMap.addOverlay(option);
        MyLocationData data = baiduMap.getLocationData();
    }
    /**
     * 请求位置数据
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "LoactionData")
    public void RequestLocationEvent(ChildLocationDataEvent dataEvent) {
        hd.removeCallbacks(runnable);
        if(TextUtils.isEmpty(dataEvent.returnData.getTime())){
            regFlag=true;
            Message msg = Message.obtain();
            msg.what = 107;
            hd.sendMessage(msg);
            return;
        }
        openLocation(dataEvent.returnData);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        regFlag=true;
        super.onDestroy();
    }
}
