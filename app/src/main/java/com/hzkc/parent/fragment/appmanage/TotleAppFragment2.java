package com.hzkc.parent.fragment.appmanage;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class TotleAppFragment2 extends BaseFragment implements View.OnClickListener {

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

    public boolean isfirst;

    private boolean regFlag;
    private Runnable runnable;
    Handler hd = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 106:
                    dissloading();
                    if (!regFlag) { //网络不成功从本地
                        getDataFromDb();
                    }
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
        TotleAppFragment2 fragment = new TotleAppFragment2();
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


        //网络请求
        showLoading();

        AppDataBeanDao dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist==null || mlist.size() == 0) {//没有app数据
            sp.edit().putBoolean("isappuse",false).commit();    //用于标识区别  应用使用列表和全部列表
            sp.edit().putBoolean("isapplist",true).commit();    //主动申请才修改appp
            EventBus.getDefault().post(new RequestChildAppListEvent(childUUID, parentUUID), "requestchildApp");
            hd.postDelayed(runnable, 8000);
        }else {
            sp.edit().putBoolean("isappuse",false).commit();    //用于标识区别  应用使用列表和全部列表
            sp.edit().putBoolean("isapplist",true).commit();    //主动申请才修改appp
            EventBus.getDefault().post(new RequestChildAppListEvent(childUUID, parentUUID), "requestchildApp");
            hd.postDelayed(runnable, 8000);
        }





        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TotleAppAdapter.ViewHolder holder = (TotleAppAdapter.ViewHolder) view.getTag();
                Boolean flag = TotleAppAdapter.getIsSelected().get((int)id);
                TotleAppAdapter.getIsSelected().put((int)id, !flag);
                adapter.notifyDataSetChanged();
            }
        });

    }

    private boolean blackclick;
    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.tv_add_te){
            boolean isNet = NetworkUtil.isConnected();
            if (!isNet) {
                ToastUtils.showToast(getActivity(), "网络不通，请检查网络再试");
                return;
            }
            sp.edit().putBoolean("addapp",true).commit();
            blackclick=false;
            removeApp();
        }else
        if(view.getId()== R.id.tv_add_black){
            boolean isNet = NetworkUtil.isConnected();
            if (!isNet) {
                ToastUtils.showToast(getActivity(), "网络不通，请检查网络再试");
                return;
            }
            sp.edit().putBoolean("addapp",true).commit();
            blackclick=true;
            removeApp();
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
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if(map.get(i)==null){
                    return;
                }
                if (map.get(i)) {
                    AppDataBean appInfo = (AppDataBean) adapter.getItem(i);
                    String appName = appInfo.getAppname();
                    AppDataBean findApp = dao.queryBuilder().where(AppDataBeanDao.Properties.Appname.eq(appName)).where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().unique();
                    if (findApp != null) {
                        if(blackclick){
                            findApp.setAppwhitelist(CmdCommon.CMD_FLAG_BLACK);//2代表黑名单
                        }else {
                            findApp.setAppwhitelist(CmdCommon.CMD_FLAG_Te);//3代表特权名单
                        }
                        dao.update(findApp);
                    }
                }
                TotleAppAdapter.getIsSelected().put(i,false);
                adapter.notifyDataSetChanged();//856
            }
            list.clear();
//            iniData();
            getDataFromDb();
            adapter.notifyDataSetChanged();
//            EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID), "requestWhiteApp");
        } else {
            Toast.makeText(getActivity(), "白名单为空，请添加白名单", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAppList() {
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            list.clear();
            for (int i = 0; i < mlist.size(); i++) {
                LogUtil.i(TAG, "initData: "+mlist.get(i).getAppname()+mlist.get(i).getAppwhitelist());
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

    }
    /**
     * 请求孩子应用列表
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whiteAppData")
    private void OrderChildAppList(WhiteAppListEvent whiteAppList) {
        regFlag=true;
        hd.removeCallbacks(runnable);
        Message msg = Message.obtain();
        Log.e("--------从服务器返回应用列表33","----");
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
//        iniData();
        getDataFromDb();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private  List<AppDataBean> mlist =null;
    //网络不成功从本地
    private void getDataFromDb() {
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            for (int i = 0; i < mlist.size(); i++) {//加载全部应用
                if (mlist.get(i).getAppwhitelist().equals(CmdCommon.CMD_FLAG_WHITE)) {//是白名单
                    list.add(mlist.get(i));
                }
            }
        }
        if (list.size() < 1) {//白名单长度为0
            flKong.setVisibility(View.VISIBLE);
            lin_hasdata.setVisibility(View.GONE);
            return;
        }
        if(adapter==null){
            adapter = new TotleAppAdapter(this.list, getActivity());
            listview.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }


}
