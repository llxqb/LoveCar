package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.beans.ZiXunRightItemBean;
import com.bhxx.lovecar.db.SqlLiteHelper;
import com.bhxx.lovecar.db.ZiXunItemDao;
import com.bhxx.lovecar.entity.ZiXunRightItemEntity;
import com.bhxx.lovecar.fragment.ZiXunItemFragment;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;

@InjectLayer(R.layout.activity_zi_xun)
public class ZiXunActivity extends BasicActivity {

    //    private final String[] titles = {"推荐", "新闻", "行情", "测评", "技术", "用车", "张三", "李四"};
    private ArrayList<ZiXunModel> titles;
    private List<ZiXunRightItemBean> listMime;
    private List<ZiXunRightItemBean> listOther;
    @InjectAll
    private Views v;


    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView back, moreItem;
        TabLayout my_zixun_tablayout;
        ViewPager my_zixun_viewpager;
    }


    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        EventBus.getDefault().register(this);
        initdb();
    }

    private void initdb() {
        titles = new ArrayList();
        listMime = new ArrayList<>();
        listOther = new ArrayList<>();
//        SqlLiteHelper.getInstance().getWritableDatabase();//创建表
        listMime = ZiXunItemDao.queryAllType("mime");
        if (listMime.size() == 0) {
            initZiXunType();
        } else {
            for (int i = 0; i < listMime.size(); i++) {
                ZiXunModel ziXunModel = new ZiXunModel();
                ziXunModel.setTypeId(listMime.get(i).getContentId());
                ziXunModel.setTypeName(listMime.get(i).getContent());
                titles.add(ziXunModel);
            }
            v.my_zixun_viewpager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
            v.my_zixun_tablayout.setupWithViewPager(v.my_zixun_viewpager);

            //表示缓存页面
//            v.my_zixun_viewpager.setOffscreenPageLimit(titles.size() - 1);
        }

    }

    private void initZiXunType() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.ZIXUN_TYPE, "zixunType", params, new MyStringCallback());
    }

    protected void onEventMainThread(ZiXunRightItemEntity entity) {
        LogUtils.i("entity.getKey()=" + entity.getKey());
        if (!TextUtils.isEmpty(entity.getKey())) {
//            initdb();
            listMime = new ArrayList();
            titles = new ArrayList();

            listMime = ZiXunItemDao.queryAllType("mime");
            for (int i = 0; i < listMime.size(); i++) {
                ZiXunModel ziXunModel = new ZiXunModel();
                ziXunModel.setTypeId(listMime.get(i).getContentId());
                ziXunModel.setTypeName(listMime.get(i).getContent());
                LogUtils.i("titles=" + listMime.get(i).getContent());
                titles.add(ziXunModel);
            }
            v.my_zixun_viewpager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
            v.my_zixun_tablayout.setupWithViewPager(v.my_zixun_viewpager);
            //表示缓存页面
//            v.my_zixun_viewpager.setOffscreenPageLimit(titles.size() - 1);
            v.my_zixun_viewpager.setCurrentItem(Integer.parseInt(entity.getKey()));
        }
    }


    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.moreItem:
                Intent intent = new Intent(ZiXunActivity.this, ZiXunRightItemActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("zixun_response=" + response);
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<ZiXunModel> listBean = JsonUtils.getBean(response, CommonListBean.class, ZiXunModel.class);
                if (listBean.getResultCode().equals("0000")) {
                    titles = (ArrayList<ZiXunModel>) listBean.getRows();

                    SqlLiteHelper.getInstance().getWritableDatabase();//创建表
                    listMime = ZiXunItemDao.queryAllType("mime");

                    if (listMime.size() == 0) {
                        for (int i = 0; i < titles.size(); i++) {
                            ZiXunRightItemBean bean = new ZiXunRightItemBean();
                            bean.setType("mime");
                            bean.setContentId(titles.get(i).getTypeId());
                            bean.setContent(titles.get(i).getTypeName().toString());
                            listMime.add(bean);
                        }
                        ZiXunItemDao.insertTypes(listMime);
                    }


                    //创建空数据的数据库 更多频道
                    if (listOther.size() == 0) {
                        ZiXunItemDao.insertTypes(listOther);
                    }

                    v.my_zixun_viewpager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
                    v.my_zixun_tablayout.setupWithViewPager(v.my_zixun_viewpager);

                    //表示缓存页面
//                    v.my_zixun_viewpager.setOffscreenPageLimit(titles.size() - 1);
                } else {
                    showToast(Constant.ERROR_WEB);
                }
            }
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> frag;

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            frag = new ArrayList<Fragment>();

            for (int i = 0; i < titles.size(); i++) {
                frag.add(ZiXunItemFragment.getInstance(titles.get(i).getTypeId(),i));
            }

        }

        @Override
        public Fragment getItem(int position) {
            return frag.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position).getTypeName();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
