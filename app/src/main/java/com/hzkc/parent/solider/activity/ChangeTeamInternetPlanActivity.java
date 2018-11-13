package com.hzkc.parent.solider.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.event.TeamInternetPlanEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.jsondata.NetPlanData;
import com.hzkc.parent.utils.LogUtil;
import com.jaeger.library.StatusBarUtil;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import org.simple.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChangeTeamInternetPlanActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView ivFinish;
    private LinearLayout llStartTime;
    private TextView tvStartTime;
    private LinearLayout llEndTime;
    private TextView tvEndTime;
    private ImageView ivRi;
    private ImageView ivYi;
    private ImageView ivEr;
    private ImageView ivSan;
    private ImageView ivSi;
    private ImageView ivWu;
    private ImageView ivLiu;
    private ImageView ivClassDay;
    private ImageView ivDayoffDay;
    private EditText etPlanName;
    private Button btComfirm;

    String[] mhour = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
    String[] mMinute = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","24",
            "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36","37",
            "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49","50",
            "51", "52", "53", "54", "55", "56", "57", "58", "59"};
    private List<String> hourList;
    private List<String> minuteList;

    private boolean classDayFlag=false;
    private boolean dayoffFlag=false;
    private boolean yiFlag=false;
    private boolean erFlag=false;
    private boolean sanFlag=false;
    private boolean siFlag=false;
    private boolean wuFlag=false;
    private boolean liuFlag=false;
    private boolean riFlag=false;
    private View dialogView;
    private AlertDialog showStartTime;
    private static final String TAG = "AddStopPlanActivi";

    private NetPlanData planInfo;
    private ArrayList<NetPlanData> planlist=new ArrayList<>();
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team_internet_plan);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        planlist=(ArrayList<NetPlanData>)getIntent().getSerializableExtra("planlist");
        position=getIntent().getIntExtra("position",-1);
        planInfo=planlist.get(position);
        initView();
        initData();

    }
    /**
     * 初始化对话框控件
     * */
    private void initDialogView(final TextView tv) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //初始化滚轮控件
        View dialogView =  View.inflate(this, R.layout.dialog_start_time, null);
        final WheelView wvTimeHour = (WheelView) dialogView.findViewById(R.id.wv_time_hour);
        final WheelView wvTimeMinute = (WheelView) dialogView.findViewById(R.id.wv_time_minute);
        Button btCancel = (Button) dialogView.findViewById(R.id.bt_cancel);
        Button btConfirm = (Button) dialogView.findViewById(R.id.bt_confirm);

        //初始化小时
        wvTimeHour.setWheelAdapter(new ArrayWheelAdapter(ChangeTeamInternetPlanActivity.this)); // 文本数据源
        wvTimeHour.setWheelSize(3);
        wvTimeHour.setLoop(true);
        WheelView.WheelViewStyle style1 = new WheelView.WheelViewStyle();
        style1.selectedTextSize = 24;
        style1.textSize = 20;
        style1.textColor = Color.GRAY;
        wvTimeHour.setStyle(style1);
        wvTimeHour.setWheelClickable(true);
        wvTimeHour.setSkin(WheelView.Skin.Holo); // common皮肤
        wvTimeHour.setWheelData(hourList);  // 数据集合
        //初始化分钟
        wvTimeMinute.setWheelAdapter(new ArrayWheelAdapter(ChangeTeamInternetPlanActivity.this)); // 文本数据源
        wvTimeMinute.setWheelSize(3);
        wvTimeMinute.setLoop(true);
        WheelView.WheelViewStyle style2 = new WheelView.WheelViewStyle();
        style2.selectedTextSize = 24;
        style2.textSize = 20;
        style2.textColor = Color.GRAY;
        wvTimeMinute.setStyle(style2);
        wvTimeMinute.setWheelClickable(true);
        wvTimeMinute.setSkin(WheelView.Skin.Holo); // common皮肤
        wvTimeMinute.setWheelData(minuteList);  // 数据集合
        wvTimeHour.setSelection(hourList.indexOf(tv.getText().toString().substring(0,2)));
        wvTimeMinute.setSelection(minuteList.indexOf(tv.getText().toString().substring(3)));
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartTime.dismiss();
            }
        });
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText((String)wvTimeHour.getSelectionItem()+
                        ":"+(String)wvTimeMinute.getSelectionItem());
                showStartTime.dismiss();
            }
        });
        builder.setView(dialogView);
        showStartTime = builder.show();
    }
    private void initData() {
        Intent intent = getIntent();
        tvStartTime.setText(planInfo.c);
        tvEndTime.setText(planInfo.d);
        etPlanName.setText(planInfo.a);
        etPlanName.setSelection(etPlanName.getText().length());
        String weekday = planInfo.b;
        if(weekday.contains("1")){
            yiFlag=true;
        }
        if(weekday.contains("2")){
            erFlag=true;
        }
        if(weekday.contains("3")){
            sanFlag=true;
        }
        if(weekday.contains("4")){
            siFlag=true;
        }
        if(weekday.contains("5")){
            wuFlag=true;
        }
        if(weekday.contains("6")){
            liuFlag=true;
        }
        if(weekday.contains("7")){
            riFlag=true;
        }
        clickLiu();
        clickWu();
        clickSi();
        clickSan();
        clickEr();
        clickYi();
        clickRi();

        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();
        for (int i = 0; i < mhour.length; i++) {
            hourList.add(mhour[i]);
        }
        for (int i = 0; i < mMinute.length; i++) {
            minuteList.add(mMinute[i]);
        }
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (TextView) findViewById(R.id.tv_cancel_plan);
        llStartTime = (LinearLayout) findViewById(R.id.ll_start_time);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        llEndTime = (LinearLayout) findViewById(R.id.ll_end_time);
        tvEndTime = (TextView) findViewById(R.id.tv_end_time);
        ivRi = (ImageView) findViewById(R.id.iv_ri);
        ivYi = (ImageView) findViewById(R.id.iv_yi);
        ivEr = (ImageView) findViewById(R.id.iv_er);
        ivSan = (ImageView) findViewById(R.id.iv_san);
        ivSi = (ImageView) findViewById(R.id.iv_si);
        ivWu = (ImageView) findViewById(R.id.iv_wu);
        ivLiu = (ImageView) findViewById(R.id.iv_liu);
        ivClassDay = (ImageView) findViewById(R.id.iv_class_day);
        ivDayoffDay = (ImageView) findViewById(R.id.iv_dayoff_day);
        etPlanName = (EditText) findViewById(R.id.et_plan_name);
        btComfirm = (Button) findViewById(R.id.bt_comfirm);

        ivFinish.setVisibility(View.VISIBLE);
        tvTopTitle.setText("修改团控计划");

        ivFinish.setOnClickListener(this);
        llStartTime.setOnClickListener(this);
        llEndTime.setOnClickListener(this);
        ivRi.setOnClickListener(this);
        ivYi.setOnClickListener(this);
        ivEr.setOnClickListener(this);
        ivSan.setOnClickListener(this);
        ivSi.setOnClickListener(this);
        ivWu.setOnClickListener(this);
        ivLiu.setOnClickListener(this);
        ivClassDay.setOnClickListener(this);
        ivDayoffDay.setOnClickListener(this);
        btComfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_plan://返回
                finish();
                break;
            case R.id.ll_start_time:
                showStartTimeDialog();
                break;
            case R.id.ll_end_time:
                showEndtTimeDialog();
                break;
            case R.id.iv_ri:
                riFlag=!riFlag;
                clickRi();
                break;
            case R.id.iv_yi:
                yiFlag=!yiFlag;
                clickYi();
                break;
            case R.id.iv_er:
                erFlag=!erFlag;
                clickEr();
                break;
            case R.id.iv_san:
                sanFlag=!sanFlag;
                clickSan();
                break;
            case R.id.iv_si:
                siFlag=!siFlag;
                clickSi();
                break;
            case R.id.iv_wu:
                wuFlag=!wuFlag;
                clickWu();
                break;
            case R.id.iv_liu:
                liuFlag=!liuFlag;
                clickLiu();
                break;
            case R.id.iv_class_day:
                clickClassDay();
                break;
            case R.id.iv_dayoff_day:
                clickDayoff();
                break;
            case R.id.bt_comfirm:
                changeStopPlan();
                break;
            default:
                break;
        }
    }
    /**
     * 设置结束时间
     * */
    private void showEndtTimeDialog() {
        initDialogView(tvEndTime);
    }
    /**
     * 设置开始时间
     * */
    private void showStartTimeDialog() {
        initDialogView(tvStartTime);
    }
    /**
     * 星期点击事件
     * */
    private void clickLiu() {
        if(liuFlag){
            ivLiu.setImageResource(R.drawable.clock_sat_on);
        }else{
            ivLiu.setImageResource(R.drawable.clock_sat_off);
        }
    }

    private void clickWu() {
        if(wuFlag){
            ivWu.setImageResource(R.drawable.clock_fri_on);
        }else{
            ivWu.setImageResource(R.drawable.clock_fri_off);
        }
    }

    private void clickSi() {
        if(siFlag){
            ivSi.setImageResource(R.drawable.clock_thur_on);
        }else{
            ivSi.setImageResource(R.drawable.clock_thur_off);
        }
    }

    private void clickSan() {
        if(sanFlag){
            ivSan.setImageResource(R.drawable.clock_wed_on);
        }else{
            ivSan.setImageResource(R.drawable.clock_wed_off);
        }
    }

    private void clickEr() {
        if(erFlag){
            ivEr.setImageResource(R.drawable.clock_tues_on);
        }else{
            ivEr.setImageResource(R.drawable.clock_tues_off);
        }
    }

    private void clickYi() {
        if(yiFlag){
            ivYi.setImageResource(R.drawable.clock_mon_on);
        }else{
            ivYi.setImageResource(R.drawable.clock_mon_off);
        }
    }

    private void clickRi() {
        if(riFlag){
            ivRi.setImageResource(R.drawable.clock_sun_on);
        }else{
            ivRi.setImageResource(R.drawable.clock_sun_off);
        }
    }

    /**
     * 点击上课日处理的逻辑
     * */
    private void clickClassDay() {
        classDayFlag=!classDayFlag;
        if(classDayFlag){
            ivClassDay.setImageResource(R.drawable.clock_class_day_on);
            yiFlag=true;
            erFlag=true;
            sanFlag=true;
            siFlag=true;
            wuFlag=true;
            ivYi.setImageResource(R.drawable.clock_mon_on);
            ivEr.setImageResource(R.drawable.clock_tues_on);
            ivSan.setImageResource(R.drawable.clock_wed_on);
            ivSi.setImageResource(R.drawable.clock_thur_on);
            ivWu.setImageResource(R.drawable.clock_fri_on);
        }else{
            ivClassDay.setImageResource(R.drawable.clock_class_day_off);
            yiFlag=false;
            erFlag=false;
            sanFlag=false;
            siFlag=false;
            wuFlag=false;
            ivYi.setImageResource(R.drawable.clock_mon_off);
            ivEr.setImageResource(R.drawable.clock_tues_off);
            ivSan.setImageResource(R.drawable.clock_wed_off);
            ivSi.setImageResource(R.drawable.clock_thur_off);
            ivWu.setImageResource(R.drawable.clock_fri_off);
        }
    }
    /**
     * 点击周末处理的逻辑
     * */
    private void clickDayoff() {
        dayoffFlag=!dayoffFlag;
        if(dayoffFlag){
            ivDayoffDay.setImageResource(R.drawable.clock_sum_day_on);
            riFlag=true;
            liuFlag=true;
            ivRi.setImageResource(R.drawable.clock_sun_on);
            ivLiu.setImageResource(R.drawable.clock_sat_on);
        }else{
            ivDayoffDay.setImageResource(R.drawable.clock_sum_day_off);
            riFlag=false;
            liuFlag=false;
            ivRi.setImageResource(R.drawable.clock_sun_off);
            ivLiu.setImageResource(R.drawable.clock_sat_off);
        }
    }
    /**
     * 修改上网禁止计划并上传到服务器
     * */
    private void changeStopPlan() {
        NetPlanDataBeanDao planDao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
        Intent intent = getIntent();
        String parentUUID = sp.getString("parentUUID", "");
        String childuuid = intent.getStringExtra("childuuid");
        String planName = etPlanName.getText().toString().trim();
        String weekDay= "";
        if(yiFlag){
            weekDay=weekDay+"1";
        }
        if(erFlag){
            weekDay=weekDay+"2";
        }
        if(sanFlag){
            weekDay=weekDay+"3";
        }
        if(siFlag){
            weekDay=weekDay+"4";
        }
        if(wuFlag){
            weekDay=weekDay+"5";
        }
        if(liuFlag){
            weekDay=weekDay+"6";
        }
        if(riFlag){
            weekDay=weekDay+"7";
        }
        LogUtil.i(TAG, "saveStopPlan: "+weekDay);
        if(TextUtils.isEmpty(weekDay)){
            Toast.makeText(this,"请选择星期！",Toast.LENGTH_SHORT).show();
            return;
        }
        String startPlanTime = tvStartTime.getText().toString();
        String endPlanTime = tvEndTime.getText().toString();

        DateFormat d_Fm = new SimpleDateFormat("HH:mm");
        Date starTime1 = null;
        Date endTime1 = null;
        try {
            starTime1 = d_Fm.parse(startPlanTime);
            endTime1 = d_Fm.parse(endPlanTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(starTime1.getTime()>endTime1.getTime()){
            Toast.makeText(this,"开始时间不能大于结束时间",Toast.LENGTH_SHORT).show();//
            return;
        }
        if(starTime1.getTime()==endTime1.getTime()){
            Toast.makeText(this,"开始时间和结束时间不能相同",Toast.LENGTH_SHORT).show();//
            return;
        }
        planlist.remove(position);
        if(planlist!=null && planlist.size()>0){
            for (int i = 0; i < planlist.size(); i++) {
                String weekday=planlist.get(i).b;
                if(checkCommom(weekday,weekDay)){
                    if(checkCommomTime(planlist.get(i).c,planlist.get(i).d,startPlanTime,endPlanTime)){
                        Toast.makeText(this,"不能选择有重叠时间段",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }
        planInfo.setIsOpen(CmdCommon.CMD_FLAG_OPEN);
        planInfo.setPlanEndTime(endPlanTime);
        planInfo.setPlanStartTime(startPlanTime);
        planInfo.setPlanName(planName);
        planInfo.setDays(weekDay);
        planlist.add(position,planInfo);
        EventBus.getDefault().post(new TeamInternetPlanEvent(childuuid,parentUUID,null,planlist), "TeamInternetPlan");
        finish();
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    /**
     * 判断两个字符串是不是有交集
     */
    private  Boolean checkCommom(String str1, String str2) {
        List<String> result = new ArrayList<>();
        int length1 = str1.length();
        int length2 = str2.length();
        for (int i = 0; i < length1; i++) {
            for (int j = 0; j < length2; j++) {
                String char1 = str1.charAt(i) + "";
                String char2 = str2.charAt(j) + "";
                if (char1.equals(char2)) {
                    result.add(char1);
                }
            }
        }
        if(result.size()==0){
            return false;
        }else {
            return  true;
        }
    }
    /**
     * 判断两个时间段是不是有交集
     */
    private  Boolean checkCommomTime(String starttime1, String endtime1,String starttime2,String endtime2) {
        DateFormat d_Fm = new SimpleDateFormat("HH:mm");
        Date sTime1 = null;
        Date eTime1 = null;
        Date sTime2 = null;
        Date eTime2 = null;
        try {
            sTime1 = d_Fm.parse(starttime1);
            eTime1 = d_Fm.parse(endtime1);
            sTime2 = d_Fm.parse(starttime2);
            eTime2 = d_Fm.parse(endtime2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        if((sTime1.getTime() <= sTime2.getTime() || sTime1.getTime() <eTime2.getTime()) && ((eTime1.getTime() >sTime2.getTime() || eTime1.getTime() >= eTime2.getTime()))){
        if((sTime1.getTime() <=sTime2.getTime() || sTime1.getTime() <=eTime2.getTime()) && ((eTime1.getTime() >=sTime2.getTime() || eTime1.getTime() >=eTime2.getTime()))){
            return true;
        }
        return false;
    }
}
