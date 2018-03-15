package com.bhxx.lovecar.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.entity.Cityentity;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.SQLCar;
import com.bhxx.lovecar.utils.SQLCity;
import com.bhxx.lovecar.views.FastLocationBarView;
import com.bhxx.lovecar.views.PinnedHeaderListView;
import com.bhxx.lovecar.views.SectionedBaseAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@InjectLayer(R.layout.activity_city_list)
public class CityListActivity extends BasicActivity {

    private String hotCity[] = {"北京", "上海", "广州", "深圳"};
    private CitySectionedAdapter sectionedAdapter;
    private List<String> headList = new ArrayList<String>();
    private List carList = new ArrayList();

    private SQLiteDatabase db;
    private Cursor cursor;
    private String cityName;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView city_back;
        PinnedHeaderListView pinnedListView;
        FastLocationBarView sidebar;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        initCityList();
    }

    private void initCityList() {
        initEvent();
        sectionedAdapter = new CitySectionedAdapter(getHeadList(), getCarList());
        v.pinnedListView.setAdapter(sectionedAdapter);


        v.pinnedListView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
                cityName = String.valueOf(view.getTag());
                EventBus.getDefault().post(new Cityentity(cityName));
                finish();
            }

            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
            }
        });
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.city_back:
                finish();
                break;
        }
    }

    class CitySectionedAdapter extends SectionedBaseAdapter implements PinnedHeaderListView.PinnedSectionedHeaderAdapter, SectionIndexer {

        private List<String> headList = new ArrayList<String>();
        private List<List> cityMap = new ArrayList<List>();

        public CitySectionedAdapter(List<String> headList, List<List> cityMap) {
            this.headList = headList;
            this.cityMap = cityMap;
        }

        @Override
        public Object getItem(int section, int position) {
            return headList.get(position);
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
            List cityList = cityMap.get(section);
            return cityList.size();
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
            layout.setTag(cityMap.get(section).get(position));
            ((TextView) layout.findViewById(R.id.textItem)).setText(cityMap.get(section).get(position).toString());
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

    private List<List> getCarList() {

        //添加热门城市
        addhostList();

        selectlist("A");
        selectlist("B");
        selectlist("C");

        selectlist("D");
        selectlist("E");
        selectlist("F");
        selectlist("G");

        selectlist("H");
        selectlist("J");
        selectlist("K");
        selectlist("L");
        selectlist("M");
        selectlist("N");

        selectlist("P");
        selectlist("Q");
        selectlist("R");
        selectlist("S");
        selectlist("T");

        selectlist("W");
        selectlist("X");
        selectlist("Y");
        selectlist("Z");

        return carList;
    }

    private void addhostList() {
        ArrayList lists = new ArrayList();
        for (int i = 0; i < 4; i++) {
            lists.add(hotCity[i]);
        }
        carList.add(lists);
    }


    private void selectlist(String word) {
        ArrayList lists = new ArrayList();
        Set<String> set = searchCity(word);
        if (set.size() > 0) {
            for (String str : set) {
                lists.add(str);
            }
        }
        carList.add(lists);
    }

    private void initEvent() {
        //设置右侧触摸监听
        v.sidebar.setOnTouchingLetterChangedListener(new FastLocationBarView.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = sectionedAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    v.pinnedListView.setSelection(position);
                }
            }
        });
    }


    private Set searchCity(String word) {
        Set set = new HashSet();
        //打开数据库输出流
        SQLCity s = new SQLCity();
        db = s.openDatabase(getApplicationContext());
        //查询数据库中testid=1的数据
        cursor = db.rawQuery("select * from T_city where NameSort=" + "'" + word + "'", null);
        String name = null;

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("CityName"));
            set.add(name);
        }
        cursor.close();
        return set;
    }

}
