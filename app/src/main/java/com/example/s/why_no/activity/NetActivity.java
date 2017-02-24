package com.example.s.why_no.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.s.why_no.R;


public class NetActivity extends Activity {

    private WebView contentWebView = null;
    private TextView msgView = null;
    private ProgressBar pb_net_speed;
    private ImageView imv_net_back;
    private TextView tv_net_title;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net);
        tv_net_title = (TextView) findViewById(R.id.tv_net_title);
        imv_net_back = (ImageView) findViewById(R.id.imv_net_back);
        imv_net_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        pb_net_speed = (ProgressBar) findViewById(R.id.pb_net_speed);
        contentWebView = (WebView) findViewById(R.id.webview);
//		msgView = (TextView) findViewById(R.id.msg);
        // ����javascript

        contentWebView.getSettings().setJavaScriptEnabled(true);
        contentWebView.getSettings().setSupportZoom(true);//支持缩放

        contentWebView.getSettings().setUseWideViewPort(true);//使内容不要超出屏幕宽度
        contentWebView.getSettings().setLoadWithOverviewMode(true);

        contentWebView.getSettings().setSupportZoom(true);
        // Technical settings
        contentWebView.getSettings().setSupportMultipleWindows(true);

        contentWebView.getSettings().setAppCacheEnabled(true);
        contentWebView.getSettings().setDatabaseEnabled(true);
        contentWebView.getSettings().setDomStorageEnabled(true);


        Intent intent = getIntent();
        final String url = intent.getStringExtra("url");
        final String title = intent.getStringExtra("title");
        Log.e("LINK " ,url);
        tv_net_title.setText(title);
        contentWebView.loadUrl(url);


        contentWebView.addJavascriptInterface(this, "qwe");
        contentWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(url);

                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        contentWebView.setWebChromeClient(new WebChromeClient(){

//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress == 100) {
//                    pb_net_speed.setVisibility(View.INVISIBLE);
//                } else {
//                    if (View.INVISIBLE == pb_net_speed.getVisibility()) {
//                        pb_net_speed.setVisibility(View.VISIBLE);
//                    }
//                    pb_net_speed.setProgress(newProgress);
//                }
//                super.onProgressChanged(view, newProgress);
//            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

}
