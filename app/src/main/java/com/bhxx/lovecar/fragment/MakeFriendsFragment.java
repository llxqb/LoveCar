package com.bhxx.lovecar.fragment;
/**
 * 交友--fragment
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.AddFriendListActivity;
import com.bhxx.lovecar.activity.LoginActivity;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.TokenUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.makeapp.javase.lang.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class MakeFriendsFragment extends BaseFragment {

    public static final String TAG = MakeFriendsFragment.class.getSimpleName();
    private Activity mActivity;
    private View rootView;
    private ViewPager viewPager;
    private CircleImageView headImageView;
    private ImageView addImageView;
    private TextView nameTextView;
    private TextView othersTextView;
    private RadioGroup friendRadioGroup;
    private RadioButton mineFriendRadioButton;
    private RadioButton fjcyRadioButton;
    private Fragment[] mFragmentArray;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_makefriends, null);
        mActivity = getActivity();
        initView();
        initEvent();

        //本地头像显示
        String serverUrl = App.app.getData(UserPreferences.USER_AVATAR);
        if (StringUtil.isValid(serverUrl) && !"null".equals(serverUrl)) {
            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + serverUrl, headImageView, LoadImage.getDefaultOptions());
        }
        //用户姓名
        String fullName = App.app.getData(UserPreferences.USER_NAME);
        if (StringUtil.isValid(fullName) && !"null".equals(fullName)) {
            nameTextView.setText(fullName);
        }
        //用户性别
        String sex = App.app.getData(UserPreferences.USER_SEX);
        String cSex = "";
        if ("0".equals(sex)) {
            cSex = getResources().getString(R.string.mine_d_sex_male);
        } else if ("1".equals(sex)) {
            cSex = getResources().getString(R.string.mine_d_sex_female);
        }
        //用户所在城市
        String city = App.app.getData(UserPreferences.USER_CITY);
        if (StringUtil.isValid(cSex) && StringUtil.isValid(city) && !"null".equals(cSex) && !"null".equals(city)) {
            othersTextView.setText(cSex + " " + city);
        } else if (StringUtil.isValid(cSex) && !"null".equals(cSex)) {
            othersTextView.setText(cSex);
        } else if (StringUtil.isValid(city) && !"null".equals(city)) {
            othersTextView.setText(city);
        }
        mFragmentArray = new Fragment[1];
        mFragmentArray[0] = Fragment.instantiate(getActivity(), CYQMineFriendFragment.class.getName());
//        mFragmentArray[1] = Fragment.instantiate(getActivity(), CYQFJCYFragment.class.getName());
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragmentArray[i];
            }

            @Override
            public int getCount() {
                return mFragmentArray.length;
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setSelectedTab(position);
            }
        });
        mineFriendRadioButton.setChecked(true);
        return rootView;
    }

    //初始化界面控件
    private void initView() {
        addImageView = (ImageView) rootView.findViewById(R.id.ivAddFriend);
        headImageView = (CircleImageView) rootView.findViewById(R.id.civUserHead);
        viewPager = (ViewPager) rootView.findViewById(R.id.vpFriend);
        nameTextView = (TextView) rootView.findViewById(R.id.tvNickName);
        othersTextView = (TextView) rootView.findViewById(R.id.tvOthers);
        friendRadioGroup = (RadioGroup) rootView.findViewById(R.id.rgFriend);
        mineFriendRadioButton = (RadioButton) rootView.findViewById(R.id.rbMineHY);
        fjcyRadioButton = (RadioButton) rootView.findViewById(R.id.rbFJCY);

    }

    //初始化控件事件
    private void initEvent() {
        friendRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rbMineHY) {
                    Log.e(TAG, "---mine friend---");
                } else if (i == R.id.rbFJCY) {
                    Log.e(TAG, "---fjcy---");
                }
            }
        });
        //添加好友
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, AddFriendListActivity.class));
            }
        });
    }

    private void setSelectedTab(int index) {
        if (index == 0) {
            mineFriendRadioButton.setChecked(true);
        } else if (index == 1) {
            fjcyRadioButton.setChecked(true);
        }
    }


    @Override
    protected void init() {
    }

    @Override
    protected void click(View view) {
    }


}
