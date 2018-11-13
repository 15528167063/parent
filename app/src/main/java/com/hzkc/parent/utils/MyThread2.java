package com.hzkc.parent.utils;

import android.content.Context;
import android.text.TextUtils;

import com.hzkc.parent.Bean.LoveTrailBean;
import com.hzkc.parent.jsondata.LocationData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo-s on 2016/12/20.
 */

public class MyThread2 implements Runnable {
    private static final String TAG = "MyThread";
    private final SimpleDateFormat dateFormat2;
    private int size;
    private Context context;
    private List<LocationData> locationDataList;
    boolean flag = true;
    private SimpleDateFormat dateFormat;
    private List<LoveTrailBean> mlist;
    private RefleshListener listener;
    private long nowTime;
    private long requestTime;

    public MyThread2(int size, Context context, List<LocationData> locationDataList) {
        this.size = size;
        this.context = context;
        this.locationDataList = locationDataList;
        dateFormat = new SimpleDateFormat("HH:mm");
        dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        mlist = new ArrayList<>();
        nowTime = System.currentTimeMillis();
    }

    public interface RefleshListener {
        public void sendData(List<LoveTrailBean> mlist);
    }

    @Override
    public void run() {
        while (flag) {
            save();
        }
    }

    public synchronized void save() {
        if (size >= 0) {
            //try{Thread.sleep(1);}catch(Exception e){}//模拟不同步的实现
//            BDLocation location = new BDLocation();
//            location.setLatitude(Double.valueOf(locationDataList
//                    .get(size)
//                    .getLatitude())
//            );
//            location.setLongitude(Double.valueOf(locationDataList
//                    .get(size)
//                    .getLongitude())
//            );
            String time = locationDataList
                    .get(size)
                    .getTime();
            //String locationDetailAdress = LocationUtils.updateWithNewLocation2(context, location);
            String locationDetailAdress=locationDataList.get(size).getLatitude();
            LogUtil.i(TAG, "openLocation111: " + locationDetailAdress);
            LogUtil.i(TAG, "openLocation111: " + time);
            //时间
            if(TextUtils.isEmpty(time)){
                return;
            }
            requestTime = Long.parseLong(time);
            String hms = dateFormat.format(requestTime);
            String ymd = dateFormat2.format(requestTime);
            long stopTime = nowTime - requestTime;
            LogUtil.e("stopTime111111111111111111111111111: "+nowTime);
            LogUtil.e("stopTime111111111111111111111111111: "+ new SimpleDateFormat("yy-MM-dd HH:mm").format(nowTime));
            LogUtil.e("stopTime222222222222222222222222222: "+requestTime);
            LogUtil.e("stopTime222222222222222222222222222: "+ new SimpleDateFormat("yy-MM-dd HH:mm").format(requestTime));
            LogUtil.e("stopTime333333333333333333333333333: "+stopTime);
            nowTime= requestTime;
            //String indexTime=stopTime/(1000*60) + " 分钟"+(stopTime%(1000*60)+"").substring(0,2)+"秒";
            String indexTime="";
            if(stopTime/(1000*60)>0){
                if(stopTime/(1000*60*60)>0){
                    long  times=  (stopTime/(3600*1000));
                    if(times>24){
                        times=times-24;
                    }
                    indexTime=times+"小时"+((stopTime/1000)%3600)/60+"分"+(stopTime/1000)%60+"秒";

//                    indexTime=stopTime/(3600*1000)+"小时"+((stopTime/1000)%3600)/60+"分"+(stopTime/1000)%60+"秒";
                }else{
                    indexTime=stopTime/(60*1000)+"分"+(stopTime/1000)%60+"秒";
                }
            }else {
                indexTime=Math.abs(stopTime/1000)+"秒";
            }
            LoveTrailBean loveTrailBean = new LoveTrailBean(hms + "", locationDetailAdress, indexTime,ymd);
            mlist.add(loveTrailBean);
            LogUtil.i(TAG, "RequestLocationEvent: mlist.size:" + mlist.size());
            size--;
            LogUtil.i(TAG, "save: " + Thread.currentThread().getName() + ",size:" + size);
            if (size % 9 == 1) {
                LogUtil.e(TAG, "save: 3333333333333333333" + ",mlist.size:" + mlist.size());
                if (listener != null) {
                    listener.sendData(mlist);
                }
//                EventBus.getDefault().post(
//                        new ChildLocationPathDataEvent(indexlist), "LoactionPathData");
            }
        } else {
            flag = false;
            LogUtil.i(TAG, "save: mlist.size():" + mlist.size() + ",mlist.size:" + mlist.size());
            if (listener != null) {
                listener.sendData(mlist);
            }
//            EventBus.getDefault().post(
//                    new ChildLocationPathDataEvent(mlist), "LoactionPathData");
            LogUtil.i(TAG, "save: " + Thread.currentThread().getName() + ",mlist.size():" + mlist.size());
        }
    }

    public void reflushPart(RefleshListener listener) {
        this.listener = listener;
    }
}
