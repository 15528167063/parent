package com.hzkc.parent.fragment.phonemanage;

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
import com.hzkc.parent.adapter.WhitePhoneAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.RequestChildAppListEvent;
import com.hzkc.parent.event.WhitePhoneListEvent;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.PhoneData;
import com.hzkc.parent.greendao.gen.PhoneDataDao;
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

public class WhitePhoneFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private int id;
    private ListView listview;
    private TextView tv_add_black;

    private WhitePhoneAdapter adapter;
    private List<PhoneData> list;
    boolean allFlag = false;
    private PhoneDataDao dao;
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
        WhitePhoneFragment fragment = new WhitePhoneFragment();
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
        v = View.inflate(getActivity(), R.layout.fragment_white_phone, null);
        listview=(ListView)v.findViewById(R.id.list_view);
        tv_add_black=(TextView) v.findViewById(R.id.tv_add_black);
        flKong = (LinearLayout) v.findViewById(R.id.fl_kongbai);
        flKong.setVisibility(View.GONE);
        lin_hasdata = (LinearLayout) v.findViewById(R.id.lin_hasdata);
        tv_add_black.setOnClickListener(this);
        return v;
    }

    @Override
    public void iniData() {
        if(list==null){
            list=new ArrayList<>();
        }
        childUUID = getActivity().getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        dao = GreenDaoManager.getInstance().getSession().getPhoneDataDao();

        //网络请求
        showLoading();
        EventBus.getDefault().post(new RequestChildAppListEvent(childUUID, parentUUID), "requestWhitePhone");
        adapter=new WhitePhoneAdapter(list,getActivity());
        listview.setAdapter(adapter);
        hd.postDelayed(runnable, 8000);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WhitePhoneAdapter.ViewHolder holder = (WhitePhoneAdapter.ViewHolder) view.getTag();
                Boolean flag = WhitePhoneAdapter.getIsSelected().get((int)id);
                WhitePhoneAdapter.getIsSelected().put((int)id, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.tv_add_black){
            boolean isNet = NetworkUtil.isConnected();
            if (!isNet) {
                ToastUtils.showToast(getActivity(), "网络不通，请检查网络再试");
                return;
            }
            sp.edit().putBoolean("addapps",true).commit();
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
            ToastUtils.showToast(MyApplication.getContext(),"请选择你要添加的号码");
            return;
        }
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if(map.get(i)==null){
                    return;
                }
                if (map.get(i)) {
                    PhoneData appInfo = (PhoneData) adapter.getItem(i);
                    String phone = appInfo.getPhone();
                    String name = appInfo.getName();
                    PhoneData findApp =null;
                    findApp = dao.queryBuilder().where(PhoneDataDao.Properties.Phone.eq(phone)).where(PhoneDataDao.Properties.Name.eq(name)).where(PhoneDataDao.Properties.Childuuid.eq(childUUID)).build().unique();
                    if (findApp != null) {
                        findApp.setState(CmdCommon.CMD_PHONE_BLACK);//0代表白名单
                    }
                    dao.update(findApp);
                }
                WhitePhoneAdapter.getIsSelected().put(i,false);
                adapter.notifyDataSetChanged();
            }
            list.clear();
            getDataFromDb();
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), "白名单为空，请添加白名单", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAppList() {
        List<PhoneData> mlist = dao.queryBuilder().where(PhoneDataDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            list.clear();
            for (int i = 0; i < mlist.size(); i++) {
                LogUtil.e(TAG, "initData: "+mlist.get(i).getName()+mlist.get(i).getPhone());
                if (mlist.get(i).getState().equals(CmdCommon.CMD_PHONE_WHITE)) {//是白名单
                    list.add(mlist.get(i));
                }
            }
        } else {//没有app数据
            LogUtil.e(TAG, "initData: 没有phone数据");
            flKong.setVisibility(View.VISIBLE);
            return;
        }
        if (list.size() < 1) {//白名单长度为0
            LogUtil.e(TAG, "initData: 白名单长度为0");
            flKong.setVisibility(View.VISIBLE);
            lin_hasdata.setVisibility(View.GONE);
            return;
        }
        adapter = new WhitePhoneAdapter(this.list, getActivity());
        listview.setAdapter(adapter);

    }
    /**
     * 请求孩子应用列表
     */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "whitePhoneData")
    private void OrderChildAppList(WhitePhoneListEvent whiteAppList) {
        regFlag=true;
        hd.removeCallbacks(runnable);
        Message msg = Message.obtain();
        Log.e("WhitePhoneFragment-107:","----");
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
        if(list==null){
            list=new ArrayList<>();
        }else {
            list.clear();
        }
        getDataFromDb();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }


    //网络不成功从本地
    private void getDataFromDb() {
        List<PhoneData> mlist = dao.queryBuilder().where(PhoneDataDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            for (int i = 0; i < mlist.size(); i++) {//加载全部应用
                if (mlist.get(i).getState().equals(CmdCommon.CMD_PHONE_WHITE)) {//是白名单
                    list.add(mlist.get(i));
                }
            }
        }
        if (list.size() < 1) {//白名单长度为0
            flKong.setVisibility(View.VISIBLE);
            lin_hasdata.setVisibility(View.GONE);
            return;
        }
        adapter = new WhitePhoneAdapter(this.list, getActivity());
        listview.setAdapter(adapter);
    }
}
