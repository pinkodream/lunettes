package com.pinkodream.lunettes.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.pinkodream.lunettes.R;

public class DetailActivity extends Activity implements OnClickListener {

    private static final String TAG = "DetailActivity";
    public static final String KEY_ARTICLE_URL = "ARTICLE_URL";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView backButton = (ImageView) findViewById(R.id.top_bar_menu_button);
        backButton.setImageResource(R.drawable.ic_arrow_left);
        backButton.setOnClickListener(this);

        mWebView = (WebView) findViewById(R.id.detail_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e(TAG, "[onReceivedError]");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "[onPageFinished]");
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(KEY_ARTICLE_URL)) {
            mWebView.loadUrl(intent.getStringExtra(KEY_ARTICLE_URL));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.top_bar_menu_button) {
            finish();
        }
    }
}
