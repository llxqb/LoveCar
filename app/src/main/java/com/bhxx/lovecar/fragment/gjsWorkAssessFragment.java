package com.bhxx.lovecar.fragment;
/**
 * 估价师 - 评价
 */

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.gjsDetailCommentAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.DynamicCommentModel;
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

public class gjsWorkAssessFragment extends BaseFragment {
    @InjectAll
    private Views v;

    private class Views {
        PullableListView my_list;
        PullToRefreshLayout my_pull;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gjswork_assess, null);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    protected void init() {
        initAssessList();
    }

    @Override
    protected void click(View view) {
    }

    private void initAssessList() {
//        List<DynamicCommentModel> list = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            DynamicCommentModel commentModel = new DynamicCommentModel();
//            commentModel.setAvatar("http://www.chinagirlol.cc/data/attachment/forum/201412/03/233758hw7o7h08kkozkcwi.jpg");
//            commentModel.setFullName("希达");
//            commentModel.setCmContent("专业的评估师，专业的评估师，全世界就你最叼，专业的评估师，专业的评估师，全世界就你最叼");
//            commentModel.setCreateTime("2017-01-01 12:00:00");//yyyy-MM-dd HH:mm:ss
//            commentModel.setStarNum(5);
//            list.add(commentModel);
//        }
//        gjsDetailCommentAdapter gjsDetailCommentAdapter = new gjsDetailCommentAdapter(list,getActivity(),R.layout.assessdetail_comment_item);
//        v.my_list.setAdapter(gjsDetailCommentAdapter);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(App.app.getData("assessId"))) {
            params.put("assessId", App.app.getData("assessId"));
        }
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.GJS_WORKASSESS, "workAssess", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("workAssess_response=" + response);
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<DynamicCommentModel> listBean = JsonUtils.getBeans(response, CommonListBean.class, DynamicCommentModel.class);
                if (listBean.getResultCode().equals("0000")) {
                    if (listBean.getRows()!= null && listBean.getRows().size() > 0) {
                        gjsDetailCommentAdapter gjsDetailCommentAdapter = new gjsDetailCommentAdapter(listBean.getRows(), getActivity(), R.layout.assessdetail_comment_item);
                        v.my_list.setAdapter(gjsDetailCommentAdapter);
                    }

                }
            }
        }
    }
}
