package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CheckUtils;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.TokenUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.TimeButton;
import com.makeapp.android.util.HandlerUtil;
import com.makeapp.javase.lang.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class FindPwdActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = FindPwdActivity.class.getSimpleName();
    private EditText mobileEditText;
    private EditText newPasswordEditText;
    private EditText confirmNewPasswordEditText;
    private EditText codeEditText;
    private TimeButton timeButton;
    private Button submitButton;
    private String safeCode = "";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            double d = Math.random() * 9000 + 1000;
//            int code = (int) d;
//            codeTextView.setText(code + "");
//            codeEditText.setText("7112");//暂时写死测试等短信接入数据库里面是7112
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        initView();
        initEvent();
    }

    //获取界面控件
    private void initView() {
        mobileEditText = (EditText) this.findViewById(R.id.etMobilephone);
        newPasswordEditText = (EditText) this.findViewById(R.id.etNewPassword);
        confirmNewPasswordEditText = (EditText) this.findViewById(R.id.etConfirmNewPassword);
        codeEditText = (EditText) this.findViewById(R.id.tvCode);
        timeButton = (TimeButton) this.findViewById(R.id.tbtnCode);
        submitButton = (Button) this.findViewById(R.id.btnFind);
    }

    //初始化控件事件
    private void initEvent() {
        timeButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
    }

    public void onBackClick(View view) {
        finish();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.tbtnCode: {
                String mobile = mobileEditText.getText().toString().trim();
                if (verifyMobile(mobile)) {
                    timeButton.setRun(true);
                    double d = Math.random() * 9000 + 1000;
                    int code = (int) d;
                    safeCode = code + "";
                    startGetCodeTask(mobile, safeCode);

//                    HandlerUtil.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mHandler.sendEmptyMessage(0x11);
//
//                        }
//                    }, 3000);
                }
            }
            break;
            case R.id.btnFind: {
                String mobile = mobileEditText.getText().toString().trim();
                if (verifyMobile(mobile)) {
                    String code = codeEditText.getText().toString().trim();
                    if (StringUtil.isInvalid(code)) {
                        showToast(getResources().getString(R.string.mine_hint_code));
                        return;
                    }
                    if (!safeCode.equals(code)) {
                        showToast(getResources().getString(R.string.mine_hint_code_error));
                        return;
                    }
                    String newPassword = newPasswordEditText.getText().toString();
                    if (StringUtil.isInvalid(newPassword)) {
                        showToast(getResources().getString(R.string.mine_hint_new_pass));
                        return;
                    }
                    if (newPassword.length() < 6) {
                        showToast(getResources().getString(R.string.mine_hint_npassword_length));
                        return;
                    }
                    String confirmNewPassword = confirmNewPasswordEditText.getText().toString();
                    if (StringUtil.isInvalid(confirmNewPassword)) {
                        showToast(getResources().getString(R.string.mine_hint_c_new_pass));
                        return;
                    }
                    if (!newPassword.equals(confirmNewPassword)) {
                        showToast(getResources().getString(R.string.mine_password_differentce));
                        return;
                    }
                    startFindPasswordTask(mobile, newPassword, code);
                }
            }
            break;
        }
    }


    @Override
    protected void init() {
        ActivityCollector.addActivity(FindPwdActivity.this);
    }

    @Override
    protected void click(View view) {
    }

    //验证手机号合法性
    private boolean verifyMobile(String mobile) {
        if (StringUtil.isInvalid(mobile)) {
            Log.e(TAG, "===mobile==1==");
            showToast(getResources().getString(R.string.mine_hint_mobile));
            return false;
        }
        if (!CheckUtils.checkMobile(mobile)) {
            Log.e(TAG, "===mobile==2==");
            showToast(getResources().getString(R.string.mine_hint_mobile_error));
            return false;
        }
        return true;
    }

    //找回密码异步请求
    private void startFindPasswordTask(String mobile, String password, String code) {
        showProgressDialog(FindPwdActivity.this, getResources().getString(R.string.operation_setup_pwding));
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_MOBILE, mobile);
        params.put(Constant.CAR_PWD, TokenUtils.generateMD5(password));
        params.put(Constant.CAR_SAFE_CODE, code);

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_MOBILE, mobile);
        hashMap.put(Constant.CAR_PWD, TokenUtils.generateMD5(password));
        hashMap.put(Constant.CAR_SAFE_CODE, code);
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startFindPasswordTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.FIND_PASSWORD, "find", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
                dismissProgressDialog();
//                showToast(Constant.ERROR_WEB);
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
                            App.app.setData(UserPreferences.USER_ID, "");//忘记密码找回后清空本地重新登录
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
    //获取验证码异步请求
    private void startGetCodeTask(String mobile, String code) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_MOBILE, mobile);
        params.put(Constant.CAR_SAFE_CODE, code);
        params.put(Constant.CAR_TYPE, "0");

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_MOBILE, mobile);
        hashMap.put(Constant.CAR_SAFE_CODE, code);
        hashMap.put(Constant.CAR_TYPE, "0");
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startGetCodeTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.MEMBER_SENDMESSAGE, "code", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "startGetCodeTask e---->" + e);
                safeCode = "";
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startGetCodeTask response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
//                            JSONObject rowJSONObject = jsonObject.optJSONObject(Constant.CAR_ROWS);

                        } else {
                            safeCode = "";
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
