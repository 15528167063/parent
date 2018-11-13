package com.hzkc.parent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyAppWhiteAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.RequestChildAppListEvent;
import com.hzkc.parent.event.RequestWhiteAppListEvent;
import com.hzkc.parent.event.WhiteAppListEvent;
import com.hzkc.parent.event.WhiteAppBackEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppWhiteActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvSelectAll;
    private TextView tvRemoveApp;
    private TextView tvTopTitle;
    private TextView ivFinish;
    private TextView ivAddPlan;
    private LinearLayout flKong;
    private ListView lvAppList;
    private String childUUID;
    private String parentUUID;
    private MyAppWhiteAdapter adapter;
    private List<AppDataBean> list;
    boolean allFlag = false;
    private AppDataBeanDao dao;
    private ProgressDialog pd;
    private boolean regFlag;
    /**
     * 倒数计时
     */
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 106:
                    if (!regFlag) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络连接不通畅,请稍后再试");
                        flKong.setVisibility(View.VISIBLE);
                    }
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    break;
                case 107://处理获取的applist
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    initAppList();
                    break;
                case 108://处理反馈
                    if(pd!=null&&pd.isShowing()){
                        pd.dismiss();
                    }
                    initAppList();
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
        setContentView(R.layout.activity_app_white);
        EventBus.getDefault().register(this);
        regFlag=false;
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                hd.sendMessage(msg);
            }
        };
        initView();
        initData();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRemoveApp = (TextView) findViewById(R.id.tv_remove_app);
        tvSelectAll = (TextView) findViewById(R.id.tv_select_all);
        ivFinish = (TextView) findViewById(R.id.tv_finish_stop_plan);
        ivAddPlan = (TextView) findViewById(R.id.tv_save);
        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);
        lvAppList = (ListView) findViewById(R.id.lv_app_list);

        tvTopTitle.setText("白名单应用");
        ivAddPlan.setText("黑名单应用");
        childUUID = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");

        ivFinish.setVisibility(View.VISIBLE);
        ivAddPlan.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        ivAddPlan.setOnClickListener(this);
        tvRemoveApp.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        list = new ArrayList<>();
    }

    private void initData() {
        dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        LogUtil.i(TAG, "initData: "+mlist.size());
        if (mlist.size() > 0) {
            flKong.setVisibility(View.INVISIBLE);
            for (int i = 0; i < mlist.size(); i++) {
                LogUtil.i(TAG, "initData: "+mlist.get(i).getAppwhitelist());
                if (mlist.get(i).getAppwhitelist().equals(CmdCommon.CMD_FLAG_WHITE)) {//是白名单
                    list.add(mlist.get(i));
                }
            }
        } else {//没有app数据
            LogUtil.i(TAG, "initData: 没有app数据");
            if(pd!=null&&pd.isShowing()){
                pd.dismiss();
            }
            pd = new ProgressDialog(this);
            //pd.setTitle("提示");
            pd.setMessage("正在加载数据中，请稍候");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(true);
            pd.show();
            EventBus.getDefault().post(new RequestChildAppListEvent(childUUID, parentUUID), "requestchildApp");
            hd.postDelayed(runnable, 8000);
            //flKong.setVisibility(View.VISIBLE);
            return;
        }
        if (list.size() < 1) {//白名单长度为0
            LogUtil.i(TAG, "initData: 白名单长度为0");
            flKong.setVisibility(View.VISIBLE);
            return;
        }
        adapter = new MyAppWhiteAdapter(this.list, AppWhiteActivity.this);
        lvAppList.setAdapter(adapter);
        lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                MyAppWhiteAdapter.ViewHolder holder = (MyAppWhiteAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = MyAppWhiteAdapter.getIsSelected().get(position);
                LogUtil.i(TAG, "onItemClick:11111 " + flag);
                // 将CheckBox的选中状况记录下来
                MyAppWhiteAdapter.getIsSelected().put(position, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean isNet = NetworkUtil.isConnected();
        switch (v.getId()) {
            case R.id.tv_finish_stop_plan:
                finish();
                break;
            case R.id.tv_save:
                Intent intent = new Intent(AppWhiteActivity.this, AddAppWhiteActivity.class);
                intent.putExtra("ChildUUID", childUUID);
                startActivityForResult(intent, 0);
                break;
            case R.id.tv_remove_app://移除app
                if (!isNet) {
                    ToastUtils.showToast(AppWhiteActivity.this, "网络不通，请检查网络再试");
                    return;
                }
                removeApp();
                break;
            case R.id.tv_select_all://全选
                selectAll();
                break;
            default:
                break;
        }
    }

    /**
     * 全选
     */
    private void selectAll() {
        if (list.size() > 0) {
            allFlag = !allFlag;
            if (allFlag) {
                // 遍历list的长度，将MyAdapter中的map值全部设为true
                for (int i = 0; i < list.size(); i++) {
                    MyAppWhiteAdapter.getIsSelected().put(i, true);
                }
            } else {
                // 遍历list的长度，将MyAdapter中的map值全部设为false
                for (int i = 0; i < list.size(); i++) {
                    MyAppWhiteAdapter.getIsSelected().put(i, false);
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "白名单为空，请添加白名单", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 移除App
     */
    private void removeApp() {
        HashMap<Integer, Boolean> map = MyAppWhiteAdapter.getIsSelected();
        if(!(map+"").contains("true")){
            ToastUtils.showToast(MyApplication.getContext(),"请选择要加入黑名单的应用");
            return;
        }
        LogUtil.i(TAG, "removeApp: " + map);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (map.get(i)) {
                    AppDataBean appInfo = (AppDataBean) adapter.getItem(i);
                    String appName = appInfo.getAppname();
                    LogUtil.i(TAG, "AddApp: " + appName);
                    AppDataBean findApp = dao.queryBuilder()
                            .where(AppDataBeanDao.Properties.Appname.eq(appName))
                            .where(AppDataBeanDao.Properties.Childuuid.eq(childUUID))
                            .build().unique();
                    if (findApp != null) {
                        findApp.setAppwhitelist(CmdCommon.CMD_FLAG_BLACK);//1代表白名单
                        dao.update(findApp);
                    }
                }
            }
            list.clear();
            initData();
            adapter.notifyDataSetChanged();
            EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID,null), "requestWhiteApp");
        } else {
            Toast.makeText(this, "白名单为空，请添加白名单", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i(TAG, "onActivityResult: " + adapter);
        LogUtil.i(TAG, "onActivityResult: " + data);
        if(data!=null){
            switch (resultCode) { //resultCode为回传的标记
                case RESULT_OK:
                    list.clear();
                    initData();
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        regFlag=true;
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
    }

    /**
     * 请求孩子应用列表
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whiteAppData")
    private void OrderChildAppList(WhiteAppListEvent whiteAppList) {
        regFlag=true;
        hd.removeCallbacks(runnable);
        Message msg = Message.obtain();
        msg.what = 107;
        hd.sendMessage(msg);
    }
    private void initAppList() {
        List<AppDataBean> mlist = dao.queryBuilder()
                .where(AppDataBeanDao.Properties.Childuuid.eq(childUUID))
                .build().list();
        Log.e(TAG, "initData: "+mlist.size());
        if (mlist.size() > 0) {
            flKong.setVisibility(View.INVISIBLE);
            for (int i = 0; i < mlist.size(); i++) {
                Log.e(TAG, "initData: "+mlist.get(i).getAppwhitelist());
                if (mlist.get(i).getAppwhitelist().equals(CmdCommon.CMD_FLAG_WHITE)) {//是白名单
                    list.add(mlist.get(i));
                }
            }
        } else {//没有app数据
            Log.e(TAG, "initData: 没有app数据");
            flKong.setVisibility(View.VISIBLE);
            return;
        }
        if (list.size() < 1) {//白名单长度为0
            Log.e(TAG, "initData: 白名单长度为0");
            flKong.setVisibility(View.VISIBLE);
            return;
        }
        adapter = new MyAppWhiteAdapter(this.list, AppWhiteActivity.this);
        lvAppList.setAdapter(adapter);
        lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                MyAppWhiteAdapter.ViewHolder holder = (MyAppWhiteAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = MyAppWhiteAdapter.getIsSelected().get(position);
                LogUtil.i(TAG, "onItemClick:11111 " + flag);
                // 将CheckBox的选中状况记录下来
                MyAppWhiteAdapter.getIsSelected().put(position, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 请求孩子应用列表
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whiteAppDataBack")
    private void OrderChildAppListBack (WhiteAppBackEvent returndata) {
        regFlag=true;
        hd.removeCallbacks(runnable);
        Message msg = Message.obtain();
        msg.what = 108;
        hd.sendMessage(msg);
    }
}
