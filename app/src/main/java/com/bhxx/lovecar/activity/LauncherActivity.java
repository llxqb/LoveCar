package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.makeapp.android.util.HandlerUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by @dpy on 2016/12/28.
 * 启动页
 *
 * @qq289513149.
 */

public class LauncherActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        HandlerUtil.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

}
