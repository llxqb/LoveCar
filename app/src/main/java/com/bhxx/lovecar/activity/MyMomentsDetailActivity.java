package com.bhxx.lovecar.activity;
/**
 * 我加入的圈子详情
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.DynamicAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.DynamicModel;
import com.bhxx.lovecar.beans.CircleModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.entity.Circleentity;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_mymoments_detail)
public class MyMomentsDetailActivity extends BasicActivity {
    private CircleModel momentsModel;
    private List<DynamicModel> list = new ArrayList<>();
    private TextView circleName, circle_count, circle_post_count, circle_brief;
    private ImageView circleImg;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView back, write_post;
        PullToRefreshLayout pull;
        PullableListView pullablelistview;
    }

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        ActivityCollector.addActivity(this);
        momentsModel = (CircleModel) getIntent().getSerializableExtra("momentsdetail");

        initHead();
        initDynamicList();

        v.pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                v.pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                initDynamicList();
                showToast(Constant.FINISH);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                v.pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                initDynamicList();
                showToast(Constant.FINISH);
            }
        });
    }

    protected void onEventMainThread(Circleentity circleentity) {
        if (circleentity.getKey() == 2) {
            initDynamicList();
        }

    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.write_post:
                Intent intent = new Intent(MyMomentsDetailActivity.this, WritePostActivity.class);
                intent.putExtra("circleId", momentsModel.getCircleId() + "");
                startActivity(intent);
                break;
        }
    }

    private void initHead() {
        View view = LayoutInflater.from(MyMomentsDetailActivity.this).inflate(R.layout.my_moments_head, null);
        circleName = (TextView) view.findViewById(R.id.circleName);
        circle_count = (TextView) view.findViewById(R.id.circle_count);
        circle_post_count = (TextView) view.findViewById(R.id.circle_post_count);
        circle_brief = (TextView) view.findViewById(R.id.circle_brief);
        circleImg = (ImageView) view.findViewById(R.id.circleImg);

        circleName.setText(momentsModel.getName());
        circle_count.setText(Html.fromHtml("圈友" + "<font color='red'>" + momentsModel.getCircleCount() + "</font>"));
        circle_post_count.setText(Html.fromHtml("帖子" + "<font color='red'>" + momentsModel.getPostCount() + "</font>"));
        circle_brief.setText(momentsModel.getBrief());
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + momentsModel.getExtend1(), circleImg, LoadImage.getDefaultOptions());
        v.pullablelistview.addHeaderView(view);
    }

    private void initDynamicList() {
        showProgressDialog(MyMomentsDetailActivity.this);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("circleId", momentsModel.getCircleId() + "");
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            params.put("userIds", App.app.getData(UserPreferences.USER_ID));
        }
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.CIRCLE_DETAIL, "detail", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response=" + response);
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<DynamicModel> listBean = JsonUtils.getBean(response, CommonListBean.class, DynamicModel.class);
                if (listBean.getResultCode().equals("0000")) {
                    DynamicAdapter dynamicAdapter = new DynamicAdapter(listBean.getRows(), MyMomentsDetailActivity.this);
                    v.pullablelistview.setAdapter(dynamicAdapter);
                } else {
                    showToast(Constant.ERROR_WEB);
                }
            }

        }
    }

    public static void start(Context context, CircleModel momentsModel) {
        Intent intent = new Intent(context, MyMomentsDetailActivity.class);
        intent.putExtra("momentsdetail", momentsModel);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
