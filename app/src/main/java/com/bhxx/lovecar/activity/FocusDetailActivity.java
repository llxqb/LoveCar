package com.bhxx.lovecar.activity;

import android.content.Context;
import android.os.Bundle;
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

import com.android.pc.ioc.event.EventBus;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.DynamicAdapter;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.DynamicModel;
import com.bhxx.lovecar.entity.Circleentity;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.android.util.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-关注-关注详情
 *
 * @qq289513149.
 */

public class FocusDetailActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = FocusDetailActivity.class.getSimpleName();
    public static final String TITLE = "title";
    public static final String OBJECTID = "objectId";
    private ListView myListView;
    private TextView titleTextView;
    private FocusDetailAdapter focusDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_detail);
        initView();
//        titleTextView.setText(getIntent().getStringExtra(TITLE));
//        List<Map> mapList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Map map = new HashMap();
//            mapList.add(map);
//        }
//        focusDetailAdapter = new FocusDetailAdapter(mapList, this);
//        myListView.setAdapter(focusDetailAdapter);

        initDynamicList(getIntent().getStringExtra(OBJECTID));
    }
    private void initDynamicList(String objectId) {
        showProgressDialog(FocusDetailActivity.this);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, objectId);
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.CIRCLE_MYDYNAMIC, 1, "detail", params, new MyStringCallback());

    }

    //初始化界面控件
    private void initView() {
        titleTextView = (TextView) ViewUtil.findViewById(this, R.id.tvTitle);
        titleTextView.setText("动态详情");
        myListView = (ListView) ViewUtil.findViewById(this, R.id.listView);
    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }

    class FocusDetailAdapter extends BaseAdapter {

        private List<Map> mapList;
        private Context context;
        private LayoutInflater layoutInflater;

        public FocusDetailAdapter(List<Map> mapList, Context context) {
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
            Map<String, Object> map = mapList.get(i);
            View root = layoutInflater.inflate(R.layout.layout_focus_detail_item, null);
            TextView content = (TextView) root.findViewById(R.id.tvContent);
            TextView tvRightFocus = (TextView) root.findViewById(R.id.tvRightFocus);
            tvRightFocus.setVisibility(View.INVISIBLE);
            if (map != null) {
                SpannableString spanString = new SpannableString("置顶 雪佛兰 乐风 1.6 手动 天窗 雪佛兰科帕奇 2011款 2.4 自动 7座豪华型(进口)");
                BackgroundColorSpan spanBackground = new BackgroundColorSpan(getColor(R.color.cancel_normal));
                spanString.setSpan(spanBackground, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan spanForeground = new ForegroundColorSpan(getColor(R.color.white));
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
            dismissProgressDialog();
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response=" + response);
            dismissProgressDialog();
            switch (id) {
                case 1:
                    //动态列表信息
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<DynamicModel> listBean = JsonUtils.getBean(response, CommonListBean.class, DynamicModel.class);
                        showToast(listBean.getResultDesc());
                        if (listBean.getResultCode().equals("0000")) {
                            DynamicAdapter dynamicAdapter = new DynamicAdapter(listBean.getRows(), FocusDetailActivity.this);

                            myListView.setAdapter(dynamicAdapter);
                        } else {
                            showToast(Constant.ERROR_WEB);
                        }
                    }
                    break;

            }
        }
    }
}
