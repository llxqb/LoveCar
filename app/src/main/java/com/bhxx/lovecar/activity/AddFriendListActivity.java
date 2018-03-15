package com.bhxx.lovecar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.makeapp.android.util.ToastUtil;
import com.makeapp.javase.lang.StringUtil;
import com.makeapp.javase.util.DataUtil;
import com.makeapp.javase.util.DateUtil;
import com.makeapp.javase.util.MapUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/24.
 * 车友圈-交友-添加好友
 *
 * @qq289513149.
 */

public class AddFriendListActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = AddFriendListActivity.class.getSimpleName();
    public static final int REQUEST_FILTER = 0x11;
    public static final int RESPONSE_FILTER = 0x12;
    public static final String START_AGE = "startAge";
    public static final String END_AGE = "endAge";
    public static final String SEX = "sex";
    public static final String CITY = "city";

    public static final String IDENTITY = "v";
    public static final String AGE = "age";
    public static final String DISTANCE = "distance";
    private ListView listView;
    private List<Map> mapList = new ArrayList<Map>();
    private ListAdapter listAdapter;
    private List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();//所有用户列表
    private List<Map<String, Object>> filterUserList = new ArrayList<Map<String, Object>>();//所有有经纬度的用户列表
    private List<Map<String, Object>> tenUserList = new ArrayList<Map<String, Object>>();//10公里用户列表


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyq_jy_addfriendlist);
        initView();
        initEvent();
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);
        startFriendListTask("null", 0, 0, "null", "null");//初始为所有请求
//        startAllUserTask();
    }

    //初始化界面控件
    private void initView() {
        listView = (ListView) this.findViewById(R.id.listView);
    }

    //初始化控件事件
    private void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    //more
    public void onMoreClick(View view) {
        startActivityForResult(new Intent(AddFriendListActivity.this, AddFriendFilterActivity.class), REQUEST_FILTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILTER && resultCode == RESPONSE_FILTER) {
            String sex = data.getStringExtra(SEX);
            String ageStart = data.getStringExtra(START_AGE);
            String ageEnd = data.getStringExtra(END_AGE);
            String city = data.getStringExtra(CITY);
            String distance = data.getStringExtra(DISTANCE);
            String identity = data.getStringExtra(IDENTITY);
            EventBus.getDefault().post(new Object());
            Log.e(TAG, "返回sex:" + sex + ";ageStart:" + ageStart + ";ageEnd:" + ageEnd + ";city:" + city + ";distance:" + distance + ";identity:" + identity);
//            startFriendListTask(identity, DataUtil.getInt(ageStart), DataUtil.getInt(ageEnd), sex, city);
//            startFilterUserList(sex, city, DataUtil.getInt(ageStart), DataUtil.getInt(ageEnd), distance, identity);
        }
    }

    //过滤特别信息(性别 年龄 城市 开始年龄 结束年龄)
    private void startFilterUserList(String sex, String city, int ageStart, int ageEnd, String distance, String identity) {
        int filterSize = filterUserList.size();
        if (filterSize == 0) {
            ToastUtil.show(this, getResources().getString(R.string.operation_tip_datanull));
            return;
        }
        List<Map<String, Object>> sexMapList = new ArrayList<Map<String, Object>>();
        //先过滤性别
        if (!"-1".equals(sex)) {//-1是不限男女
            for (int i = 0; i < filterSize; i++) {
                Map<String, Object> map = filterUserList.get(i);
                String tSex = MapUtil.getString(map, Constant.CAR_USER_SEX);
                if (tSex.equals(sex)) {
                    sexMapList.add(map);
                }
            }
        } else {//那就是所有性别不分男女
            for (int i = 0; i < filterSize; i++) {
                sexMapList.add(filterUserList.get(i));
            }
        }
        //从性别后的集合过滤年龄
        int sexFilterSize = sexMapList.size();
        List<Map<String, Object>> ageMapList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < sexFilterSize; i++) {
            Map<String, Object> map = sexMapList.get(i);
            String birthday = MapUtil.getString(map, Constant.CAR_USER_BIRTHDAY);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int cYear = calendar.get(Calendar.YEAR);
            Calendar uCalendar = Calendar.getInstance();
            uCalendar.setTime(DateUtil.getDate(birthday, "yyyy-MM-dd"));
            int uYear = uCalendar.get(Calendar.YEAR);
            int uAge = Math.abs(cYear - uYear + 1);
            if (uAge > ageStart && uAge <= ageEnd) {
                ageMapList.add(map);
            }
        }
        //从年龄后的集合过滤城市
        int ageFilterSize = ageMapList.size();
        List<Map<String, Object>> cityMapList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < ageFilterSize; i++) {
            Map<String, Object> map = ageMapList.get(i);
            String tCity = MapUtil.getString(map, Constant.CAR_USER_CITY);
            if (tCity.equals(city)) {
                cityMapList.add(map);
            }
        }
        //从城市后的集合过滤距离
        int cityFilterSize = cityMapList.size();
        List<Map<String, Object>> distanceMapList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < cityFilterSize; i++) {
            Map<String, Object> map = cityMapList.get(i);
            String lat = MapUtil.getString(map, Constant.CAR_USER_LATITUDEY);
            String lon = MapUtil.getString(map, Constant.CAR_USER_LONGITUDEX);
            double dLat = DataUtil.getDouble(lat, 0);
            double dLon = DataUtil.getDouble(lon, 0);
            LatLng filterLatLng = new LatLng(dLat, dLon);
            String mLat = App.app.getData(UserPreferences.USER_LATITUDEY);
            String mLon = App.app.getData(UserPreferences.USER_LONGITUDEX);
            double mdLat = DataUtil.getDouble(mLat, 0);
            double mdLon = DataUtil.getDouble(mLon, 0);
            LatLng mLatLng = new LatLng(mdLat, mdLon);
            double ten = DistanceUtil.getDistance(mLatLng, filterLatLng);
            double tenAbs = Math.abs(ten);
            Log.e(TAG, "过滤tenAbs----->" + tenAbs);
            if ("0".equals(distance)) {//3km
                if (tenAbs <= 3000 && tenAbs > 1000) {
                    map.put(DISTANCE, String.format("%.2f", tenAbs / 1000) + "公里");
                    distanceMapList.add(map);
                } else {
                    map.put(DISTANCE, String.format("%.2f", tenAbs) + "米");
                    distanceMapList.add(map);
                }
            } else if ("1".equals(distance)) {//5km
                if (tenAbs <= 5000 && tenAbs > 1000) {
                    map.put(DISTANCE, String.format("%.2f", tenAbs / 1000) + "公里");
                    distanceMapList.add(map);
                } else {
                    map.put(DISTANCE, String.format("%.2f", tenAbs) + "米");
                    distanceMapList.add(map);
                }
            } else if ("2".equals(distance)) {//8km
                if (tenAbs <= 8000 && tenAbs > 1000) {
                    map.put(DISTANCE, String.format("%.2f", tenAbs / 1000) + "公里");
                    distanceMapList.add(map);
                } else {
                    map.put(DISTANCE, String.format("%.2f", tenAbs) + "米");
                    distanceMapList.add(map);
                }
            } else if ("3".equals(distance)) {//距离最远大于100km
                if (tenAbs > 100000) {
                    map.put(DISTANCE, String.format("%.2f", tenAbs / 1000) + "公里");
                    distanceMapList.add(map);
                }
            } else if ("4".equals(distance)) {//距离最近50m
                if (tenAbs <= 50) {
                    map.put(DISTANCE, String.format("%.2f", tenAbs) + "米");
                    distanceMapList.add(map);
                }
            } else {//不限
                distanceMapList.add(map);
            }
        }
        //从距离后的集合过滤身份
        int distanceFilterSize = distanceMapList.size();
        List<Map<String, Object>> identityMapList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < distanceFilterSize; i++) {
            Map<String, Object> map = distanceMapList.get(i);
            String tIdentity = MapUtil.getString(map, Constant.CAR_USER_IDENTITY);
            if ("0".equals(identity) && "0".equals(tIdentity)) {//评估师
                identityMapList.add(map);
            } else if ("1".equals(identity) && "1".equals(tIdentity)) {//车主
                identityMapList.add(map);
            } else if ("2".equals(tIdentity)) {//不限
                identityMapList.add(map);
            }
        }
        tenUserList = new ArrayList<>();
        tenUserList.addAll(identityMapList);

        Log.e(TAG, "过滤tenUserList.size():" + tenUserList.size());
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(AddFriendListActivity.this);
    }

    @Override
    protected void click(View view) {
    }

    public void onBackClick(View view) {
        finish();
    }

    class ListAdapter extends BaseAdapter {

        LayoutInflater inflater;

        public ListAdapter() {
            inflater = (LayoutInflater) AddFriendListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int i) {
            return mapList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Map map = mapList.get(i);
            View layout = inflater.inflate(R.layout.layout_cyq_jy_addfrienditem, null);
            TextView nameTextView = (TextView) layout.findViewById(R.id.tvName);
            TextView ageTextView = (TextView) layout.findViewById(R.id.tvAge);
            TextView cityTextView = (TextView) layout.findViewById(R.id.tvDistance);
            ImageView rzImageView = (ImageView) layout.findViewById(R.id.ivRZ);
            ImageView sexImageView = (ImageView) layout.findViewById(R.id.ivSex);
            CircleImageView circleImageView = (CircleImageView) layout.findViewById(R.id.civHead);
            ImageLoader.getInstance().displayImage(MapUtil.getString(map, Constant.CAR_USER_AVATAR), circleImageView, LoadImage.getHeadImgOptions());
            if (map != null) {
                String name = MapUtil.getString(map, Constant.CAR_USER_FULLNAME);
                if (StringUtil.isValid(name) && !"null".equals(name)) {
                    nameTextView.setText(name);
                } else {
                    nameTextView.setText("");
                }
                boolean b = MapUtil.getBoolean(map, IDENTITY, false);
                rzImageView.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                String sex = MapUtil.getString(map, Constant.CAR_USER_SEX);
                if ("0".equals(sex)) {
                    sexImageView.setImageResource(R.mipmap.icon_man_blue);
                } else {
                    sexImageView.setImageResource(R.mipmap.icon_woman_pink);
                }
                String age = MapUtil.getString(map, AGE);
                if (StringUtil.isValid(age) && !"null".equals(age)) {
                    ageTextView.setText(age);
                } else {
                    ageTextView.setText("");
                }
                String distance = MapUtil.getString(map, "dis");
//                cityTextView.setText(MapUtil.getString(map, Constant.CAR_USER_CITY) + "/" + MapUtil.getString(map, DISTANCE));
                double d = DataUtil.getDouble(distance, 0) * 1000;//
                String city = MapUtil.getString(map, Constant.CAR_USER_CITY);
                if (StringUtil.isValid(city) && !"null".equals(city)) {
                    if (d >= 1000) {
                        cityTextView.setText(city + "/" + String.format("%.2f", d / 1000) + "公里");
                    } else {
                        cityTextView.setText(city + "/" + String.format("%.2f", d) + "米");
                    }
                } else {
                    if (d >= 1000) {
                        cityTextView.setText(String.format("%.2f", d / 1000) + "公里");
                    } else {
                        cityTextView.setText(String.format("%.2f", d) + "米");
                    }
                }
            }
            Button addButton = (Button) layout.findViewById(R.id.btnAdd);
            String isFriend = MapUtil.getString(map, Constant.CAR_ISFRIEND);//0好友1不是好友
            addButton.setEnabled(!"0".equals(isFriend));
            final String userId = MapUtil.getString(map, Constant.CAR_USER_ID);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog("", String.format(AddFriendListActivity.this.getResources().getString(R.string.op_add_to_friend), MapUtil.getString(map, Constant.CAR_USER_FULLNAME)), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AddFriendListActivity.super.cancelAlertDialog();
                            //添加好友的接口
                            startAddToMyFriendTask(userId);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AddFriendListActivity.super.cancelAlertDialog();
                        }
                    }, AddFriendListActivity.this.getResources().getString(R.string.operation_confirm), AddFriendListActivity.this.getResources().getString(R.string.operation_cancel));

                }
            });
            return layout;
        }
    }

    //获取推荐好友异步请求
    private void startFriendListTask(String identity, int ageStart, int ageEnd, String sex, String city) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        if (StringUtil.isValid(identity) && !"null".equals(identity)) {
            params.put(Constant.CAR_IDENTITY, identity);//0会员 1评估师
        }
        if (ageStart >= 0 && ageEnd >= 0 && ageStart < ageEnd) {
            params.put(Constant.CAR_AGESTART, ageStart + "");
            params.put(Constant.CAR_AGEEND, ageEnd + "");
        }
        if (StringUtil.isValid(sex) && !"null".equals(sex)) {
            params.put(Constant.CAR_USER_SEX, sex);//0男 1女
        }
        if (StringUtil.isValid(city) && !"null".equals(city)) {
            params.put(Constant.CAR_USER_CITY, city);
        }
        Log.e(TAG, "startFriendListTask params----->" + params);
        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        if (StringUtil.isValid(identity) && !"null".equals(identity)) {
            hashMap.put(Constant.CAR_IDENTITY, identity);//0会员 1评估师
        }
        if (ageStart >= 0 && ageEnd >= 0 && ageStart < ageEnd) {
            hashMap.put(Constant.CAR_AGESTART, ageStart + "");
            hashMap.put(Constant.CAR_AGEEND, ageEnd + "");
        }
        if (StringUtil.isValid(sex) && !"null".equals(sex)) {
            hashMap.put(Constant.CAR_USER_SEX, sex);//0男 1女
        }
        if (StringUtil.isValid(city) && !"null".equals(city)) {
            hashMap.put(Constant.CAR_USER_CITY, city);
        }
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startFriendListTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.FRIEND_FRIENDUSER, "friendUser", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "startFriendListTask e---->" + e);
//                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startFriendListTask response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));
                            JSONArray rowJSONArray = jsonObject.optJSONArray(Constant.CAR_ROWS);
                            if (rowJSONArray != null) {
                                userList = new ArrayList<Map<String, Object>>();
                                mapList = new ArrayList<Map>();
                                listAdapter = new ListAdapter();
                                listView.setAdapter(listAdapter);
                                int length = rowJSONArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject item = rowJSONArray.optJSONObject(i);
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put(Constant.CAR_USER_AVATAR, GlobalValues.IP1 + item.optString(Constant.CAR_USER_AVATAR));//头像
                                    map.put(Constant.CAR_USER_FULLNAME, item.optString(Constant.CAR_USER_FULLNAME));//用户名
                                    map.put(Constant.CAR_USER_ID, item.optString(Constant.CAR_USER_ID));//用户ID
                                    map.put(Constant.CAR_USER_SEX, item.optString(Constant.CAR_USER_SEX));//用户性别0男1女
                                    map.put(Constant.CAR_USER_BIRTHDAY, item.optString(Constant.CAR_USER_BIRTHDAY));//用户生日
                                    map.put(Constant.CAR_USER_CITY, item.optString(Constant.CAR_USER_CITY));//用户所在城市
                                    map.put(Constant.CAR_USER_LONGITUDEX, item.optString(Constant.CAR_USER_LONGITUDEX));//用户经度
                                    map.put(Constant.CAR_USER_LATITUDEY, item.optString(Constant.CAR_USER_LATITUDEY));//用户纬度
                                    map.put(Constant.CAR_USER_IDENTITY, item.optString(Constant.CAR_USER_IDENTITY));//
                                    map.put(Constant.CAR_ISFRIEND, item.optString(Constant.CAR_ISFRIEND));//
                                    map.put("dis", item.optString("distance"));//
                                    map.put("age", item.optString("age"));//
                                    userList.add(map);
                                }
                                mapList.addAll(userList);
                                listAdapter = new ListAdapter();
                                listView.setAdapter(listAdapter);
//                                filterUserList();//10公里以内的用户
                            }

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

    //获取所有用户异步请求
    private void startAllUserTask() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startAllUserTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.FRIEND_USERLIST, "userlist", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "startAllUserTask e---->" + e);
//                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startAllUserTask response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));
                            JSONArray rowJSONArray = jsonObject.optJSONArray(Constant.CAR_ROWS);
                            if (rowJSONArray != null) {
                                userList = new ArrayList<Map<String, Object>>();
                                int length = rowJSONArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject item = rowJSONArray.optJSONObject(i);
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put(Constant.CAR_USER_AVATAR, GlobalValues.IP1 + item.optString(Constant.CAR_USER_AVATAR));//头像
                                    map.put(Constant.CAR_USER_FULLNAME, item.optString(Constant.CAR_USER_FULLNAME));//用户名
                                    map.put(Constant.CAR_USER_ID, item.optString(Constant.CAR_USER_ID));//用户ID
                                    map.put(Constant.CAR_USER_SEX, item.optString(Constant.CAR_USER_SEX));//用户性别0男1女
                                    map.put(Constant.CAR_USER_BIRTHDAY, item.optString(Constant.CAR_USER_BIRTHDAY));//用户生日
                                    map.put(Constant.CAR_USER_CITY, item.optString(Constant.CAR_USER_CITY));//用户所在城市
                                    map.put(Constant.CAR_USER_LONGITUDEX, item.optString(Constant.CAR_USER_LONGITUDEX));//用户经度
                                    map.put(Constant.CAR_USER_LATITUDEY, item.optString(Constant.CAR_USER_LATITUDEY));//用户纬度
                                    map.put(Constant.CAR_USER_IDENTITY, item.optString(Constant.CAR_USER_IDENTITY));//
                                    userList.add(map);
                                }
                                filterUserList();//10公里以内的用户
                            }

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

    //小于10公里的用户
    private void filterUserList() {
        int size = userList.size();
        filterUserList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Map<String, Object> map = userList.get(i);
            String lat = MapUtil.getString(map, Constant.CAR_USER_LATITUDEY);
            String lon = MapUtil.getString(map, Constant.CAR_USER_LONGITUDEX);
            if (StringUtil.isValid(lat) && StringUtil.isValid(lon)) {
                filterUserList.add(map);
            }
        }
        int filterSize = filterUserList.size();
        tenUserList = new ArrayList<>();
        for (int i = 0; i < filterSize; i++) {
            Map<String, Object> map = filterUserList.get(i);
            String lat = MapUtil.getString(map, Constant.CAR_USER_LATITUDEY);
            String lon = MapUtil.getString(map, Constant.CAR_USER_LONGITUDEX);
            double dLat = DataUtil.getDouble(lat, 0);
            double dLon = DataUtil.getDouble(lon, 0);
            LatLng filterLatLng = new LatLng(dLat, dLon);
            String mLat = App.app.getData(UserPreferences.USER_LATITUDEY);
            String mLon = App.app.getData(UserPreferences.USER_LONGITUDEX);
            double mdLat = DataUtil.getDouble(mLat, 0);
            double mdLon = DataUtil.getDouble(mLon, 0);
            LatLng mLatLng = new LatLng(mdLat, mdLon);
            double ten = DistanceUtil.getDistance(mLatLng, filterLatLng);
            double tenAbs = Math.abs(ten);
            Log.e(TAG, "tenAbs----->" + tenAbs);
            if (tenAbs > 1000) {
                map.put(DISTANCE, String.format("%.2f", tenAbs / 1000) + "公里");
            } else {
                map.put(DISTANCE, String.format("%.2f", tenAbs) + "米");
            }
            if (tenAbs <= 10000) {
                tenUserList.add(map);
            }
        }
        Log.e(TAG, "tenUserList.size():" + tenUserList.size());
        int tenSize = tenUserList.size();
        mapList = new ArrayList<>();
        for (int i = 0; i < tenSize; i++) {
            Map item = tenUserList.get(i);
            String userId = MapUtil.getString(item, Constant.CAR_USER_ID);
            if (!userId.equals(App.app.getData(UserPreferences.USER_ID))) {
                Map map = new HashMap();
                map.put(Constant.CAR_USER_ID, MapUtil.getString(item, Constant.CAR_USER_ID));//用户id
                map.put(Constant.CAR_USER_AVATAR, MapUtil.getString(item, Constant.CAR_USER_AVATAR));//头像
                map.put(Constant.CAR_USER_FULLNAME, MapUtil.getString(item, Constant.CAR_USER_FULLNAME));//姓名
                String identity = MapUtil.getString(item, Constant.CAR_USER_IDENTITY);
                boolean b = "0".equals(identity) ? false : true;//0会员1评估师
                map.put(IDENTITY, b);//是否平台已认证
                map.put(Constant.CAR_USER_SEX, MapUtil.getString(item, Constant.CAR_USER_SEX));//0男1女
                String birthday = MapUtil.getString(item, Constant.CAR_USER_BIRTHDAY);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                int cYear = calendar.get(Calendar.YEAR);
                Calendar uCalendar = Calendar.getInstance();
                uCalendar.setTime(DateUtil.getDate(birthday, "yyyy-MM-dd"));
                int uYear = uCalendar.get(Calendar.YEAR);

                map.put(AGE, Math.abs(cYear - uYear + 1) + "");//年龄
                map.put(Constant.CAR_USER_CITY, MapUtil.getString(item, Constant.CAR_USER_CITY));//所在城市
                map.put(DISTANCE, MapUtil.getString(item, DISTANCE));//所在距离
                mapList.add(map);
            }
        }
        listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);
    }

    //添加好友异步请求
    private void startAddToMyFriendTask(String userId) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_TOUSER, userId);
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));
        Log.e(TAG, "params----->" + params);
        MyOkHttp.postMap(GlobalValues.ADDFRIEND, 2, "addFriend", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startAddToMyFriendTask response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
                            startFriendListTask("null", 0, 0, "null", "null");
                            EventBus.getDefault().post(new Object());
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
}
