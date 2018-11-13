package com.hzkc.parent.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.jaeger.library.StatusBarUtil;

public class WebDataCommdActivity extends AppCompatActivity implements View.OnClickListener {
    public WebView webView;
    public String content,title,time,laiyuan;
    public TextView tv_time,tv_title,tv_laiyuan;
    private ImageView ivFinish;
    private TextView tvTopTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weburl);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        webView=(WebView)findViewById(R.id.webview);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_laiyuan=(TextView)findViewById(R.id.tv_laiyuan);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvTopTitle.setText("资讯详情");

        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        initTitle();
        initWebView();
        webView.loadDataWithBaseURL("", content.replace("<p", "<p style=text-align:justify;font-size:16px;color:#323232;text-indent:2em;'"), "text/html", "UTF-8", "");
    }

    private void initTitle() {
        content=getIntent().getStringExtra("content");
        title=getIntent().getStringExtra("title");
        time=getIntent().getStringExtra("time");
        laiyuan=getIntent().getStringExtra("laiyuan");
        if(!TextUtils.isEmpty(title)){
            tv_title.setText(title);
        }
        if(!TextUtils.isEmpty(time)){
            tv_time.setText(time);
        }
        if(!TextUtils.isEmpty(laiyuan)){
            tv_laiyuan.setText("来源 ： "+laiyuan);
        }
    }
    private void initWebView(){
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(false);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        // 点击链接后不使用其他的浏览器打开
        webView.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            imgReset();//重置webview中img标签的图片大小
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http:") || url.startsWith("https:")) {
                return false;
            }
            view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            view.loadUrl(url);
            return true;
        }
    }
        private void imgReset() {
            webView.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "var img = objs[i];   " +
                    "    img.style.maxWidth = '90%'; img.style.height = 'auto';  " +
                    "}" +
                    "})()");
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
