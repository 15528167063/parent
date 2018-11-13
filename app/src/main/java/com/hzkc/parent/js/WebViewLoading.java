package com.hzkc.parent.js;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import com.hzkc.parent.R;
import com.hzkc.parent.utils.LogUtil;
import com.hzkc.parent.utils.ProveUtil;
import com.hzkc.parent.utils.TransformUtil;
import com.hzkc.parent.view.AffirmDialog;

/**
 * 自带加载框的webView
 *
 * @author lwj
 * @date 2017/9/29 17:49
 */
public class WebViewLoading extends WebView {
    public static final String APP_CACHE_DIRNAME = "/app_database/";

    public ProgressBar progressbar;
    /**
     * 是否显示ProgressBar
     */
    private boolean isShowPro = true;

    private onWebViewClient webViewClient;
    private onWebChromeClient webChromeClient;
    private Context context;
    /**
     * 长按保存图片确认dialog
     */
    private AffirmDialog saveDialog = null;
    /**
     * 保存图片的地址
     */
    private String savaeImageUrl;

    public void setWebChromeClient(onWebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
    }

    public void setWebViewClient(onWebViewClient webViewClient) {
        this.webViewClient = webViewClient;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public WebViewLoading(Context context) {
        this(context, null);
    }

    public WebViewLoading(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public WebViewLoading(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        // 初始化进度条
        progressbar = new ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal);
        // 设置进度条风格
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, TransformUtil.dip2px(3), 0, 0));
        addView(progressbar);
        initWebViewSetting();
        setBackgroundResource(android.R.color.transparent);
    }

    public void setShowPro(boolean showPro) {
        isShowPro = showPro;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * 初始化webView 设置
     */
    private void initWebViewSetting() {
        //强制开启debug 调试
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            setWebContentsDebuggingEnabled(true);
//        }

        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);//资源加载超时操作
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true); //支持通过JS打开新窗口
        webSettings.setAllowFileAccess(true); // 允许访问文件

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }
        /**
         * 视频点击的时候 会转圈下后面就加载失败,没有在 5.0 以下的真机测试
         * 不知道能不能播放,5.0 以上的手机要加这个
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //启用地理定位
        webSettings.setGeolocationEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setDomStorageEnabled(true);//隐藏原生的缩放控件



        //设置字体大小
        webSettings.setTextZoom(100);
        webSettings.setDefaultFontSize(14);
        webSettings.setDefaultFixedFontSize(14);

        //开启密码存储
        webSettings.setSavePassword(true);
        //优先使用缓存:
        if (!ProveUtil.IfHasNet()) {
            //没有网络
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            //有网络
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

        webSettings.setDomStorageEnabled(true);//  开启 DOM storage API 功能
        webSettings.setDatabaseEnabled(true);//开启 database storage API 功能
        webSettings.setAppCacheEnabled(true);//开启 Application Caches 功能
        String appCachePath = getContext().getCacheDir().getAbsolutePath() + APP_CACHE_DIRNAME;
        //设置  Application Caches 缓存目录，web浏览器中所有的东西，从页面、图片到脚本、css等等
        webSettings.setAppCachePath(appCachePath);
        //设置数据缓存，存储一些简单的用key/value对即可解决的数据，根据作用范围的不同，有Session Storage和Local Storage两种
        webSettings.setDatabasePath(appCachePath);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        // 注意： 每个 Application 只调用一次 WebSettings.setAppCachePath()，WebSettings.setAppCacheMaxSize()

        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式


        //3.0以上开启硬件加速
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

//        if (Build.VERSION.SDK_INT >= 19) {
//            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
        //支持下载
        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String s, String s1, String s2, String s3, long l) {
                Uri uri = Uri.parse(s);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            }
        });
        //设置返回
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
                        goBack();
                        return true;
                    }
                }
                return false;
            }
        });

        setWebViewClient(mWebViewClient);
        setWebChromeClient(mWebChromeClient);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HitTestResult hitTestResult = getHitTestResult();
                /**
                 * 如果是图片类型或者是带有图片链接的类型
                 * WebView.HitTestResult.UNKNOWN_TYPE    未知类型
                 WebView.HitTestResult.PHONE_TYPE    电话类型
                 WebView.HitTestResult.EMAIL_TYPE    电子邮件类型
                 WebView.HitTestResult.GEO_TYPE    地图类型
                 WebView.HitTestResult.SRC_ANCHOR_TYPE    超链接类型
                 WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE    带有链接的图片类型
                 WebView.HitTestResult.IMAGE_TYPE    单纯的图片类型
                 WebView.HitTestResult.EDIT_TEXT_TYPE    选中的文字类型
                 */


                if (hitTestResult.getType() == HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    savaeImageUrl = hitTestResult.getExtra();
                    if (!TextUtils.isEmpty(savaeImageUrl)) {
                        if (saveDialog == null) {
                            saveDialog = new AffirmDialog(context);
                            saveDialog.setAffirmClickListener(saveImageClick);
                            saveDialog.setTitleText("是否保存图片？");
                        }
                        saveDialog.show();
                    }
                    LogUtil.e("onLongClick", "图片地址-----》：" + savaeImageUrl);
                    return true;
                }
                return false;
            }
        });
    }

//    private void downloadImageFile() {
//        //下载图片
//        HttpManageDownload.getInstance().asyncDownloadFile(context, "save_web_img", savaeImageUrl, null,
//                new HttpDownloadCallback() {
//                    @Override
//                    public void preHttp(String threadId) {
//                        LogUtil.e(threadId, "开始下载图片");
//                    }
//
//                    @Override
//                    public void onDownloadSuccess(String threadId, File f, String fileName) {
//                        Looper.prepare();
//                        Toast.makeText(context, R.string.web_download_sucess, Toast.LENGTH_SHORT).show();
//                        LogUtil.e(threadId, "下载图片成功");
//                        // 其次把文件插入到系统图库
//                        try {
//                            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                                    f.getAbsolutePath(), fileName, null);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        //通知图库更新
//                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        Uri uri = Uri.fromFile(f);
//                        intent.setData(uri);
//                        context.sendBroadcast(intent);
//                        Looper.loop();
//                    }
//
//                    @Override
//                    public void onDownloading(String threadId, long progress, long count) {
//                        LogUtil.e(threadId, "progress:" + progress);
//                    }
//
//                    @Override
//                    public void onDownloadFailed(String threadId, Exception e) {
//                        LogUtil.e(threadId, "下载图片失败" + e.getMessage());
//                        Looper.prepare();
//                        Toast.makeText(context, R.string.web_download_fail, Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                    }
//
//                    @Override
//                    public void cancelHttp(String threadId) {
//
//                    }
//                });
//
//    }


    /**
     * 保存图片点击事件
     */
    private OnClickListener saveImageClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (saveDialog != null) {
                saveDialog.dismiss();
            }
            switch (v.getId()) {
                case R.id.affirm_confirm:
//                    downloadImageFile();
                    break;
            }
        }
    };


    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (webViewClient != null) {
                return webViewClient.shouldOverrideUrlLoading(view, url);
            } else {
                return false;
            }
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (webViewClient != null) {
                webViewClient.onPageStarted(view, url, favicon);
            }

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();    //表示等待证书响应
            // handler.cancel();      //表示挂起连接，为默认方式
            // handler.handleMessage(null);    //可做其他处理
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (webViewClient != null) {
                webViewClient.onReceivedError(view, request, error);
            }
            super.onReceivedError(view, request, error);
//            // 断网或者网络连接超时
//            int errorCode = error.getErrorCode();
//            if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
//                view.loadUrl("about:blank"); // 避免出现默认的错误界面
//                view.loadUrl(mErrorUrl);
//            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            //可以通过错误码来判断错误
//            int statusCode = errorResponse.getStatusCode();
//            System.out.println("onReceivedHttpError code = " + statusCode);
//            if (404 == statusCode || 500 == statusCode) {
//                view.loadUrl("about:blank");// 避免出现默认的错误界面
//                view.loadUrl(mErrorUrl);
//            }
            if (webViewClient != null) {
                webViewClient.onReceivedHttpError(view, request, errorResponse);
            }
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //页面加载结束时调用
            if (webViewClient != null) {
                webViewClient.onPageFinished(view, url);
            }
            super.onPageFinished(view, url);
        }


    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            //警告框
            if (webChromeClient != null) {
                return webChromeClient.onJsAlert(view, url, message, result);
            } else {
                return false;
            }
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            //确认框
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            //输入框
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            if (webChromeClient != null) {
                webChromeClient.openFileChooser(uploadMsg);
            }
        }

        // For Android 3.0+
        private void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            if (webChromeClient != null) {
                webChromeClient.openFileChooser(uploadMsg, acceptType);
            }
        }

        // For Android  > 4.1.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            if (webChromeClient != null) {
                webChromeClient.openFileChooser(uploadMsg, acceptType, capture);
            }
        }

        // For Android 5.0+
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (webChromeClient != null) {
                return webChromeClient.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            } else {
                return true;
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (isShowPro) {
                //显示加载精度条
                if (newProgress == 100) {
                    // 加载完成隐藏进度条
                    ((WebViewLoading) view).progressbar.setVisibility(GONE);
                } else {
                    if (((WebViewLoading) view).progressbar.getVisibility() == GONE)
                        ((WebViewLoading) view).progressbar.setVisibility(VISIBLE);
                    ((WebViewLoading) view).progressbar.setProgress(newProgress);
                }
            } else {
                //不显示精度条
                ((WebViewLoading) view).progressbar.setVisibility(GONE);
            }
            super.onProgressChanged(view, newProgress);
            if (webChromeClient != null) {
                webChromeClient.onProgressChanged(view, newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
//             android 6.0 以下通过title获取来判断是否出现错误
            if (webChromeClient != null) {
                webChromeClient.onReceivedTitle(view, title);
            }
            super.onReceivedTitle(view, title);
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//                if (title.contains("404") || title.contains("500") || title.contains("Error")) {
////                    view.loadUrl("about:blank");// 避免出现默认的错误界面
////                    view.loadUrl(mErrorUrl);
//
//                }
//            }
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }


//设置数据库容量
//        @Override
//        public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize,
//                                            long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
//            super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
//            quotaUpdater.updateQuota(estimatedDatabaseSize * 2);
//        }
//
//        @Override
//        public void onReachedMaxAppCacheSize(long spaceNeeded, long quota, WebStorage.QuotaUpdater quotaUpdater) {
//            super.onReachedMaxAppCacheSize(spaceNeeded, quota, quotaUpdater);
//            quotaUpdater.updateQuota(spaceNeeded * 2);
//        }
    };


    /**
     * 重载WebViewClient监听
     */
    public interface onWebViewClient {
        boolean shouldOverrideUrlLoading(WebView view, String url);

        void onPageStarted(WebView view, String url, Bitmap favicon);

        void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error);

        void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse);

        void onPageFinished(WebView view, String url);

    }

    /**
     * 重载WebChromeClient
     */
    public interface onWebChromeClient {

        boolean onJsAlert(WebView view, String url, String message, JsResult result);

        void openFileChooser(ValueCallback<Uri> uploadMsg);

        void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType);


        void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture);

        boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);

        void onProgressChanged(WebView view, int newProgress);

        void onReceivedTitle(WebView view, String title);

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            super.onRestoreInstanceState(state);
        } catch (Exception e) {
        }
        state = null;
    }


}
