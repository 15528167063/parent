package com.hzkc.parent.solider.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.adapter.MyListviewSoliderAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.TeamInternetPlanEvent;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.jsondata.NetPlanData;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * tuankong团控
 */

public class TeamControllActivity extends BaseActivity implements View.OnClickListener {
    private ListView lv;
    private MyListviewSoliderAdapter mAdapter;
    private TextView bt_selectall;
    private ImageView ivAddPlan;
    private TextView bt_cancel;
    private TextView bt_deselectall;
    private LinearLayout llPlanControl;
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private NetPlanDataBeanDao dao;
    private ArrayList<NetPlanData> planlist=new ArrayList<>();
    private static final String TAG = "StopInternetPlanActivit";
    private String childuuid;
    boolean allFlag = false;
    private String parentUUID;
    private ProgressDialog pd;
    private boolean stopFlag;
    private LinearLayout flKong;
    private AffirmDialog affirmDialog;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 106:
                    if (!stopFlag) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络连接不通畅,请稍后再试");
                    }
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                case 107:
                    dissloading();
                    flKong.setVisibility(View.GONE);
                    lv.setVisibility(View.VISIBLE);
                    initAppList();
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable runnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_internet_plan);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        EventBus.getDefault().register(this);
        stopFlag=false;
        initView();
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                handler.sendMessage(msg);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    /**
     * 初始化控件
     */
    private boolean regFlag;
    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        bt_selectall = (TextView) findViewById(R.id.bt_selectall);
        bt_cancel = (TextView) findViewById(R.id.bt_cancleselectall);
        bt_deselectall = (TextView) findViewById(R.id.bt_deselectall);
        llPlanControl = (LinearLayout) findViewById(R.id.ll_plan_control);

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivAddPlan = (ImageView) findViewById(R.id.iv_add_plan);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);

        tvTopTitle.setText("设定团控时间");
        ivFinish.setVisibility(View.VISIBLE);
        ivAddPlan.setVisibility(View.VISIBLE);

        affirmDialog = new AffirmDialog(this);
        affirmDialog.setTitleText("是否删除选中的团控计划？");
        affirmDialog.setAffirmClickListener(this);

        regFlag=false;
        ivFinish.setOnClickListener(this);
        ivAddPlan.setOnClickListener(this);
        // 全选按钮的回调接口
        bt_selectall.setOnClickListener(this);
        // 取消按钮的回调接口
        bt_cancel.setOnClickListener(this);
        // 删除按钮的回调接口
        bt_deselectall.setOnClickListener(this);
        // 绑定listView的监听器
    }

    /**
     * 初始化数据
     */
    private void initData() {
        llPlanControl.setVisibility(View.GONE);
        initListdata();
//        mAdapter = new MyListviewSoliderAdapter(planlist, this);
//        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (llPlanControl.getVisibility() == View.VISIBLE) {
                    // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                    MyListviewSoliderAdapter.ViewHolder holder = (MyListviewSoliderAdapter.ViewHolder) view.getTag();
                    holder.cb.toggle();
                    LogUtil.e("--------"+TAG, holder.cb.isChecked()+"");
                    MyListviewSoliderAdapter.getIsSelected().put(position, holder.cb.isChecked());
                    mAdapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(TeamControllActivity.this, ChangeTeamInternetPlanActivity.class);
                    intent.putExtra("planlist",planlist);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                mAdapter.CbFlags = true;
                llPlanControl.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    /**
     * 初始化Dao和planList
     */
    private void initListdata() {
        Intent intent = getIntent();
        childuuid = intent.getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        submitDate();

    }

    @Override
    public void onClick(View v) {
        boolean isNet = NetworkUtil.isConnected();
        switch (v.getId()) {
            case R.id.bt_selectall://选择全部
                selectAll();
                break;
            case R.id.bt_deselectall://删除
                if (!isNet) {
                    ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
                    return;
                }
                if (affirmDialog != null) {
                    affirmDialog.show();
                }
                break;
            case R.id.bt_cancleselectall://取消
                cancelSelect();
                break;
            case R.id.iv_add_plan:
                Intent intent = new Intent(this, AddTeamInternetPlanActivity.class);
                intent.putExtra("childuuid", childuuid);
                intent.putExtra("planlist",planlist);
                startActivity(intent);
                break;
            case R.id.iv_finish:
                finish();
                break;
            case R.id.affirm_cancel:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                break;
            case R.id.affirm_confirm:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                deleteSelect();
            default:
                break;
        }
    }

    /**
     * 执行删除团控item操作
     */
    private int num;
    private void deleteSelect() {
        HashMap<Integer, Boolean> map = MyListviewSoliderAdapter.getIsSelected();
        if(!(map+"").contains("true")){
            ToastUtils.showToast(MyApplication.getContext(),"请选择要删除的团控计划");
            return;
        }
        stopFlag=false;
        ArrayList<NetPlanData> lists = new ArrayList<>();
        for (int i = 0; i < planlist.size(); i++) {
            if (map.get(i)) {
                lists.add(planlist.get(i));
            }
        }
        planlist.removeAll(lists);
        dissloading();
        showLoading();
//        mAdapter.notifyDataSetChanged();
        cancelSelect();
        if(planlist.size()==0){
            flKong.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
        }
        EventBus.getDefault().post(new TeamInternetPlanEvent(childuuid, parentUUID,null, this.planlist), "TeamInternetPlan");
        handler.postDelayed(runnable, 10000);
    }

    /**
     * 执行选择全部操作
     */
    private void selectAll() {
        allFlag = !allFlag;
        if (allFlag) {
            // 遍历list的长度，将MyAdapter中的map值全部设为true
            for (int i = 0; i < planlist.size(); i++) {
                MyListviewSoliderAdapter.getIsSelected().put(i, true);
            }
        } else {
            // 遍历list的长度，将MyAdapter中的map值全部设为false
            for (int i = 0; i < planlist.size(); i++) {
                MyListviewSoliderAdapter.getIsSelected().put(i, false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 执行取消操作
     */
    private void cancelSelect() {
        allFlag = false;
        // 遍历list的长度，将已选的按钮设为未选
        for (int i = 0; i < planlist.size(); i++) {
            if (MyListviewSoliderAdapter.getIsSelected().get(i)) {
                MyListviewSoliderAdapter.getIsSelected().put(i, false);
            }
        }
        mAdapter.CbFlags = false;
        llPlanControl.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 处理应用列表
     */
    private void initAppList() {
        if(planlist==null){
            planlist=new ArrayList<>();
        }
        if(planlist.size()>0){
            mAdapter = new MyListviewSoliderAdapter(planlist, this);
            lv.setAdapter(mAdapter);
            mAdapter.changPlanFlag(new MyListviewSoliderAdapter.PlanFlagChangeListener() {
                @Override
                public void ivClick(int position) {
                    boolean isNet = NetworkUtil.isConnected();
                    if (!isNet) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
                        return;
                    }
                    stopFlag=false;
                    dissloading();
                    showLoading();
                    NetPlanData netPlanData = planlist.get(position);
                    planlist.remove(position);
                    if(netPlanData.e.equals("1")){
                        netPlanData.setIsOpen("0");
                    }else {
                        netPlanData.setIsOpen("1");
                    }
                    netPlanData.setPlanEndTime(netPlanData.d);
                    netPlanData.setPlanStartTime(netPlanData.c);
                    netPlanData.setPlanName(netPlanData.a);
                    netPlanData.setDays(netPlanData.b);
                    planlist.add(position,netPlanData);
                    mAdapter.notifyDataSetChanged();
                    EventBus.getDefault().post(new TeamInternetPlanEvent(childuuid,parentUUID,null,planlist), "TeamInternetPlan");
                    handler.postDelayed(runnable, 10000);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopFlag=true;
        super.onDestroy();
    }

    /**
     * 请求团控数据
     */
    private void submitDate() {
        showLoading();
        EventBus.getDefault().post(new TeamInternetPlanEvent(null, parentUUID,null,null), "requestTeamAppList");
    }

    /**
     * 团控数据返回
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "TeamControllData")
    private void AppStopMineList(TeamInternetPlanEvent whiteAppList) {
        LogUtil.e(TAG, "服务器返回团控数据："+ whiteAppList.data);
        regFlag=true;
        handler.removeCallbacks(runnable);
        if(whiteAppList.data==null || whiteAppList.data.equals("[]")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dissloading();
                    flKong.setVisibility(View.VISIBLE);
                    lv.setVisibility(View.GONE);
                }
            });
            return;
        }
        planlist = new Gson().fromJson(whiteAppList.data, new TypeToken<List<NetPlanData>>() { }.getType());
        Message msg = Message.obtain();
        msg.what = 107;
        handler.sendMessage(msg);
    }


    /**
     * 修改团控团控数据返回
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "TeamControllSubmit")
    private void changStateMineList(TeamInternetPlanEvent data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopFlag=true;
                dissloading();
            }
        });
    }
}
