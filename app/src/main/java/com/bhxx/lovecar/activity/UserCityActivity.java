package com.bhxx.lovecar.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.service.LocationService;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.views.FastLocationBarView;
import com.bhxx.lovecar.views.PinnedHeaderListView;
import com.bhxx.lovecar.views.SectionedBaseAdapter;
import com.makeapp.android.util.ToastUtil;
import com.makeapp.android.util.ViewUtil;
import com.makeapp.javase.lang.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @dpy on 2016/12/1.
 *
 * @qq289513149.
 */

public class UserCityActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = UserCityActivity.class.getSimpleName();
    public static final int RESULT_CITY = 0x11;
    public static final int BD_TypeServerSuccess = 0x12;
    public static final String REQUEST_TITLE = "title";
    public static final String RESPONSE_CITY = "city";
    private List<String> headList = new ArrayList<String>();
    private List<String[]> cityList = new ArrayList<String[]>();
    private TextView currentCity;
    private TextView titleTextView;
    private LocationService locationService;
    private FastLocationBarView sideBar;
    private TextView dialog;
    private CitySectionedAdapter sectionedAdapter;
    private PinnedHeaderListView listView;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BD_TypeServerSuccess: {
                    String city = String.valueOf(msg.obj);
                    if (StringUtil.isValid(city) && !"null".equals(city)) {
                        currentCity.setText(city);
                    }
                }
                break;
                case BDLocation.TypeServerError: {
                    ToastUtil.show(UserCityActivity.this, getResources().getString(R.string.op_bd_net_diff));
                }
                break;
                case BDLocation.TypeCriteriaException: {
                    ToastUtil.show(UserCityActivity.this, getResources().getString(R.string.op_bd_net_fxms));
                }
                break;
            }
        }
    };
    //定位结果回调，重写onReceiveLocation方法
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                Message message = mHandler.obtainMessage();
                message.obj = location.getCity();
                if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    message.what = BDLocation.TypeNetWorkException;
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    message.what = BDLocation.TypeCriteriaException;
                } else {
                    message.what = BD_TypeServerSuccess;
                }
                mHandler.sendMessage(message);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercity);
        initView();
        initEvent();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(UserCityActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(UserCityActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showDialog(getResources().getString(R.string.op_permission_title), getResources().getString(R.string.op_permission_con_loc),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UserCityActivity.super.cancelAlertDialog();
                                    //请求权限
                                    ActivityCompat.requestPermissions(UserCityActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UserCityActivity.super.cancelAlertDialog();
                                }
                            }, getResources().getString(R.string.operation_confirm), getResources().getString(R.string.operation_cancel));
                } else {
                    //请求权限
                    ActivityCompat.requestPermissions(UserCityActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                }
            } else {
                onStartLocation();
            }
        } else {
            onStartLocation();
        }
        titleTextView.setText(getIntent().getStringExtra(REQUEST_TITLE));
        sectionedAdapter = new CitySectionedAdapter(getHeadList(), getCityList());

        listView.setAdapter(sectionedAdapter);
        listView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
                String city = String.valueOf(view.getTag());
                if (StringUtil.isValid(city)) {
                    Intent intent = new Intent();
                    intent.putExtra(RESPONSE_CITY, city);
                    setResult(RESULT_CITY, intent);
                    finish();
                }
            }

            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
            }
        });
    }

    //百度开始定位
    private void onStartLocation() {
        locationService = ((App) getApplication()).locationService;
        //注册监听
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();// 定位SDK
    }

    //初始化界面控件
    private void initView() {
        titleTextView = (TextView) ViewUtil.findViewById(this, R.id.tvTitle);
        listView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
        sideBar = (FastLocationBarView) ViewUtil.findViewById(this, R.id.sidebar);
        dialog = (TextView) ViewUtil.findViewById(this, R.id.dialog);
        currentCity = (TextView) ViewUtil.findViewById(this, R.id.tv_currentcity);
    }

    private void initEvent() {
        currentCity.setOnClickListener(this);
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new FastLocationBarView.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = sectionedAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
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

    @Override
    public void onClick(View view) {
        String city = currentCity.getText().toString().trim();
        Log.e(TAG, "自动定位城市----->" + city);
        if (StringUtil.isValid(city)) {
            Intent intent = new Intent();
            intent.putExtra(RESPONSE_CITY, city);
            setResult(RESULT_CITY, intent);
            finish();
        }
    }

    //android6.0以上的权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100: {//定位 系统给的权限提醒后 确认 长度>0 否则提示
                Log.e(TAG, "100");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//权限请求确认
                    onStartLocation();
                } else {//权限请求取消
                    showToast(getResources().getString(R.string.op_permission_loc));
                }
            }
        }
    }

    private List<String> getHeadList() {
        headList.add("热门城市");
        headList.add("A");
        headList.add("B");
        headList.add("C");
        headList.add("D");
        headList.add("E");
        headList.add("F");
        headList.add("G");

        headList.add("H");
        headList.add("J");
        headList.add("K");
        headList.add("L");
        headList.add("M");
        headList.add("N");

        headList.add("P");
        headList.add("Q");
        headList.add("R");
        headList.add("S");
        headList.add("T");

        headList.add("W");
        headList.add("X");
        headList.add("Y");
        headList.add("Z");

        return headList;
    }

    private List<String[]> getCityList() {
        String[] hotCitys = this.getResources().getStringArray(R.array.hotcity);
        cityList.add(hotCitys);
        String[] ACitys = this.getResources().getStringArray(R.array.A);
        cityList.add(ACitys);
        String[] BCitys = this.getResources().getStringArray(R.array.B);
        cityList.add(BCitys);
        String[] CCitys = this.getResources().getStringArray(R.array.C);
        cityList.add(CCitys);
        String[] DCitys = this.getResources().getStringArray(R.array.D);
        cityList.add(DCitys);
        String[] ECitys = this.getResources().getStringArray(R.array.E);
        cityList.add(ECitys);
        String[] FCitys = this.getResources().getStringArray(R.array.F);
        cityList.add(FCitys);
        String[] GCitys = this.getResources().getStringArray(R.array.G);
        cityList.add(GCitys);

        String[] HCitys = this.getResources().getStringArray(R.array.H);
        cityList.add(HCitys);
        String[] JCitys = this.getResources().getStringArray(R.array.J);
        cityList.add(JCitys);
        String[] KCitys = this.getResources().getStringArray(R.array.K);
        cityList.add(KCitys);
        String[] LCitys = this.getResources().getStringArray(R.array.L);
        cityList.add(LCitys);
        String[] MCitys = this.getResources().getStringArray(R.array.M);
        cityList.add(MCitys);
        String[] NCitys = this.getResources().getStringArray(R.array.N);
        cityList.add(NCitys);

        String[] PCitys = this.getResources().getStringArray(R.array.P);
        cityList.add(PCitys);
        String[] QCitys = this.getResources().getStringArray(R.array.Q);
        cityList.add(QCitys);
        String[] RCitys = this.getResources().getStringArray(R.array.R);
        cityList.add(RCitys);
        String[] SCitys = this.getResources().getStringArray(R.array.S);
        cityList.add(SCitys);
        String[] TCitys = this.getResources().getStringArray(R.array.T);
        cityList.add(TCitys);

        String[] WCitys = this.getResources().getStringArray(R.array.W);
        cityList.add(WCitys);
        String[] XCitys = this.getResources().getStringArray(R.array.X);
        cityList.add(XCitys);
        String[] YCitys = this.getResources().getStringArray(R.array.Y);
        cityList.add(YCitys);
        String[] ZCitys = this.getResources().getStringArray(R.array.Z);
        cityList.add(ZCitys);

        return cityList;
    }

    class CitySectionedAdapter extends SectionedBaseAdapter implements PinnedHeaderListView.PinnedSectionedHeaderAdapter, SectionIndexer {

        private List<String> headList = new ArrayList<String>();
        private List<String[]> cityMap = new ArrayList<String[]>();

        public CitySectionedAdapter(List<String> headList, List<String[]> cityMap) {
            this.headList = headList;
            this.cityMap = cityMap;
        }

        @Override
        public Object getItem(int section, int position) {
            return cityMap.get(section)[position];
        }

        @Override
        public long getItemId(int section, int position) {
            return position;
        }

        @Override
        public int getSectionCount() {//7个分段
            return headList.size();
        }

        @Override
        public int getCountForSection(int section) {//7个分段，每个分段下面的数量
            String[] cityList = cityMap.get(section);
            return cityList.length;
        }


        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            LinearLayout layout = null;
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.mine_city_item, null);
            } else {
                layout = (LinearLayout) convertView;
            }
            layout.setTag(cityMap.get(section)[position]);
            ((TextView) layout.findViewById(R.id.textItem)).setText(cityMap.get(section)[position]);
            return layout;
        }

        @Override
        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {//设置每个分段的头部
            LinearLayout layout = null;
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.mine_city_item_head, null);
            } else {
                layout = (LinearLayout) convertView;
            }
            layout.setTag(headList.get(section));
            ((TextView) layout.findViewById(R.id.textItem)).setText(headList.get(section));
            return layout;
        }


        @Override
        public Object[] getSections() {
            return new Object[0];
        }

        @Override
        public int getPositionForSection(int section) {
            int indexCount = 0;
            for (int i = 0; i < getSectionCount(); i++) {
                String sortStr = headList.get(i);
                indexCount += getCountForSection(i);
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return indexCount - getCountForSection(i) + i;
                }
            }
            return -1;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }


}
