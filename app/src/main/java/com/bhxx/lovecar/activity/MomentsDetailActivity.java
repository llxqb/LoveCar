package com.bhxx.lovecar.activity;
/**
 * 为加入的圈子详情
 */

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import com.bhxx.lovecar.beans.CommonBean;
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

@InjectLayer(R.layout.activity_moments_detail)
public class MomentsDetailActivity extends BasicActivity {
    private CircleModel momentsModel;
    private List<DynamicModel> list = new ArrayList<>();
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView back, circleImg;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView circle_add, circleName, circle_count, circle_post_count, circle_brief;
        PullToRefreshLayout pull;
        PullableListView pullablelistview;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        momentsModel = (CircleModel) getIntent().getSerializableExtra("momentsdetail");
        if (momentsModel.getIsAdd().equals("1")) {
            v.circle_add.setVisibility(View.VISIBLE);
        } else {
            v.circle_add.setVisibility(View.GONE);
        }
        v.circleName.setText(momentsModel.getName());
        v.circle_count.setText("圈友" + momentsModel.getCircleCount());
        v.circle_post_count.setText("帖子" + momentsModel.getPostCount());
        v.circle_brief.setText(momentsModel.getBrief());
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + momentsModel.getExtend1(), v.circleImg, LoadImage.getDefaultOptions());

        initDynamicList();

        v.pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                v.pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                v.pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }

        });
    }

    private void initDynamicList() {
        showProgressDialog(MomentsDetailActivity.this);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("circleId", momentsModel.getCircleId() + "");
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            params.put("userIds", App.app.getData(UserPreferences.USER_ID));
        }
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.CIRCLE_DETAIL, 1, "detail", params, new MyStringCallback());
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.circle_add:
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    IntentUtil.setIntent(MomentsDetailActivity.this, LoginActivity.class);
                    showToast("请先登陆");
                } else {
                    //添加进圈子
                    LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                    params.put("userId", App.app.getData(UserPreferences.USER_ID));
                    params.put("circleId", momentsModel.getCircleId() + "");
                    params.put("name", momentsModel.getName());
                    params.put("sign", SingUtils.getMd5SignMsg(params));

                    MyOkHttp.postMap(GlobalValues.CIRCLE_ADD, 2, "add_circle", params, new MyStringCallback());
                }
                break;
        }
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
            switch (id) {
                case 1:
                    //动态列表信息
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<DynamicModel> listBean = JsonUtils.getBean(response, CommonListBean.class, DynamicModel.class);
                        if (listBean.getResultCode().equals("0000")) {
                            DynamicAdapter dynamicAdapter = new DynamicAdapter(listBean.getRows(), MomentsDetailActivity.this);
                            v.pullablelistview.setAdapter(dynamicAdapter);
                        } else {
                            showToast(Constant.ERROR_WEB);
                        }
                    }
                    break;
                case 2:
                    //添加圈子
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        showToast(bean.getResultDesc());

                        EventBus.getDefault().post(new Circleentity(1));
                        //跳转到我的圈子详情
                        MyMomentsDetailActivity.start(MomentsDetailActivity.this, momentsModel);
                        finish();
                    }
                    break;
            }
        }
    }

    public static void start(Context context, CircleModel momentsModel) {
        Intent intent = new Intent(context, MomentsDetailActivity.class);
        intent.putExtra("momentsdetail", momentsModel);
        context.startActivity(intent);
    }
}
