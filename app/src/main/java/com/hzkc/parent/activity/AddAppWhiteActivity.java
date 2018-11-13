package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.MyAddAppWhiteAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.RequestWhiteAppListEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddAppWhiteActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvSelectAll;
    private TextView tvAddApp;
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private ListView lvAppList;
    private String childUUID;
    private LinearLayout flKong;
    private String parentUUID;
    private MyAddAppWhiteAdapter adapter;
    private List<AppDataBean> list;
    boolean allFlag = false;
    private AppDataBeanDao dao;
    private boolean flag =false; //是否对应用进行过操作

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_app_white);
        initView();
        initData();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvAddApp = (TextView) findViewById(R.id.tv_add_app);
        tvSelectAll = (TextView) findViewById(R.id.tv_select_all);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        lvAppList = (ListView) findViewById(R.id.lv_app_list);
        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);

        tvTopTitle.setText("黑名单应用");
        childUUID = getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");

        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        tvAddApp.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        list = new ArrayList<>();
    }

    private void initData() {
        dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder()
                .where(AppDataBeanDao.Properties.Childuuid.eq(childUUID))
                .build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.INVISIBLE);
            for (int i = 0; i < mlist.size(); i++) {
                if (!mlist.get(i).getAppwhitelist().equals(CmdCommon.CMD_FLAG_WHITE)) {//不是白名单
                    list.add(mlist.get(i));
                }
            }
        } else {//没有app数据
            flKong.setVisibility(View.VISIBLE);
            LogUtil.i(TAG, "initData: 没有app数据");
            return;
        }
        if (list.size() < 1) {//白名单长度为0
            flKong.setVisibility(View.VISIBLE);
            LogUtil.i(TAG, "initData: 白名单长度为0");
            return;
        }
        adapter = new MyAddAppWhiteAdapter(this.list, AddAppWhiteActivity.this);
        lvAppList.setAdapter(adapter);
        lvAppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                MyAddAppWhiteAdapter.ViewHolder holder = (MyAddAppWhiteAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = MyAddAppWhiteAdapter.getIsSelected().get(position);
                LogUtil.i(TAG, "onItemClick:11111 " + flag);
                // 将CheckBox的选中状况记录下来
                MyAddAppWhiteAdapter.getIsSelected().put(position, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        boolean isNet = NetworkUtil.isConnected();
        if (!isNet) {
            ToastUtils.showToast(AddAppWhiteActivity.this, "网络不通，请检查网络再试");
            return;
        }
        switch (v.getId()) {
            case R.id.iv_finish:
                Intent intent = new Intent();
                if(flag){
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
            case R.id.tv_add_app://添加白名单app
                if (!isNet) {
                    ToastUtils.showToast(AddAppWhiteActivity.this, "网络不通，请检查网络再试");
                    return;
                }
                AddApp();
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
                    MyAddAppWhiteAdapter.getIsSelected().put(i, true);
                }
            } else {
                // 遍历list的长度，将MyAdapter中的map值全部设为false
                for (int i = 0; i < list.size(); i++) {
                    MyAddAppWhiteAdapter.getIsSelected().put(i, false);
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "没有应用黑名单，请添加", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 移除App
     */
    private void AddApp() {
        HashMap<Integer, Boolean> map = MyAddAppWhiteAdapter.getIsSelected();
        if(!(map+"").contains("true")){
            ToastUtils.showToast(MyApplication.getContext(),"请选择要加入白名单的应用");
            return;
        }
        LogUtil.i(TAG, "AddApp: " + map);
        if((map+"").contains("true")){
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
                            findApp.setAppwhitelist(CmdCommon.CMD_FLAG_WHITE);//1代表白名单
                            dao.update(findApp);
                        }
                    }
                }
                list.clear();
                initData();
                adapter.notifyDataSetChanged();
                EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID,null), "requestWhiteApp");
            } else {
                Toast.makeText(this, "没有应用黑名单，请添加", Toast.LENGTH_SHORT).show();
            }
            flag=true;
        }else {
            Toast.makeText(this, "请选择你要添加的应用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            if(flag){
                setResult(RESULT_OK, intent);
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
