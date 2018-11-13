package com.hzkc.parent.solider.activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.adapter.StopAppListAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.RequestStopAppListEvent;
import com.hzkc.parent.jsondata.AppData;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/8.
 */

public class MineAppManageActivity extends BaseActivity implements View.OnClickListener {
    private TextView  tv_head,tv_save,tv_cancel_plan;
    private TextView  tv_add;
    private ListView listView;
    private RelativeLayout disslog;
    private StopAppListAdapter adpter;
    private List<AppData> planlist=new ArrayList<>();
    private boolean regFlag;
    private Runnable runnable;
    private LinearLayout flKong;


    /**
     * 倒数计时set
     */
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 106:
                    if (!regFlag) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络连接不通畅,请稍后再试");
                    }
                    dissloading();
                    break;
                case 107://处理获取的applist
                    dissloading();
                    flKong.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    initAppList();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_appmanage);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        EventBus.getDefault().register(this);
        regFlag=false;
        initView();
        initData();
    }


    private ImageView iv_help;
    private void initView() {
        tv_head = (TextView) findViewById(R.id.tv_top_title);
        tv_save = (TextView) findViewById(R.id.tv_submit);
        tv_cancel_plan = (TextView) findViewById(R.id.tv_cancel_plan);
        tv_add = (TextView) findViewById(R.id.tv_add);
        listView = (ListView) findViewById(R.id.listview);
        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);

        iv_help= (ImageView) findViewById(R.id.iv_help);
        iv_help.setVisibility(View.VISIBLE);
        tv_cancel_plan.setVisibility(View.VISIBLE);
        tv_save.setVisibility(View.VISIBLE);
        tv_head.setText("自定义禁用名单");
        tv_save.setText("确定");
        tv_add.setOnClickListener(this);
        iv_help.setOnClickListener(this);
        tv_cancel_plan.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                hd.sendMessage(msg);
            }
        };
    }
    private String childUUID;
    private String parentUUID;
    private void initData() {
        childUUID = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        showLoading();
        EventBus.getDefault().post(new RequestStopAppListEvent(childUUID, parentUUID,null,null), "requestStopAppList");
        hd.postDelayed(runnable, 7000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_add:
                showPop();
                break;
            case R.id.tv_submit:  //提交
                submit();
                break;
            case R.id.tv_cancel_plan:
                finish();
                break;
            case R.id.iv_help:
                showHelppop();
                break;
        }
    }
    /**
     * 提交应用
     */
    private void submit() {
        showLoading();
        EventBus.getDefault().post(new RequestStopAppListEvent(childUUID, parentUUID,planlist,null), "requestSubmitAppList");
    }
    /**
     * 添加应用
     */
    public  void  ensure(EditText editText1,EditText editText2,RadioButton radioButton1, AlertDialog ad){
        if(TextUtils.isEmpty(editText1.getText().toString() )){
            Toast.makeText(this, "请输入应用名", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(editText2.getText().toString() )){
            Toast.makeText(this, "请输入包名", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < planlist.size(); i++) {
            if(planlist.get(i).a.equals(editText2.getText().toString())){
                Toast.makeText(this, "该应用已被添加", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String state=null;
        if(radioButton1.isChecked()){
            state="0";
        }else {
            state="1";
        }
        if(planlist==null){
            planlist=new ArrayList<>();
        }
        planlist.add(new AppData(editText2.getText().toString(),state,editText1.getText().toString(),"1"));
        flKong.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        if(adpter==null){
            adpter=new StopAppListAdapter(planlist,this);
            listView.setAdapter(adpter);
            adpter.setNodatdalistener(new StopAppListAdapter.Nodatda() {
                @Override
                public void setNoData() {
                    flKong.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            });
        }else {
            adpter.notifyDataSetChanged();
        }
        editText1.getText().clear();
        editText2.getText().clear();
        ad.dismiss();
    }

    /**
     * 请求返回的应用列表
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "appStopMinewData")
    private void AppStopMineList(RequestStopAppListEvent whiteAppList) {
        LogUtil.e(TAG, "服务返回禁用名单APP55："+ whiteAppList.data);
        regFlag=true;
        hd.removeCallbacks(runnable);
        if(whiteAppList.data==null || whiteAppList.data.equals("[]")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dissloading();
                    flKong.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            });
            return;
        }
        planlist = new Gson().fromJson(whiteAppList.data, new TypeToken<List<AppData>>() { }.getType());
        Message msg = Message.obtain();
        msg.what = 107;
        hd.sendMessage(msg);
    }
    /**
     * 处理应用列表
     */
    private void initAppList() {
        if(planlist==null){
            planlist=new ArrayList<>();
        }
        if( planlist.size()>0){
            adpter=new StopAppListAdapter(planlist,this);
            listView.setAdapter(adpter);
        }
        if(adpter!=null){
            adpter.setNodatdalistener(new StopAppListAdapter.Nodatda() {
                @Override
                public void setNoData() {
                    flKong.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            });
        }
    }
    /**
     * 提交名单回包
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "appSubmitMinewData")
    private void AppSubmitMineList(RequestStopAppListEvent whiteAppList) {
        if(whiteAppList.data.equals("1")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dissloading();
                    Toast.makeText(MineAppManageActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 点击注销时弹出确认框打电话
     */
    public void showPop(){
        final AlertDialog ad=new AlertDialog.Builder(this).create();
        ad.show();
        ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        Window window = ad.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 有白色背景，加这句代码
        View view= LayoutInflater.from(this).inflate(R.layout.activity_diolog,null);
        final EditText editText1 = (EditText) view.findViewById(R.id.editText1);
        final EditText editText2 = (EditText) view.findViewById(R.id.editText2);
        final RadioButton rb_01 = (RadioButton) view.findViewById(R.id.rb_01);
        RadioButton rb_02 = (RadioButton) view.findViewById(R.id.rb_02);

        TextView tv_sure = (TextView) view.findViewById(R.id.tv_sure);
        TextView tv_cancle = (TextView) view.findViewById(R.id.tv_cancle);
        ad.setView(view);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad.dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ensure(editText1,editText2,rb_01,ad);
            }
        });

        window.setContentView(view);
    }
    /**
     * 点击帮助时弹出确认框打电话
     */
    public void showHelppop(){
        AlertDialog ad=new AlertDialog.Builder(this).create();
        View view= LayoutInflater.from(this).inflate(R.layout.activity_helps,null);
        ad.setView(view);
        ad.show();
    }
}
