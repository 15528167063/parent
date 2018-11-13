package com.hzkc.parent.fragment.phonemanage;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.BlackPhoneAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.fragment.BaseFragment;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.PhoneData;
import com.hzkc.parent.greendao.gen.PhoneDataDao;
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

public class BlackPhoneFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private int id;
    private ListView listview;
    private TextView tv_remove_app;
    private BlackPhoneAdapter adapter;
    private List<PhoneData> list;
    boolean allFlag = false;
    private PhoneDataDao dao;
    private String childUUID;
    private String parentUUID;
    private LinearLayout flKong,lin_hasdata;
    private boolean flag =false; //是否对应用进行过操作
    public static final String TAG = "-----BlackAppFragment:";
    public static Fragment getInstance(int classesId) {
        BlackPhoneFragment fragment = new BlackPhoneFragment();
        fragment.id = classesId;
        return fragment;
    }

    @Override
    public View initView() {
        v = View.inflate(getActivity(), R.layout.fragment_black_app, null);
        listview=(ListView)v.findViewById(R.id.list_view);
        tv_remove_app=(TextView) v.findViewById(R.id.tv_remove_app);
        lin_hasdata = (LinearLayout) v.findViewById(R.id.lin_hasdata);
        flKong = (LinearLayout) v.findViewById(R.id.fl_kongbai);
        tv_remove_app.setOnClickListener(this);
        return v;
    }

    @Override
    public void iniData() {
        childUUID = getActivity().getIntent().getStringExtra("ChildUUID");
        parentUUID = sp.getString("parentUUID", "");
        if(list==null){
            list=new ArrayList<>();
        }
        dao = GreenDaoManager.getInstance().getSession().getPhoneDataDao();
        List<PhoneData> mlist = dao.queryBuilder().where(PhoneDataDao.Properties.Childuuid.eq(childUUID)).build().list();
        if (mlist.size() > 0) {
            flKong.setVisibility(View.GONE);
            lin_hasdata.setVisibility(View.VISIBLE);
            for (int i = 0; i < mlist.size(); i++) {
                if (mlist.get(i).getState().equals(CmdCommon.CMD_PHONE_BLACK)) {//不是白名单
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

        adapter = new BlackPhoneAdapter(list, getActivity());
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                BlackPhoneAdapter.ViewHolder holder = (BlackPhoneAdapter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                Boolean flag = BlackPhoneAdapter.getIsSelected().get((int)id);
                LogUtil.i(TAG, "onItemClick:11111 " + flag);
                // 将CheckBox的选中状况记录下来
                BlackPhoneAdapter.getIsSelected().put(position, !flag);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        boolean isNet = NetworkUtil.isConnected();
        if(view.getId()== R.id.tv_remove_app){
            if (!isNet) {
                ToastUtils.showToast(getActivity(), "网络不通，请检查网络再试");
                return;
            }
            sp.edit().putBoolean("addapps",true).commit();
            AddApp();
        }
    }

    /**
     * 移除App
     */
    private void AddApp() {
        HashMap<Integer, Boolean> map = BlackPhoneAdapter.getIsSelected();
        if (!(map + "").contains("true")) {
            ToastUtils.showToast(MyApplication.getContext(), "请选择你要添加的号码");
            return;
        }
        LogUtil.i(TAG, "AddApp: " + map);
        if ((map + "").contains("true")) {
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (map.get(i)) {
                        PhoneData appInfo = (PhoneData) adapter.getItem(i);
                        String phone = appInfo.getPhone();
                        String name = appInfo.getName();
                        PhoneData findApp = dao.queryBuilder().where(PhoneDataDao.Properties.Phone.eq(phone)).where(PhoneDataDao.Properties.Name.eq(name)).where(PhoneDataDao.Properties.Childuuid.eq(childUUID)).build().unique();
                        if (findApp != null) {
                            findApp.setState(CmdCommon.CMD_PHONE_WHITE);//0代表白名单
                            dao.update(findApp);
                        }
                    }
                }
                list.clear();
                iniData();
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getActivity(), "没有电话黑名单，请添加", Toast.LENGTH_SHORT).show();
            }
            flag = true;
        } else {
            Toast.makeText(getActivity(), "请选择你要添加的电话", Toast.LENGTH_SHORT).show();
        }
    }
    public void updata() {
        if(list==null){
            list=new ArrayList<>();
        }else {
            list.clear();
        }
        iniData();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
}