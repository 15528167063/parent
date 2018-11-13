package com.hzkc.parent.fragment.appmanage;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.TotleAppAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.RequestChildAppListEvent;
import com.hzkc.parent.event.RequestWhiteAppListEvent;
import com.hzkc.parent.event.WhiteAppListEvent;
import com.hzkc.parent.fragment.BaseFragment;
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

/**
 * Created by lenovo-s on 2016/10/20.
 */

public class TotleAppFragment1 extends BaseFragment implements View.OnClickListener {

    private View v;
    private int id;
    private ListView listview;
    private TextView tv_add_te;
    private TextView tv_add_black;

    private TotleAppAdapter adapter;
    private List<AppDataBean> list;
    boolean allFlag = false;
    private AppDataBeanDao dao;
    private String childUUID;
    private String parentUUID;
    private LinearLayout flKong,lin_hasdata;

    private boolean regFlag;
    private Runnable runnable;
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 106:
                    if (!regFlag) {
                        ToastUtils.showToast(MyApplication.getContext(),"网络连接不通畅,请稍后再试");
                        flKong.setVisibility(View.VISIBLE);
                        lin_hasdata.setVisibility(View.GONE);
                    }
                    dissloading();
                    break;
                case 107://处理获取的applist
                    dissloading();
                    initAppList();
                    break;
                case 108://处理反馈
                    dissloading();
                    initAppList();
                    break;
                default:
                    break;
            }
        }
    };

    public static Fragment getInstance(int classesId) {
        TotleAppFragment1 fragment = new TotleAppFragment1();
        fragment.id = classesId;
        return fragment;
    }
    @Override
    public View initView() {
        regFlag=false;
        EventBus.getDefault().register(this);
        runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 106;
                hd.sendMessage(msg);
            }
        };
        v = View.inflate(getActivity(), R.layout.fragment_totle_app, null);
        listview=(ListView)v.findViewById(R.id.list_view);
        tv_add_te=(TextView) v.findViewById(R.id.tv_add_te);
        tv_add_black=(TextView) v.findViewById(R.id.tv_add_black);
        flKong = (LinearLayout) v.findViewById(R.id.fl_kongbai);
        flKong.setVisibility(View.GONE);
        lin_hasdata = (LinearLayout) v.findViewById(R.id.lin_hasdata);
        tv_add_te.setOnClickListener(this);
        tv_add_black.setOnClickListener(this);
        return v;
    }

    @Override
    public void iniData() {

        list = new ArrayList<>();
        childUUID = getActivity().getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");

         dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            for (int i = 0; i < mlist.size(); i++) {//加载全部应用
                if (mlist.get(i).getAppwhitelist().equals(CmdCommon.CMD_FLAG_WHITE)) {//是白名单
                list.add(mlist.get(i));
                }
            }
        } else {//没有app数据
            showLoading();
            sp.edit().putBoolean("isappuse",false).commit();    //用于标识区别  应用使用列表和全部列表
            EventBus.getDefault().post(new RequestChildAppListEvent(childUUID, parentUUID), "requestchildApp");
            hd.postDelayed(runnable, 8000);
            return;
        }
        if (list.size() < 1) {//白名单长度为0
            flKong.setVisibility(View.VISIBLE);
            lin_hasdata.setVisibility(View.GONE);
            return;
        }
        adapter = new TotleAppAdapter(this.list,getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                TotleAppAdapter.ViewHolder holder = (TotleAppAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = TotleAppAdapter.getIsSelected().get(position);
                // 将CheckBox的选中状况记录下来
                TotleAppAdapter.getIsSelected().put(position, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.tv_add_te){
            boolean isNet = NetworkUtil.isConnected();
            if (!isNet) {
                ToastUtils.showToast(getActivity(), "网络不通，请检查网络再试");
                return;
            }
            removeApp();
        }
        if(view.getId()== R.id.tv_add_black){
            boolean isNet = NetworkUtil.isConnected();
            if (!isNet) {
                ToastUtils.showToast(getActivity(), "网络不通，请检查网络再试");
                return;
            }
        }
    }
    /**
     * 移除App
     */
    public static final String TAG = "这是我打得一个removeApp:";
    private void removeApp() {
        HashMap<Integer, Boolean> map = adapter.getIsSelected();
        if(!(map+"").contains("true")){
            ToastUtils.showToast(MyApplication.getContext(),"请选择要加入管控的应用");
            return;
        }
        LogUtil.e(TAG, "removeApp: " + map);
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
            iniData();
            adapter.notifyDataSetChanged();


            EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID,null), "requestWhiteApp");
        } else {
            Toast.makeText(getActivity(), "白名单为空，请添加白名单", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAppList() {
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        LogUtil.i(TAG, "initData: "+mlist.size());
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            for (int i = 0; i < mlist.size(); i++) {
                LogUtil.i(TAG, "initData: "+mlist.get(i).getAppwhitelist());
                if (mlist.get(i).getAppwhitelist().equals(CmdCommon.CMD_FLAG_WHITE)) {//是白名单
                    list.add(mlist.get(i));
                }
            }
        } else {//没有app数据
            LogUtil.i(TAG, "initData: 没有app数据");
            flKong.setVisibility(View.VISIBLE);
            return;
        }
        if (list.size() < 1) {//白名单长度为0
            LogUtil.i(TAG, "initData: 白名单长度为0");
            flKong.setVisibility(View.VISIBLE);
            lin_hasdata.setVisibility(View.GONE);
            return;
        }
        adapter = new TotleAppAdapter(this.list, getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                TotleAppAdapter.ViewHolder holder = (TotleAppAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = TotleAppAdapter.getIsSelected().get(position);
                LogUtil.i(TAG, "onItemClick:11111 " + flag);
                // 将CheckBox的选中状况记录下来
                TotleAppAdapter.getIsSelected().put(position, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }
    /**
     * 请求孩子应用列表
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whiteAppData")
    private void OrderChildAppList(WhiteAppListEvent whiteAppList) {
        regFlag=true;
        hd.removeCallbacks(runnable);
        Message msg = Message.obtain();
        msg.what = 107;
        hd.sendMessage(msg);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        regFlag=true;
        dissloading();
        EventBus.getDefault().unregister(this);
    }

    public void updata() {
        list.clear();
        iniData();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
}
