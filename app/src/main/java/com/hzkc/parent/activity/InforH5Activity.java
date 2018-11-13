package com.hzkc.parent.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzkc.parent.Bean.InformatDetalisResult;
import com.hzkc.parent.R;
import com.hzkc.parent.js.JSWebInterface;
import com.hzkc.parent.mina.Constants;
import com.hzkc.parent.utils.LogUtil;
import com.jaeger.library.StatusBarUtil;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class InforH5Activity extends BaseActivity implements View.OnClickListener {
    public WebView webView;
    public String content,title,time,laiyuan;
    public TextView tv_time,tv_title,tv_laiyuan;
    private ImageView ivFinish,iv_share;
    private TextView tvTopTitle;
    private String infor_id;
    private String parentUUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5url);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.blue));
        webView=(WebView)findViewById(R.id.webview);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_laiyuan=(TextView)findViewById(R.id.tv_laiyuan);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvTopTitle.setText("资讯详情");

        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        ivFinish.setVisibility(View.VISIBLE);
        iv_share.setVisibility(View.VISIBLE);
        ivFinish.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        infor_id=getIntent().getStringExtra("messageId");
        title=getIntent().getStringExtra("title");
        parentUUID=sp.getString("parentUUID", "");
        initWebView();
        webView.addJavascriptInterface(new JSWebInterface(null,infor_id,parentUUID), "parent_info");
        webView.loadUrl(Constants.PHP_INFOR+"Newdetail/index");
    }
    private void initTitle(InformatDetalisResult data) {
        content=data.getContent();
        title=getIntent().getStringExtra("title");
        time=data.getCreate_time();
        laiyuan=data.getKeyword();
        if(!TextUtils.isEmpty(title)){
            tv_title.setText(title);
        }
        if(!TextUtils.isEmpty(time)){
            tv_time.setText(time);
        }
        if(!TextUtils.isEmpty(laiyuan)){
            tv_laiyuan.setText("来源 ： "+laiyuan);
        }
        webView.loadDataWithBaseURL("", content.replace("<p", "<p style=text-align:justify;font-size:16px;color:#323232;text-indent:2em;'"), "text/html", "UTF-8", "");
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
        webView.getSettings().setDomStorageEnabled(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        // 点击链接后不使用其他的浏览器打开
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
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
            case R.id.iv_share:
                showShare(1);
                break;
            default:
                break;
        }
    }

    /**
     * 分享的设置
     */
    private void showShare(final int type) {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();

        String url = Constants.PHP_INFOR + "Newdetail/index?infor_id=" + infor_id + "&hide=1";
        LogUtil.e("url",url);
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        if(type==1){
            oks.setTitleUrl(url);
        }else {
            oks.setTitleUrl("http://www.ycz365.com/Yczshare/index.php?code="+parentUUID);
        }
        // text是分享文本，所有平台都需要这个字段
        oks.setText("优成长——科学管控孩子，手机专业防沉迷");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl("http://www.ycz365.com/login_icon.png");
        // url仅在微信（包括好友和朋友圈）中使用
        if(type==1){
            oks.setUrl(url);
        }else {
            oks.setUrl("http://www.ycz365.com/Yczshare/index.php?code="+parentUUID);
        }
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        if(type==1){
            oks.setSiteUrl(url);
        }else {
            oks.setSiteUrl("http://www.ycz365.com/Yczshare/index.php?code="+parentUUID);
        }
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogUtil.e("setCallback","分享成功........................................");
                sp.edit().putString("shareApp","shareApp").commit();
                if(type==1){
//                    showResult();
                }
            }
            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogUtil.e("setCallback","分享失败........................................");
            }
            @Override
            public void onCancel(Platform platform, int i) {
                LogUtil.e("setCallback","分享取消........................................");
            }
        });
        // 启动分享GUI
        oks.show(this);
    }


}
