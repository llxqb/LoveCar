package com.bhxx.lovecar.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.UserPreferences;

import java.util.ArrayList;
import java.util.List;

public class ConversationMsgFragment extends BaseFragment {
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TabLayout tablayout;
        ViewPager viewpager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_conversationmsg_page, null);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    protected void init() {
        if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            showToast("请先登录");
        }
        v.viewpager.setOffscreenPageLimit(3);
        v.viewpager.setAdapter(new MyPageAdapter(getChildFragmentManager()));

        v.tablayout.setupWithViewPager(v.viewpager);
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private final String[] titles = {"进圈", "帖子", "交友"};
        private List<Fragment> fragments = new ArrayList<Fragment>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            MomentsFragment momentsFragment = new MomentsFragment();
            PostFragment postFragment = new PostFragment();
            MakeFriendsFragment makeFriendsFragment = new MakeFriendsFragment();
            fragments.add(momentsFragment);
            fragments.add(postFragment);
            fragments.add(makeFriendsFragment);
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

    @Override
    protected void click(View view) {
    }
}
