package com.bhxx.lovecar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.android.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import io.rong.imkit.RongIM;
import okhttp3.Call;

/**
 * Created by @dpy on 2017/1/6.
 * 我的-设置
 *
 * @qq289513149.
 */

public class SetupActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = SetupActivity.class.getSimpleName();
    private View infoView;//个人信息
    private View passwordView;//修改密码
    private View opinionView;//意见反馈
    private View protocolView;//用户协议
    private View versionView;//版本更新

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initView();
        initEvent();
    }

    //初始化界面控件
    private void initView() {
        infoView = this.findViewById(R.id.llInfo);
        passwordView = this.findViewById(R.id.llModifyPassword);
        opinionView = this.findViewById(R.id.llOpinion);
        protocolView = this.findViewById(R.id.llProtocol);
        versionView = this.findViewById(R.id.llVersion);
    }

    //初始化控件事件
    private void initEvent() {
        infoView.setOnClickListener(this);
        passwordView.setOnClickListener(this);
        opinionView.setOnClickListener(this);
        protocolView.setOnClickListener(this);
        versionView.setOnClickListener(this);
    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llInfo: {//个人信息
                startActivity(new Intent(this, UserDetailActivity.class));
            }
            break;
            case R.id.llModifyPassword: {//修改密码
                startActivity(new Intent(this, ModifyPasswordActivity.class));
            }
            break;
            case R.id.llOpinion: {//意见反馈
                startActivity(new Intent(this, UserOpinionActivity.class));
            }
            break;
            case R.id.llProtocol: {//用户协议
                startActivity(new Intent(this, UserProtocolActivity.class));
            }
            break;
            case R.id.llVersion: {//版本更新
                ToastUtil.show(this, "当前已经是最新版本！");
            }
            break;
        }
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }

    public void onLogoutAppClick(View view){
                new AlertDialog.Builder(this).setTitle("提示").setMessage("确定要退出登录?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        startOutLoginTask();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
//
////                //退出登录直接清空id
////                showDialog(getContext(), getResources().getString(R.string.operation_tip), getResources().getString(R.string.operation_confirm_exit),
////                        new View.OnClickListener() {
////                            @Override
////                            public void onClick(View view) {
////                                //清空id
////                                App.app.setData(UserPreferences.USER_ID, "");
////                                //跳入首页
////                                if (mContext == null) {
////                                    mContext = getActivity();
////                                }
////                                ((MainActivity) mContext).exitLoadHome();
////                                MinePageFragment.super.cancelAlertDialog();
////                            }
////                        }, new View.OnClickListener() {
////                            @Override
////                            public void onClick(View view) {
////                                MinePageFragment.super.cancelAlertDialog();
////                            }
////                        }, getResources().getString(R.string.operation_ok), getResources().getString(R.string.operation_cancel));
//            }
//            break;
    }
    //登录异步请求
    private void startOutLoginTask() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_KEY, sign);
        MyOkHttp.postMap(GlobalValues.OUTLOGIN, "outlogin", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.i("response=" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));

                            App.app.clearDatabase("Cache");
                            ActivityCollector.finishAll();
                            Intent intent = new Intent(SetupActivity.this, LoginActivity.class);
                            intent.putExtra("islogin", "true");
                            startActivity(intent);
                            RongIM.getInstance().logout();

                        } else {
                            showToast(Constant.ERROR_WEB);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
