package com.bhxx.lovecar.activity;
/**
 * 车辆详情
 */

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.LinkedHashMap;

import io.rong.imkit.RongIM;
import okhttp3.Call;

@InjectLayer(R.layout.activity_car_detail)
public class CarDetailActivity extends BasicActivity {

    private int carId;
    private CarModel carModel;
    //分享
    private static String CONTENT;
    private UMImage imagelocal;
    private String shareUrl;
    private static String TITLE = "爱估车";
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView cardetail_back;
        WebView carDetail_webView;
        ProgressBar pb;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        LinearLayout cardetail_connection_layout, cardetail_share_layout;
        ImageView cardetail_connection;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView cardetail_connection_tv, msg, tel;
    }

    @Override
    protected void init() {
        //设置全屏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActivityCollector.addActivity(this);
        imagelocal = new UMImage(this, R.mipmap.icon_logo);
        imagelocal.setThumb(new UMImage(this, R.mipmap.icon_logo));
//        carId = getIntent().getStringExtra("carId");
        carModel = (CarModel) getIntent().getSerializableExtra("carModel");
        if (carModel != null) {
            CONTENT = carModel.getRemarks();
//            imagelocal = carModel.getAcpgPictures().getUrl();
        }
        carId = carModel.getCarId();
        initdata();
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            initcollection();//查看是否收藏
        }

    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.cardetail_back:
                finish();
                break;
            case R.id.cardetail_connection_layout:
                if (v.cardetail_connection_tv.getText().toString().equals("收藏")) {
                    if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                        showToast("请先登录");
                        return;
                    } else {
                        v.cardetail_connection.setImageResource(R.mipmap.icon_chpj);
                        connection();
                    }
                } else if (v.cardetail_connection_tv.getText().toString().equals("已收藏")) {
                    v.cardetail_connection.setImageResource(R.mipmap.bt_sc);
                    unconnection();
                }
                break;
            case R.id.msg:
                if (RongIM.getInstance() != null) {
                    if (carModel.getUserId() != null) {
                        RongIM.getInstance().startPrivateChat(CarDetailActivity.this, "" + carModel.getUserId() + "", carModel.getContacts());
                    }
                }
                break;
            case R.id.tel:
                if (!TextUtils.isEmpty(carModel.getMobile())) {
                    Intent call = new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + carModel.getMobile()));
                    startActivity(call);
                } else {
                    showToast("暂无号码");
                }
                break;
            case R.id.cardetail_share_layout:
                //分享
                showShareDialog();
                break;
        }
    }

    private void connection() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            showToast("请先登录");
        } else {
            params.put("userId", App.app.getData(UserPreferences.USER_ID));
        }
        params.put("carId", carId + "");
        params.put("createType", "0");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.CONNECTION, 2, "connection", params, new MyStringCallback());
    }

    private void unconnection() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("carId", carId + "");
        params.put("createType", "0");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.UNCONNECTION, 3, "connection", params, new MyStringCallback());
    }

    private void initdata() {
        v.pb.setMax(100);
        v.carDetail_webView.getSettings().setJavaScriptEnabled(true);
        v.carDetail_webView.getSettings().setSupportZoom(false);
        v.carDetail_webView.getSettings().setBuiltInZoomControls(false);
        v.carDetail_webView.getSettings().setDisplayZoomControls(false);
        v.carDetail_webView.setWebChromeClient(new WebChromeViewClient());

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("carId", carId + "");

        shareUrl = GlobalValues.CAR_DETAIL + "?carId=" + carId + "&sign=" + SingUtils.getMd5SignMsg(params);
        v.carDetail_webView.loadUrl(shareUrl);

    }

    private class WebChromeViewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            v.pb.setProgress(newProgress);
            if (newProgress == 100) {
                v.pb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

    }


    private void initcollection() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("carId", carId + "");
        params.put("createType", "0");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.ISCONNECTION, 1, "isconnection", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    LogUtils.i("response1=" + response);
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getRows() != null) {
                            if (bean.getRows().toString().equals("1")) {
                                v.cardetail_connection.setImageResource(R.mipmap.bt_sc);
                                v.cardetail_connection_tv.setText("收藏");
                            } else {
                                v.cardetail_connection.setImageResource(R.mipmap.icon_chpj);
                                v.cardetail_connection_tv.setText("已收藏");
                            }
                        }

                    }
                    break;
                case 2:
                    LogUtils.i("response2=" + response);
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            v.cardetail_connection.setImageResource(R.mipmap.icon_chpj);
                            showToast("已收藏");
                            v.cardetail_connection_tv.setText("已收藏");
                        }
                    }
                    break;
                case 3:
                    LogUtils.i("response3=" + response);
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            v.cardetail_connection.setImageResource(R.mipmap.bt_sc);
                            v.cardetail_connection_tv.setText("收藏");
                        }
                    }
                    break;
            }
        }
    }

    public void showShareDialog() {
        final Dialog dialog = new Dialog(CarDetailActivity.this, R.style.style_dialog);
        dialog.setContentView(R.layout.layout_third_group);
        dialog.setCanceledOnTouchOutside(true);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (v.getId()) {
                    case R.id.btnWechat:
                        new ShareAction(CarDetailActivity.this)
                                .withText(CONTENT)
                                .withMedia(imagelocal)
                                .withTargetUrl(shareUrl)
                                .withTitle(TITLE)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(shareListener).share();
                        break;
                    case R.id.btnQQ:
                        new ShareAction(CarDetailActivity.this)
                                .withText(CONTENT)
                                .withMedia(imagelocal)
                                .withTargetUrl(shareUrl)
                                .withTitle(TITLE)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(shareListener).share();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.btnWechat).setOnClickListener(listener);
        dialog.findViewById(R.id.btnQQ).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        dialog.show();
    }

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(CarDetailActivity.this, "分享成功了", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(CarDetailActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };
}
