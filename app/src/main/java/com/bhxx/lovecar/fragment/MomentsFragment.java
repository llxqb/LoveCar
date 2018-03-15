package com.bhxx.lovecar.fragment;
/**
 * 进圈--fragment
 */

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.MomentsDetailActivity;
import com.bhxx.lovecar.activity.SearchMomentsActivity;
import com.bhxx.lovecar.adapter.MyMementsAdapter;
import com.bhxx.lovecar.adapter.MyMementsTuijianAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CircleModel;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.entity.Circleentity;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.MyListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

public class MomentsFragment extends BaseFragment {
    @InjectAll
    private Views v;
    private List<CircleModel> myMomentslist = new ArrayList();
    private List<CircleModel> myMomentstuijianlist;
    private MyMementsTuijianAdapter myMementsTuijianAdapter;
    private MyMementsAdapter myMementsAdapter;
    private int page = 1;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView my_moments_changelist, search_text;
        MyListView my_moments_listview, my_moments_tuijianlistview;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        LinearLayout search_circle_layout;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_moments, null);
        Handler_Inject.injectFragment(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    protected void init() {
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            //我加入的圈子
            initMyMomentslist();
        }
        //推荐的圈子
        initTuijianlist();
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.my_moments_changelist://换一批
                showToast("换一批");
//                page++;
//                initTuijianlist();
                break;
            case R.id.search_text://搜索
                IntentUtil.setIntent(getActivity(), SearchMomentsActivity.class);
                break;
            case R.id.search_circle_layout:
                IntentUtil.setIntent(getActivity(), SearchMomentsActivity.class);
                break;
        }
    }

    private void onEventMainThread(Circleentity entity) {
        switch (entity.getKey()) {
            case 1:
                //我加入的圈子
                initMyMomentslist();
                //推荐的圈子
                initTuijianlist();
                break;
        }
    }


    private void initMyMomentslist() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.CIRCLE_ADD_LIST, 2, "Mycilcle", params, new MyStringCallback());

    }

    private void initTuijianlist() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            params.put("userId", App.app.getData(UserPreferences.USER_ID));
        }
        params.put("isRecommend", "0");
        params.put("pageNo", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.CIRCLE_TUIJIAN, 1, "tuijian", params, new MyStringCallback());

    }


    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response=" + response);
            switch (id) {
                case 1:
                    //圈子推荐
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<CircleModel> listBean = JsonUtils.getBean(response, CommonListBean.class, CircleModel.class);
                        if (listBean.getResultCode().equals("0000")) {
                            myMementsTuijianAdapter = new MyMementsTuijianAdapter(listBean.getRows(), getActivity(), R.layout.mymoments_tuijian_item);
                            v.my_moments_tuijianlistview.setAdapter(myMementsTuijianAdapter);
                        } else {
                            showToast(Constant.ERROR_WEB);
                        }
                    }
                    break;
                case 2:
                    //我加入的圈子
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<CircleModel> MycirclelistBean = JsonUtils.getBean(response, CommonListBean.class, CircleModel.class);
                        if (MycirclelistBean.getResultCode().equals("0000")) {
                            myMementsAdapter = new MyMementsAdapter(MycirclelistBean.getRows(), getActivity(), R.layout.mymoments_item);
                            v.my_moments_listview.setAdapter(myMementsAdapter);
                        } else {
                            showToast(Constant.ERROR_WEB);
                        }
                    }
                    break;
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
