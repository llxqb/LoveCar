package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.DateUtils;
import com.bhxx.lovecar.views.CircleImageView;
import com.makeapp.javase.util.MapUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by @dpy on 2016/12/9.
 * 我的-估价师个人中心
 *
 * @qq289513149.
 */

public class GJSAuthActivity extends BasicActivity implements View.OnClickListener {

    public static final String TAG = GJSAuthActivity.class.getSimpleName();
    private CircleImageView gjsHeadImageView;//估价师头像
    private TextView gjsNameTextView;//估价师姓名
    private TextView gjsLevelTextView;//估价师级别
    private TextView gjsExpTextView;//估价师经验
    private TextView gjsLocTextView;//估价师位置
    private View releaseView; //发布服务
    private View accountView; //账户明细
    private TextView cOrdersTextView;//当前订单
    private TextView hOrdersTextView;//历史订单
    private ListView mListView;
    private List<Map> mapList;//订单数据
    private OrderAdapter orderAdapter;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gjs_auth);
        initView();
        initEvent();
        inflater = LayoutInflater.from(this);
        cOrdersTextView.setSelected(true);
        mapList = new ArrayList<Map>();
        initData(0);
    }

    //初始化界面控件
    public void initView() {
        gjsHeadImageView = (CircleImageView) this.findViewById(R.id.civGjsHead);
        gjsNameTextView = (TextView) this.findViewById(R.id.tvGjsName);
        gjsLevelTextView = (TextView) this.findViewById(R.id.tvGjsLevel);
        gjsExpTextView = (TextView) this.findViewById(R.id.tvGjsExp);
        gjsLocTextView = (TextView) this.findViewById(R.id.tvGjsLoc);
        releaseView = this.findViewById(R.id.llRelease);
        accountView = this.findViewById(R.id.llAccount);
        cOrdersTextView = (TextView) this.findViewById(R.id.tvCOrders);
        hOrdersTextView = (TextView) this.findViewById(R.id.tvHOrders);
        mListView = (ListView) this.findViewById(R.id.listView);
    }

    //初始化控件事件
    public void initEvent() {
        releaseView.setOnClickListener(this);
        accountView.setOnClickListener(this);
        cOrdersTextView.setOnClickListener(this);
        hOrdersTextView.setOnClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    public void onBackClick(View view) {
        finish();
    }

    //初始数据
    private void initData(int type) {
        for (int i = 0; i < 10; i++) {
            Map map = new HashMap();
            map.put("name", "奔驰C级 2015款 1.6T 自动 C180L");
            map.put("time", DateUtils.format(new Date(), "yyyy-MM-dd   HH:mm"));
            map.put("price", 50 * (i + 1));
            mapList.add(map);
        }
        orderAdapter = new OrderAdapter(type);
        mListView.setAdapter(orderAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llRelease: {//发布服务
                Log.e(TAG, "llRelease");
                startActivity(new Intent(GJSAuthActivity.this, GjsReleaseServicesActivity.class));
            }
            break;
            case R.id.llAccount: {//账户明细
                Log.e(TAG, "llAccount");

            }
            break;
            case R.id.tvCOrders: {//当前订单
                Log.e(TAG, "tvCOrders");
                cOrdersTextView.setSelected(true);
                hOrdersTextView.setSelected(false);
                initData(0);
//                startOrdersTask(0);
            }
            break;
            case R.id.tvHOrders: {//历史订单
                Log.e(TAG, "tvHOrders");
                hOrdersTextView.setSelected(true);
                cOrdersTextView.setSelected(false);
                initData(1);
//                startOrdersTask(1);
            }
            break;
        }
    }

    private void startOrdersTask(int tag) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (tag == 0) {//当前订单
            params.put("ordertype", "0");
        } else {//历史订单
            params.put("ordertype", "0");
        }
    }

    class OrderAdapter extends BaseAdapter {

        private int orderType = 0;//0当前订单，1历史订单

        public OrderAdapter(int orderType) {
            this.orderType = orderType;
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
            Map map = mapList.get(i);
            View root = inflater.inflate(R.layout.layout_gjs_orders_item, null);
            TextView name = (TextView) root.findViewById(R.id.tvName);
            name.setText(MapUtil.getString(map, "name"));
            TextView time = (TextView) root.findViewById(R.id.tvTime);
            time.setText(MapUtil.getString(map, "time"));
            TextView price = (TextView) root.findViewById(R.id.tvPrice);
            price.setText("￥" + MapUtil.getString(map, "price"));
            TextView receive = (TextView) root.findViewById(R.id.tvReceive);
            RatingBar ratingBar = (RatingBar) root.findViewById(R.id.ratingBar);
            if (this.orderType == 0) {
                receive.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);
            } else {
                receive.setVisibility(View.GONE);
                ratingBar.setVisibility(View.VISIBLE);
            }
            return root;
        }
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }
}
