package com.bhxx.lovecar.fragment;
/**
 * 估价师 - 记录
 */

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class gjsWorkRecordFragment extends BaseFragment {
    @InjectAll
    private Views v;

    private class Views {
        PullableListView my_list;
        PullToRefreshLayout my_pull;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gjswork_record, null);
        Handler_Inject.injectFragment(this, rootView);
        initData();
        return rootView;
    }

    private void initData() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(App.app.getData("assessId"))) {
            params.put("assessId", App.app.getData("assessId"));
        }
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.GJS_RECORD, "record", params, new MyStringCallback());
    }

    @Override
    protected void init() {
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

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("reocrd_response=" + response);
        }
    }

}
