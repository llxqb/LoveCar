package com.bhxx.lovecar.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.javase.util.MapUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/6.
 * 我的-估价师个人中心-当前订单
 *
 * @qq289513149.
 */

public class CurrentOrderListFragment extends BaseFragment {

    private static final String TAG = CurrentOrderListFragment.class.getSimpleName();
    private View rootView;
    private ListView mListView;
    private ListAdapter listAdapter;
    private Activity mActivity;
    private List<Map> mapList;//订单数据
    private LayoutInflater layoutInflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_gjs_info_common_list, null);
        mActivity = getActivity();
        layoutInflater = LayoutInflater.from(mActivity);
        initView();
        initEvent();
        startCurrentOrdersListTask();
        return rootView;
    }

    //初始化界面控件
    private void initView() {
        mListView = (ListView) rootView.findViewById(R.id.listView);
    }

    //初始化控件事件
    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }

    class ListAdapter extends BaseAdapter {

        private List<Map> mapList;
        private Context context;
        private LayoutInflater layoutInflater;

        public ListAdapter(List<Map> mapList, Context context) {
            this.mapList = mapList;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int i) {
            return mapList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
//            final Map<String, Object> map = mapList.get(i);
            View root = layoutInflater.inflate(R.layout.layout_account_list_item, null);
            TextView content = (TextView) root.findViewById(R.id.tvContent);
            TextView price = (TextView) root.findViewById(R.id.tvPrice);
            TextView time = (TextView) root.findViewById(R.id.tvTime);

            return root;
        }
    }
    //获取当前订单的异步请求
    private void startCurrentOrdersListTask() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        Log.e(TAG, "startCurrentOrdersListTask assessId--->" + App.app.getData(UserPreferences.USER_ASSESSID));

        params.put(Constant.CAR_USER_ASSESSID, App.app.getData(UserPreferences.USER_ASSESSID));
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));
        Log.e(TAG, "startCurrentOrdersListTask params--->" + params);
        MyOkHttp.postMap(GlobalValues.ORDER_PRESENTLIST, "list", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "startCurrentOrdersListTask e--->" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startCurrentOrdersListTask response--->" + response);
                mapList = new ArrayList<Map>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            JSONObject rowJSONObject = jsonObject.optJSONObject(Constant.CAR_ROWS);
//                            orderAdapter = new OrderAdapter(0);
//                            mListView.setAdapter(orderAdapter);
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

    class OrderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int i) {
            return mapList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Map map = mapList.get(i);
            View root = layoutInflater.inflate(R.layout.layout_gjs_orders_item, null);
            TextView name = (TextView) root.findViewById(R.id.tvName);
            name.setText(MapUtil.getString(map, "name"));
            TextView time = (TextView) root.findViewById(R.id.tvTime);
            time.setText(MapUtil.getString(map, "time"));
            TextView price = (TextView) root.findViewById(R.id.tvPrice);
            price.setText("￥" + MapUtil.getString(map, "price"));
            TextView receive = (TextView) root.findViewById(R.id.tvReceive);
            RatingBar ratingBar = (RatingBar) root.findViewById(R.id.ratingBar);
//            if (this.orderType == 0) {
//                receive.setVisibility(View.VISIBLE);
//                ratingBar.setVisibility(View.GONE);
//            } else {
//                receive.setVisibility(View.GONE);
//                ratingBar.setVisibility(View.VISIBLE);
//            }
            return root;
        }
    }

}
