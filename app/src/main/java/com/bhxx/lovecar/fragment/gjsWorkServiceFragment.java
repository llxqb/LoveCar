package com.bhxx.lovecar.fragment;
/**
 * 估价师 - 服务
 */

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.internet.CallBack;
import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.GJSAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.GJSModel;
import com.bhxx.lovecar.beans.GJSPublishServiceModel;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

public class gjsWorkServiceFragment extends BaseFragment {
    @InjectAll
    private Views v;

    private class Views {
        //        @InjectBinder(listeners = {OnClick.class}, method = "click")
        PullableListView my_list;
        PullToRefreshLayout my_pull;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gjswork_service, null);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    protected void init() {
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
    }

    private void initGjslist() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(App.app.getData("assessId"))) {
            params.put("assessId", App.app.getData("assessId"));
        }
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.GJS_SERVER, 0, params, new MyStringCallback());
    }


    private class MyStringCallback extends CommonCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("gjsWork_response=" + response);
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<GJSPublishServiceModel> listBean = JsonUtils.getBeans(response, CommonListBean.class, GJSPublishServiceModel.class);
                if (listBean.getResultCode().equals("0000")) {
                    GJSAdapter gjsAdapter = new GJSAdapter(listBean.getRows(), getActivity(), R.layout.gjs_item, "serverdetail");
                    v.my_list.setAdapter(gjsAdapter);
                }
            }
        }
    }

}
