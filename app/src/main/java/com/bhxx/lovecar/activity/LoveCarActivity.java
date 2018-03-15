package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.fragment.LCOrderItemFragment;
import com.bhxx.lovecar.fragment.LoveCarItemFragment;
import com.bhxx.lovecar.fragment.ZiXunItemFragment;
import com.bhxx.lovecar.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-爱车
 *
 * @qq289513149.
 */

public class LoveCarActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = LoveCarActivity.class.getSimpleName();
    private final String[] titles = new String[2];
    private TabLayout loveCarTabLayout;
    private ViewPager loveCarViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lovecar);
        initView();
        initEvent();
        titles[0] = getResources().getString(R.string.mine_lovecar);
        titles[1] = getResources().getString(R.string.mine_order);
        loveCarViewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        loveCarViewPager.setOffscreenPageLimit(titles.length - 1);
        loveCarTabLayout.setupWithViewPager(loveCarViewPager);
    }

    //初始化界面控件
    private void initView() {
        loveCarTabLayout = (TabLayout) this.findViewById(R.id.tlLovecar);
        loveCarViewPager = (ViewPager) this.findViewById(R.id.vpLovecar);
    }

    //初始化界面事件
    private void initEvent() {

    }

    public void onTradeHistory(View view) {
        startActivity(new Intent(this, TradeHistoryActivity.class));
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
            frag.add(LoveCarItemFragment.getInstance(0));
            frag.add(LCOrderItemFragment.getInstance(1));
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
