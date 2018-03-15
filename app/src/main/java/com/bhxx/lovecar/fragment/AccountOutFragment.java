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
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/6.
 * 我的-账户中心-支出记录
 *
 * @qq289513149.
 */

public class AccountOutFragment extends BaseFragment {

    private static final String TAG = AccountOutFragment.class.getSimpleName();
    private View rootView;
    private ListView mListView;
    private ListAdapter listAdapter;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_account_in, null);
        mActivity = getActivity();
        initView();
        initEvent();
//        startAccountOutTask();
        return rootView;
    }

    private void startAccountOutTask() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.CIRCLE_MYDYNAMIC, 1, "account", params, new MyStringCallback());

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

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
//            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            Log.e(TAG, "response---->" + response);

        }
    }
}
