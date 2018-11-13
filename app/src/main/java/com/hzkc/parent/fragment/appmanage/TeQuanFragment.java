package com.hzkc.parent.fragment.appmanage;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.activity.AppManageActivity;
import com.hzkc.parent.adapter.TeQuanAppWhiteAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.AppDataBean;
import com.hzkc.parent.greendao.gen.AppDataBeanDao;
import com.hzkc.parent.jsondata.CmdCommon;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.NetworkUtil;
import com.hzkc.parent.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lenovo-s on 2016/10/20.
 */

public class TeQuanFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private int id;
    private ListView listview;
    private TextView tv_add_white,tv_add_black;


    private TeQuanAppWhiteAdapter adapter;
    private List<AppDataBean> list;
    boolean allFlag = false;
    private AppDataBeanDao dao;
    private String childUUID;
    private String parentUUID;
    private LinearLayout flKong,lin_hasdata;
    private boolean flag =false; //是否对应用进行过操作
    public static final String TAG = "-----BlackAppFragment:";
    public static Fragment getInstance(int classesId) {
        TeQuanFragment fragment = new TeQuanFragment();
        fragment.id = classesId;
        return fragment;
    }

    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.fragment_tequan_app, null);
        listview=(ListView)v.findViewById(R.id.list_view);
        tv_add_black=(TextView) v.findViewById(R.id.tv_add_black);
        tv_add_white=(TextView) v.findViewById(R.id.tv_add_white);
        lin_hasdata = (LinearLayout) v.findViewById(R.id.lin_hasdata);
        flKong = (LinearLayout) v.findViewById(R.id.fl_kongbai);
        tv_add_black.setOnClickListener(this);
        tv_add_white.setOnClickListener(this);
        return v;
    }

    @Override
    public void iniData() {
        childUUID = getActivity().getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        list = new ArrayList<>();
        dao = GreenDaoManager.getInstance().getSession().getAppDataBeanDao();
        List<AppDataBean> mlist = dao.queryBuilder().where(AppDataBeanDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            for (int i = 0; i < mlist.size(); i++) {
                if (mlist.get(i).getAppwhitelist().equals(CmdCommon.CMD_FLAG_Te)) {//特权名单
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
        adapter = new TeQuanAppWhiteAdapter(this.list, getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                TeQuanAppWhiteAdapter.ViewHolder holder = (TeQuanAppWhiteAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = TeQuanAppWhiteAdapter.getIsSelected().get((int)id);
                LogUtil.i(TAG, "onItemClick:11111 " + flag+position);
                // 将CheckBox的选中状况记录下来
                if(flag==null){
                    flag=false;
                }
                TeQuanAppWhiteAdapter.getIsSelected().put(position, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public  boolean blackclick;
    @Override
    public void onClick(View view) {
        boolean isNet = NetworkUtil.isConnected();
        if(view.getId()== R.id.tv_add_black){
            if (!isNet) {
                ToastUtils.showToast(getActivity(), "网络不通，请检查网络再试");
                return;
            }
            sp.edit().putBoolean("addapp",true).commit();
            blackclick=true;
            AddApp();
        }
        if(view.getId()== R.id.tv_add_white){
            if (!isNet) {
                ToastUtils.showToast(getActivity(), "网络不通，请检查网络再试");
                return;
            }
            sp.edit().putBoolean("addapp",true).commit();
            blackclick=false;
            AddApp();
        }
    }

    /**
     * 移除App
     */
    private void AddApp() {
        HashMap<Integer, Boolean> map = TeQuanAppWhiteAdapter.getIsSelected();
        if (!(map + "").contains("true")) {
            ToastUtils.showToast(MyApplication.getContext(), "请选择你要添加的应用");
            return;
        }
        LogUtil.i(TAG, "AddApp: " + map);
        if ((map + "").contains("true")) {
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (map.get(i)) {
                        AppDataBean appInfo = (AppDataBean) adapter.getItem(i);
                        String apppackName = appInfo.getApppackgename();
                        AppDataBean findApp = dao.queryBuilder()
                                .where(AppDataBeanDao.Properties.Apppackgename.eq(apppackName))
                                .where(AppDataBeanDao.Properties.Childuuid.eq(childUUID))
                                .build().unique();
                        if (findApp != null) {
                            if(blackclick){
                                findApp.setAppwhitelist(CmdCommon.CMD_FLAG_BLACK);
                            }else {
                                findApp.setAppwhitelist(CmdCommon.CMD_FLAG_WHITE);
                            }
                            dao.update(findApp);
                            ((AppManageActivity)getActivity()).addDatas(findApp);
                        }
                    }
                }
                list.clear();
                iniData();
                adapter.notifyDataSetChanged();
//                EventBus.getDefault().post(new RequestWhiteAppListEvent(childUUID, parentUUID), "requestWhiteApp");
            } else {
                Toast.makeText(getActivity(), "没有应用黑名单，请添加", Toast.LENGTH_SHORT).show();
            }
            flag = true;
        } else {
            Toast.makeText(getActivity(), "请选择你要添加的应用", Toast.LENGTH_SHORT).show();
        }
    }
    public void updata() {
        list.clear();
        iniData();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
}
