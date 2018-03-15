package com.bhxx.lovecar.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.MessageAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.DateUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/5.
 * 我的-消息-系统消息
 *
 * @qq289513149.
 */

public class MessageSystemFragment extends LazyLoadFragment {

    public static final String TAG = MessageSystemFragment.class.getSimpleName();
    private PullToRefreshLayout messagePullToRefreshLayout;
    private PullableListView messagePullableListView;
    private boolean isPrepared = false;
    private boolean hasLoadOnce = false;
    private MessageAdapter messageAdapter;
    private int page = 1;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContentLayoutId() > 0) {
            this.messagePullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.pullMessage);
            this.messagePullableListView = (PullableListView) view.findViewById(R.id.lvMessage);
            initdata();
        }
        isPrepared = true;
        lazyLoad();
    }

    private void initdata() {
        messagePullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                messageAdapter = null;
                page = 1;
                startGetInfoListTask();
                messagePullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                messagePullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                startGetInfoListTask();
                messagePullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                messagePullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }
        });
    }


    private void initData() {
        List<Map> mapList = new ArrayList<Map>();
//        for (int i = 0; i < 10; i++) {
//            Map map = new HashMap();
//            map.put("content", "系统爱车估价发布了新版本");
//            map.put("time", DateUtils.format(new Date(), "yyyy-MM-dd   HH:mm"));
//            map.put("detail", "查看详情" + i);
//            mapList.add(map);
//        }
        showContent();
        messageAdapter = new MessageAdapter(mapList, getActivity());
        messagePullableListView.setAdapter(messageAdapter);
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || hasLoadOnce || !isVisible) {
            return;
        }
        hasLoadOnce = true;
        showLoading();
        initData();
        startGetInfoListTask();
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
        return R.layout.layout_message_system;
    }

    @Override
    protected int getNothingLayoutId() {
        return 0;
    }

    //获取系统消息异步请求
    public void startGetInfoListTask() {
        showProgressBar(getResources().getString(R.string.loading));
        String userId = App.app.getData(UserPreferences.USER_ID);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, userId);

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, userId);
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startGetInfoListTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.INFO_LIST, "getinfo", hashMap, new CommonCallback() {
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

                            JSONArray jsonArray = jsonObject.getJSONArray("rows");
                            if (jsonArray != null && jsonArray.length() > 0) {
                                List<Map> mapList = new ArrayList<Map>();
                                int length = jsonArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject item = jsonArray.optJSONObject(i);
                                    Map map = new HashMap();
                                    map.put("content", item.optString("title"));
                                    map.put("time", item.optString("createDate"));
                                    map.put("detail", "");
                                    mapList.add(map);
                                }
                                if (mapList.size() > 0) {
                                    messageAdapter = new MessageAdapter(mapList, getActivity());
                                    messagePullableListView.setAdapter(messageAdapter);
                                }
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
