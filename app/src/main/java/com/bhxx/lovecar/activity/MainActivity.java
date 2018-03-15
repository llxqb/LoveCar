package com.bhxx.lovecar.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.service.LocationService;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.fragment.ConversationMsgFragment;
import com.bhxx.lovecar.fragment.HomePage;
import com.bhxx.lovecar.fragment.MinePageFragment;
import com.bhxx.lovecar.fragment.SelectCarFragment;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.AppUpdateUtil;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.javase.lang.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;

@InjectLayer(R.layout.activity_main)
public class MainActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private Fragment home;
    private Fragment selectcar;
    private Fragment msg;
    private Fragment user;
    private int updateTimes;//更新次数
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x11:
                    AppUpdateUtil.newInstance(MainActivity.this).downloadFile(MainActivity.this);
                    break;
            }
        }
    };
    private Handler updateHandler = new Handler();
    private Runnable updateRunnable;
    public static final int BD_TypeServerSuccess = 0x12;
    private LocationService locationService;
    private boolean location = true;
    private double longitudeX;
    private double latitudeY;
    private String userCity;
    private Handler mCityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BD_TypeServerSuccess: {
                    if (longitudeX > 0 && latitudeY > 0 && location && StringUtil.isValid(userCity)) {
                        location = false;
                        startSendLocationTask();
                    }
                }
                break;
                case BDLocation.TypeServerError: {
//                    ToastUtil.show(MainActivity.this, getResources().getString(R.string.op_bd_net_diff));
                }
                break;
                case BDLocation.TypeCriteriaException: {
//                    ToastUtil.show(MainActivity.this, getResources().getString(R.string.op_bd_net_fxms));
                }
                break;
            }
        }
    };
    //定位结果回调，重写onReceiveLocation方法
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Message message = mCityHandler.obtainMessage();
                longitudeX = location.getLongitude();
                latitudeY = location.getLatitude();
                userCity = location.getCity();

                if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    message.what = BDLocation.TypeNetWorkException;
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    message.what = BDLocation.TypeCriteriaException;
                } else {
                    if (location.getLongitude() != 4.9E-324 && location.getLatitude() != 4.9E-324) {
                        message.what = BD_TypeServerSuccess;
                        mCityHandler.sendMessage(message);
                    }
                }
            }
        }

    };

    private class Views {
        FrameLayout main_frame;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageButton main_home_bt, main_selectcar_bt, main_msg_bt, main_mine_bt;
    }

    @Override
    protected void init() {
//        ActivityCollector.finishAll();
        ActivityCollector.addActivity(MainActivity.this);
        showHome();

        if (!TextUtils.isEmpty(App.app.getData("Utoken"))) {
            //Utoken=J/7XIQqJZZpiiewaCE/7gDQreleT+EJMqK6/sB5RIP3VosDsiajhHf721HskiFcS+SV17PuncU83qzjP53c91damhcp8TaOS
            //YMfwO90szNG9D+4CuOlZV4gDLS4hiFzE4yLQzVakgRvaTcoazntk0inuDpOFzfWTeSo0NMW8bfCbkePGskN4qA==
            connect(App.app.getData("Utoken"));

            //设置会话界面的功能
            InputProvider.ExtendProvider[] ep = {
                    new CameraInputProvider(RongContext.getInstance()),
                    new ImageInputProvider(RongContext.getInstance()),
//                        new LocationInputProvider(RongContext.getInstance()),//地理位置
            };
            //我需要让他在什么会话类型中的 bar 展示
            RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, ep);
        }


        //自动更新一天只检查一次
//        updateTimes = 150;
//        updateRunnable = new Runnable() {
//            @Override
//            public void run() {
//                //自动更新
//                updateTimes++;
//                if (updateTimes > 24 * 6) {
//                    updateTimes = 0;
//                    long currentTime = System.currentTimeMillis();
//                    long lastTime = PreferencesUtil.getLong(MainActivity.this, "updatetime", 0);
//                    if (currentTime - lastTime > 24 * 60 * 60 * 1000) {//一天内只检查一次版本
//                        AppUpdateUtil.checkVersion(MainActivity.this, mHandler, 0x11);
//                    }
//                }
//
//                updateHandler.postDelayed(updateRunnable, 10 * 60 * 1000);//10分钟后请求一次
//            }
//        };
//        updateHandler.postDelayed(updateRunnable, 1000);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialog(getResources().getString(R.string.op_permission_title), getResources().getString(R.string.op_permission_con_loc),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MainActivity.super.cancelAlertDialog();
                                    //请求权限
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MainActivity.super.cancelAlertDialog();
                                }
                            }, getResources().getString(R.string.operation_confirm), getResources().getString(R.string.operation_cancel));
                } else {
                    //请求权限
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                }
            } else {
                onStartLocation();
            }
        } else {
            onStartLocation();
        }
    }


    /**
     * 每次点击清除状态
     */
    private void clearState() {
        v.main_home_bt.setImageResource(R.mipmap.tab_home_pre);
        v.main_selectcar_bt.setImageResource(R.mipmap.tab_selectcar_pre);
        v.main_msg_bt.setImageResource(R.mipmap.tab_msg_pre);
        v.main_mine_bt.setImageResource(R.mipmap.tab_mine_pre);
    }

    @Override
    protected void click(View view) {
        clearState();
        switch (view.getId()) {
            case R.id.main_home_bt:
                v.main_home_bt.setImageResource(R.mipmap.tab_home);
                showHome();
                break;
            case R.id.main_selectcar_bt:
                v.main_selectcar_bt.setImageResource(R.mipmap.tab_selectcar);
                showmainSelectCar();
                break;
            case R.id.main_msg_bt:
                v.main_msg_bt.setImageResource(R.mipmap.tab_msg);
                showmainMsg();
                break;
            case R.id.main_mine_bt:
                //验证用户是否已经登录
                if (!isLogin(MainActivity.this)) {
                    //没登陆
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    App.app.setData("isMain", "isMain");
                } else {
                    v.main_mine_bt.setImageResource(R.mipmap.tab_mine);
                    showmainUser();
                }
                break;
        }
    }

    /**
     * 家长首页
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 展示首页
     */
    private void showHome() {
        home = getSupportFragmentManager().findFragmentByTag("home");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (home == null) {
            home = new HomePage();
            transaction.add(R.id.main_frame, home);
        } else {
            transaction.show(home);
        }
        if (msg != null) {
            transaction.hide(msg);
        }
        if (selectcar != null) {
            transaction.hide(selectcar);
        }
        if (user != null) {
            transaction.hide(user);
        }
        //commit方法是在Activity的onSaveInstanceState()之后调用的，onResume()调用showHome()会出错
        transaction.commitAllowingStateLoss();
    }

    /**
     * 选车
     */
    private void showmainSelectCar() {
        selectcar = getSupportFragmentManager().findFragmentByTag("selectcar");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (selectcar == null) {
            selectcar = new SelectCarFragment();
            transaction.add(R.id.main_frame, selectcar);
        } else {
            transaction.show(selectcar);
        }
        if (home != null) {
            transaction.hide(home);
        }
        if (msg != null) {
            transaction.hide(msg);
        }
        if (user != null) {
            transaction.hide(user);
        }
        transaction.commit();
    }

    /**
     * 展示车友圈消息
     */
    private void showmainMsg() {
        msg = getSupportFragmentManager().findFragmentByTag("msg");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (msg == null) {
            msg = new ConversationMsgFragment();
            transaction.add(R.id.main_frame, msg);
        } else {
            transaction.show(msg);
        }
        if (home != null) {
            transaction.hide(home);
        }
        if (selectcar != null) {
            transaction.hide(selectcar);
        }
        if (user != null) {
            transaction.hide(user);
        }
        transaction.commit();
    }


    /**
     * 展示个人主页
     */
    private void showmainUser() {
        user = getSupportFragmentManager().findFragmentByTag("user");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (user == null) {
            user = new MinePageFragment();
            transaction.add(R.id.main_frame, user);
        } else {
            transaction.show(user);
        }
        if (home != null) {
            transaction.hide(home);
        }
        if (msg != null) {
            transaction.hide(msg);
        }
        if (selectcar != null) {
            transaction.hide(selectcar);
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //恢复主界面都跳到首页
        if (!TextUtils.isEmpty(App.app.getData("isMain"))) {
            v.main_home_bt.setImageResource(R.mipmap.tab_home);
            showHome();
            App.app.setData("isMain", "");
        }
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

    //用户退出爱车估价后直接调用首页
    public void exitLoadHome() {
        v.main_home_bt.setImageResource(R.mipmap.tab_home);
        showHome();
    }
    public void onEvent(Object o) {
        Log.e("main onEvent", "====onEvent===");
    }

    //android6.0以上的权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100: {//定位 系统给的权限提醒后 确认 长度>0 否则提示
                Log.e("MainActivity", "100");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//权限请求确认
                    onStartLocation();
                } else {//权限请求取消
                    showToast(getResources().getString(R.string.op_permission_loc));
                }
            }
        }
    }

    //百度开始定位
    private void onStartLocation() {
        locationService = ((App) getApplication()).locationService;
        //注册监听
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();// 定位SDK
    }

    //上传经纬度异步请求
    private void startSendLocationTask() {
        String userId = App.app.getData(UserPreferences.USER_ID);
        if (StringUtil.isInvalid(userId) || "null".equals(userId)) {
            return;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, userId);
        params.put(Constant.CAR_USER_LONGITUDEX, longitudeX + "");//经度
        params.put(Constant.CAR_USER_LATITUDEY, latitudeY + "");//纬度
        params.put(Constant.CAR_USER_CITY, userCity);//纬度

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, userId);
        hashMap.put(Constant.CAR_USER_LONGITUDEX, longitudeX + "");//经度
        hashMap.put(Constant.CAR_USER_LATITUDEY, latitudeY + "");//纬度
        hashMap.put(Constant.CAR_USER_CITY, userCity);//纬度

        hashMap.put(Constant.CAR_KEY, sign);
        Log.e("MainActivity", "startSendLocationTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.FRIEND_MEMBERXY, "friendXY", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("MainActivity", "startSendLocationTask e---->" + e);
//                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("MainActivity", "startSendLocationTask response---->" + response);
                locationService.unregisterListener(mListener);
                locationService.stop();
                App.app.setData(UserPreferences.USER_LATITUDEY, latitudeY + "");
                App.app.setData(UserPreferences.USER_LONGITUDEX, longitudeX + "");
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            App.app.setData(UserPreferences.USER_LATITUDEY, latitudeY + "");
                            App.app.setData(UserPreferences.USER_LONGITUDEX, longitudeX + "");
//                            showToast(jsonObject.optString("resultDesc"));
//                            locationService.unregisterListener(mListener);
//                            locationService.stop();
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
