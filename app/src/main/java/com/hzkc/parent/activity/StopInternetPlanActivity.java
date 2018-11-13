package com.hzkc.parent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyListviewAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.NetControlBackEvent;
import com.hzkc.parent.event.SlbhEvent;
import com.hzkc.parent.event.StopInternetPlanEvent;
import com.hzkc.parent.event.YjgkEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.entity.NetPlanDataBean;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.NetPlanDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;


public class StopInternetPlanActivity extends BaseActivity implements View.OnClickListener {
    private ListView lv;
    private MyListviewAdapter mAdapter;
    private TextView bt_selectall;
    private ImageView ivAddPlan;
    private TextView bt_cancel;
    private TextView bt_deselectall;
    private LinearLayout llPlanControl;
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private NetPlanDataBeanDao dao;
    private List<NetPlanDataBean> planlist;
    private static final String TAG = "StopInternetPlanActivit";
    private String childuuid;
    boolean allFlag = false;
    private String parentUUID;
    private ProgressDialog pd;
    private boolean stopFlag;
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
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    /////////////////////一键管控关闭
                    stopOtherControl();
                    /////////////////////
                    break;
                default:
                    break;
            }
        }
    };

    private void stopOtherControl() {
        //一键管控关闭
        /////////////////////
        ChildContrlFlagDao childDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        ChildContrlFlag findChild = childDao.queryBuilder().where(ChildContrlFlagDao.Properties.Childuuid.eq(childuuid)).build().unique();
        if (findChild.getYjgkflag().equals(CmdCommon.CMD_FLAG_OPEN) ) {
            EventBus.getDefault().post(new YjgkEvent(parentUUID, childuuid, "0"), "yjgk");
            findChild.setYjgkflag(CmdCommon.CMD_FLAG_CLOSE);
            childDao.update(findChild);
            sp.edit().putBoolean("yjgk"+childuuid,false).commit();
            sp.edit().putBoolean("yjgkclose",true).commit();
        }
        /////////////////////
        if (findChild.getSlbhflag().equals(CmdCommon.CMD_FLAG_OPEN) ) {
            String slbhSpacetime = findChild.getSlbhSpacetime();
            String slbhResttime = findChild.getSlbhResttime();
            EventBus.getDefault().post(new SlbhEvent(parentUUID, childuuid, "2", slbhSpacetime, slbhResttime), "slbh");
            findChild.setSlbhflag(CmdCommon.CMD_FLAG_CLOSE);
            childDao.update(findChild);
        }
        /////////////////////
    }

    private Runnable runnable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_internet_plan);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
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
    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        bt_selectall = (TextView) findViewById(R.id.bt_selectall);
        bt_cancel = (TextView) findViewById(R.id.bt_cancleselectall);
        bt_deselectall = (TextView) findViewById(R.id.bt_deselectall);
        llPlanControl = (LinearLayout) findViewById(R.id.ll_plan_control);

        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivAddPlan = (ImageView) findViewById(R.id.iv_add_plan);
        ivFinish = (ImageView) findViewById(R.id.iv_finish_back);

        tvTopTitle.setText("管控计划");
        ivFinish.setVisibility(View.VISIBLE);
        ivAddPlan.setVisibility(View.VISIBLE);

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
        //初始化数据库
        initListAndDao();
        // 实例化自定义的MyAdapter
        mAdapter = new MyListviewAdapter(planlist, this);
        // 绑定Adapter
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (llPlanControl.getVisibility() == View.VISIBLE) {
                    // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                    MyListviewAdapter.ViewHolder holder = (MyListviewAdapter.ViewHolder) view.getTag();
                    // 改变CheckBox的状态
                    holder.cb.toggle();
                    // 将CheckBox的选中状况记录下来
                    LogUtil.e("--------"+TAG, holder.cb.isChecked()+"");
                    MyListviewAdapter.getIsSelected().put(position, holder.cb.isChecked());
                    mAdapter.notifyDataSetChanged();
                } else {
                    Intent intent = new Intent(StopInternetPlanActivity.this, ChangeStopInternetPlanActivity.class);
                    LogUtil.i(TAG, "onItemClick: " + planlist.get(position).get_id());
                    intent.putExtra("planId", "" + planlist.get(position).get_id());
                    intent.putExtra("childuuid", childuuid);
                    startActivity(intent);
                }
            }
        });
        //设置listView的条目长按监听
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
        /**
         * 改变计划的状态
         */
        mAdapter.changPlanFlag(new MyListviewAdapter.PlanFlagChangeListener() {
            @Override
            public void ivClick(int position) {
                boolean isNet = NetworkUtil.isConnected();
                if (!isNet) {
                    ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
                    return;
                }
                stopFlag=false;
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
                pd = new ProgressDialog(StopInternetPlanActivity.this);
                pd.setMessage("正在执行操作，请稍候");
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setCancelable(false);
                pd.show();
                long planId = planlist.get(position).get_id();
                String planflag = planlist.get(position).getPlanflag();
                NetPlanDataBean findData = dao.queryBuilder()
                        .where(NetPlanDataBeanDao.Properties._id.eq(planId))
                        .build().unique();
                LogUtil.i(TAG, "ivClick: 状态发生改变了");
                // 改变IMageView的状态
                if (findData != null) {
                    if (planflag.equals("1")) {
                        findData.setPlanflag("0");
                        planlist.get(position).setPlanflag("0");
                    } else {
                        findData.setPlanflag("1");
                        planlist.get(position).setPlanflag("1");
                    }
                    dao.update(findData);
                    mAdapter.notifyDataSetChanged();
                }
                EventBus.getDefault().post(new StopInternetPlanEvent(childuuid,parentUUID), "stopInternetPlan");
                handler.postDelayed(runnable, 10000);
            }
        });
    }

    /**
     * 初始化Dao和planList
     */
    private void initListAndDao() {
        dao = GreenDaoManager.getInstance().getSession().getNetPlanDataBeanDao();
        Intent intent = getIntent();
        childuuid = intent.getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        planlist = dao.queryBuilder().where(NetPlanDataBeanDao.Properties.Childuuid.eq(childuuid))
                .orderDesc(NetPlanDataBeanDao.Properties._id)
                .build().list();
        String first = sp.getString(childuuid+"first", "");
        if (planlist.size() == 0 && TextUtils.isEmpty(first)) {
            NetPlanDataBean user2 = new NetPlanDataBean(null, childuuid, "上午学习时间", "12345", "7:00", "12:00", "0");
            NetPlanDataBean user3 = new NetPlanDataBean(null, childuuid, "下午学习时间", "12345", "14:00", "18:00", "0");
            dao.insert(user2);
            dao.insert(user3);
            planlist.add(user2);
            planlist.add(user3);
            sp.edit().putString(childuuid+"first", "不是第一次").commit();
        }

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
                deleteSelect();
                break;
            case R.id.bt_cancleselectall://取消
                cancelSelect();
                break;
            case R.id.iv_add_plan:
                Intent intent = new Intent(this, AddStopInternetPlanActivity.class);
                intent.putExtra("childuuid", childuuid);
                startActivity(intent);
                break;
            case R.id.iv_finish_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 执行删除操作
     */
    private void deleteSelect() {
        HashMap<Integer, Boolean> map = MyListviewAdapter.getIsSelected();
        if(!(map+"").contains("true")){
            ToastUtils.showToast(MyApplication.getContext(),"请选择要删除的管控计划");
            return;
        }
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
        pd = new ProgressDialog(StopInternetPlanActivity.this);
        pd.setMessage("正在执行操作，请稍候");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCancelable(false);
        pd.show();
        stopFlag=false;
        for (int i = 0; i < planlist.size(); i++) {
            if (map.get(i)) {
                NetPlanDataBean planInfo = (NetPlanDataBean) mAdapter.getItem(i);
                Long planid = planInfo.get_id();
                NetPlanDataBean findplan = dao.queryBuilder().where(NetPlanDataBeanDao.Properties._id.eq(planid)).build().unique();
                if (findplan != null) {
                    dao.deleteByKey(findplan.get_id());
                }
            }
        }
        initData();
        EventBus.getDefault().post(new StopInternetPlanEvent(childuuid, parentUUID), "stopInternetPlan");
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
                MyListviewAdapter.getIsSelected().put(i, true);
            }
        } else {
            // 遍历list的长度，将MyAdapter中的map值全部设为false
            for (int i = 0; i < planlist.size(); i++) {
                MyListviewAdapter.getIsSelected().put(i, false);
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
            if (MyListviewAdapter.getIsSelected().get(i)) {
                MyListviewAdapter.getIsSelected().put(i, false);
            }
        }
        mAdapter.CbFlags = false;
        llPlanControl.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopFlag=true;
        super.onDestroy();
    }
    /**
     * 请求孩子的管控计划
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "netcontrolback")
    private void OrderChildStopPlan (NetControlBackEvent requestData) {
        LogUtil.i(TAG, "OrderChildStopPlan: 请求孩子的管控计划");
        stopFlag=true;
        handler.removeCallbacks(runnable);
        Message msg = Message.obtain();
        msg.what = 107;
//        ToastUtils.showToast(MyApplication.getContext(),"网络不通，请检查网络再试");
        handler.sendMessage(msg);
    }

}
