package com.hzkc.parent.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hzkc.parent.R;
import com.hzkc.parent.appcation.MyApplication;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ToastUtils;
import com.jaeger.library.StatusBarUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

public class FindCommentActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private TextView tvSave;
    private EditText content;
    public SharedPreferences sp;
    private String parentUUID;
    private String msgId;
    private String nc;
    private String txid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_comment);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        initIView();
        initData();
    }

    private void initIView() {
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvSave = (TextView) findViewById(R.id.tv_save);
        content = (EditText) findViewById(R.id.content);

        ivFinish.setVisibility(View.VISIBLE);
        tvSave.setVisibility(View.VISIBLE);

        tvSave.setText("发送");
        tvTopTitle.setText("发评论");
        ivFinish.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        //自动弹出软键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(content, 0);
            }
        }, 998);
    }

    private void initData() {
        sp = getSharedPreferences("info", MODE_PRIVATE);
        parentUUID = sp.getString("parentUUID", "");
        String phone = sp.getString("phoneNum", "");
        nc = sp.getString("nc", "");
        txid = phone.substring(phone.length() - 2);
        msgId = getIntent().getStringExtra("msgId");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
            case R.id.tv_save:
                sendComment();
                break;
            default:
                break;
        }
    }

    /**
     * 发送评论
     */
    private void sendComment() {
        final String myComment = content.getText().toString().trim();
        if (TextUtils.isEmpty(myComment)) {
            ToastUtils.showToast(FindCommentActivity.this, "输入内容为空");
            return;
        }
        tvSave.setClickable(false);
        tvSave.setEnabled(false);
        sp.edit().putString("refresh", "refresh").commit();
        String url = null;
        try {
            url = Constants.FIND_URL_SEND_COMMENT + "id=" + msgId + "&nc=" + URLEncoder.encode(nc, "UTF-8") + "&text=" + URLEncoder.encode(myComment, "UTF-8") + "&hfid=" + parentUUID + "&txid=" + txid;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //发布评论
        LogUtil.e("url:" + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        toast("评论发送成功");
                        LogUtil.e("sendComment", "评论发送成功" + response);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                toast("评论发送失败");
                tvSave.setClickable(true);
                tvSave.setEnabled(true);
            }
        });
        MyApplication.getRequestQueue().add(stringRequest);
    }

    /**
     * 吐司的一个小方法
     */
    private void toast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(RegisterUserActivity.this, str, Toast.LENGTH_SHORT).show();
                ToastUtils.showToast(FindCommentActivity.this, str);
            }
        });
    }

}
