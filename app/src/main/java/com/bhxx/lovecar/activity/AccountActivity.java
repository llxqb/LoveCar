package com.bhxx.lovecar.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.fragment.AccountInFragment;
import com.bhxx.lovecar.fragment.AccountOutFragment;
import com.bhxx.lovecar.fragment.AccountTXFragment;

/**
 * Created by @dpy on 2017/1/6.
 * 我的-账户中心
 *
 * @qq289513149.
 */

public class AccountActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = AccountActivity.class.getSimpleName();
    private TextView accountTextView;//账户余额显示数字
    private Button withdrawButton;//去提现按钮
    private RadioGroup accountRadioGroup;
    private RadioButton inRadioButton;
    private RadioButton outRadioButton;
    private RadioButton txRadioButton;
    private ViewPager viewPager;
    private Fragment[] mFragmentArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mFragmentArray = new Fragment[3];
        mFragmentArray[0] = Fragment.instantiate(this, AccountInFragment.class.getName());
        mFragmentArray[1] = Fragment.instantiate(this, AccountOutFragment.class.getName());
        mFragmentArray[2] = Fragment.instantiate(this, AccountTXFragment.class.getName());
        initView();
        initEvent();
        inRadioButton.setChecked(true);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragmentArray[i];
            }

            @Override
            public int getCount() {
                return mFragmentArray.length;
            }
        });
    }

    //初始化界面控件
    private void initView() {
        accountTextView = (TextView) this.findViewById(R.id.tvAccount);
        withdrawButton = (Button) this.findViewById(R.id.btnWithdraw);
        accountRadioGroup = (RadioGroup) this.findViewById(R.id.rgAccount);
        inRadioButton = (RadioButton) this.findViewById(R.id.rbtnIn);
        outRadioButton = (RadioButton) this.findViewById(R.id.rbtnOut);
        txRadioButton = (RadioButton) this.findViewById(R.id.rbtnTX);
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);
    }

    //初始化控件事件
    private void initEvent() {
        withdrawButton.setOnClickListener(this);
        accountRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbtnIn:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rbtnOut:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.rbtnTX:
                        viewPager.setCurrentItem(2);
                        break;
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        inRadioButton.setChecked(true);
                        break;
                    case 1:
                        outRadioButton.setChecked(true);
                        break;
                    case 2:
                        txRadioButton.setChecked(true);
                        break;
                }
            }
        });
    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnWithdraw: {

            }
            break;
        }
    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }
}
