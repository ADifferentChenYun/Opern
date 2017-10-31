package com.yun.opern.ui.activitys;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yun.opern.R;
import com.yun.opern.ui.bases.BaseActivity;
import com.yun.opern.views.ActionBarNormal;

import butterknife.BindView;


/**
 * Created by 允儿 on 2016/5/20 0020.
 */
public class WebViewActivity extends BaseActivity {

    private String mPrevUrl;
    @BindView(R.id.actionbar)
    ActionBarNormal actionBar;
    @BindView(R.id.activity_pay_web_view)
    WebView mWebView;
    @BindView(R.id.activity_pay_pb)
    ProgressBar mProgressBar;

    @Override
    protected int contentViewRes() {
        mPrevUrl = getIntent().getExtras().getString("url");
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new PayWebViewClient());
        mWebView.setWebChromeClient(new PayWebViewChromeClient());
        mWebView.loadUrl(mPrevUrl);
    }

    private class PayWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (mPrevUrl != null) {
                if (!mPrevUrl.equals(url)) {
                    mPrevUrl = url;
                    mWebView.loadUrl(url);
                    return true;
                } else {
                    return false;
                }
            } else {
                mPrevUrl = url;
                mWebView.loadUrl(url);
                return true;
            }
        }
    }

    private class PayWebViewChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (actionBar != null) {
                actionBar.setTitle(title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (mProgressBar == null) {
                return;
            }
            mProgressBar.setProgress(newProgress);
            if (newProgress != 100) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            } else {
                mProgressBar.setProgress(newProgress);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && this.mWebView.canGoBack()) {
            this.mWebView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.setWebViewClient(null);
            mWebView.setWebChromeClient(null);
            mWebView = null;
        }
    }
}