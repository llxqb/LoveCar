package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CheckUtils;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.TokenUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.javase.lang.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.Call;


public class LoginActivity extends BasicActivity implements View.OnClickListener, TextWatcher {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private EditText usernameEditText;//用户名
    private EditText passwordEditText;//用户密码
    private CheckBox agreeCheckBox;//用户协议
    private TextView forgetPassTextView;//忘记密码
    private TextView registerTextView;//去注册
    private TextView wechatTextView;//微信登录
    private TextView passVisibleTextView;//密码是否可见
    private TextView qqTextView;//QQ登录
    private Button loginButton;//登录
    private String islogin;//记录跳转到登陆界面的状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
//        PayTask payTask = new PayTask(LoginActivity.this);
//        String version = payTask.getVersion();
//        Log.e(TAG, "alipay version--->" + version);
    }

    //初始化界面控件
    private void initView() {
        usernameEditText = (EditText) this.findViewById(R.id.etUserName);
        passwordEditText = (EditText) this.findViewById(R.id.etPassword);
        agreeCheckBox = (CheckBox) this.findViewById(R.id.checkbox);
        forgetPassTextView = (TextView) this.findViewById(R.id.tvForgetPassword);
        passVisibleTextView = (TextView) this.findViewById(R.id.tvPasswordVisible);
        registerTextView = (TextView) this.findViewById(R.id.tvGoRegister);
        wechatTextView = (TextView) this.findViewById(R.id.tvWechat);
        qqTextView = (TextView) this.findViewById(R.id.tvQQ);
        loginButton = (Button) this.findViewById(R.id.btnLogin);
    }

    //初始化控件事件
    private void initEvent() {
        usernameEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        agreeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginButton.setEnabled(StringUtil.isValid(username) && StringUtil.isValid(password) && b);
            }
        });
//        passVisibleTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int type = passwordEditText.getInputType();
//                if (type == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
//                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
//                    passVisibleTextView.setBackgroundResource(R.mipmap.icon_open);
//                } else if (type == InputType.TYPE_CLASS_TEXT) {
//                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                    passVisibleTextView.setBackgroundResource(R.mipmap.icon_close);
//                }
//            }
//        });

        forgetPassTextView.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
        wechatTextView.setOnClickListener(this);
        qqTextView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvForgetPassword: {//忘记密码
                startActivity(new Intent(LoginActivity.this, FindPwdActivity.class));
            }
            break;
            case R.id.btnLogin: {//登录按钮
                String username = usernameEditText.getText().toString();
                String mobile = username.replace(" ", "");
                if (!CheckUtils.checkMobile(mobile)) {
                    showToast(getResources().getString(R.string.mine_hint_mobile_error));
                    return;
                }
                String password = passwordEditText.getText().toString();
                if (password.length() < 6) {
                    showToast(getResources().getString(R.string.mine_hint_password_length));
                    return;
                }
                startLoginTask(mobile, password);
            }
            break;
            case R.id.tvQQ: {//第三方qq登录

            }
            break;
            case R.id.tvWechat: {//第三方微信登录

            }
            break;
            case R.id.tvGoRegister: {//还没有注册，去注册
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
            break;
        }
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(LoginActivity.this);

        islogin = getIntent().getStringExtra("islogin");
        //增加沉浸式状态栏
//        new StatusBar(this).initState();
    }

    @Override
    protected void click(View view) {

    }

    //登录异步请求
    private void startLoginTask(String username, String password) {
        showProgressDialog(LoginActivity.this, getResources().getString(R.string.operation_loading));
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_MOBILE, username);
        params.put(Constant.CAR_PWD, TokenUtils.generateMD5(password));
        params.put(Constant.CAR_LOGIN_PLATFORM, "2");//2
        if (StringUtil.isInvalid(App.registrationId)) {
            App.registrationId = JPushInterface.getRegistrationID(getApplicationContext());
        }
        params.put(Constant.CAR_REGISTRATION_ID, App.registrationId);

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_MOBILE, username);
        hashMap.put(Constant.CAR_PWD, TokenUtils.generateMD5(password));
        hashMap.put(Constant.CAR_LOGIN_PLATFORM, "2");//2
        hashMap.put(Constant.CAR_REGISTRATION_ID, App.registrationId);
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startLoginTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.LOGIN, "login", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "startLoginTask e---->" + e);
                dismissProgressDialog();
//                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                Log.e(TAG, "startLoginTask response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
                            JSONObject rowJSONObject = jsonObject.optJSONObject(Constant.CAR_ROWS);
                            LogUtils.i("islogin=" + islogin);
                            if (null != rowJSONObject) {
                                saveUserInfoPreference(rowJSONObject);
                                if (TextUtils.isEmpty(islogin)) {
                                    finish();
                                } else {
                                    IntentUtil.setIntent(LoginActivity.this, MainActivity.class);
                                    finish();
                                }
                            }
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (usernameEditText.isFocused()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < charSequence.length(); i++) {
                if (i != 3 && i != 8 && charSequence.charAt(i) == ' ') {
                    continue;
                } else {
                    sb.append(charSequence.charAt(i));
                    if ((sb.length() == 4 || sb.length() == 9)
                            && sb.charAt(sb.length() - 1) != ' ') {
                        sb.insert(sb.length() - 1, ' ');
                    }
                }
            }
            if (!sb.toString().equals(charSequence.toString())) {
                int index = start + 1;
                if (sb.charAt(start) == ' ') {
                    if (before == 0) {
                        index++;
                    } else {
                        index--;
                    }
                } else {
                    if (before == 1) {
                        index--;
                    }
                }
                usernameEditText.setText(sb.toString());
                usernameEditText.setSelection(index);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        loginButton.setEnabled(StringUtil.isValid(username) && StringUtil.isValid(password) && agreeCheckBox.isChecked());
    }

    //保存用户个人信息到本地
    private void saveUserInfoPreference(JSONObject jsonObject) {
        String userId = jsonObject.optString(Constant.CAR_USER_ID);
        String userAvatar = jsonObject.optString(Constant.CAR_USER_AVATAR);
        String fullName = jsonObject.optString(Constant.CAR_USER_FULLNAME);
        String niceName = jsonObject.optString(Constant.CAR_USER_NICKNAME);
        String userSex = jsonObject.optString(Constant.CAR_USER_SEX);
        String userBirthday = jsonObject.optString(Constant.CAR_USER_BIRTHDAY);
        String userCity = jsonObject.optString(Constant.CAR_USER_CITY);
        String userMobile = jsonObject.optString(Constant.CAR_USER_MOBILE);
        String userPwd = jsonObject.optString(Constant.CAR_USER_PWD);
        String userLat = jsonObject.optString(Constant.CAR_USER_LATITUDEY);
        String userLon = jsonObject.optString(Constant.CAR_USER_LONGITUDEX);
        String userIdentity = jsonObject.optString(Constant.CAR_USER_IDENTITY);
        String userAssessStatus = jsonObject.optString(Constant.CAR_USER_ASSESSSTATUS);
        String userAssessiD = jsonObject.optString(Constant.CAR_USER_ASSESSID);
        String Utoken = jsonObject.optString("token");

        App.app.setData(UserPreferences.USER_ID, userId);
        App.app.setData(UserPreferences.USER_AVATAR, userAvatar);
        App.app.setData(UserPreferences.USER_PWD, userPwd);
        App.app.setData(UserPreferences.USER_NAME, fullName);
        App.app.setData(UserPreferences.USER_NICK, niceName);
        App.app.setData(UserPreferences.USER_SEX, userSex);
        App.app.setData(UserPreferences.USER_BIRTHDAY, userBirthday);
        App.app.setData(UserPreferences.USER_CITY, userCity);
        App.app.setData(UserPreferences.USER_MOBILE, userMobile);
        App.app.setData(UserPreferences.USER_LATITUDEY, userLat);
        App.app.setData(UserPreferences.USER_LONGITUDEX, userLon);
        App.app.setData(UserPreferences.USER_IDENTITY, userIdentity);
        App.app.setData(UserPreferences.USER_ASSESSSTATUS, userAssessStatus);
        App.app.setData(UserPreferences.USER_ASSESSID, userAssessiD);
        App.app.setData("Utoken", Utoken);
        connect(Utoken);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        App.app.setData(UserPreferences.USER_BACK, "true");
    }


    /**
     * 建立与融云服务器的连接
     * * @param令牌
     */
    private void connect(String token) {
        /**
         * IMKit SDK调用第二步,建立与服务器的连接
         */
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            /**
             * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
             */
            @Override
            public void onTokenIncorrect() {
                LogUtils.i("--onTokenIncorrect");
            }

            /**
             * 连接融云成功
             * @param userid 当前 token
             */
            @Override
            public void onSuccess(String userid) {
                LogUtils.i("--onSuccess" + userid);
            }

            /**
             * 连接融云失败
             * @param errorCode 错误码，可到官网 查看错误码对应的注释
             */
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtils.i("--onError" + errorCode);
            }
        });
    }

}
