package com.hzkc.parent.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.event.InitEvent;
import com.hzkc.parent.greendao.GreenDaoManager;
import com.hzkc.parent.greendao.entity.ChildsTable;
import com.hzkc.parent.greendao.gen.ChildsTableDao;
import com.hzkc.parent.jsondata.CmdCommon;

import org.simple.eventbus.EventBus;

/**
 * 孩子资料界面
 */
public class ChildDetailsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle,tv_name,tv_sex,tv_school,tv_class;
    private ImageView ivFinish,iv_head;
    private static final String TAG = "AddChildActivity";
    private String selectchildUUID;
    private ChildsTableDao childDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_informat);
        initView();
        initData();
    }
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_left);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_class = (TextView) findViewById(R.id.tv_class);
        tv_school = (TextView) findViewById(R.id.tv_school);
        iv_head = (ImageView) findViewById(R.id.iv_head);

        tvTopTitle.setText("孩子资料");
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
    }
    private void initData() {
        selectchildUUID = getIntent().getStringExtra("ChildUUID");
        childDao = GreenDaoManager.getInstance().getSession().getChildsTableDao();
        ChildsTable findChild = childDao.queryBuilder().where(ChildsTableDao.Properties.Childuuid.eq(selectchildUUID)).build().unique();
        if (findChild != null) {
            tv_school.setText(findChild.getSchool());
            tv_name.setText(findChild.getName());
            tv_class.setText(findChild.getNianji());

            if (findChild.getSex().equals(CmdCommon.FLAG_BOY)){
                tv_sex.setText("男");
                iv_head.setImageResource(R.drawable.child_detail_icon_boy);
            }else {
                tv_sex.setText("女");
                iv_head.setImageResource(R.drawable.child_detail_icon_girl);
            }
        }else {
            String parentUUID = sp.getString("parentUUID", "");
            EventBus.getDefault().post(new InitEvent(parentUUID), "init");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left://返回界面
                finish();
                break;
            default:
                break;
        }
    }



    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
