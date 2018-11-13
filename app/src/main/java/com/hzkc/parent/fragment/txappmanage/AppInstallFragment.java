package com.hzkc.parent.fragment.txappmanage;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.InstallAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.event.InstallAppListEvent;
import com.hzkc.parent.event.RequestInstallEvent;
import com.hzkc.parent.event.UnInstallAppListEvent;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.utils.ToastUtils;
import com.hzkc.parent.view.AffirmDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by lenovo-s on 2016/10/20.
 */

public class AppInstallFragment extends BaseFragment implements View.OnClickListener {
    private View v;
    private int id;
    public ListView listView;
    public TextView tv_install;
    public LinearLayout flKong,lin_hasdata;
    public InstallAdapter adapter;
    private List<AppDataBean> list;
    private AppDataBeanDao dao;
    private String childUUID;
    private String parentUUID;
    public ImageView iv_install;
    private AffirmDialog affirmDialog;
    public AppInstallFragment() {
    }
    public static Fragment getInstance(int classesId) {
        AppInstallFragment fragment = new AppInstallFragment();
        fragment.id = classesId;
        return fragment;
    }
    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.activity_app_manage, null);
        EventBus.getDefault().register(this);
        listView= (ListView) v.findViewById(R.id.list_view);
        tv_install= (TextView) v.findViewById(R.id.tv_install);
        flKong= (LinearLayout) v.findViewById(R.id.fl_kongbai);
        lin_hasdata= (LinearLayout) v.findViewById(R.id.lin_hasdata);
        iv_install=(ImageView) v.findViewById(R.id.iv_yjgk);
        affirmDialog = new AffirmDialog(getActivity());
        affirmDialog.setTitleText("是否确认允许孩子安装应用程序?");
        affirmDialog.setCancelText("允许");
        affirmDialog.setEnsureText("禁止");
        childUUID = getActivity().getIntent().getStringExtra("ChildUUID");
        if(sp.getString("install" + childUUID, "").equals("0")){
            iv_install.setImageResource(R.drawable.onff);
        }else {
            iv_install.setImageResource(R.drawable.oon);
        }

        affirmDialog.setAffirmClickListener(this);
        iv_install.setOnClickListener(this);
        tv_install.setOnClickListener(this);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void iniData() {
        parentUUID = sp.getString("parentUUID", "");
        list = new ArrayList<>();
        dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            for (int i = 0; i < mlist.size(); i++) {
                if(mlist.get(i)==null || mlist.get(i).getIssystem()==null){
                    if(!mlist.get(i).getAppwhitelist().equals("4")){
                        list.add(mlist.get(i));
                    }
                    continue;
                }
                if(mlist.get(i).getIssystem().equals("0") && !mlist.get(i).getAppwhitelist().equals("4")){
                    list.add(mlist.get(i));
                }
            }
        } else {//没有app数据
            flKong.setVisibility(View.VISIBLE);
            lin_hasdata.setVisibility(View.GONE);
            return;
        }
        if (list.size() < 1) {//白名单长度为0
            flKong.setVisibility(View.VISIBLE);
            lin_hasdata.setVisibility(View.GONE);
            return;
        }
        adapter = new InstallAdapter(this.list, getActivity(),parentUUID,childUUID);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                InstallAdapter.ViewHolder holder = (InstallAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = InstallAdapter.getIsSelected().get((int)id);
                // 将CheckBox的选中状况记录下来
                InstallAdapter.getIsSelected().put((int)id, !flag);
//                if(!flag){
//                    ((ImageView)(listView.getChildAt((int)id).findViewById(R.id.iv_select_flag))).setImageResource(R.drawable.white_list_check_y);
//                }else {
//                    ((ImageView)(listView.getChildAt((int)id).findViewById(R.id.iv_select_flag))).setImageResource(R.drawable.white_list_check_n);
//                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_yjgk:
                if (affirmDialog != null) {
                    affirmDialog.show();
                }
                break;
            case R.id.affirm_confirm:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                if(!sp.getString("install" + childUUID, "").equals("0")){
                    EventBus.getDefault().post(new RequestInstallEvent(childUUID, parentUUID,"0"), "requestInstall");
                }
                break;
            case R.id.tv_install:
                addInstallApp();

                break;
            case R.id.affirm_cancel:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                if(!sp.getString("install" + childUUID, "").equals("1")){
                    EventBus.getDefault().post(new RequestInstallEvent(childUUID, parentUUID,"1"), "requestInstall");
                }
                break;
        }
    }

    /**
     * 卸载应用程序
     */

    private void addInstallApp() {
        HashMap<Integer, Boolean> map = adapter.getIsSelected();
        if(!(map+"").contains("true")){
            ToastUtils.showToast(MyApplication.getContext(),"请选择要卸载的应用");
            return;
        }

        String childLineState = sp.getString(childUUID + "online", "");
        if (childLineState.equals(CmdCommon.CMD_OFFLINE)) {//下线
            ToastUtils.showToast(MyApplication.getContext(), "孩子处于离线状态,无法卸载应用程序");
            return;
        }

        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                dissloading();
                Toast.makeText(getActivity(), "发送成功，等待孩子端自动卸载。", Toast.LENGTH_SHORT).show();
            }
        },1000);

        if (list.size() > 0) {
            List<AppDataBean>datas=new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (map.get(i) == null) {
                    return;
                }
                if (map.get(i)) {
                    AppDataBean appInfo = (AppDataBean) adapter.getItem(i);
                    datas.add(appInfo);
                    String appName = appInfo.getAppname();
                    Log.e("----------appname", appName);
//                    list.remove(appInfo);
//                    InstallAdapter.getIsSelected().put(i,false);
                    AppDataBean findApp = dao.queryBuilder().where(AppDataBeanDao.Properties.Appname.eq(appName)).where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().unique();
                    if (findApp != null) {
                        findApp.setAppwhitelist("4");//4代表 暂时卸载
                    }
                    dao.update(findApp);
                }
            }
            list.clear();
            adapter.notifyDataSetChanged();
            iniData();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showLoading();
                }
            });
            EventBus.getDefault().post(new UnInstallAppListEvent(childUUID, parentUUID,datas), "UnInstallAppListEvent");
        }
    }


    public Handler handle=new Handler();
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 请求孩子应用列表
     * */
    @Subscriber(mode = ThreadMode.ASYNC, tag = "AppInstallEnble")
    private void InstallAppListBack (InstallAppListEvent returndata) {
        if(returndata.f.equals("0")){
            sp.edit().putString("install"+childUUID,"0").commit();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iv_install.setImageResource(R.drawable.onff);
                    Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            sp.edit().putString("install"+childUUID,"1").commit();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iv_install.setImageResource(R.drawable.oon);
                    Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
