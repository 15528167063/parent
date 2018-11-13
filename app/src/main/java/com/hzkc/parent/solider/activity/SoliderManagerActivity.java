package com.hzkc.parent.solider.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.activity.BaseActivity;
import com.hzkc.parent.adapter.MySoliderListviewAdapter;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildContrlFlag;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.gen.ChildContrlFlagDao;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class SoliderManagerActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private ListView lvChildList;
    private ChildsTableDao childDao;
    private List<ChildsTable> list;
    private MySoliderListviewAdapter adapter;
    private ChildContrlFlagDao flagDao;
    private String parentUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_manager);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
    }

    private void initData() {
        childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        flagDao = GreenDaoManager.getInstance().getSession().getChildContrlFlagDao();
        List<ChildContrlFlag> mlist = flagDao.queryBuilder().where(ChildContrlFlagDao.Properties.Parentuuid.eq(parentUUID)).build().list();
        LogUtil.i(TAG, "initData: size:"+mlist.size());
        if(mlist.size()>0){
            for (int i = 0; i < mlist.size(); i++) {
                ChildsTable findchild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(mlist.get(i).getChilduuid())).build().unique();
                list.add(findchild);
            }
            if (this.list.size() > 0) {
                adapter = new MySoliderListviewAdapter(this.list, this);
                lvChildList.setAdapter(adapter);
                lvChildList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectChilduuid = SoliderManagerActivity.this.list.get(position).getChilduuid();
                        String childFrom = SoliderManagerActivity.this.list.get(position).getChildfrom();
                        Intent intent = new Intent(SoliderManagerActivity.this, SoliderDetailActivity.class);
                        intent.putExtra("ChildUUID",selectChilduuid);
                        intent.putExtra("childFrom",childFrom);
                        intent.putExtra("type","1");
                        LogUtil.e("childFromchildFrom", "onItemClick: " + childFrom);
                        startActivity(intent);
                    }
                });
            } else {//暂无士兵
                ToastUtils.showToast(MyApplication.getContext(), "暂无士兵");
            }
        }else{
            ToastUtils.showToast(MyApplication.getContext(), "暂无士兵");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        initData();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        lvChildList = (ListView) findViewById(R.id.lv_child_list);
        parentUUID = sp.getString("parentUUID", "");

        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        tvTopTitle.setText("士兵管理");
        list=new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            default:
                break;
        }
    }

}
