package com.hzkc.parent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hzkc.parent.R;
import com.hzkc.parent.view.MyWebview;

public class FunctionActivity extends BaseActivity {
    MyWebview wbvFunction;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        wbvFunction=(MyWebview)findViewById(R.id.wbv_function);

        Intent intent = getIntent();
        String url = intent.getStringExtra("urlPath");
        initwbvFunction();
        wbvFunction.loadUrl(url);

    }



    void initwbvFunction(){
        WebSettings webSettings = wbvFunction.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);//关键点
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放

        wbvFunction.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        // 开启DOM缓存。
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDatabasePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setAppCacheMaxSize(1024*1024*8);
        wbvFunction.setWebViewClient(new WebViewClient() {
            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(wbvFunction != null){
            ViewGroup viewGroup = (ViewGroup)wbvFunction.getParent();
            if(null != viewGroup){
                viewGroup.removeView(wbvFunction);
            }
            wbvFunction.stopLoading();
            wbvFunction.removeAllViews();
            wbvFunction.destroy();
            wbvFunction = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && wbvFunction.canGoBack()) {
            wbvFunction.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
