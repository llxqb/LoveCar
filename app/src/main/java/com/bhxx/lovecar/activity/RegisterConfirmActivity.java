package com.bhxx.lovecar.activity;
/**
 * 注册界面
 */

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.TokenUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.javase.lang.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/1.
 * 注册用户界面
 *
 * @qq289513149.
 */
public class RegisterConfirmActivity extends BasicActivity {

    private static final String TAG = RegisterConfirmActivity.class.getSimpleName();
    private String phone;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_confirm);
        phone = getIntent().getStringExtra("phone");
        initView();
        initEvent();

    }

    //获取界面控件
    private void initView() {
        passwordEditText = (EditText) this.findViewById(R.id.etPassword);
        confirmPasswordEditText = (EditText) this.findViewById(R.id.etConfirmPassword);
        registerButton = (Button) this.findViewById(R.id.btnRegister);
    }

    //初始化界面控件事件
    private void initEvent() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                if (StringUtil.isInvalid(password)) {
                    showToast(getResources().getString(R.string.mine_hint_password));
                    return;
                }
                if (password.length() < 6) {
                    showToast(getResources().getString(R.string.mine_hint_password_length));
                    return;
                }
                if (StringUtil.isInvalid(confirmPassword)) {
                    showToast(getResources().getString(R.string.mine_mobile_cnew_pass));
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    showToast(getResources().getString(R.string.mine_password_differentce));
                    return;
                }
                showProgressDialog(RegisterConfirmActivity.this, getResources().getString(R.string.operation_registering));
                register(phone, password);
            }
        });
    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {
    }

    private void register(String phone, String pwd) {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_MOBILE, phone);
        params.put(Constant.CAR_PWD, TokenUtils.generateMD5(pwd));
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));
        Log.e(TAG, "register params----->" + params);
        MyOkHttp.postMap(GlobalValues.REGISTER, 1, "register", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            LogUtils.e(TAG + e.toString());
            dismissProgressDialog();
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.e(TAG + " response--->" + response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (null != jsonObject) {
                    String resultCode = jsonObject.optString("resultCode");
                    if (!Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                        showToast(phone + jsonObject.optString("resultDesc"));
                    } else {
                        showToast(jsonObject.optString("resultDesc"));
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dismissProgressDialog();
        }
    }
}
