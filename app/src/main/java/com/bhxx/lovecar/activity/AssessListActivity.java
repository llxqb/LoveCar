package com.bhxx.lovecar.activity;
/**
 * 爱车估价信息列表
 */
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.AssessListAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.entity.CarEntity;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_assess_list)
public class AssessListActivity extends BasicActivity {

    private List<CarModel> list = new ArrayList<>();
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView assess_back, assess_add;
        PullToRefreshLayout my_pull;
        PullableListView my_list;
        LinearLayout no_add_car_layout;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView addcar_tv;
    }


    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        EventBus.getDefault().register(this);
        initAssessList();
        v.my_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                initAssessList();
                v.my_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.my_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                initAssessList();
                v.my_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.my_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }
        });
    }
    protected  void onEventMainThread(CarEntity carEntity){
        if(carEntity.getKey().equals("writeCarinfo")){
            initAssessList();
        }
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.assess_back:
                finish();
                break;
            case R.id.assess_add:
                IntentUtil.setIntent(AssessListActivity.this, WriteCarinfoActivity.class);
                break;
            case R.id.addcar_tv:
                IntentUtil.setIntent(this, WriteCarinfoActivity.class);
                break;
        }
    }

    private void initAssessList() {
        showProgressDialog(AssessListActivity.this);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.ASSESS_LIST_CAR, "list", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
            dismissProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            LogUtils.i("response=" + response);
            CommonListBean<CarModel> listBean = JsonUtils.getBean(response, CommonListBean.class, CarModel.class);
//            showToast(listBean.getResultDesc());
            if (listBean.getResultCode().equals("0000")) {
                if (listBean.getRows().size() > 0) {
                    AssessListAdapter assessListAdapter = new AssessListAdapter(listBean.getRows(), AssessListActivity.this, R.layout.assess_car_item);
                    v.my_list.setAdapter(assessListAdapter);
                }
            } else {
                showToast(Constant.ERROR_WEB);
            }

        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
