package com.bhxx.lovecar.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.CollectZXCarAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;
import com.makeapp.javase.lang.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;


public class CollectZXItemFragment extends LazyLoadFragment {

    private static final String TAG = CollectZXItemFragment.class.getSimpleName();
    private PullToRefreshLayout lcPullToRefreshLayout;
    private PullableListView lcPullableListView;
    private boolean isPrepared = false;
    private boolean hasLoadOnce = false;
    private CollectZXCarAdapter collectZXCarAdapter;
    private int page = 1;
    private static final String SYMBOL = ";";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Handler_Inject.injectFragment(this, view);
        if (getContentLayoutId() > 0) {
            this.lcPullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.pullLoveCar);
            this.lcPullableListView = (PullableListView) view.findViewById(R.id.lvLoveCar);
            initdata();
        }
        isPrepared = true;
        lazyLoad();
    }


    private void initdata() {
        lcPullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                collectZXCarAdapter = null;
                page = 1;
                initData();
                lcPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                lcPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initData();
                lcPullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                lcPullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }
        });


    }

    private void initData() {
        showContent();
        startZXListTask();
    }

    /**
     * 我的活动实例化
     *
     * @param type
     * @return
     */
    public static CollectZXItemFragment getInstance(int type) {
        LogUtils.e("type--->" + type);
        CollectZXItemFragment fragment = new CollectZXItemFragment();
        Bundle bd = new Bundle();
        bd.putInt("type", type);
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || hasLoadOnce || !isVisible) {
            return;
        }
        hasLoadOnce = true;
        showLoading();
        initData();
    }

    @Override
    protected int getLoadingLayoutId() {
        return R.layout.loading_layout;
    }

    @Override
    protected int getErrorLayoutId() {
        return 0;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.layout_lc_fragment_item;
    }

    @Override
    protected int getNothingLayoutId() {
        return 0;
    }

    //我的-收藏-咨询列表异步请求
    private void startZXListTask() {
        showProgressBar(getResources().getString(R.string.operation_collecting));
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_USER_CREATETYPE, "1");//1：资讯;0：车源

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_USER_CREATETYPE, "1");//1：资讯;0：车源
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startZXListTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.COLLECT_LIST, "collect", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
                dismissProgressBar();
                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressBar();
                Log.e(TAG, "response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
                            JSONArray rowJSONArray = jsonObject.optJSONArray(Constant.CAR_ROWS);
                            int length = rowJSONArray.length();
                            if (null != rowJSONArray && length > 0) {
                                List<ZiXunModel> carList = new ArrayList<ZiXunModel>();
                                for (int i = 0; i < length; i++) {
                                    ZiXunModel bean = new ZiXunModel();
                                    JSONObject item = rowJSONArray.optJSONObject(i);

                                    String title = item.optString(Constant.CAR_ZX_TITLE);
                                    String time = item.optString(Constant.CAR_USER_CREATETIME);
                                    String type = item.optString(Constant.CAR_ZX_TYPENAME);

                                    bean.setMessageId(item.optInt("messageId"));
                                    bean.setTitle(title);
                                    bean.setTypeName(type);
                                    bean.setCreateTime(time);

                                    JSONObject pictures = item.optJSONObject(Constant.CAR_ACPG_PICTURES);
                                    if (pictures != null) {
                                        String urls = pictures.optString(Constant.CAR_ACPG_PICTURES_URL);
                                        if (StringUtil.isValid(urls) && !"null".equals(urls)) {
                                            if (urls.contains(SYMBOL)) {//多张
                                                bean.setPicture(GlobalValues.IP1 + urls.split(SYMBOL)[0]);
                                            } else {
                                                bean.setPicture(GlobalValues.IP1 + urls);
                                            }
                                        }
                                    }

                                    carList.add(bean);
                                }
                                collectZXCarAdapter = new CollectZXCarAdapter(carList, getActivity(), R.layout.layout_collectzxcar_item);
                                lcPullableListView.setAdapter(collectZXCarAdapter);
                            }
                        } else {
                            showToast(jsonObject.optString("resultDesc"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
