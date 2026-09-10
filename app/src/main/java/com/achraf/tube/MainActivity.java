package com.achraf.tube;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // غيّر هذا الرابط هنا لو تغير عنوان السيرفر المحلي عندك
    private static final String SERVER_URL = "http://10.42.0.1:2222";

    private WebView webView;
    private ProgressBar progressBar;
    private TextView errorText;

    // خاص بدعم تشغيل الفيديو بملء الشاشة (HTML5 video fullscreen)
    private View customFullscreenView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private FrameLayout fullscreenContainer;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);

        fullscreenContainer = new FrameLayout(this);
        fullscreenContainer.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setupWebView();
        loadSite();
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setMediaPlaybackRequiresUserGesture(false); // يسمح بتشغيل الفيديو تلقائيًا زي يوتيوب
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                // نبقي كل التصفح جوه نفس السيرفر المحلي فقط
                if (uri.getHost() != null && uri.getHost().equals(Uri.parse(SERVER_URL).getHost())) {
                    return false; // خلي الـ WebView يفتحها هو
                }
                return true; // أي رابط خارجي، تجاهله (مفيش نت أصلاً)
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    progressBar.setVisibility(View.GONE);
                    errorText.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                // يدخل هنا لما الموقع يشغل فيديو بوضع ملء الشاشة (مثلاً HTML5 video.requestFullscreen)
                if (customFullscreenView != null) {
                    hideCustomView();
                    return;
                }
                customFullscreenView = view;
                customViewCallback = callback;

                ((ViewGroup) webView.getParent()).addView(fullscreenContainer);
                fullscreenContainer.addView(customFullscreenView);
                fullscreenContainer.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }

            @Override
            public void onHideCustomView() {
                hideCustomView();
            }
        });
    }

    private void hideCustomView() {
        if (customFullscreenView == null) return;
        fullscreenContainer.setVisibility(View.GONE);
        fullscreenContainer.removeView(customFullscreenView);
        ((ViewGroup) webView.getParent()).removeView(fullscreenContainer);
        webView.setVisibility(View.VISIBLE);
        customFullscreenView = null;
        if (customViewCallback != null) {
            customViewCallback.onCustomViewHidden();
        }
    }

    private void loadSite() {
        webView.loadUrl(SERVER_URL);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // لو صفحة الخطأ ظاهرة، أي زر يعيد المحاولة
        if (errorText.getVisibility() == View.VISIBLE) {
            loadSite();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (customFullscreenView != null) {
            hideCustomView();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        super.onDestroy();
    }
}
