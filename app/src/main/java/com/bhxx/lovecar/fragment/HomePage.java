package com.bhxx.lovecar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.AssessListActivity;
import com.bhxx.lovecar.activity.CityListActivity;
import com.bhxx.lovecar.activity.GJSActivity;
import com.bhxx.lovecar.activity.SearchActivity;
import com.bhxx.lovecar.activity.ZiXunActivity;
import com.bhxx.lovecar.activity.ZiXunDetailActivity;
import com.bhxx.lovecar.adapter.HomeActivityCarAdapter;
import com.bhxx.lovecar.adapter.HomeActivitygjsAdapter;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.GJSModel;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.beans.bannerModel;
import com.bhxx.lovecar.entity.Cityentity;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;

public class HomePage extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ViewPager parent_home_viewpager;
    private LinearLayout parent_home_dot;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;
    private AdvAdapter adapter;
    private ImageView home_search;
    private RelativeLayout home_cityname_rel, parent_model_zx, parent_model_gjs, parent_model_gj;
    private TextView home_citynametv;
    private MyGridView home_gjs_gridview;
    ListView home_list;
    SwipeRefreshLayout swipe_ly;
    private List<String> scrollImages;
    private List<String> scrollId;
    private List<ZiXunModel> actModelList;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_cityname_rel:
                IntentUtil.setIntent(getActivity(), CityListActivity.class);
                break;
            case R.id.home_search:
                IntentUtil.setIntent(getActivity(), SearchActivity.class);
                break;
            case R.id.parent_model_zx:
                IntentUtil.setIntent(getActivity(), ZiXunActivity.class);
                break;
            case R.id.parent_model_gjs:
                IntentUtil.setIntent(getActivity(), GJSActivity.class);
                break;
            case R.id.parent_model_gj:
                IntentUtil.setIntent(getActivity(), AssessListActivity.class);
                break;
        }
    }

    @Override
    public void onRefresh() {
        getHomeActivityCarList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_page, container, false);
        home_list = (ListView) rootView.findViewById(R.id.home_list);
        swipe_ly = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_ly);
        swipe_ly.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipe_ly.setOnRefreshListener(this);
        scrollImages = new ArrayList<String>();
        scrollId = new ArrayList<String>();
        actModelList = new ArrayList<ZiXunModel>();
        initHead();
        initViewPager();
        Handler_Inject.injectFragment(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    /**
     * 头部
     */
    private void initHead() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_page_head, null);
        parent_home_viewpager = (ViewPager) view.findViewById(R.id.parent_home_viewpager);
        parent_home_dot = (LinearLayout) view.findViewById(R.id.parent_home_dot);
        home_cityname_rel = (RelativeLayout) view.findViewById(R.id.home_cityname_rel);
        home_citynametv = (TextView) view.findViewById(R.id.home_citynametv);
        home_cityname_rel.setOnClickListener(this);
        home_search = (ImageView) view.findViewById(R.id.home_search);
        home_search.setOnClickListener(this);
        parent_model_zx = (RelativeLayout) view.findViewById(R.id.parent_model_zx);
        parent_model_zx.setOnClickListener(this);
        parent_model_gjs = (RelativeLayout) view.findViewById(R.id.parent_model_gjs);
        parent_model_gjs.setOnClickListener(this);
        parent_model_gj = (RelativeLayout) view.findViewById(R.id.parent_model_gj);
        parent_model_gj.setOnClickListener(this);

        home_gjs_gridview = (MyGridView) view.findViewById(R.id.home_gjs_gridview);
        home_list.addHeaderView(view);
    }

    @Override
    protected void init() {
        showProgressDialog(getActivity());
        getViewPagerList();//获取viewpager数据信息
        getHomeActivityCarList();//获取车辆列表信息
        getHomeActivityGJSList();//获取估价师列表信息
    }

    @Override
    protected void click(View view) {
    }

    /**
     * 获取viewpager数据信息
     */
    private void getViewPagerList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.HOME_BANNER, 0, "home_banner", params, new MyStringCallback());
    }

    /**
     * 获取首页好车推荐列表
     */
    private void getHomeActivityCarList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("isDefault", "0");
        params.put("isPbulish", "0");
        params.put("pageNo", "1");
        params.put("pageSize", GlobalValues.PAGE_SIZE);

        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.HOME_CAR_TUIJIAN, 1, "carcomment", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
            dismissProgressDialog();
            swipe_ly.setRefreshing(false);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            switch (id) {
                case 0:
                    //轮播接口
                    swipe_ly.setRefreshing(false);
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<bannerModel> listBean = JsonUtils.getBean(response, CommonListBean.class, bannerModel.class);
                        for (int i = 0; i < listBean.getRows().size(); i++) {

                            scrollId.add(listBean.getRows().get(i).getObjectid() + "");
                            scrollImages.add(listBean.getRows().get(i).getPicture());

                            ZiXunModel ziXunModel = new ZiXunModel();
                            ziXunModel.setMessageId(listBean.getRows().get(i).getObjectid());
                            ziXunModel.setTitle(listBean.getRows().get(i).getTitle());
                            actModelList.add(ziXunModel);
                        }
                        initViewPager();
                    }
                    break;
                case 1:
                    //好车推荐
                    swipe_ly.setRefreshing(false);
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<CarModel> carListBean = JsonUtils.getBean(response, CommonListBean.class, CarModel.class);
                        if (carListBean.getResultCode().equals("0000")) {
                            HomeActivityCarAdapter adapter = new HomeActivityCarAdapter(carListBean.getRows(), getActivity(), R.layout.home_car_item);
                            home_list.setAdapter(adapter);
                        } else {
                            showToast(Constant.ERROR_WEB);
                        }
                    }
                    break;
                case 2:
                    //首页估价师
                    LogUtils.i("gjs_response=" + response);
                    swipe_ly.setRefreshing(false);
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<GJSModel> gjsListBean = JsonUtils.getBean(response, CommonListBean.class, GJSModel.class);
                        if (gjsListBean.getResultCode().equals("0000")) {
                            HomeActivitygjsAdapter adapter = new HomeActivitygjsAdapter(gjsListBean.getRows(), getActivity(), R.layout.gjs_hme_grid_item);
                            home_gjs_gridview.setAdapter(adapter);
                        } else {
                            showToast(Constant.ERROR_WEB);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 获取首页估价师列表
     */
    private void getHomeActivityGJSList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("isDefault", "0");
        params.put("sign", SingUtils.getMd5SignMsg(params));
//        LogUtils.i("params=" + JsonUtils.Object2Json(params));
        MyOkHttp.postMap(GlobalValues.HOME_GJSLIST, 2, "gjsList", params, new MyStringCallback());
    }


    private void onEventMainThread(Cityentity cityentity) {
        home_citynametv.setText(cityentity.getKey());
    }


    // 滚图
    private void initViewPager() {
        adapter = new AdvAdapter(scrollId, scrollImages);
        parent_home_viewpager.setAdapter(adapter);
        parent_home_viewpager.addOnPageChangeListener(new GuidePageChangeListener());
        initDot(scrollImages.size());

        parent_home_viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                     case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isContinue) {
                        viewHandler.sendEmptyMessage(what.get());
                        whatOption();
                    }
                }
            }
        }).start();
    }

    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > Integer.MAX_VALUE - 1) {
            what.set(0);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

        }
    }

    private final Handler viewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            parent_home_viewpager.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }
    };

    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            what.getAndSet(arg0);
            setCurrentDot(arg0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private final class AdvAdapter extends PagerAdapter {

        private List<String> imageUrls;
        private List<String> imageId;

        public AdvAdapter(List<String> scrollId, List<String> imageUrls) {
            super();
            this.imageUrls = imageUrls;
            this.imageId = scrollId;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            if (imageUrls != null && imageUrls.size() > 0) {
//                imageView.setImageResource(imageUrls.get(position % imageUrls.size()));
                ImageLoader.getInstance().displayImage(GlobalValues.IP1 + imageUrls.get(position % imageUrls.size()), imageView, LoadImage.getDefaultOptions());
            }


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ZiXunDetailActivity.class);
                    intent.putExtra("zixunModel", actModelList.get(position % imageUrls.size()));
                    startActivity(intent);
                }
            });
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public List<String> getImageUrls() {
            return imageUrls;
        }
    }

    private void initDot(int num) {

        parent_home_dot.removeAllViews();
        int margin = (int) (getResources().getDisplayMetrics().density * 5);
        for (int i = 0; i < num; i++) {

            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.mipmap.default_bg_02);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = margin;
            params.rightMargin = margin;
            params.weight = 1;
            parent_home_dot.addView(imageView, params);
        }
        setCurrentDot(0);
    }

    private void setCurrentDot(int position) {

        AdvAdapter adapter = (AdvAdapter) parent_home_viewpager.getAdapter();
        int urlNum = adapter.getImageUrls() == null ? 0 : adapter.getImageUrls().size();
        if (urlNum == 0) {
            urlNum = position;
        }

        for (int i = 0; i < parent_home_dot.getChildCount(); i++) {

            ImageView view = (ImageView) parent_home_dot.getChildAt(i);
            if (i == position % urlNum) {
                view.setImageResource(R.mipmap.w_l);
            } else {
                view.setImageResource(R.mipmap.w_h);
            }
        }
    }


}
