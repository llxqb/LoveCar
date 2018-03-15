package com.bhxx.lovecar.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.makeapp.javase.util.MapUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by @dpy on 2016/12/6.
 *
 * @qq289513149.
 */

public class CYQFJCYFragment extends BaseFragment {
    private View rootView;
    private ListView listView;
    private FJCYAdapter fjcyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_cyq_fjcy, null);
        initView();
        initEvent();
        List<Map> mapList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map map = new HashMap();
            map.put("name", "小红" + i);
            map.put("content", "韩国特别调查组的相关资料数量将超过载重1吨的卡车装载量.");
            map.put("distance", Math.floor(Math.random() * 100) + "m");
            map.put("time", Math.floor(Math.random() * 100) + "分钟前");
            mapList.add(map);
        }
        fjcyAdapter = new FJCYAdapter(mapList, getActivity());
        listView.setAdapter(fjcyAdapter);
        return rootView;
    }

    //初始化控件
    private void initView() {
        listView = (ListView) rootView.findViewById(R.id.listView);
    }

    //初始化控件事件
    private void initEvent() {

    }

    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }

    class FJCYAdapter extends BaseAdapter {

        private List<Map> mapList;
        private LayoutInflater layoutInflater;
        private Context mContext;

        public FJCYAdapter(List<Map> mapList, Context mContext) {
            this.mapList = mapList;
            this.mContext = mContext;
            layoutInflater = LayoutInflater.from(mContext);
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
            View root = layoutInflater.inflate(R.layout.layout_cyq_friend_fjcy_item, null);
            TextView name = (TextView) root.findViewById(R.id.tvUserName);
            name.setText(MapUtil.getString(map, "name", "xiaoming"));
            TextView content = (TextView) root.findViewById(R.id.tvContent);
            content.setText(MapUtil.getString(map, "content", "第三方的方式第三方的方式"));
            TextView distance = (TextView) root.findViewById(R.id.tvDistance);
            distance.setText(MapUtil.getString(map, "distance", "110km"));
            TextView time = (TextView) root.findViewById(R.id.tvTime);
            time.setText(MapUtil.getString(map, "time", "1小时前"));

            return root;
        }
    }
}
