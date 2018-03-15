package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CheckUtils;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.TokenUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.TimeButton;
import com.makeapp.android.util.HandlerUtil;
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
public class RegisterActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText mobileEditText;//手机号
    private EditText codeEditText;//验证码
    private TextView tipTextView;//协议
    private TimeButton timeButton;//获取验证码按钮
    private Button nextButton;//下一步
    private String safeCode = "";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            double d = Math.random() * 9000 + 1000;
//            int code = (int) d;
//            codeEditText.setText(code + "");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
        SpannableString spanString = new SpannableString(getResources().getString(R.string.mine_hint_cartip));
        ForegroundColorSpan spanForeground = new ForegroundColorSpan(getResources().getColor(R.color.orange_normal));
        spanString.setSpan(spanForeground, 7, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tipTextView.setText(spanString);
    }

    //获取界面控件
    private void initView() {
        mobileEditText = (EditText) this.findViewById(R.id.etMobilephone);
        codeEditText = (EditText) this.findViewById(R.id.tvCode);
        tipTextView = (TextView) this.findViewById(R.id.tvTip);
        timeButton = (TimeButton) this.findViewById(R.id.tbtnCode);
        nextButton = (Button) this.findViewById(R.id.btnNext);
    }

    //初始化控件点击事件
    private void initEvent() {
        timeButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
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
            case R.id.btnNext: {
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


                    Intent intent = new Intent(RegisterActivity.this, RegisterConfirmActivity.class);
                    intent.putExtra("phone", mobile);
                    startActivity(intent);
                    finish();
                }
            }
            break;
        }
    }

    //验证手机号合法性
    private boolean verifyMobile(String mobile) {
        if (StringUtil.isInvalid(mobile)) {
            showToast(getResources().getString(R.string.mine_hint_mobile));
            return false;
        }
        if (!CheckUtils.checkMobile(mobile)) {
            showToast(getResources().getString(R.string.mine_hint_mobile_error));
            return false;
        }
        return true;
    }


    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {
    }

    //获取验证码异步请求
    private void startGetCodeTask(String mobile, String code) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_MOBILE, mobile);
        params.put(Constant.CAR_SAFE_CODE, code);
        params.put(Constant.CAR_TYPE, "1");

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_MOBILE, mobile);
        hashMap.put(Constant.CAR_SAFE_CODE, code);
        hashMap.put(Constant.CAR_TYPE, "1");
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
                dismissProgressDialog();
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
