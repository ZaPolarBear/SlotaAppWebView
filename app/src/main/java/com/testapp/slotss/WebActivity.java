package com.testapp.slotss;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onesignal.OneSignal;

public class WebActivity extends Activity {


    WebView webPage;
    Context context;
    String content;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        webPage = findViewById(R.id.webone);
        Bundle bundle = getIntent().getExtras();
        content = bundle.getString("url");
        context = this;
        webPage.getSettings().setJavaScriptEnabled(true);
        webPage.getSettings().setAppCacheEnabled(false);
        webPage.getSettings().setDomStorageEnabled(true);
        OneSignal.initWithContext(this);
        OneSignal.setAppId("03d44663-4ac3-4603-b120-4fd040c173f7");
        webPage.addJavascriptInterface(this, "android");
        webPage.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        if (savedInstanceState != null)
            ((WebView)findViewById(R.id.webone)).restoreState(savedInstanceState.getBundle("webViewState"));
        webPage.loadUrl(content);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webPage, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }

    }

    @Override
    public void onBackPressed() {
        if (webPage.canGoBack()) {
            webPage.goBack();
        } else {
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = context.getApplicationContext().
                getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("lastUrl",webPage.getUrl());
        edit.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(webPage != null) {
            SharedPreferences prefs = context.getApplicationContext().
                    getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            String s = prefs.getString("lastUrl","");
            if(!s.equals("")) {
                webPage.loadUrl(s);
                if (Build.VERSION.SDK_INT >= 21) {
                    CookieManager.getInstance().setAcceptThirdPartyCookies(webPage, true);
                } else {
                    CookieManager.getInstance().setAcceptCookie(true);
                }
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle bundle = new Bundle();
        webPage.saveState(bundle);
        outState.putBundle("webViewState", bundle);
    }
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Bundle bundle = new Bundle();
        webPage.saveState(bundle);
        state.putBundle("webViewState", bundle);
    }
}

