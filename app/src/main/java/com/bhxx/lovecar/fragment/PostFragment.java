package com.bhxx.lovecar.fragment;
/**
 * 帖子--fragment
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.UserPreferences;

public class PostFragment extends BaseFragment {

    private static final String TAG = PostFragment.class.getSimpleName();
    private RadioGroup tzRadioGroup;
    private View rootView;
    private RadioButton tzRadioButton;
    private RadioButton hfRadioButton;
    private ViewPager tzViewPager;
    private Fragment[] mFragmentArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post, null);
        initView();
        initEvent();
        mFragmentArray = new Fragment[2];
        mFragmentArray[0] = Fragment.instantiate(getActivity(), MineTZFragment.class.getName());
        mFragmentArray[1] = Fragment.instantiate(getActivity(), MineHFFragment.class.getName());
//        tzViewPager.setOffscreenPageLimit(1);
        tzViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragmentArray[i];
            }

            @Override
            public int getCount() {
                return mFragmentArray.length;
            }
        });

        tzViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setSelectedTab(position);
            }
        });
        return rootView;
    }

    //初始化控件
    private void initView() {
        tzRadioGroup = (RadioGroup) rootView.findViewById(R.id.rgTZ);
        tzRadioButton = (RadioButton) rootView.findViewById(R.id.rbMineTZ);
        hfRadioButton = (RadioButton) rootView.findViewById(R.id.rbMineHF);
        tzViewPager = (ViewPager) rootView.findViewById(R.id.vpTZ);
    }

    //初始化控件事件
    private void initEvent() {
        tzRadioButton.setChecked(true);

        tzRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbMineTZ) {//选中我的帖子
                    Log.e(TAG, "mine tz");
                    tzViewPager.setCurrentItem(0);
                } else if (i == R.id.rbMineHF) {//选中我的回复
                    Log.e(TAG, "mine hf");
                    tzViewPager.setCurrentItem(1);
                }
            }
        });
    }

    private void setSelectedTab(int index) {
        if (index == 0) {
            tzRadioButton.setChecked(true);
        } else if (index == 1) {
            hfRadioButton.setChecked(true);
        }
    }

    @Override
    protected void init() {
    }

    @Override
    protected void click(View view) {
    }
}
