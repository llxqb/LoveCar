package com.bhxx.lovecar.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CarAssessInfoModel;
import com.bhxx.lovecar.beans.GJSModel;
import com.bhxx.lovecar.beans.GJSPublishServiceModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.fragment.gjsWorkAssessFragment;
import com.bhxx.lovecar.fragment.gjsWorkRecordFragment;
import com.bhxx.lovecar.fragment.gjsWorkServiceFragment;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

@InjectLayer(R.layout.activity_gjswork_detail)
public class GJSWorkDetailActivity extends BasicActivity {

    @InjectAll
    private Views v;
    private GJSPublishServiceModel gjsModel;

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        if (getIntent() != null) {
            gjsModel = (GJSPublishServiceModel) getIntent().getSerializableExtra("gjsModel");
            if (gjsModel != null) {
                App.app.setData("assessId", gjsModel.getAssessId() + "");
                initView();
            }
        }
        v.viewpager.setOffscreenPageLimit(3);
        v.viewpager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        v.tablayout.setupWithViewPager(v.viewpager);
    }

    private void initView() {

        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + gjsModel.getAvatar(), (ImageView) v.gjsworkDetail_avatar, LoadImage.getHeadImgOptions());
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + gjsModel.getAvatar(), (ImageView) v.gjsworkDetail_avatar_background, LoadImage.getDefaultOptions());

        v.gjs_workdetail_title.setText(gjsModel.getAssessName());
        if (gjsModel.getAssessTimes() == null) {
            v.gjsworkDetail_assessNum.setText(Html.fromHtml("已评估" + "<font color='red'>" + 0 + "</font>" + "次"));
        } else {
            v.gjsworkDetail_assessNum.setText(Html.fromHtml("已评估" + "<font color='red'>" + gjsModel.getAssessTimes() + "</font>" + "次"));
        }
        v.gjsworkDetail_city.setText(gjsModel.getServiceRegion());

        v.gjsworkDetail_level.setRating(Float.parseFloat(gjsModel.getGrade() + 1));
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.gjs_workdetail_back:
                finish();
                break;
        }
    }

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView gjs_workdetail_back, gjsworkDetail_avatar_background, gjsworkDetail_avatar;
        TextView gjs_workdetail_title, gjsworkDetail_assessNum, gjsworkDetail_city;
        RatingBar gjsworkDetail_level;
        TabLayout tablayout;
        ViewPager viewpager;

    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private final String[] titles = {"服务", "记录", "评价"};
        private List<Fragment> fragments = new ArrayList<Fragment>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            gjsWorkServiceFragment gjsWorkServiceFragment = new gjsWorkServiceFragment();
            gjsWorkRecordFragment gjsWorkRecordFragment = new gjsWorkRecordFragment();
            gjsWorkAssessFragment gjsWorkAssessFragment = new gjsWorkAssessFragment();
            fragments.add(gjsWorkServiceFragment);
            fragments.add(gjsWorkRecordFragment);
            fragments.add(gjsWorkAssessFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
