package com.bhxx.lovecar.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.Inflater;

import okhttp3.Call;

public class GJSServerCarActivity extends BasicActivity implements View.OnClickListener {

    //    private final String[] CAR = {"不限", "小型车", "紧凑型车", "微型车", "中大型车", "大型车", "中型车", "MPV", "SUV", "微面", "皮卡", "电动车", "其他"};
    private GridView gjs_server_car_gridview;
    private ImageView back;
    List<String> lists = new ArrayList<>();

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gjs_server_car);
        initView();
        initData();
    }

    private void initView() {
        gjs_server_car_gridview = (GridView) findViewById(R.id.gjs_server_car_gridview);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    private void initData() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.GJS_CARTYPE, 0, "carType", params, new MyStringCallback());

//        gjs_server_car_gridview.setAdapter(new ArrayAdapter<String>(GJSServerCarActivity.this, R.layout.item_text, CAR));//ArrayAdapter
//        gjs_server_car_gridview.setAdapter(new MyAdapter());
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) GJSServerCarActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final TextView text;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_text, null);
            }
            text = (TextView) convertView.findViewById(R.id.text);
            text.setText(lists.get(position).toString());
            if (!TextUtils.isEmpty(App.app.getData("gjsServerCarType"))) {
                if (App.app.getData("gjsServerCarType").equals(lists.get(position).toString())) {
                    text.setBackgroundResource(R.drawable.bg_round_corner_com_app_gray);
                    text.setTextColor(Color.parseColor("#ffffff"));
                }
            }


            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text.setBackgroundResource(R.drawable.bg_round_corner_com_app_gray);
                    text.setTextColor(Color.parseColor("#ffffff"));
                    App.app.setData("gjsServerCarType", lists.get(position).toString());
                    showToast(lists.get(position).toString());
                    finish();
                }
            });
            return convertView;
        }
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray rows = jsonObject.getJSONArray("rows");
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject item = rows.getJSONObject(i);
                        String typeName = item.getString("typeName");
                        lists.add(typeName);
                        gjs_server_car_gridview.setAdapter(new MyAdapter());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
