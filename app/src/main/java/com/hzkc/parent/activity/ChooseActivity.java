package com.hzkc.parent.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.hzkc.parent.R;
import com.hzkc.parent.adapter.DemoAdapter;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.impl.ScaleTransformer;
import com.hzkc.parent.view.GalleryLayoutManager;

import java.util.List;


/**
 * Created by chensuilun on 2017/3/22.
 */

public class ChooseActivity extends BaseActivity {

    RecyclerView mMainRecycle1;
    public String childuuid, parentUUID;
    private List<ChildsTable> list;
    private IntentFilter mFilter;
    private MeBroadcastReceiver meBroadcastReceiver;
    private DemoAdapter demoAdapter1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);
        mMainRecycle1 = (RecyclerView) findViewById(R.id.main_recycle1);
        childuuid = getIntent().getStringExtra("childUUid");
        parentUUID = sp.getString("parentUUID", "");
        initData();
        initView();
    }

    private void initData() {
        meBroadcastReceiver = new MeBroadcastReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction("sp");
        registerReceiver(meBroadcastReceiver, mFilter);
        ChildsTableDao childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();       //获取保存的小孩列表信息
        list = childDao.queryBuilder().build().list();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(meBroadcastReceiver);
    }

    protected void initView() {
        if (list == null || list.size() == 0) {
            return;
        }
        GalleryLayoutManager layoutManager1 = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL);
        layoutManager1.attach(mMainRecycle1, 0);
        layoutManager1.setItemTransformer(new ScaleTransformer());
        demoAdapter1 = new DemoAdapter(childuuid, list, ChooseActivity.this) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return super.onCreateViewHolder(parent, viewType);
            }
        };
        demoAdapter1.setOnItemClickListener(new DemoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mMainRecycle1.smoothScrollToPosition(position);
            }
        });
        mMainRecycle1.setAdapter(demoAdapter1);
    }
    private Handler handler=new Handler();
    public class MeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("sp".equals(intent.getAction())) {
                ChildsTable student = (ChildsTable)intent.getSerializableExtra("student");
                sp.edit().putString("ChildUUID", student.getChilduuid()).putString("childName",student.getName()).putString("childSex",student.getSex()).putString("childFrom",student.getChildfrom()).commit();
                Log.e("-------team111"+childuuid, sp.getBoolean("team"+student.getChilduuid(), false)+"getChildfrom：："+student.getChildfrom());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },300);
            }
        }
    }
}