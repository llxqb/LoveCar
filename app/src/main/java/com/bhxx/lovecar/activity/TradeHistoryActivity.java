package com.bhxx.lovecar.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.DateUtils;
import com.makeapp.android.util.ViewUtil;
import com.makeapp.javase.util.MapUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by @dpy on 2016/12/2.
 * 交易记录
 *
 * @qq289513149.
 */

public class TradeHistoryActivity extends BasicActivity implements View.OnClickListener {

    private static final String TAG = TradeHistoryActivity.class.getSimpleName();

    private ListView mListView;
    private TradeHistoryAdapter historyAdapter;
    private LayoutInflater layoutInflater;
    private List<Map> mapList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tradehistory);
        initView();
        initEvent();
        layoutInflater = LayoutInflater.from(this);
        mapList = new ArrayList<Map>();
        for (int i = 0; i < 10; i++) {
            Map map = new HashMap();
            map.put("content", "A级(进口) 2016款估价");
            map.put("type", "估价费" + i);
            map.put("time", DateUtils.format(new Date(), "yyyy-MM-dd   HH:mm"));
            map.put("price", "+" + 10 * (i + 1));
            mapList.add(map);
        }
        historyAdapter = new TradeHistoryAdapter();
        mListView.setAdapter(historyAdapter);
    }

    //初始化控件
    private void initView() {
        mListView = (ListView) ViewUtil.findViewById(this, R.id.listView);
    }

    //初始化控件事件
    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map map = (Map) view.getTag();
                if (null != map) {

                }
            }
        });
    }

    public void onBackClick(View view) {
        finish();
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

    class TradeHistoryAdapter extends BaseAdapter {

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
            View root = layoutInflater.inflate(R.layout.layout_tradehistory_item, null);
            TextView content = (TextView) root.findViewById(R.id.tvContent);
            TextView type = (TextView) root.findViewById(R.id.tvType);
            TextView time = (TextView) root.findViewById(R.id.tvTime);
            TextView price = (TextView) root.findViewById(R.id.tvPrice);
            content.setText(MapUtil.getString(map, "content"));
            type.setText(MapUtil.getString(map, "type"));
            time.setText(MapUtil.getString(map, "time"));
            price.setText(MapUtil.getString(map, "price"));
            return root;
        }
    }
}
