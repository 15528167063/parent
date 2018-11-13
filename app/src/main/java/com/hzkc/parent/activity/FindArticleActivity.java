package com.hzkc.parent.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.R;
import com.hzkc.parent.mina.Constants;
import com.jaeger.library.StatusBarUtil;

public class FindArticleActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTopTitle;
    private ImageView ivFinish;
    private WebView wvFindNews;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_article);
        ininView();
        initData();
    }

    private void ininView() {
        type = getIntent().getStringExtra("type");
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        wvFindNews = (WebView) findViewById(R.id.wv_find_news);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        if(type.equals("2")){
            tvTopTitle.setText("使用流程");
        }else if(type.equals("3")){
            tvTopTitle.setText("常见问题");
        }
        else if(type.equals("4")){
            tvTopTitle.setText("安卓管控问题");
        }else {
            tvTopTitle.setText("功能介绍");
        }
        ivFinish.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
    }
    private  String type ="1";
    private void initData() {

        WebSettings settings = wvFindNews.getSettings();
        //不使用缓存
        settings.setJavaScriptEnabled(true);
        String url=null;

        if(type.equals("2")){
            url= Constants.PHP_URL+"h5/help/Usepro";
        }else if(type.equals("3")){
            url=Constants.PHP_URL+"h5/help/Compro";;
        }
        else if(type.equals("4")){
            url=Constants.PHP_URL+"h5/help/Controlpro";
        }else {
            url=Constants.PHP_URL+"h5/help/Funintro";
        }
        wvFindNews.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_finish:
                finish();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyWebView();
    }
    /**
     * 销毁webview
     * */
    public void destroyWebView() {
        if(wvFindNews != null) {
            wvFindNews.clearHistory();
            wvFindNews.clearCache(true);
            wvFindNews.loadUrl("about:blank"); // clearView() should be changed to loadUrl("about:blank"), since clearView() is deprecated now
            wvFindNews.freeMemory();
            wvFindNews.pauseTimers();
            wvFindNews = null; // Note that mWebView.destroy() and mWebView = null do the exact same thing
        }

    }
}
