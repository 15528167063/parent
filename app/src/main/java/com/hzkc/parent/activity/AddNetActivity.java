package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzkc.parent.R;
import com.hzkc.parent.event.RequestNetControlEvent;
import com.jaeger.library.StatusBarUtil;

import org.simple.eventbus.EventBus;

public class AddNetActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView tvFinishNetControl;
    private EditText etNetName;
    private TextView tvNet1;
    private TextView tvNet2;
    private TextView tvNet3;
    private TextView tvSaveNet;
    private String childUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_net);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
        initData();
    }

    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvFinishNetControl = (ImageView) findViewById(R.id.iv_finish);
        etNetName = (EditText) findViewById(R.id.et_net_name);
        tvNet1 = (TextView) findViewById(R.id.tv_net_1);
        tvNet2 = (TextView) findViewById(R.id.tv_net_2);
        tvNet3 = (TextView) findViewById(R.id.tv_net_3);
        tvSaveNet = (TextView) findViewById(R.id.tv_save_net);
        childUUID = getIntent().getStringExtra("ChildUUID");

        tvTopTitle.setText("添加网址");
        tvFinishNetControl.setVisibility(View.VISIBLE);
        tvFinishNetControl.setOnClickListener(this);
        tvNet1.setOnClickListener(this);
        tvNet2.setOnClickListener(this);
        tvNet3.setOnClickListener(this);
        tvSaveNet.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_net_1:
                etNetName.setText("baidu");
                break;
            case R.id.tv_net_2:
                etNetName.setText("qq");
                break;
            case R.id.tv_net_3:
                etNetName.setText("sohu");
                break;
            case R.id.tv_save_net:
                save();
                break;
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
