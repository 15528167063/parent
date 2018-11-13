package com.hzkc.parent.solider.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.adapter.ChooseChilldAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/6/7.
 */
public class ChooseChildrenActivity extends BaseActivity implements View.OnClickListener {
    public String childuuid, parentUUID;
    private List<ChildsTable> list;
    private ListView listView;
    private TextView  tv_top_title,tv_cancel_plan,tv_zhangdan;
    private ChooseChilldAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_children);

        tv_top_title = (TextView) findViewById(R.id.tv_top_title);
        tv_cancel_plan = (TextView) findViewById(R.id.tv_cancel_plan);
        tv_zhangdan = (TextView) findViewById(R.id.tv_save1);
        tv_cancel_plan.setVisibility(View.VISIBLE);
        tv_zhangdan.setVisibility(View.VISIBLE);
        tv_top_title.setText("选择士兵");
        tv_zhangdan.setText("确定");
        tv_cancel_plan.setOnClickListener(this);
        tv_zhangdan.setOnClickListener(this);

        listView=(ListView)findViewById(R.id.list_view) ;
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        childuuid = getIntent().getStringExtra("childUUid");
        parentUUID = sp.getString("parentUUID", "");
        initData();
        initView();
    }
    private void initData() {
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();       //获取保存的小孩列表信息
        list = childDao.queryBuilder().build().list();
    }
    protected void initView() {
        if (list == null || list.size() == 0) {
            return;
        }
        adapter=new ChooseChilldAdapter(list,this,childuuid);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChooseChilldAdapter.ViewHolder holder = (ChooseChilldAdapter.ViewHolder) view.getTag();
                Boolean flag = ChooseChilldAdapter.getIsSelected().get((int)id);
                for (int i = 0; i < list.size(); i++) {
                    ChooseChilldAdapter.getIsSelected().put(i, false);
                }
                ChooseChilldAdapter.getIsSelected().put((int)id, true);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel_plan:
                finish();
                break;
            case R.id.tv_save1:
                ChildsTable student = null;
                HashMap<Integer, Boolean> map = adapter.getIsSelected();
                if (!(map + "").contains("true")) {
                    ToastUtils.showToast(MyApplication.getContext(), "请选择士兵");
                    return;
                }
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        if (map.get(i) == null) {
                            return;
                        }
                        if (map.get(i)) {
                            student = (ChildsTable) adapter.getItem(i);
                            sp.edit().putString("ChildUUID", student.getChilduuid()).putString("childName", student.getName()).putString("childSex", student.getSex()).putString("childFrom", student.getChildfrom()).commit();
                            Log.e("-------team111" + childuuid, student.getName() + "getChildfrom：：" + student.getChildfrom());
                            finish();
                        }
                    }
                }
                break;
        }
    }
}
