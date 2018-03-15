package com.bhxx.lovecar.activity;
/**
 * 主页-估价师
 */

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.GJSAdapter;
import com.bhxx.lovecar.adapter.GJSPublishServiceAdapter;
import com.bhxx.lovecar.beans.CarAssessInfoModel;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.GJSModel;
import com.bhxx.lovecar.beans.GJSPublishServiceModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;
import com.makeapp.javase.json.JSONUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_gjs)
public class GJSActivity extends BasicActivity {

    @InjectAll
    private Views v;


    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView gjs_back, gjs_selectcartype;
        PullToRefreshLayout my_pull;
        PullableListView my_list;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        initGjslist();
        v.my_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                v.my_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.my_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                v.my_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.my_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.gjs_back:
                finish();
                break;
            case R.id.gjs_selectcartype:
                IntentUtil.setIntent(GJSActivity.this, GJSServerCarActivity.class);
                break;
        }
    }

    /**
     * 获取所有估价师发布的服务
     */
    private void initGjslist() {
//        List<GJSModel> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            GJSModel model = new GJSModel();
//
//            if (i % 2 == 0) {
//                model.setAssessName("王美丽");
//                model.setAvatar("http://www.chinagirlol.cc/data/attachment/forum/201412/03/233758hw7o7h08kkozkcwi.jpg");
//                model.setGrade("4");
//            } else {
//                model.setAssessName("林望风");
//                model.setAvatar("http://i2.cqnews.net/car/attachement/jpg/site82/20120817/5404a6b61e3c1197fb211d.jpg");
//                model.setGrade("4.5");
//            }
//
//            model.setServiceRegion("上海");
//            model.setAssessTimes(20);
//            model.setServicePrice(100.00);
//
//            //缺少估价车型 ...
//            list.add(model);
//            GJSAdapter gjsAdapter = new GJSAdapter(list, GJSActivity.this, R.layout.gjs_item);
//            v.my_list.setAdapter(gjsAdapter);
//        }
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.GJS_ALLSERVER, 0, "allServerList", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response=" + response);
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<GJSPublishServiceModel> gjsListBean = JsonUtils.getBean(response, CommonListBean.class, GJSPublishServiceModel.class);
                if (gjsListBean.getResultCode().equals("0000")) {
                    GJSPublishServiceAdapter adapter = new GJSPublishServiceAdapter(gjsListBean.getRows(), GJSActivity.this, R.layout.gjs_item, "gjsdetail");
                    v.my_list.setAdapter(adapter);
                } else {
                    showToast(Constant.ERROR_WEB);
                }
            }
        }

    }
}
