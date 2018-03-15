package com.bhxx.lovecar.activity;

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
import com.bhxx.lovecar.fragment.MessagePersonalFragment;
import com.bhxx.lovecar.fragment.MessageSystemFragment;
import com.bhxx.lovecar.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @dpy on 2016/12/5.
 * 我的-消息
 *
 * @qq289513149.
 */

public class MessageCenterActivity extends BasicActivity implements View.OnClickListener {

    public static final String TAG = MessageCenterActivity.class.getSimpleName();
    private TabLayout messageTabLayout;
    private ViewPager messageViewPager;
    private final String[] titles = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagecenter);
        titles[0] = getResources().getString(R.string.mine_message_personal);
        titles[1] = getResources().getString(R.string.mine_message_system);
        initView();
        initEvent();
        messageViewPager.setAdapter(new MessageCenterActivity.MyPageAdapter(getSupportFragmentManager()));
        messageViewPager.setOffscreenPageLimit(titles.length - 1);
        messageTabLayout.setupWithViewPager(messageViewPager);
    }

    //初始化界面控件
    public void initView() {
        messageTabLayout = (TabLayout) this.findViewById(R.id.tlMessage);
        messageViewPager = (ViewPager) this.findViewById(R.id.vpMessage);
    }

    //初始化控件事件
    public void initEvent() {

    }

    @Override
    public void onClick(View view) {

    }

    public void onBackClick(View view) {
        finish();
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
            frag.add(new MessagePersonalFragment());
            frag.add(new MessageSystemFragment());
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
