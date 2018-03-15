package com.bhxx.lovecar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.ZiXunDetailActivity;
import com.bhxx.lovecar.adapter.ZiXunAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.beans.bannerModel;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;


public class ZiXunItemFragment extends LazyLoadFragment {

    private ViewPager my_zixun_viewpager;
    private LinearLayout my_zixun_dot;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;
    private AdvAdapter adapter;
    private int type;
    private int type2;
    private PullToRefreshLayout my_zixun_pull;
    private PullableListView my_zixun_list;
    private boolean isPrepared = false;
    private boolean hasLoadOnce = false;//拒绝重复加载
    private ZiXunAdapter ziXunAdapter;
    private int page = 1;
    private List<String> scrollImages;
    private List<String> scrollId;
    private List<ZiXunModel> actModelList;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Handler_Inject.injectFragment(this, view);
//        LogUtils.i("onViewCreated()");
        if (getContentLayoutId() > 0) {
            this.my_zixun_pull = (PullToRefreshLayout) view.findViewById(R.id.my_zixun_pull);
            this.my_zixun_list = (PullableListView) view.findViewById(R.id.my_zixun_list);

            if (getArguments() != null) {
                this.type = getArguments().getInt("type");
                this.type2 = getArguments().getInt("type2");
                LogUtils.i("type2=" + type);
            }
            scrollImages = new ArrayList<String>();
            scrollId = new ArrayList<String>();
            actModelList = new ArrayList<ZiXunModel>();
            inithead();
            initViewPager();
            initdata();
            getViewPagerList();
        }
        isPrepared = true;
        hasLoadOnce = false;
        lazyLoad();

    }

    /**
     * 初始化头部
     */
    private void inithead() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.head_viewpager_item, null);
        my_zixun_viewpager = (ViewPager) view.findViewById(R.id.my_zixun_viewpager);
        my_zixun_dot = (LinearLayout) view.findViewById(R.id.my_zixun_dot);
        my_zixun_list.addHeaderView(view);
    }

    private void initdata() {
        my_zixun_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                ziXunAdapter = null;
                page = 1;
                initActList();
                my_zixun_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                my_zixun_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initActList();
                my_zixun_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                my_zixun_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }
        });
    }

    /**
     * 获取viewpager数据信息
     */
    private void getViewPagerList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("status", "2");
        params.put("typeId", type + "");
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.ZIXUN_BANNER, 0, "zixun_banner", params, new MyStringCallback());
    }

    private void initActList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        LogUtils.i("type=" + type);
        params.put("typeId", type + "");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.ZIXUN_TYPE_ITEM, 1, "item", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 0:
                    //轮播图
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<ZiXunModel> listBean = JsonUtils.getBean(response, CommonListBean.class, ZiXunModel.class);
                        for (int i = 0; i < listBean.getRows().size(); i++) {
                            scrollId.add(listBean.getRows().get(i).getMessageId() + "");
                            scrollImages.add(listBean.getRows().get(i).getPicture());

                            actModelList.add(listBean.getRows().get(i));
                        }
                        initViewPager();
                    }
                    break;
                case 1:
                    //资讯列表
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<ZiXunModel> listBean = JsonUtils.getBean(response, CommonListBean.class, ZiXunModel.class);
                        if (listBean.getResultCode().equals("0000")) {
                            showContent();
                            ziXunAdapter = new ZiXunAdapter(listBean.getRows(), getActivity(), R.layout.zixun_item);
                            my_zixun_list.setAdapter(ziXunAdapter);
                        }
                    }
                    break;
            }

        }
    }

    /**
     * 我的活动实例化
     *
     * @param type
     * @return
     */
    public static ZiXunItemFragment getInstance(int type, int type2) {
        ZiXunItemFragment fragment = new ZiXunItemFragment();
        Bundle bd = new Bundle();
        LogUtils.i("type1=" + type);
        bd.putInt("type", type);
        bd.putInt("type2", type2);
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || hasLoadOnce || !isVisible) {
            return;
        }
        hasLoadOnce = true;
        LogUtils.i("type000=" + type);
//        LogUtils.i("type2=" + type2);
        App.app.setData("CurrentCheckTitle", type2 + "");
        showLoading();
        initActList();
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
        return R.layout.fragment_zixun_item_layout;
    }

    @Override
    protected int getNothingLayoutId() {
        return 0;
    }


    // 滚图
    private void initViewPager() {
        adapter = new AdvAdapter(scrollId, scrollImages);
        my_zixun_viewpager.setAdapter(adapter);
        my_zixun_viewpager.addOnPageChangeListener(new GuidePageChangeListener());
        initDot(scrollImages.size());
//        List<Integer> scrollImages = new ArrayList<Integer>();
//        scrollImages.add(R.mipmap.pic_04);
//        scrollImages.add(R.mipmap.pic_05);
//        scrollImages.add(R.mipmap.pic_09);
//        adapter = new AdvAdapter(scrollImages);
//        my_zixun_viewpager.setAdapter(adapter);
//        my_zixun_viewpager.addOnPageChangeListener(new GuidePageChangeListener());
//        initDot(scrollImages.size());

        my_zixun_viewpager.setOnTouchListener(new View.OnTouchListener() {
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
            my_zixun_viewpager.setCurrentItem(msg.what);
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


    private final class AdvAdapter extends PagerAdapter {

        private List<String> imageUrls;
        private List<String> imageId;

        public AdvAdapter(List<String> imageId, List<String> imageUrls) {
            super();
            this.imageId = imageId;
            this.imageUrls = imageUrls;
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

        my_zixun_dot.removeAllViews();
        int margin = (int) (getResources().getDisplayMetrics().density * 5);
        for (int i = 0; i < num; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.mipmap.default_bg_02);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = margin;
            params.rightMargin = margin;
            params.weight = 1;
            my_zixun_dot.addView(imageView, params);
        }
        setCurrentDot(0);
    }

    private void setCurrentDot(int position) {

        AdvAdapter adapter = (AdvAdapter) my_zixun_viewpager.getAdapter();
        int urlNum = adapter.getImageUrls() == null ? 0 : adapter.getImageUrls().size();
        if (urlNum == 0) {
            urlNum = position;
        }

        for (int i = 0; i < my_zixun_dot.getChildCount(); i++) {

            ImageView view = (ImageView) my_zixun_dot.getChildAt(i);
            if (i == position % urlNum) {
                view.setImageResource(R.mipmap.w_l);
            } else {
                view.setImageResource(R.mipmap.w_h);
            }
        }
    }
}
