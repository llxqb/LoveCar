package com.bhxx.lovecar.activity;

import android.os.Bundle;
import android.view.View;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-爱车-订单-评价
 *
 * @qq289513149.
 */

public class MarkActivity extends BasicActivity implements View.OnClickListener {

    public static final String TAG = MarkActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
    }

    //返回
    public void onBackClick(View view) {
        finish();
    }

    //提交评论
    public void onSubmitPL(View view) {

        showDialog("", "提交评论成功", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MarkActivity.super.cancelAlertDialog();
                finish();
            }
        }, null, getResources().getString(R.string.operation_know), "");

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }
}
