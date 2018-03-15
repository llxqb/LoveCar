package com.bhxx.lovecar.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.DynamicAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.DynamicModel;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.android.util.ToastUtil;
import com.makeapp.javase.util.MapUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/6.
 * 车友圈-帖子-我的帖子
 *
 * @qq289513149.
 */

public class MineTZFragment extends BaseFragment {

    private static final String TAG = MineTZFragment.class.getSimpleName();
    private View rootView;
    private ListView mListView;
    private MineTZDetailAdapter detailAdapter;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_carcircyle_tz, null);
        Handler_Inject.injectFragment(this, rootView);
        mActivity = getActivity();
        initView();
        initEvent();
        List<Map> mapList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Map map = new HashMap();
//            map.put("name","我的帖子-奥迪"+i);
//            mapList.add(map);
//        }
//        detailAdapter = new MineTZDetailAdapter(mapList, mActivity);
//        mListView.setAdapter(detailAdapter);
        initDynamicList();
        return rootView;
    }

    private void initDynamicList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.CIRCLE_MYDYNAMIC, 1, "detail", params, new MyStringCallback());

    }

    //初始化界面控件
    private void initView() {
        mListView = (ListView) rootView.findViewById(R.id.listView);
    }

    //初始化控件事件
    private void initEvent() {

    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }

    class MineTZDetailAdapter extends BaseAdapter {

        private List<Map> mapList;
        private Context context;
        private LayoutInflater layoutInflater;

        public MineTZDetailAdapter(List<Map> mapList, Context context) {
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
            final Map<String, Object> map = mapList.get(i);
            View root = layoutInflater.inflate(R.layout.layout_focus_detail_item, null);
            TextView content = (TextView) root.findViewById(R.id.tvContent);
            TextView tvRightFocus = (TextView) root.findViewById(R.id.tvRightFocus);
            TextView username = (TextView) root.findViewById(R.id.tvUserName);
            username.setText(MapUtil.getString(map, "name", ""));
            tvRightFocus.setVisibility(View.VISIBLE);
            tvRightFocus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUtil.show(context, "关注" + MapUtil.getString(map, "name", ""));
                }
            });
            if (map != null) {
                SpannableString spanString = new SpannableString("置顶 雪佛兰 乐风 1.6 手动 天窗 雪佛兰科帕奇 2011款 2.4 自动 7座豪华型(进口)");
                BackgroundColorSpan spanBackground = new BackgroundColorSpan(getResources().getColor(R.color.cancel_normal));
                spanString.setSpan(spanBackground, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan spanForeground = new ForegroundColorSpan(getResources().getColor(R.color.white));
                spanString.setSpan(spanForeground, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                AbsoluteSizeSpan spanSize = new AbsoluteSizeSpan(24);
                spanString.setSpan(spanSize, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setText(spanString);
            }
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
            LogUtils.e("response=" + response);
            switch (id) {
                case 1:
                    //动态列表信息
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<DynamicModel> listBean = JsonUtils.getBean(response, CommonListBean.class, DynamicModel.class);
                        if (listBean.getResultCode().equals("0000")) {
                            DynamicAdapter dynamicAdapter = new DynamicAdapter(listBean.getRows(), mActivity);
                            mListView.setAdapter(dynamicAdapter);
                        } else {
//                            showToast(Constant.ERROR_WEB);
                        }
                    }
                    break;

            }
        }
    }
}
