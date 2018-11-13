package com.hzkc.parent.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ChangeNcActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private TextView tvMyNc;
    private TextView ivFinish;
    private EditText etMyNc;
    private TextView btComfirm;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nc);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initView();
    }
    private void initView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvMyNc = (TextView) findViewById(R.id.tv_my_nc);
        ivFinish = (TextView) findViewById(R.id.tv_finish_accountmanager);
        etMyNc = (EditText) findViewById(R.id.et_my_nc);
        btComfirm = (TextView) findViewById(R.id.bt_comfirm);
        ivFinish.setVisibility(View.VISIBLE);

//        String extra = getIntent().getStringExtra("join");
//        if(extra.equals("0")){
//            ivFinish.setText("我的");
//        }
        String nc = sp.getString("nc", "");
        userid = sp.getString("parentUUID", "");
        tvMyNc.setText("当前昵称为： "+nc);
        tvTopTitle.setText("修改昵称");
        ivFinish.setOnClickListener(this);
        btComfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finish_accountmanager:
                finish();
                break;
            case R.id.bt_comfirm:
                try {
                    changeNc();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    /**
     * 修改昵称
     * */
    private void changeNc() throws UnsupportedEncodingException {
        final String nc = etMyNc.getText().toString().trim();
        if(TextUtils.isEmpty(nc)){
            ToastUtils.showToast(ChangeNcActivity.this,"输入的昵称为空");
            return;
        }
        String url = Constants.FIND_URL_API_CHANGE_NC+"userid="+userid+"&nc="+ URLEncoder.encode(nc,"UTF-8");
        Log.e("------------>","url2"+url);
        //注册完成接口
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ToastUtils.showToast(ChangeNcActivity.this,"昵称修改成功");
                        sp.edit().putString("nc",nc).commit();
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ToastUtils.showToast(ChangeNcActivity.this,"昵称修改失败");
            }
        });
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(stringRequest1);
    }
}
