package com.bhxx.lovecar.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.DynamicCommentAdapter;
import com.bhxx.lovecar.adapter.photo.PicAdappter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.DynamicCommentModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.beans.ZiXunModel;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_zi_xun_detail)
public class ZiXunDetailActivity extends BasicActivity {

    private String messageId;
    private List<DynamicCommentModel> commentlist = new ArrayList<>();
    private int iscollection = 1;//默认1为未收藏
    private int islike = 1;//默认1为为点赞
    private ZiXunModel zixunModel;
    //分享
    private static String CONTENT;
    private UMImage imagelocal;
    private String shareUrl;
    private static String TITLE = "爱估车";
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView back, share;
        WebView zixunDetail_webView;
        ProgressBar pb;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        LinearLayout zixun_msg_layout, zixun_like_layout, zixun_collection_layout;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView zixun_msg_num, zixun_like_num, zixun_collection_num, send_message_tv, hidetv;
        ImageView zixun_msg_icon, zixun_like_icon, zixun_collection_icon;
        ListView zixun_detail_listview;
        RelativeLayout zixunDetail_buttom;
        EditText send_message_et;
        LinearLayout msg_list_layout;

    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);

        zixunModel = (ZiXunModel) getIntent().getSerializableExtra("zixunModel");
        if (zixunModel != null) {
            messageId = zixunModel.getMessageId() + "";
            CONTENT = zixunModel.getTitle();
        }
        initdata();
        initmsgList();
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            //初始化 是否已收藏 点赞
            initcollection();
        }
    }

    private void initcollection() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("messageId", messageId);
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            params.put("userId", App.app.getData(UserPreferences.USER_ID));
        }
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.ZIXUN_IS_CONNECTION, 0, "iscollection", params, new MyStringCallback());
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.zixun_msg_layout:
//                v.msg_listview_layout.setVisibility(View.VISIBLE);
                v.zixunDetail_buttom.setVisibility(View.VISIBLE);
                break;
            case R.id.send_message_tv:
                //发送消息
                if (TextUtils.isEmpty(v.send_message_et.getText().toString())) {
                    showToast("请输入评论内容");
                    return;
                } else {
                    if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                        showToast("请先登录");
                        return;
                    } else {
                        sendMessage(v.send_message_et.getText().toString());
                    }
                }
                break;
            case R.id.zixun_collection_layout:
                //收藏
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    return;
                } else {
                    if (iscollection == 0) {
                        showToast("已收藏");
                        return;
                    }
                    collection();
                }
                break;
            case R.id.zixun_like_layout:
                //点赞
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    return;
                } else {
                    if (islike == 0) {
                        showToast("已点赞");
                        return;
                    }
                    like();
                }
                break;
            case R.id.share:
                showShareDialog();
                break;
        }
    }

    private void like() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            showToast("请先登录");
            return;
        } else {
            params.put("userId", App.app.getData(UserPreferences.USER_ID));
        }
        params.put("objectId", messageId);
        params.put("createType", "1");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.ZIXUN_LIKE, 4, "like", params, new MyStringCallback());
    }

    private void collection() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("messageId", messageId);
        params.put("createType", "1");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.ZIXUN_CONNECTION, 3, "collection", params, new MyStringCallback());

    }

    private void sendMessage(String content) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            showToast("请先登录");
            return;
        } else {
            params.put("userId", App.app.getData(UserPreferences.USER_ID));
        }
        params.put("messageId", messageId);
        params.put("cmContent", content);
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.ZIXUN_COMMENT, 1, "zixun_comment", params, new MyStringCallback());
    }

    private void initdata() {
        v.pb.setMax(100);
        v.zixunDetail_webView.getSettings().setJavaScriptEnabled(true);
        v.zixunDetail_webView.getSettings().setSupportZoom(false);
        v.zixunDetail_webView.getSettings().setBuiltInZoomControls(false);
        v.zixunDetail_webView.getSettings().setDisplayZoomControls(false);
        v.zixunDetail_webView.setWebChromeClient(new WebChromeViewClient());

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("messageId", messageId);
        shareUrl = GlobalValues.ZIXUN_DETAIL + "?messageId=" + messageId + "&sign=" + SingUtils.getMd5SignMsg(params);
        v.zixunDetail_webView.loadUrl(shareUrl);

    }

    private class WebChromeViewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            v.pb.setProgress(newProgress);
            if (newProgress == 100) {
                v.msg_list_layout.setVisibility(View.VISIBLE);
                v.zixunDetail_buttom.setVisibility(View.VISIBLE);
                v.pb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    private void initmsgList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("messageId", messageId);
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.ZIXUN_COMMENT_LIST, 2, "comment_list", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 0:
                    //初始化请求
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<ZiXunModel> bean = JsonUtils.getBean(response, CommonBean.class, ZiXunModel.class);
                        if (bean.getResultCode().equals("0000")) {
                            if (bean.getRows().getCollectStatus().equals("0")) {
                                v.zixun_collection_icon.setImageResource(R.mipmap.icon_collection_ed);
                                iscollection = 0;
                            }
                            if (bean.getRows().getHitStatus().equals("0")) {
                                v.zixun_like_icon.setImageResource(R.mipmap.icon_fabulous_ed);
                                islike = 0;
                            }
                        }
                    }
                    break;
                case 1:
                    //发表评论
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            showToast("评论成功");
                            v.send_message_et.setText("");
                            initmsgList();
                        }
                    }
                    break;
                case 2:
                    //评论集合
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<DynamicCommentModel> dynamicListBean = JsonUtils.getBean(response, CommonListBean.class, DynamicCommentModel.class);
                        if (dynamicListBean.getResultCode().equals("0000")) {
                            if (dynamicListBean.getRows().size() > 0) {
                                v.hidetv.setVisibility(View.GONE);
                            } else {
                                v.hidetv.setVisibility(View.VISIBLE);
                            }
                            DynamicCommentAdapter dynamicCommentAdapter = new DynamicCommentAdapter(dynamicListBean.getRows(), ZiXunDetailActivity.this, R.layout.dynamic_comment_item);
                            v.zixun_detail_listview.setAdapter(dynamicCommentAdapter);
                        }
                    }
                    break;
                case 3:
                    //收藏
                    LogUtils.i("response=" + response);
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            showToast("收藏成功");
                            v.zixun_collection_icon.setImageResource(R.mipmap.icon_collection_ed);
                            iscollection = 0;
                        }
                    }
                    break;
                case 4:
                    //点赞
                    LogUtils.i("response=" + response);
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            showToast("点赞成功");
                            v.zixun_like_icon.setImageResource(R.mipmap.icon_fabulous_ed);
                            islike = 0;
                        }
                    }
                    break;
            }
        }
    }


    public void showShareDialog() {
        imagelocal = new UMImage(this, R.mipmap.icon_logo);
        imagelocal.setThumb(new UMImage(this, R.mipmap.icon_logo));

        final Dialog dialog = new Dialog(ZiXunDetailActivity.this, R.style.style_dialog);
        dialog.setContentView(R.layout.layout_third_group);
        dialog.setCanceledOnTouchOutside(true);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (v.getId()) {
                    case R.id.btnWechat:
                        new ShareAction(ZiXunDetailActivity.this)
                                .withText(CONTENT)
                                .withMedia(imagelocal)
                                .withTargetUrl(shareUrl)
                                .withTitle(TITLE)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(shareListener).share();
                        break;
                    case R.id.btnQQ:
                        new ShareAction(ZiXunDetailActivity.this)
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
            Toast.makeText(ZiXunDetailActivity.this, "分享成功", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ZiXunDetailActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };

}
