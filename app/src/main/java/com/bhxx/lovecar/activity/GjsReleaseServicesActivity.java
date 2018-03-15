package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bigkoo.pickerview.OptionsPickerView;
import com.makeapp.android.util.ToastUtil;
import com.makeapp.javase.lang.StringUtil;
import com.makeapp.javase.util.MapUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/9.
 * 我的-估价师个人中心-发布服务
 *
 * @qq289513149.
 */

public class GjsReleaseServicesActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = GjsReleaseServicesActivity.class.getSimpleName();
    public static final int REQUEST_USERNAME = 0x11;
    public static final int REQUEST_CARTYPE = 0x12;
    private View typeView;
    private TextView typeTextView;//车型
    private View priceView;//价格
    private TextView priceTextView;
    private EditText editText;
    private CheckBox checkBox;
    private String carPrice;
    private String carTypeId;
    private ArrayList<Map<String, Object>> carTypeList = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gjs_release);
        initView();
        initEvent();
        startCarTypeTask();
    }

    //初始化界面控件
    private void initView() {
        typeView = this.findViewById(R.id.llCarType);
        typeTextView = (TextView) this.findViewById(R.id.tvCarType);
        priceView = this.findViewById(R.id.llPrice);
        priceTextView = (TextView) this.findViewById(R.id.tvPrice);
        editText = (EditText) this.findViewById(R.id.etContent);
        checkBox = (CheckBox) this.findViewById(R.id.checkBox);
    }

    //初始化控件事件
    private void initEvent() {
        typeView.setOnClickListener(this);
        priceView.setOnClickListener(this);
    }

    public void onBackClick(View view) {
        String typeName = typeTextView.getText().toString();
        String price = priceTextView.getText().toString();
        String des = editText.getText().toString();
        if (StringUtil.isValid(typeName) || StringUtil.isValid(price) || StringUtil.isValid(des)) {
            showDialog("", getResources().getString(R.string.op_gjs_server_giveup), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GjsReleaseServicesActivity.super.cancelAlertDialog();
                    finish();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GjsReleaseServicesActivity.super.cancelAlertDialog();
                }
            }, getResources().getString(R.string.operation_ok), getResources().getString(R.string.operation_cancel));
            return;
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llCarType: {
                if (carTypeList.size() > 0) {
                    selectedCarTypeName();
                }
            }
            break;
            case R.id.llPrice: {
                String title = getResources().getString(R.string.mine_gjs_release_carprice);
                Intent intent = new Intent(GjsReleaseServicesActivity.this, SmallTextActivity.class);
                intent.putExtra(SmallTextActivity.REQUEST_TITLE, title);
                intent.putExtra(SmallTextActivity.REQUEST_TYPE, true);
                intent.putExtra(SmallTextActivity.REQUEST_CONTENT, priceTextView.getText().toString());
                startActivityForResult(intent, REQUEST_USERNAME);
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_USERNAME && resultCode == SmallTextActivity.RESULT_CONTENT) {
            carPrice = data.getStringExtra(SmallTextActivity.RESPONSE_CONTENT);
            priceTextView.setText(carPrice + getResources().getString(R.string.mine_gjs_re_hint_s_carprice));
        }
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }

    //选择车型
    private void selectedCarTypeName() {
        Log.e(TAG, "carTypeList.size():" + carTypeList.size());
        OptionsPickerView pickerView = new OptionsPickerView(this);
        final ArrayList<String> stringList = new ArrayList<String>();
        int size = carTypeList.size();
        for (int i = 0; i < size; i++) {
            stringList.add(MapUtil.getString(carTypeList.get(i), Constant.CAR_TYPENAME));
        }
        Log.e(TAG, "stringList.size():" + stringList.size());
        pickerView.setPicker(stringList);
        pickerView.setTitle(getResources().getString(R.string.mine_hint_gjs_cartype));
        pickerView.setCyclic(false);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String carName = stringList.get(options1);
                typeTextView.setText(carName);
                carTypeId = MapUtil.getString(carTypeList.get(options1), Constant.CAR_TYPEID);
                Log.e(TAG, "carTypeId-->" + carTypeId + ";carName-->" + carName);
            }
        });
        pickerView.show();
    }

    //车型类别异步请求
    public void startCarTypeTask() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_KEY, sign);
        MyOkHttp.postMap(GlobalValues.CARTYPE_LIST, "cartype", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                carTypeList = new ArrayList<Map<String, Object>>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            JSONArray rowJSONArray = jsonObject.optJSONArray(Constant.CAR_ROWS);
                            if (rowJSONArray != null && rowJSONArray.length() > 0) {
                                int length = rowJSONArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject item = rowJSONArray.optJSONObject(i);
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put(Constant.CAR_TYPEID, item.optString(Constant.CAR_TYPEID));
                                    map.put(Constant.CAR_TYPENAME, item.optString(Constant.CAR_TYPENAME));
                                    carTypeList.add(map);
                                }
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

    //评估师添加服务异步请求
    public void startAddClick(View view) {
        showProgressDialog(GjsReleaseServicesActivity.this, getResources().getString(R.string.operation_adding));
        String typeName = typeTextView.getText().toString();
        if (StringUtil.isInvalid(typeName)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_gjs_cartype));
            return;
        }
        if (StringUtil.isInvalid(carPrice)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_gjs_carprice));
            return;
        }
        String des = editText.getText().toString();
        if (StringUtil.isInvalid(des)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_gjs_cardes));
            return;
        }
        if (!checkBox.isChecked()) {
            ToastUtil.show(this, getResources().getString(R.string.mine_gjs_caragree));
            return;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ASSESSID, App.app.getData(UserPreferences.USER_ASSESSID));
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_CARTYPENAME, typeName);
        params.put(Constant.CAR_TYPEID, carTypeId);
        params.put(Constant.CAR_SERVICEPRICE, carPrice);
        params.put(Constant.CAR_DESCRIPTION, des);

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ASSESSID, App.app.getData(UserPreferences.USER_ASSESSID));
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_CARTYPENAME, typeName);
        hashMap.put(Constant.CAR_TYPEID, carTypeId);
        hashMap.put(Constant.CAR_SERVICEPRICE, carPrice);
        hashMap.put(Constant.CAR_DESCRIPTION, des);
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.ASSESSSERVICE_ADDSERVICE, "addservice", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e----->" + e);
                dismissProgressDialog();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "response----->" + response);
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
                            finish();
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
