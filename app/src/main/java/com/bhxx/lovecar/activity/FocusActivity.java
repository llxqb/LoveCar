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
import android.widget.ListView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.makeapp.javase.lang.StringUtil;
import com.makeapp.javase.util.MapUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-关注
 *
 * @qq289513149.
 */

public class FocusActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = FocusActivity.class.getSimpleName();
    private ListView myListView;
    private FocusAdapter focusAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        initView();
        initEvent();

        startFocusTask();
    }

    public void onBackClick(View view) {
        finish();
    }

    //初始化界面控件
    private void initView() {
        myListView = (ListView) this.findViewById(R.id.listView);
    }

    //初始化事件
    private void initEvent() {
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map map = (Map) view.getTag();
                if (null != map) {
                    String name = MapUtil.getString(map, "name");
                    Intent intent = new Intent(FocusActivity.this, FocusDetailActivity.class);
                    intent.putExtra(FocusDetailActivity.TITLE, name);
                    intent.putExtra(FocusDetailActivity.OBJECTID, MapUtil.getString(map,Constant.CAR_OBJECTID));
                    startActivity(intent);
                }
            }
        });
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

    class FocusAdapter extends BaseAdapter {

        private List<Map> mapList;
        private Context context;
        private LayoutInflater layoutInflater;

        public FocusAdapter(List<Map> mapList, Context context) {
            this.mapList = mapList;
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
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
            Map<String, Object> map = mapList.get(i);
            View root = layoutInflater.inflate(R.layout.layout_focus_item, null);
            if (map != null) {
                CircleImageView circleImageView = (CircleImageView) root.findViewById(R.id.civUserHead);
                TextView textView = (TextView) root.findViewById(R.id.tvUserName);
                String avatar = MapUtil.getString(map, Constant.CAR_USER_AVATAR, "");
                if (StringUtil.isValid(avatar) && !"null".equals(avatar)) {
                    ImageLoader.getInstance().displayImage(GlobalValues.IP1 + avatar, circleImageView, LoadImage.getDefaultOptions());
                }
                final String name = MapUtil.getString(map, Constant.CAR_USER_FULLNAME, "");
                textView.setText(name);
                TextView cancel = (TextView) root.findViewById(R.id.tvCancelFocus);
                textView.setText(name);
                final String objectId = MapUtil.getString(map, Constant.CAR_OBJECTID);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "cancel focus");
                        showDialog("", String.format(context.getResources().getString(R.string.op_cancel_focus), name), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FocusActivity.super.cancelAlertDialog();
                                //取消关注的接口
                                Log.e(TAG, "objectId----->" + objectId + "name----->" + name);
                                startCancelFocusTask(objectId);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FocusActivity.super.cancelAlertDialog();
                            }
                        }, context.getResources().getString(R.string.operation_confirm), context.getResources().getString(R.string.operation_cancel));
                    }
                });
                root.setTag(map);
            }
            return root;
        }
    }

    //关注异步请求
    private void startFocusTask() {
        showProgressDialog(FocusActivity.this, getResources().getString(R.string.operation_focusing));
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_USER_CREATETYPE, "0");//0会员/1动态/2车源

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_USER_CREATETYPE, "0");//0会员/1动态/2车源
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startFocusTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.CARE_LIST, "focus", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                LogUtils.e( "e---->" + e.toString());
//                Log.e(TAG, "e---->" + e);
                dismissProgressDialog();
                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                Log.e(TAG, "response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
                            JSONArray rowJSONArray = jsonObject.optJSONArray(Constant.CAR_ROWS);
                            if (null != rowJSONArray && rowJSONArray.length() > 0) {
                                int size = rowJSONArray.length();
                                List<Map> mList = new ArrayList<>();
                                for (int i = 0; i < size; i++) {
                                    JSONObject object = rowJSONArray.optJSONObject(i);
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put(Constant.CAR_USER_FULLNAME, object.optString(Constant.CAR_USER_FULLNAME));
                                    map.put(Constant.CAR_USER_AVATAR, object.optString(Constant.CAR_USER_AVATAR));
                                    map.put(Constant.CAR_OBJECTID, object.optString(Constant.CAR_OBJECTID));
                                    mList.add(map);
                                }
                                focusAdapter = new FocusAdapter(mList, FocusActivity.this);
                                myListView.setAdapter(focusAdapter);
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

    //取消关注异步请求
    public void startCancelFocusTask(String objectId) {
        showProgressDialog(FocusActivity.this, getResources().getString(R.string.operation_submitting));

        String userId = App.app.getData(UserPreferences.USER_ID);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, userId);
        params.put(Constant.CAR_OBJECTID, objectId);
        params.put(Constant.CAR_USER_CREATETYPE, "0");

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, userId);
        hashMap.put(Constant.CAR_OBJECTID, objectId);
        hashMap.put(Constant.CAR_USER_CREATETYPE, "0");

        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startCancelFocusTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.CARE_DELCARE, "modify", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
                dismissProgressDialog();
                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                Log.e(TAG, "response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
                            startFocusTask();
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
