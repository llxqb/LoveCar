package com.bhxx.lovecar.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.GJSAssessListAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.GJSPublishServiceModel;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.AuthResult;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.OrderInfoUtil2_0;
import com.bhxx.lovecar.utils.PayResult;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;

@InjectLayer(R.layout.activity_look_car)
public class LookCarActivity extends BasicActivity {
    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "";

    /**
     * 支付宝账户登录授权业务：入参pid值
     */
    public static final String PID = "";
    /**
     * 支付宝账户登录授权业务：入参target_id值
     */
    public static final String TARGET_ID = "";

    /**
     * 商户私钥，pkcs8格式
     */
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(LookCarActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(LookCarActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(LookCarActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(LookCarActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    @InjectAll
    private Views v;
    private GJSPublishServiceModel gjsModel;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView back, lookcar_avatar, zhifu_type;
        TextView lookcar_carType, lookcar_username, lookcar_city, lookcar_assessNum, lookcar_price;
        RatingBar lookcar_level;
        MyListView lovecar_assess_listview;
        RelativeLayout lookcar_add_layout;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        gjsModel = (GJSPublishServiceModel) getIntent().getSerializableExtra("gjsModel");
        if (gjsModel != null) {
            initView();
        }
        initLoveCarList();
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.zhifu_type:

                break;
        }
    }

    private void initLoveCarList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            params.put("userId", App.app.getData(UserPreferences.USER_ID));
        } else {
            showToast("请先登录");
        }
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.ASSESS_LIST_CAR, "list", params, new MyStringCallback());
    }

    private void initView() {
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + gjsModel.getAvatar(), (ImageView) v.lookcar_avatar, LoadImage.getHeadImgOptions());
        v.lookcar_username.setText(gjsModel.getAssessName());
        v.lookcar_city.setText(gjsModel.getServiceRegion());
        v.lookcar_price.setText("￥" + gjsModel.getServicePrice());
        if (gjsModel.getAssessTimes() == null) {
            v.lookcar_assessNum.setText(Html.fromHtml("估价" + "<font color='red'>" + 0 + "</font>" + "次"));
        } else {
            v.lookcar_assessNum.setText(Html.fromHtml("估价" + "<font color='red'>" + gjsModel.getAssessTimes() + "</font>" + "次"));
        }
        v.lookcar_level.setRating(Float.parseFloat(gjsModel.getGrade()+1));
        v.lookcar_carType.setText(gjsModel.getCarTypeName());
    }

    /**
     * 支付宝支付业务
     *
     * @param v
     */
    public void payV2(View v) {
        if (TextUtils.isEmpty(APPID) || TextUtils.isEmpty(RSA_PRIVATE)) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String sign = OrderInfoUtil2_0.getSign(params, RSA_PRIVATE);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(LookCarActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.e("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<CarModel> carListBean = JsonUtils.getBean(response, CommonListBean.class, CarModel.class);
                if (carListBean.getResultCode().equals("0000")) {
                    if (carListBean.getRows().size() > 0) {
                        GJSAssessListAdapter assessListAdapter = new GJSAssessListAdapter(carListBean.getRows(), LookCarActivity.this, R.layout.gjs_assess_car_item);
                        v.lovecar_assess_listview.setAdapter(assessListAdapter);
                        v.lookcar_add_layout.setVisibility(View.GONE);
                    } else {
                        GJSAssessListAdapter assessListAdapter = new GJSAssessListAdapter(carListBean.getRows(), LookCarActivity.this, R.layout.gjs_assess_car_item);
                        v.lovecar_assess_listview.setAdapter(assessListAdapter);
                    }
                } else {
                    showToast(Constant.ERROR_WEB);
                }
            }
        }
    }
}
