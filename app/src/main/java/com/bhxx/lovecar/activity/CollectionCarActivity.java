package com.bhxx.lovecar.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.fragment.CollectXCItemFragment;
import com.bhxx.lovecar.fragment.CollectZXItemFragment;
import com.bhxx.lovecar.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-收藏
 *
 * @qq289513149.
 */

public class CollectionCarActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = CollectionCarActivity.class.getSimpleName();
    private final String[] titles = new String[2];
    private TabLayout collectCarTabLayout;
    private ViewPager collectCarViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collectcar);
        initView();
        initEvent();
        titles[0] = getResources().getString(R.string.mine_zx);
        titles[1] = getResources().getString(R.string.mine_xc);
        collectCarViewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        collectCarViewPager.setOffscreenPageLimit(titles.length - 1);
        collectCarTabLayout.setupWithViewPager(collectCarViewPager);
    }

    //初始化界面控件
    private void initView() {
        collectCarTabLayout = (TabLayout) this.findViewById(R.id.tlCollectCar);
        collectCarViewPager = (ViewPager) this.findViewById(R.id.vpCollectCar);
    }

    //初始化界面事件
    private void initEvent() {

    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }

    private class MyPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> frag = new ArrayList<Fragment>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            frag.add(CollectZXItemFragment.getInstance(0));
            frag.add(CollectXCItemFragment.getInstance(1));
        }

        @Override
        public Fragment getItem(int position) {
            return frag.get(position);
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
