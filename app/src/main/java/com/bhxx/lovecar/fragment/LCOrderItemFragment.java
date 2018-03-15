package com.bhxx.lovecar.fragment;

import android.os.Bundle;
import android.view.View;

import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.LoveCarAdapter;
import com.bhxx.lovecar.adapter.LoveCarOrderAdapter;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;
import com.makeapp.javase.util.DateUtil;

import java.util.ArrayList;
import java.util.List;


public class LCOrderItemFragment extends LazyLoadFragment {

    private PullToRefreshLayout lcPullToRefreshLayout;
    private PullableListView lcPullableListView;
    private boolean isPrepared = false;
    private boolean hasLoadOnce = false;
    private LoveCarOrderAdapter loveCarOrderAdapter;
    private int page = 1;

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
                loveCarOrderAdapter = null;
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
        List<CarModel> carList = new ArrayList<CarModel>();
        for (int i = 0; i < 10; i++) {
            CarModel bean = new CarModel();
            if (i == 0 || i == 2) {
                bean.setCarName("奔驰C级 2015款 1.6T 自动 C180L");
                bean.setCarLicenseTime(DateUtil.getStringDate());
                bean.setKmNumber(107.50);
                bean.setCarLicenseAddress("北京");

            } else if (i == 1 || i == 3 || i == 5) {
                bean.setCarName("奔驰D级 2017款 1.7T 自动 C190L");
                bean.setCarLicenseTime(DateUtil.getStringDate());
                bean.setKmNumber(245.70);
                bean.setCarLicenseAddress("上海");
            } else {
                bean.setCarName("奔驰G级 2018款 1.8T 自动 C200L");
                bean.setCarLicenseTime(DateUtil.getStringDate());
                bean.setKmNumber(355.10);
                bean.setCarLicenseAddress("深圳");
            }
            bean.setAssessTime(DateUtil.getStringTime());
            bean.setCarImg("http://www.sfs-cn.com/node3/ypjs/node25212/node25217/images/00213825.jpg");
            carList.add(bean);
        }
        showContent();
        loveCarOrderAdapter = new LoveCarOrderAdapter(carList, getActivity(), R.layout.layout_lcorder_item);
        lcPullableListView.setAdapter(loveCarOrderAdapter);

    }

    /**
     * 实例化
     *
     * @param type
     * @return
     */
    public static LCOrderItemFragment getInstance(int type) {
        LogUtils.e("type--->" + type);
        LCOrderItemFragment fragment = new LCOrderItemFragment();
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

}
