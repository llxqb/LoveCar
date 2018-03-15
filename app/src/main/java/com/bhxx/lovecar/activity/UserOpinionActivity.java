package com.bhxx.lovecar.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CheckUtils;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.android.util.ToastUtil;
import com.makeapp.javase.lang.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/5.
 * 我的-用户意见
 *
 * @qq289513149.
 */

public class UserOpinionActivity extends BasicActivity implements View.OnClickListener, TextWatcher {
    private static final String TAG = UserOpinionActivity.class.getSimpleName();
    private EditText contentEditText;
    private EditText mobileEditText;
    private TextView countTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinion);
        initView();
        initEvent();
        mobileEditText.setText(App.app.getData(UserPreferences.USER_MOBILE));
    }

    //初始化控件
    private void initView() {
        contentEditText = (EditText) this.findViewById(R.id.etContent);
        countTextView = (TextView) this.findViewById(R.id.tvCount);
        mobileEditText = (EditText) this.findViewById(R.id.etMobilephone);
    }

    //初始化控件事件
    private void initEvent() {
        contentEditText.addTextChangedListener(this);
    }

    public void onBackClick(View view) {
        finish();
    }

    //提交
    public void onSubmitClick(View view) {
        String content = contentEditText.getText().toString();
        if (StringUtil.isInvalid(content)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_hint_opinion));
            return;
        }
        String mobile = mobileEditText.getText().toString();
        if (StringUtil.isInvalid(mobile)) {
            mobile = App.app.getData(UserPreferences.USER_MOBILE);
        } else {
            if (!CheckUtils.checkMobile(mobile)) {
                ToastUtil.show(this, getResources().getString(R.string.mine_hint_mobile_error));
                return;
            }
        }
        startSubmitTask(content, mobile);
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        int length = editable.length();
        countTextView.setText(length + "/100");
    }

    //提交意见异步请求
    private void startSubmitTask(String content, String mobile) {
        showProgressDialog(UserOpinionActivity.this, getResources().getString(R.string.operation_submitting));
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_MOBILE, mobile);
        params.put(Constant.CAR_USER_CONTENT, content);

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_MOBILE, mobile);
        hashMap.put(Constant.CAR_USER_CONTENT, content);
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startSubmitTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.IDEA_ADDINDEA, "idea", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
                dismissProgressDialog();
                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                Log.e(TAG, "response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
                            finish();
                        } else {
                            showToast(jsonObject.optString("resultDesc"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
