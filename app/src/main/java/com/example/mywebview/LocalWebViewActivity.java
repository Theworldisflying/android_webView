package com.example.mywebview;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LocalWebViewActivity extends AppCompatActivity {
    private Button backButton;
    private WebView webView;
    private ProgressBar progressBar;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webviewlayout);

        //参考文档  https://blog.csdn.net/carson_ho/article/details/64904691/
        //不跳转浏览器下载文件自动安装  https://blog.csdn.net/xutingting_/article/details/79931820

        backButton = (Button)findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressBar = (ProgressBar)findViewById(R.id.progressbar);//进度条

        webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/html/html.html");
        webView.addJavascriptInterface(this,"androidclik");////添加js监听，这样html就能调客户端，添加js交互接口类，并起别名 imagelistner
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js(这里不开启，js方法无法响应)
        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据
        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //不显示缩放按钮
        webSettings.setDisplayZoomControls(false);

    }

//    //js调用android方法方式一，或者（@JavascriptInterface）
//    // js通信接口
//    public class JavascriptInterface{
//        private Context context;
//
//        public JavascriptInterface(Context context) {
//            this.context = context;
//        }
///**
//     * 与js交互时用到的方法，在js里直接调用的
//     */
//        @JavascriptInterface
//        public void click(String str) {
//            Toast.makeText(getApplicationContext(),"===="+str,Toast.LENGTH_SHORT).show();
//        }
//        }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);

            // html加载完成之后，添加监听按钮的点击js函数
//            webView.loadUrl("javascript:(function(){function(){var btn=document.getElementById(\"mybtn\");btn.addEventListener('click',function () {window.androidclik.click(\"==\"),false);}})()");

            //添加js方法
            /*
            1,addJavascriptInterface 添加js交互接口类，并起别名；（这里是 androidclik ）
            2，使用别名调用android 里 addJavascriptInterface类型的声明函数
             */
            //格式 javascript:(function(){})()
            //这里拦截了mybtn的原有响应方法
            webView.loadUrl("javascript:(function(){var btn = document.getElementById(\"mybtn\");btn.onclick = function () {window.androidclik.getClient(\"==\");}})()");

            //调用js 并传值
            webView.loadUrl("javascript:btnlis('this`s android load func')");

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    };
    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient(){
        ////不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(view.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定",null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();
            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progressBar.setProgress(newProgress);
        }
        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }
    };


    /**
     * JS调用android的方法
     * @param str
     * @return
     */
    @JavascriptInterface
    public void getClient(String str){
        Log.i("ansen","html调用客户端:"+str);
        Toast.makeText(getApplicationContext(),"====="+str,Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("pre==","是否有上一个页面："+webView.canGoBack());
        if (webView.canGoBack()&&keyCode==KeyEvent.KEYCODE_BACK){////点击返回按钮的时候判断有没有上一页
            webView.goBack();// goBack()表示返回webView的上一页面
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
        webView = null;
    }
}
