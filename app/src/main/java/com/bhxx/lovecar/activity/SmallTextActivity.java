package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.makeapp.javase.lang.StringUtil;

/**
 * Created by @dpy on 2016/12/1.
 * 单行公共文本输入框
 *
 * @qq289513149.
 */

public class SmallTextActivity extends BasicActivity implements View.OnClickListener {

    public static final String TAG = SmallTextActivity.class.getSimpleName();
    public static final String REQUEST_TITLE = "title";
    public static final String REQUEST_CONTENT = "content";
    public static final String REQUEST_TYPE = "type";
    public static final int RESULT_CONTENT = 0x11;
    public static final String RESPONSE_CONTENT = "content";
    public boolean isNumber = false;
    private TextView titleTextView;
    private TextView saveTextView;
    private EditText contentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smalltext);
        initView();
        initEvent();
        contentEditText.requestFocus();
        titleTextView.setText(getIntent().getStringExtra(REQUEST_TITLE));
        contentEditText.setText(getIntent().getStringExtra(REQUEST_CONTENT));
        isNumber = getIntent().getBooleanExtra(REQUEST_TYPE, false);
        if (isNumber) {
            contentEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    //初始化控件
    private void initView() {
        titleTextView = (TextView) this.findViewById(R.id.tvTitle);
        saveTextView = (TextView) this.findViewById(R.id.tvSave);
        contentEditText = (EditText) this.findViewById(R.id.etContent);
    }

    //初始化控件事件
    private void initEvent() {
        saveTextView.setOnClickListener(this);
    }

    //返回
    public void onLeftBackClick(View view) {
        String content = contentEditText.getText().toString();
        if (StringUtil.isValid(content)) {
            showDialog(getResources().getString(R.string.operation_tip), getResources().getString(R.string.op_tip_save), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SmallTextActivity.super.cancelAlertDialog();
                    finish();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SmallTextActivity.super.cancelAlertDialog();
                }
            }, getResources().getString(R.string.operation_confirm), getResources().getString(R.string.operation_cancel));
        } else {
            finish();
        }
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tvSave: {
                Log.e(TAG, "tvSave");
                String content = contentEditText.getText().toString();
                Intent intent = new Intent();
                intent.putExtra(RESPONSE_CONTENT, content);
                setResult(RESULT_CONTENT, intent);
                finish();
            }
            break;
        }
    }
}
