package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.TokenUtils;
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
 * 我的-修改密码
 *
 * @qq289513149.
 */

public class ModifyPasswordActivity extends BasicActivity implements View.OnClickListener {

    public static final String TAG = ModifyPasswordActivity.class.getSimpleName();
    public static final int REQUEST_OLD_PASSWORD = 0x11;
    public static final int REQUEST_NEW_PASSWORD = 0x12;
    public static final int REQUEST_CNEW_PASSWORD = 0x13;
    private View oldView;
    private View newView;
    private View cNewView;
    private EditText oldTextView;
    private EditText newTextView;
    private EditText cNewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initView();
        initEvent();
    }

    //初始化界面控件
    public void initView() {
        oldView = this.findViewById(R.id.llOldPassword);
        newView = this.findViewById(R.id.llNewPassword);
        cNewView = this.findViewById(R.id.llCNewPassword);
        oldTextView = (EditText) this.findViewById(R.id.tvOldPass);
        newTextView = (EditText) this.findViewById(R.id.tvNewPass);
        cNewTextView = (EditText) this.findViewById(R.id.tvCNewPass);
    }

    //初始化界面控件事件
    public void initEvent() {
//        oldView.setOnClickListener(this);
//        newView.setOnClickListener(this);
//        cNewView.setOnClickListener(this);
    }

    public void onBackClick(View view) {
        finish();
    }

    //确认修改按钮
    public void onConfirmModify(View view) {
        String oPass = oldTextView.getText().toString();
        String nPass = newTextView.getText().toString();
        String cNPass = cNewTextView.getText().toString();
        String md5OPass = App.app.getData(UserPreferences.USER_PWD);
        if (!md5OPass.equals(TokenUtils.generateMD5(oPass))) {
            showToast(getResources().getString(R.string.mine_password_old_error));
            return;
        }
        if (StringUtil.isInvalid(nPass)) {
            showToast(getResources().getString(R.string.mine_hint_new_pass));
            return;
        }
        if (StringUtil.isInvalid(cNPass)) {
            showToast(getResources().getString(R.string.mine_hint_c_new_pass));
            return;
        }
        if (nPass.length() < 6) {
            showToast(getResources().getString(R.string.mine_hint_npassword_length));
            return;
        }
        if (!nPass.equals(cNPass)) {
            showToast(getResources().getString(R.string.mine_hint_n_pass_diff));
            return;
        }
        String mobile = App.app.getData(UserPreferences.USER_MOBILE);
        String userId = App.app.getData(UserPreferences.USER_ID);
        startModifyPasswordTask(mobile, oPass, cNPass, userId);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llOldPassword: {//原密码
                Intent intent = new Intent(ModifyPasswordActivity.this, SmallTextActivity.class);
                intent.putExtra(SmallTextActivity.REQUEST_TYPE, false);
                intent.putExtra(SmallTextActivity.REQUEST_TITLE, getResources().getString(R.string.mine_mobile_old_pass));
                startActivityForResult(intent, REQUEST_OLD_PASSWORD);
            }
            break;
            case R.id.llNewPassword: {//新密码
                Intent intent = new Intent(ModifyPasswordActivity.this, SmallTextActivity.class);
                intent.putExtra(SmallTextActivity.REQUEST_TYPE, false);
                intent.putExtra(SmallTextActivity.REQUEST_TITLE, getResources().getString(R.string.mine_mobile_new_pass));
                startActivityForResult(intent, REQUEST_NEW_PASSWORD);
            }
            break;
            case R.id.llCNewPassword: {//确认新密码
                Intent intent = new Intent(ModifyPasswordActivity.this, SmallTextActivity.class);
                intent.putExtra(SmallTextActivity.REQUEST_TYPE, false);
                intent.putExtra(SmallTextActivity.REQUEST_TITLE, getResources().getString(R.string.mine_mobile_cnew_pass));
                startActivityForResult(intent, REQUEST_CNEW_PASSWORD);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OLD_PASSWORD && resultCode == SmallTextActivity.RESULT_CONTENT) {
            oldTextView.setText(data.getStringExtra(SmallTextActivity.RESPONSE_CONTENT));
        } else if (requestCode == REQUEST_NEW_PASSWORD && resultCode == SmallTextActivity.RESULT_CONTENT) {
            newTextView.setText(data.getStringExtra(SmallTextActivity.RESPONSE_CONTENT));
        } else if (requestCode == REQUEST_CNEW_PASSWORD && resultCode == SmallTextActivity.RESULT_CONTENT) {
            cNewTextView.setText(data.getStringExtra(SmallTextActivity.RESPONSE_CONTENT));
        }
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(ModifyPasswordActivity.this);
    }

    @Override
    protected void click(View view) {

    }

    //修改密码异步请求
    private void startModifyPasswordTask(String mobile, String password,final String upassword, String userId) {
        showProgressDialog(ModifyPasswordActivity.this, getResources().getString(R.string.operation_passwording));
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_MOBILE, mobile);
        params.put(Constant.CAR_PWD, TokenUtils.generateMD5(password));
        params.put(Constant.CAR_UPWD, TokenUtils.generateMD5(upassword));
        params.put(Constant.CAR_USER_ID, userId);

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_MOBILE, mobile);
        hashMap.put(Constant.CAR_PWD, TokenUtils.generateMD5(password));
        hashMap.put(Constant.CAR_UPWD, TokenUtils.generateMD5(upassword));
        hashMap.put(Constant.CAR_USER_ID, userId);
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startFindPasswordTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.UPDATE_PASSWORD, "find", hashMap, new CommonCallback() {
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
                            App.app.setData(UserPreferences.USER_PWD, upassword);//修改之后本地保存一个
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
