package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.fragment.AccountInFragment;
import com.bhxx.lovecar.fragment.AccountOutFragment;
import com.bhxx.lovecar.fragment.AccountTXFragment;
import com.bhxx.lovecar.fragment.CurrentOrderListFragment;
import com.bhxx.lovecar.fragment.HistoryOrderListFragment;
import com.bhxx.lovecar.fragment.MineOrderListFragment;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.makeapp.javase.util.DataUtil;
import com.makeapp.javase.util.MapUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/9.
 * 我的-估价师个人中心
 *
 * @qq289513149.
 */

public class GJSInfoActivity extends BasicActivity implements View.OnClickListener {

    public static final String TAG = GJSInfoActivity.class.getSimpleName();
    private CircleImageView gjsHeadImageView;//估价师头像
    private TextView gjsNameTextView;//估价师姓名
    private TextView gjsLevelTextView;//估价师级别
    private TextView gjsExpTextView;//估价师经验
    private TextView gjsLocTextView;//估价师位置
    private RadioGroup orderRadioGroup;
    private RadioButton currentRadioButton;
    private RadioButton historyRadioButton;
    private RadioButton mineRadioButton;
    private ViewPager viewPager;
    private Fragment[] mFragmentArray;
    private int gjsLevel;
    private int gjsExperience;
    private String serverUrl;//估价师证书上传地址
    private String background;//学历

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gjs_info);
        mFragmentArray = new Fragment[3];
        mFragmentArray[0] = Fragment.instantiate(this, CurrentOrderListFragment.class.getName());
        mFragmentArray[1] = Fragment.instantiate(this, HistoryOrderListFragment.class.getName());
        mFragmentArray[2] = Fragment.instantiate(this, MineOrderListFragment.class.getName());
        initView();
        initEvent();
        currentRadioButton.setChecked(true);
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
        startGetAssessInfo();
    }

    //初始化界面控件
    public void initView() {
        gjsHeadImageView = (CircleImageView) this.findViewById(R.id.civGjsHead);
        gjsNameTextView = (TextView) this.findViewById(R.id.tvGjsName);
        gjsLevelTextView = (TextView) this.findViewById(R.id.tvGjsLevel);
        gjsExpTextView = (TextView) this.findViewById(R.id.tvGjsExp);
        gjsLocTextView = (TextView) this.findViewById(R.id.tvGjsLoc);
        orderRadioGroup = (RadioGroup) this.findViewById(R.id.rgOrder);
        currentRadioButton = (RadioButton) this.findViewById(R.id.rbtnCurrent);
        historyRadioButton = (RadioButton) this.findViewById(R.id.rbtnHistory);
        mineRadioButton = (RadioButton) this.findViewById(R.id.rbtnMine);
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);
    }

    //初始化控件事件
    public void initEvent() {
        orderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbtnCurrent: {
                        viewPager.setCurrentItem(0);
                    }
                    break;
                    case R.id.rbtnHistory: {
                        viewPager.setCurrentItem(1);
                    }
                    break;
                    case R.id.rbtnMine: {
                        viewPager.setCurrentItem(2);
                    }
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
                        currentRadioButton.setChecked(true);
                        break;
                    case 1:
                        historyRadioButton.setChecked(true);
                        break;
                    case 2:
                        mineRadioButton.setChecked(true);
                        break;
                }
            }
        });
    }

    public void onBackClick(View view) {
        finish();
    }

    //发布服务
    public void onReleaseClick(View view) {
        startActivity(new Intent(GJSInfoActivity.this, GjsReleaseServicesActivity.class));
    }

    @Override
    public void onClick(View view) {

    }

    //获取估价师信息异步请求
    private void startGetAssessInfo() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        Log.e(TAG, "assessId--->" + App.app.getData(UserPreferences.USER_ASSESSID));
        params.put(Constant.CAR_USER_ASSESSID, App.app.getData(UserPreferences.USER_ASSESSID));
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.ASSESS_SELECTASSESS, "info", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "startGetAssessInfo e--->" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startGetAssessInfo response--->" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            JSONObject rowJSONObject = jsonObject.optJSONObject(Constant.CAR_ROWS);
                            //头像
                            String avatar = rowJSONObject.optString(Constant.CAR_USER_AVATAR);
                            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + avatar, gjsHeadImageView, LoadImage.getDefaultOptions());
                            //姓名
                            String assessName = rowJSONObject.optString(Constant.CAR_ASSESSNAME);
                            gjsNameTextView.setText(assessName);
                            //估价师等级
                            String grade = rowJSONObject.optString(Constant.CAR_GRADE);
                            gjsLevel = DataUtil.getInt(grade, 0);
                            String[] levels = getResources().getStringArray(R.array.gjs_level);
                            gjsLevelTextView.setText(levels[gjsLevel]);
                            //估价师经验
                            String workingAge = rowJSONObject.optString(Constant.CAR_WORKINGAGE);
                            gjsExperience = DataUtil.getInt(workingAge, 0);
                            String[] experiences = getResources().getStringArray(R.array.gjs_experience);
                            gjsExpTextView.setText(experiences[gjsExperience] + "经验");
                            //估价师所在地
                            String city = rowJSONObject.optString(Constant.CAR_SERVICEREGION);
                            gjsLocTextView.setText(city);
                            //学历
                            background = rowJSONObject.optString(Constant.CAR_BACKGROUND);
                            //估价师证书地址
                            serverUrl = rowJSONObject.optString(Constant.CAR_EXTEND1);
                        } else {
                            showToast(jsonObject.optString("resultDesc"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }







    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }
}
