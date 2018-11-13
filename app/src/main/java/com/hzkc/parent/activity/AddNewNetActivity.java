package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.Bean.NetInfoBean;
import com.hzkc.parent.R;
import com.hzkc.parent.adapter.AddNetNewControlAdapter;
import com.hzkc.parent.event.RequestNetControlEvent;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.view.AffirmDialog;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddNewNetActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView tvFinishNetControl;
    private EditText etNetName;
    private String childUUID;
    private ListView listView;
    private LinearLayout flKong;
    private AffirmDialog affirmDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_net_new);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.newcolor));
        initView();
        initData();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvFinishNetControl = (ImageView) findViewById(R.id.iv_finish_back);
        etNetName = (EditText) findViewById(R.id.et_net_name);
        flKong = (LinearLayout) findViewById(R.id.fl_kongbai);
        listView = (ListView) findViewById(R.id.lv_net_list);
        childUUID = getIntent().getStringExtra("ChildUUID");
        tvTopTitle.setText("当前已禁用网址");
        tvFinishNetControl.setVisibility(View.VISIBLE);

        affirmDialog = new AffirmDialog(this);
        affirmDialog.setTitleText("您是否确定删除禁用网站？");
        affirmDialog.setAffirmClickListener(this);
        tvFinishNetControl.setOnClickListener(this);
    }
    private List<NetInfoBean> list;
    private AddNetNewControlAdapter adapter;
    private void initData() {
        list=new ArrayList<>();
        list.add(new NetInfoBean("百度","www.baidu.com"));
        list.add(new NetInfoBean("百度1","www.baidu.com"));
        list.add(new NetInfoBean("百度2","www.baidu.com"));
        list.add(new NetInfoBean("百度3","www.baidu.com"));
        if (list.size() < 1) {//网址数据为0
            LogUtil.i(TAG, "initData: 网址数据长度为0");
            flKong.setVisibility(View.VISIBLE);
            return;
        }
        adapter = new AddNetNewControlAdapter(this.list, AddNewNetActivity.this);
        adapter.setListener(new AddNetNewControlAdapter.ForbitCilckListener() {
            @Override
            public void onFbClick(int position, int type) {
                positions=position;
                affirmDialog.show();

            }
        });
        listView.setAdapter(adapter);
    }
    private int positions=-1;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish_back:
                finish();
                break;
            case R.id.affirm_cancel:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                break;
            case R.id.affirm_confirm:
                if (affirmDialog != null) {
                    affirmDialog.hide();
                }
                list.remove(positions);
                adapter.notifyDataSetChanged();
            default:
                break;
        }
    }

    private void save() {
        String netName = etNetName.getText().toString().trim();
        String netData = sp.getString(childUUID + "netList", "");
        if(netData.contains(netName)){
            Toast.makeText(this,"您输入的网址已经存在，请输入其他网址",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(netData)){
            sp.edit().putString(childUUID + "netList", netName).commit();
        }else{
            sp.edit().putString(childUUID + "netList", netData+"#,#"+netName).commit();
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        EventBus.getDefault().post(new RequestNetControlEvent(childUUID), "requestNetControl");
        finish();
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
