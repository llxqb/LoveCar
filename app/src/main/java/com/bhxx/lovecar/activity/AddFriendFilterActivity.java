package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.views.UserItem1;
import com.bigkoo.pickerview.OptionsPickerView;

import java.util.ArrayList;

/**
 * Created by @dpy on 2016/12/24.
 * 车友圈-交友-添加好友-过滤more
 *
 * @qq289513149.
 */

public class AddFriendFilterActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = AddFriendFilterActivity.class.getSimpleName();
    public static final int REQUEST_CITY = 0x11;
    public static final int RESPONSE_CITY = 0x12;
    private View boyView;
    private View girlView;
    private View normalView;
    private String sex = "0";//进来默认是选中男的
    private String ageStart;
    private String ageEnd;
    private String city;
    private String distance;
    private String identity;
    private UserItem1 ageUserItem1;
    private UserItem1 cityUserItem1;
    private UserItem1 distanceUserItem1;
    private UserItem1 identityUserItem1;
    private TextView okTextView;
    private TextView cancelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyq_jy_addfriendfilter);
        initView();
        initEvent();
        boyView.setSelected(true);
        sex = "0";
    }

    //初始化界面控件
    private void initView() {
        boyView = this.findViewById(R.id.llBoy);
        girlView = this.findViewById(R.id.llGirl);
        normalView = this.findViewById(R.id.llNormal);
        ageUserItem1 = (UserItem1) this.findViewById(R.id.uiAge);
        cityUserItem1 = (UserItem1) this.findViewById(R.id.uiCity);
        distanceUserItem1 = (UserItem1) this.findViewById(R.id.uiDistance);
        identityUserItem1 = (UserItem1) this.findViewById(R.id.uiType);
        okTextView = (TextView) this.findViewById(R.id.tvOk);
        cancelTextView = (TextView) this.findViewById(R.id.tvCancel);
    }

    //初始化控件事件
    private void initEvent() {
        boyView.setOnClickListener(this);
        girlView.setOnClickListener(this);
        normalView.setOnClickListener(this);
        ageUserItem1.setOnClickListener(this);
        cityUserItem1.setOnClickListener(this);
        distanceUserItem1.setOnClickListener(this);
        identityUserItem1.setOnClickListener(this);
        okTextView.setOnClickListener(this);
//        cancelTextView.setOnClickListener(this);
    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llBoy: {
                sex = "0";
                boyView.setSelected(true);
                girlView.setSelected(false);
                normalView.setSelected(false);

            }
            break;
            case R.id.llGirl: {
                sex = "1";
                boyView.setSelected(false);
                girlView.setSelected(true);
                normalView.setSelected(false);
            }
            break;
            case R.id.llNormal: {
                sex = "";
                boyView.setSelected(false);
                girlView.setSelected(false);
                normalView.setSelected(true);
            }
            break;
            case R.id.uiAge: {//年龄
                selectedAge();
            }
            break;
            case R.id.uiCity: {//城市
                String title = getResources().getString(R.string.mine_detail_city);
                Intent intent = new Intent(AddFriendFilterActivity.this, UserCityActivity.class);
                intent.putExtra(UserCityActivity.REQUEST_TITLE, title);
                String city = cityUserItem1.getContent();
                intent.putExtra(SmallTextActivity.REQUEST_CONTENT, city);
                startActivityForResult(intent, REQUEST_CITY);
            }
            break;
            case R.id.uiDistance: {//距离
                selectedDistance();
            }
            break;
            case R.id.uiType: {//身份
                selectedIdentity();
            }
            break;
            case R.id.tvOk: {
                Intent intent = new Intent();
                Log.e(TAG, "sex:" + sex + ";ageStart:" + ageStart + ";ageEnd:" + ageEnd + ";city:" + city + ";distance:" + distance + ";identity:" + identity);
                //0:男;1:女;-1:不限
                intent.putExtra(AddFriendListActivity.SEX, sex);
                //ageStart开始年龄
                intent.putExtra(AddFriendListActivity.START_AGE, ageStart);
                //ageEnd结束年龄
                intent.putExtra(AddFriendListActivity.END_AGE, ageEnd);
                String city = cityUserItem1.getContent();
                //city所在城市
                intent.putExtra(AddFriendListActivity.CITY, city);
                //0:3km;1:5km;2:8km;3:距离最远;4:距离最近;5:不限
                intent.putExtra(AddFriendListActivity.DISTANCE, distance);
                //0:评估师;1:车主;2:不限
                intent.putExtra(AddFriendListActivity.IDENTITY, identity);
                setResult(AddFriendListActivity.RESPONSE_FILTER, intent);
                finish();
            }
            break;
            case R.id.tvCancel: {

            }
            break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CITY && resultCode == UserCityActivity.RESULT_CITY) {
            cityUserItem1.setContent(data.getStringExtra(UserCityActivity.RESPONSE_CITY));
        }
    }

    //年龄选择
    private void selectedAge() {
        OptionsPickerView pickerView = new OptionsPickerView(this);
        final ArrayList<String> ageList = new ArrayList<String>();
        String[] ages = getResources().getStringArray(R.array.age);
        for (int i = 0; i < ages.length; i++) {
            ageList.add(ages[i]);
        }
        pickerView.setPicker(ageList);
        pickerView.setTitle(getResources().getString(R.string.mine_detail_age));
        pickerView.setCyclic(false);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String age = ageList.get(options1);
                ageUserItem1.setContent(age);
                switch (options1) {
                    case 0:
                        ageStart = "0";
                        ageEnd = "19";
                        break;
                    case 1:
                        ageStart = "20";
                        ageEnd = "29";
                        break;
                    case 2:
                        ageStart = "30";
                        ageEnd = "39";
                        break;
                    case 3:
                        ageStart = "40";
                        ageEnd = "49";
                        break;
                    case 4:
                        ageStart = "50";
                        ageEnd = "59";
                        break;
                    case 5:
                        ageStart = "60";
                        ageEnd = "200";
                        break;
                }
            }
        });
        pickerView.show();
    }

    //选择距离
    private void selectedDistance() {
        OptionsPickerView pickerView = new OptionsPickerView(this);
        final ArrayList<String> distanceList = new ArrayList<String>();
        String[] distances = getResources().getStringArray(R.array.distance);
        for (int i = 0; i < distances.length; i++) {
            distanceList.add(distances[i]);
        }
        pickerView.setPicker(distanceList);
        pickerView.setTitle(getResources().getString(R.string.car_cyq_af_filter_d));
        pickerView.setCyclic(false);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                distance = distanceList.get(options1);
                distanceUserItem1.setContent(distance);
                if (options1 != 5) {
                    distance = (options1 + 1) + "";
                } else {
                    distance = "";
                }
            }
        });
        pickerView.show();
    }

    //身份
    private void selectedIdentity() {
        OptionsPickerView pickerView = new OptionsPickerView(this);
        final ArrayList<String> identityList = new ArrayList<String>();
        String[] identitys = getResources().getStringArray(R.array.identity);
        for (int i = 0; i < identitys.length; i++) {
            identityList.add(identitys[i]);
        }
        pickerView.setPicker(identityList);
        pickerView.setTitle(getResources().getString(R.string.car_cyq_af_filter_type));
        pickerView.setCyclic(false);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                identity = identityList.get(options1);
                identityUserItem1.setContent(identity);
                if (options1 == 0) {
                    identity = "1";
                } else if (options1 == 1) {
                    identity = "0";
                } else {
                    identity = "";
                }
            }
        });
        pickerView.show();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }
}
