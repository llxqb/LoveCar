package com.bhxx.lovecar.fragment;
/**
 * 选车 fragment
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.CarTypeActivity;
import com.bhxx.lovecar.adapter.SelectCarSearchAdapter;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.entity.CarTypeentity;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

public class SelectCarFragment extends BaseFragment {

    private List<CarModel> carlist = new ArrayList<>();
    private String[] newscarSort = new String[]{"不限", "最新上架", "里程最少", "价格最低", "价格最高", "车龄最短"};
    private String[] priceSort = new String[]{"不限", "3万以下", "3-5万", "5-7万", "7-9万", "9-12万", "12-16万", "16-20万", "20万以上"};
    private String minPrice, maxPrice, orderBy, carName;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        private LinearLayout sort_newscar_layout, sort_brand_layout, sort_price_layout;
        private PullToRefreshLayout my_pull;
        private PullableListView my_list;
        private LinearLayout tag1_layout, tag2_layout, tag3_layout;
        private TextView tag1_tv, tag2_tv, tag3_tv, tab1_tv;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        private ImageView tag1_img, tag2_img, tag3_img;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selectcar_page, null);
        Handler_Inject.injectFragment(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    protected void init() {
        searchList("", "", "", "");

        v.my_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                v.my_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.my_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                v.my_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                v.my_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }
        });
    }


    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.sort_newscar_layout:
                //最新上架
                showDialog1();
                break;
            case R.id.sort_brand_layout:
                //品牌
                Intent intent = new Intent(getActivity(), CarTypeActivity.class);
                intent.putExtra("tablayout", "ssss");
                startActivity(intent);
                break;
            case R.id.sort_price_layout:
                //价格
                showDialog3();
                break;
            case R.id.tag1_img:
                orderBy = "";
                v.tag1_layout.setVisibility(View.GONE);
                searchList(minPrice, maxPrice, orderBy, carName);
                break;
            case R.id.tag2_img:
                carName = "";
                v.tag2_layout.setVisibility(View.GONE);
                searchList(minPrice, maxPrice, orderBy, carName);
                break;
            case R.id.tag3_img:
                minPrice = "";
                maxPrice = "";
                v.tag3_layout.setVisibility(View.GONE);
                searchList(minPrice, maxPrice, orderBy, carName);
                break;
        }
    }

    protected void onEventMainThread(CarTypeentity entity) {
        carName = entity.getKey1();
        if (entity.getKey2().equals("selectcar")) {
            v.tag2_tv.setText(carName);
            v.tag2_layout.setVisibility(View.VISIBLE);
            searchList(minPrice, maxPrice, orderBy, carName);
        }
    }

    private void showDialog1() {
        // 获取Dialog布局
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_selectcar_newscar, null);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        ListView selectcar_dialog_listview = (ListView) view.findViewById(R.id.selectcar_dialog_listview);
        LinearLayout tablayout2 = (LinearLayout) view.findViewById(R.id.tablayout2);
        LinearLayout tablayout3 = (LinearLayout) view.findViewById(R.id.tablayout3);
        selectcar_dialog_listview.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.dialog_list_item_text, newscarSort));

        // 定义Dialog布局和参数
        final Dialog dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        dialog.show();

        selectcar_dialog_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                v.tab1_tv.setText(newscarSort[position]);
                if (position != 0) {
                    orderBy = position + "";
                    searchList(minPrice, maxPrice, orderBy, carName);
                }
                dialog.dismiss();

            }
        });

        tablayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), CarTypeActivity.class);
                intent.putExtra("tablayout", "ssss");
                startActivity(intent);
            }
        });
        tablayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDialog3();
            }
        });
    }

    private void showDialog3() {
        // 获取Dialog布局
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_selectcar_price, null);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        GridView selectcar_dialog_gridview = (GridView) view.findViewById(R.id.selectcar_dialog_gridview);
        TextView sure = (TextView) view.findViewById(R.id.sure);
        LinearLayout tablayout1 = (LinearLayout) view.findViewById(R.id.tablayout1);
        LinearLayout tablayout2 = (LinearLayout) view.findViewById(R.id.tablayout2);

        final EditText minPrice_et = (EditText) view.findViewById(R.id.minPrice_et);//区间最低
        final EditText maxPrice_et = (EditText) view.findViewById(R.id.maxPrice_et);//区间最高

        selectcar_dialog_gridview.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item_text, priceSort));

        // 定义Dialog布局和参数
        final Dialog dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        dialog.show();

        tablayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showDialog1();
            }
        });
        tablayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), CarTypeActivity.class);
                intent.putExtra("tablayout", "ssss");
                startActivity(intent);
            }
        });
        selectcar_dialog_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        minPrice = "";
                        maxPrice = "";
                        break;
                    case 1:
                        minPrice = "";
                        maxPrice = "3.0";
                        break;
                    case 2:
                        minPrice = "3.0";
                        maxPrice = "5.0";
                        break;
                    case 3:
                        minPrice = "5.0";
                        maxPrice = "7.0";
                        break;
                    case 4:
                        minPrice = "7.0";
                        maxPrice = "9.0";
                        break;
                    case 5:
                        minPrice = "9.0";
                        maxPrice = "12.0";
                        break;
                    case 6:
                        minPrice = "12.0";
                        maxPrice = "16.0";
                        break;
                    case 7:
                        minPrice = "16.0";
                        maxPrice = "20.0";
                        break;
                    case 8:
                        minPrice = "20.0";
                        maxPrice = "";
                        break;
                }
                v.tag3_tv.setText(priceSort[position]);
                v.tag3_layout.setVisibility(View.VISIBLE);
                searchList(minPrice, maxPrice, orderBy, carName);
                dialog.dismiss();
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vs) {
                if (TextUtils.isEmpty(minPrice_et.getText().toString())) {
                    showToast("请输入最低价格");
                    return;
                }
                if (TextUtils.isEmpty(maxPrice_et.getText().toString())) {
                    showToast("请输入最高价格");
                    return;
                }
                v.tag3_tv.setText(minPrice_et.getText().toString() + "-" + maxPrice_et.getText().toString() + "万");
                v.tag3_layout.setVisibility(View.VISIBLE);
                searchList(minPrice_et.getText().toString(), maxPrice_et.getText().toString(), orderBy, carName);
                dialog.dismiss();
            }
        });
    }

    /**
     * 根据条件查询
     */
    private void searchList(String minPrice, String maxPrice, String orderBy, String carName) {
        showProgressDialog(getActivity());
        LogUtils.i("minPrice=" + minPrice + " maxPrice=" + maxPrice + " orderBy=" + orderBy + " carName=" + carName);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(minPrice)) {
            params.put("expectationPriceMin", minPrice);
        }
        if (!TextUtils.isEmpty(maxPrice)) {
            params.put("expectationPriceMax", maxPrice);
        }
        if (!TextUtils.isEmpty(orderBy)) {
            params.put("orderBy", orderBy);
        }
        if (!TextUtils.isEmpty(carName)) {
            params.put("carName", carName);
        }
        params.put("isPublish", "0");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.SELECT_CAR, 1, "price", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
            dismissProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response=" + response);
            dismissProgressDialog();
            CommonListBean<CarModel> listBean = JsonUtils.getBean(response, CommonListBean.class, CarModel.class);
            switch (id) {
                case 1:
                    if (listBean.getResultCode().equals("0000")) {
                        if (listBean.getRows().size() > 0) {
                            SelectCarSearchAdapter selectCarSearchAdapter = new SelectCarSearchAdapter(listBean.getRows(), getActivity(), R.layout.select_car_item);
                            v.my_list.setAdapter(selectCarSearchAdapter);
                        } else {
                            SelectCarSearchAdapter selectCarSearchAdapter = new SelectCarSearchAdapter(listBean.getRows(), getActivity(), R.layout.select_car_item);
                            v.my_list.setAdapter(selectCarSearchAdapter);
                            showToast("此查询条件无数据");
                        }
                    } else {
                        showToast(Constant.ERROR_WEB);
                    }
                    break;
            }

        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
