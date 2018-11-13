package com.hzkc.parent.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lenovo-s on 2016/12/15.
 */

public class LocationUtils {

    private static String childadress;

    public static String updateWithNewLocation(Context context, BDLocation location) {
        String placename;
        if (location != null) {
            Geocoder geocoder = new Geocoder(context);
            List places = null;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            try {
                //将百度地图装换为gps
                LatLng lng = convertBaiduToGPS(latLng);
                places = geocoder.getFromLocation(lng.latitude, lng.longitude, 5);
                //Toast.makeText(context, places.size() + "", Toast.LENGTH_LONG).show();
                LogUtil.e("places:"+places);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (places != null && places.size() > 0) {
                //一下的信息将会具体到某条街
                //其中getAddressLine(0)表示国家，getAddressLine(1)表示精确到某个区，getAddressLine(2)表示精确到具体的街
                if(((Address) places.get(0)).getAddressLine(2)!=null){
                    placename = ((Address) places.get(0)).getAddressLine(0) + ", " + ((Address) places.get(0)).getAddressLine(1) + ", "
                            + ((Address) places.get(0)).getAddressLine(2);
                }else if(((Address) places.get(0)).getFeatureName()!=null){
                    placename = ((Address) places.get(0)).getAddressLine(0) + ", " + ((Address) places.get(0)).getAddressLine(1) + ", "
                            + ((Address) places.get(0)).getFeatureName();
                } else{
                    placename = ((Address) places.get(0)).getAddressLine(0) + ", " + ((Address) places.get(0)).getAddressLine(1);
                }
            } else {
                //latlngToAddress(latLng);
                //placename = childadress;
                placename = "无法获取地理信息";
                LogUtil.e("childadress:"+placename);
            }
        } else {
            placename = "无法获取地理信息";
        }
        return placename;
    }
    public static LatLng convertGPSToBaidu(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }

    public static LatLng convertBaiduToGPS(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        CoordinateConverter converter = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
        double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
        BigDecimal bdLatitude = new BigDecimal(latitude);
        bdLatitude = bdLatitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal bdLongitude = new BigDecimal(longitude);
        bdLongitude = bdLongitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        return new LatLng(bdLatitude.doubleValue(), bdLongitude.doubleValue());
    }

    public static String updateWithNewLocation2(Context context, BDLocation location) {
        String placename;
        if (location != null) {
            Geocoder geocoder = new Geocoder(context);
            List places = null;
            try {
                //将百度地图装换为gps
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng lng = convertBaiduToGPS(latLng);
                places = geocoder.getFromLocation(lng.latitude, lng.longitude, 5);
                //Toast.makeText(context, places.size() + "", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (places != null && places.size() > 0) {
                //一下的信息将会具体到某条街
                //其中getAddressLine(0)表示国家，getAddressLine(1)表示精确到某个区，getAddressLine(2)表示精确到具体的街

                //placename = ((Address) places.get(0)).getAddressLine(0) + ", " + ((Address) places.get(0)).getAddressLine(2);
                if(((Address) places.get(0)).getAddressLine(2)!=null){
                    placename = ((Address) places.get(0)).getAddressLine(0) + ", "+ ((Address) places.get(0)).getAddressLine(2);
                }else if(((Address) places.get(0)).getFeatureName()!=null){
                    placename = ((Address) places.get(0)).getAddressLine(0) + ", " + ((Address) places.get(0)).getFeatureName();
                }else{
                    placename = ((Address) places.get(0)).getAddressLine(0) + ", " + ((Address) places.get(0)).getAddressLine(1);
                }
            } else {
                placename = "无法获取地理信息";
            }
        } else {
            placename = "无法获取地理信息";
        }
        return placename;
    }
    /**
     * 经纬度或地址相互转换
     *
     * @param latlng
     */
    private static void latlngToAddress(LatLng latlng) {
        // 在OnCreate方法里创建地理编码检索实例
        childadress="";
        GeoCoder geoCoder = GeoCoder.newInstance();
        // 设置反地理经纬度坐标,请求位置时,需要一个经纬度
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
        //设置地址或经纬度反编译后的监听,这里有两个回调方法,
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            //经纬度转换成地址
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null ||  result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //Toast.makeText(getActivity(), "找不到该地址!",Toast.LENGTH_SHORT).show();
                }
                //tv_address.setText("地址:" + result.getAddress());
                childadress=result.getAddress();
                LogUtil.e("childadress:"+childadress);
            }

            //把地址转换成经纬度
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                // 详细地址转换在经纬度
                //String address=result.getAddress();
            }
        });
    }
}
