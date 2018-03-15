package com.bhxx.lovecar.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.javase.lang.StringUtil;
import com.makeapp.javase.util.MapUtil;

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
 * Created by @dpy on 2016/12/6.
 * 我的-估价师个人中心-我的服务
 *
 * @qq289513149.
 */

public class MineOrderListFragment extends BaseFragment {

    private static final String TAG = MineOrderListFragment.class.getSimpleName();
    private static final String CAR_TYPE = "carTypeName";
    private static final String CAR_PRICE = "servicePrice";
    private static final String CAR_TIMES = "assessTimes";
    private static final String CAR_GOOD_COMMENT = "starts";
    private View rootView;
    private ListView mListView;
    private ListAdapter listAdapter;
    private Activity mActivity;
    private List<Map<String, Object>> mapList;//订单数据
    private LayoutInflater layoutInflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_gjs_info_common_list, null);
        mActivity = getActivity();
        layoutInflater = LayoutInflater.from(mActivity);
        initView();
        initEvent();
        startMineOrdersListTask();
        return rootView;
    }

    //初始化界面控件
    private void initView() {
        mListView = (ListView) rootView.findViewById(R.id.listView);
    }

    //初始化控件事件
    private void initEvent() {
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                OptionsPickerView pickerView = new OptionsPickerView(mActivity);
//                String[] strings = mActivity.getResources().getStringArray(R.array.gjs_op);
//                final ArrayList<String> options1Items = new ArrayList<String>();
//                for (int j = 0; j < strings.length; j++) {
//                    options1Items.add(strings[j]);
//                }
//                pickerView.setPicker(options1Items);
//                pickerView.setTitle(getResources().getString(R.string.mine_hint_education));
//                pickerView.setCyclic(false);
//                pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//                    @Override
//                    public void onOptionsSelect(int options1, int option2, int options3) {
//                        String op = options1Items.get(options1);
//                        Log.e(TAG, "op-->" + op);
//
//                    }
//                });
//                pickerView.show();
//            }
//        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                //如果这条服务仅仅只是添加的服务，有三种操作
                builder.setItems(R.array.gjs_op, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Log.e(TAG, "0");
                        } else if (which == 1) {
                            Log.e(TAG, "1");
                        } else if (which == 2) {
                            Log.e(TAG, "2");
                        }
                    }
                });
//                AlertDialog dialog = builder.show();
                //已经发布的服务只有一个操作
//                builder.setItems(R.array.gjs_del, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) {
//                            Log.e(TAG, "0");
//                        } else if (which == 1) {
//                            Log.e(TAG, "1");
//                        }
//                    }
//                });
                builder.show();
                return false;
            }
        });
    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }

    class ListAdapter extends BaseAdapter {

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
            final Map<String, Object> map = mapList.get(i);
            View root = layoutInflater.inflate(R.layout.layout_gjs_info_mine_order_item, null);
            TextView typeTextView = (TextView) root.findViewById(R.id.tvCarType);
            typeTextView.setText(MapUtil.getString(map, CAR_TYPE));
            TextView priceTextView = (TextView) root.findViewById(R.id.tvCarPrice);
            priceTextView.setText("¥" + MapUtil.getString(map, CAR_PRICE));
            TextView timesTextView = (TextView) root.findViewById(R.id.tvTimes);
            String times = MapUtil.getString(map, CAR_TIMES);
            if (StringUtil.isValid(times)) {
                String str = getResources().getString(R.string.op_gj_times);
                String content = String.format(str, times);
                int end = content.indexOf(getResources().getString(R.string.op_gj_times_end));
                SpannableString spanString = new SpannableString(content);
                ForegroundColorSpan front = new ForegroundColorSpan(getResources().getColor(R.color.text_6666));
                spanString.setSpan(front, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan middle = new ForegroundColorSpan(getResources().getColor(R.color.orange_normal));
                spanString.setSpan(middle, 4, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan last = new ForegroundColorSpan(getResources().getColor(R.color.text_6666));
                spanString.setSpan(last, end, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                timesTextView.setText(spanString);
            }
            TextView goodCommentTextView = (TextView) root.findViewById(R.id.tvGoodComment);
            goodCommentTextView.setText(MapUtil.getString(map, CAR_GOOD_COMMENT));
            return root;
        }
    }

    //获取我的服务的异步请求
    private void startMineOrdersListTask() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ASSESSID, App.app.getData(UserPreferences.USER_ASSESSID));
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));
        Log.e(TAG, "startMineOrdersListTask params--->" + params);
        MyOkHttp.postMap(GlobalValues.ASSESSSERVICE_MYLIST, "list", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "startMineOrdersListTask e--->" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startMineOrdersListTask response--->" + response);
                mapList = new ArrayList<Map<String, Object>>();
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
                                    String type = item.optString(CAR_TYPE);
                                    if (StringUtil.isValid(type) && !"null".equals(type)) {
                                        map.put(CAR_TYPE, type);
                                    } else {
                                        map.put(CAR_TYPE, "");
                                    }
                                    String price = item.optString(CAR_PRICE);
                                    if (StringUtil.isValid(price) && !"null".equals(price)) {
                                        map.put(CAR_PRICE, price);
                                    } else {
                                        map.put(CAR_PRICE, "");
                                    }
                                    String times = item.optString(CAR_TIMES);
                                    if (StringUtil.isValid(times) && !"null".equals(times)) {
                                        map.put(CAR_TIMES, times);
                                    } else {
//                                        map.put(CAR_TIMES, DataUtil.getInt(Math.floor(Math.random() * 1000), 111) + "");
                                        map.put(CAR_TIMES, "0");
                                    }
                                    String goodComment = item.optString(CAR_GOOD_COMMENT);
                                    if (StringUtil.isValid(goodComment) && !"null".equals(goodComment)) {
                                        map.put(CAR_GOOD_COMMENT, goodComment);
                                    } else {
                                        map.put(CAR_GOOD_COMMENT, "");
                                    }
                                    mapList.add(map);
                                }
                                if (mapList.size() > 0) {
                                    listAdapter = new ListAdapter();
                                    mListView.setAdapter(listAdapter);
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
}
