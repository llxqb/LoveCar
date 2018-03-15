package com.bhxx.lovecar.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.bhxx.lovecar.R;

/**
 * Created by @dpy on 2016/12/5.
 * 我的-用户协议
 *
 * @qq289513149.
 */

public class UserProtocolActivity extends BasicActivity implements View.OnClickListener {

    public static final String TAG = UserProtocolActivity.class.getSimpleName();
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        initView();
        initEvent();
        webView.loadUrl("file:///android_asset/service_terms.html");
    }

    //初始化控件

    public void initView() {
        webView = (WebView) this.findViewById(R.id.webView);
    }

    //初始化控件事件
    public void initEvent() {

    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }
}
