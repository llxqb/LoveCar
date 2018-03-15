package com.bhxx.lovecar.activity;
/**
 * 选择车辆品牌
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.bhxx.lovecar.entity.CarTypeentity;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.SQLCar;
import com.bhxx.lovecar.utils.Trans2PinYin;
import com.bhxx.lovecar.views.FastLocationBarView;
import com.bhxx.lovecar.views.MyGridView;
import com.bhxx.lovecar.views.PinnedHeaderListView;
import com.bhxx.lovecar.views.SectionedBaseAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@InjectLayer(R.layout.activity_car_type)
public class CarTypeActivity extends BasicActivity {

    //    private final String[] cartypeRight = {"奥迪A1", "奥迪A2", "奥迪A3", "奥迪A4", "奥迪A5", "奥迪A6", "奥迪A7"};
    private CitySectionedAdapter sectionedAdapter;
    private List<String> headList = new ArrayList<String>();
    private List carList = new ArrayList();

    private SQLiteDatabase db;
    private Cursor cursor;
    private List secondlist;
    private ArrayList thridlist;
    private String name1, name2;
    private String tablayout;//记录选车还是估车
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView cartype_back;
        PinnedHeaderListView pinnedListView;
        FastLocationBarView sidebar;
        ListView listview_right;
        LinearLayout layout_right;
        TextView tv_right;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView icon_aodi, icon_baoma, icon_biek, icon_byadi, icon_dazhong, icon_fute, icon_qirui, icon_xuefl;
        LinearLayout hotcar_layout;
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.cartype_back:
                finish();
                break;
            case R.id.icon_aodi:
                name1 = "奥迪";
                selectHotCar(name1);
                break;
            case R.id.icon_baoma:
                name1 = "宝马";
                selectHotCar(name1);
                break;
            case R.id.icon_biek:
                name1 = "别克";
                selectHotCar(name1);
                break;
            case R.id.icon_byadi:
                name1 = "比亚迪";
                selectHotCar(name1);
                break;
            case R.id.icon_dazhong:
                name1 = "大众";
                selectHotCar(name1);
                break;
            case R.id.icon_fute:
                name1 = "福特";
                selectHotCar(name1);
                break;
            case R.id.icon_qirui:
                name1 = "奇瑞";
                selectHotCar(name1);
                break;
            case R.id.icon_xuefl:
                name1 = "雪佛兰";
                selectHotCar(name1);
                break;
        }
    }

    private void selectHotCar(String name) {
        secondlist = selectSecondlist(name);
//        v.sidebar.setVisibility(View.GONE);
//        v.layout_right.setVisibility(View.VISIBLE);
//        v.tv_right.setText(name);
//        v.listview_right.setAdapter(new ArrayAdapter<String>(CarTypeActivity.this, R.layout.dialog_list_item_text, secondlist));

        showDialog1(name1, secondlist);

    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        tablayout = getIntent().getStringExtra("tablayout");
        initEvent();
        sectionedAdapter = new CitySectionedAdapter(getHeadList(), getCarList());
        v.pinnedListView.setAdapter(sectionedAdapter);


        v.pinnedListView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
                name1 = String.valueOf(view.getTag());

                secondlist = selectSecondlist(name1);
                v.sidebar.setVisibility(View.GONE);
                v.layout_right.setVisibility(View.VISIBLE);
                v.tv_right.setText(name1);
                v.listview_right.setAdapter(new ArrayAdapter<String>(CarTypeActivity.this, R.layout.dialog_list_item_text, secondlist));

            }

            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
            }
        });
        v.listview_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                name2 = name1 + " " + secondlist.get(position).toString();

                if (!TextUtils.isEmpty(tablayout)) {
                    EventBus.getDefault().post(new CarTypeentity(name2,"selectcar"));
                    finish();
                } else {
                    thridlist = selectThridlist(secondlist.get(position).toString());
                    if (thridlist.toString().equals("[]")) {
                        thridlist = new ArrayList();
                        thridlist.add(name2);
                    }

                    Intent intent = new Intent(CarTypeActivity.this, CarTypeThridActivity.class);
                    intent.putExtra("name2", name2);
                    intent.putCharSequenceArrayListExtra("thridlist", thridlist);
                    startActivity(intent);
                    finish();
                }
            }
        });


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
            ImageView brandImg = null;
            String brandName;
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.mine_car_item, null);
            } else {
                layout = (LinearLayout) convertView;
            }
            layout.setTag(cityMap.get(section).get(position));
            ((TextView) layout.findViewById(R.id.textItem)).setText(cityMap.get(section).get(position).toString());
            brandImg = (ImageView) layout.findViewById(R.id.brandImg);

//            brandImg.setImageResource(getResource("ac_schnitzer"));

            brandName = Trans2PinYin.getInstance().convertAll(cityMap.get(section).get(position).toString());

            int resId = getResource(brandName.toLowerCase().replaceAll(" ", "_"));//变小写  替换空格
            if (resId == -1) {
                brandImg.setImageResource(R.mipmap.icon_brand);
            } else {
                brandImg.setImageResource(resId);
            }

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

            TextView textItem = ((TextView) layout.findViewById(R.id.textItem));
            textItem.setText(headList.get(section));
            textItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vs) {
                    v.sidebar.setVisibility(View.VISIBLE);
                    v.layout_right.setVisibility(View.GONE);
                }
            });
            if (headList.get(section).equals("A")) {
                v.hotcar_layout.setVisibility(View.VISIBLE);
            } else {
                v.hotcar_layout.setVisibility(View.GONE);
            }

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
        headList.add("A");
        headList.add("B");
        headList.add("C");

        headList.add("D");
//        headList.add("E");
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

        selectFirstlist("A");
        selectFirstlist("B");
        selectFirstlist("C");

        selectFirstlist("D");
//        selectFirstlist("E");
        selectFirstlist("F");
        selectFirstlist("G");

        selectFirstlist("H");
        selectFirstlist("J");
        selectFirstlist("K");
        selectFirstlist("L");
        selectFirstlist("M");
        selectFirstlist("N");

        selectFirstlist("P");
        selectFirstlist("Q");
        selectFirstlist("R");
        selectFirstlist("S");
        selectFirstlist("T");

        selectFirstlist("W");
        selectFirstlist("X");
        selectFirstlist("Y");
        selectFirstlist("Z");

        return carList;
    }

    /**
     * 查询第一层集合
     *
     * @param firstword
     */
    private void selectFirstlist(String firstword) {
        List lists = new ArrayList();
        Set<String> set = searchfirstcar(firstword);
        for (String str : set) {
            lists.add(str);
        }
        carList.add(lists);
    }

    /**
     * 查询第二层集合  （根据品牌查询）
     *
     * @param secondword
     */
    private List selectSecondlist(String secondword) {
        List lists = new ArrayList();
        Set<String> set = searchsecondcar(secondword);
        for (String str : set) {
            lists.add(str);
        }
        return lists;
    }

    /**
     * 查询第三层集合  （根据品牌查询）
     *
     * @param thridword
     */
    private ArrayList selectThridlist(String thridword) {
        ArrayList lists = new ArrayList();
        Set<String> set = searchThridcar(thridword);
        if (set.size() > 0) {
            for (String str : set) {
                lists.add(str);
            }
        }
        return lists;
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


    private Set searchfirstcar(String firstword) {
        Set set = new HashSet();
        //打开数据库输出流
        SQLCar s = new SQLCar();
        db = s.openDatabase(getApplicationContext());
        //查询数据库中testid=1的数据
        cursor = db.rawQuery("select * from acpg_car_config where initial=" + "'" + firstword + "'", null);
        String name = null;

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("brand"));
            set.add(name);
        }
        cursor.close();
        return set;
    }

    private Set searchsecondcar(String secondword) {
        Set set = new HashSet();
        //打开数据库输出流
        SQLCar s = new SQLCar();
        db = s.openDatabase(getApplicationContext());
        //查询数据库中testid=1的数据
        cursor = db.rawQuery("select * from acpg_car_config where brand=" + "'" + secondword + "'", null);
        String name = null;

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("car_x"));
            set.add(name);
        }
        cursor.close();
        return set;
    }

    private Set searchThridcar(String thridword) {
        Set set = new HashSet();
        //打开数据库输出流
        SQLCar s = new SQLCar();
        db = s.openDatabase(getApplicationContext());
        //查询数据库中testid=1的数据
        cursor = db.rawQuery("select * from acpg_car_config where car_x=" + "'" + thridword + "'", null);
        String name = null;

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex("car_name"));
            set.add(name);
        }
        cursor.close();
        return set;
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName
     * @return
     */
    public int getResource(String imageName) {
        try {
            Field field = R.mipmap.class.getDeclaredField(imageName);
            field.setAccessible(true);
            int id = field.getInt(field.getName());
            return id;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return -1;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void showDialog1(String name, final List list) {
        // 获取Dialog布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_listview_item_text, null);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth((int) (display.getWidth() * 0.7));
        view.setMinimumHeight((int) (display.getHeight() * 0.7));

        // 获取自定义Dialog布局中的控件
        ListView selectcar_dialog_listview = (ListView) view.findViewById(R.id.dialog_item_listview);
        TextView carName = (TextView) view.findViewById(R.id.carName);
        selectcar_dialog_listview.setAdapter(new ArrayAdapter<String>(this, R.layout.dialog_list_item_text, list));

        // 定义Dialog布局和参数
        final Dialog dialog = new Dialog(CarTypeActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.RIGHT | Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        dialog.show();

        selectcar_dialog_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                name2 = name1 + " " + list.get(position).toString();

                if (!TextUtils.isEmpty(tablayout)) {
                    EventBus.getDefault().post(new CarTypeentity(name2,"selectcar"));
                    finish();
                } else {
                    thridlist = selectThridlist(list.get(position).toString());
                    if (thridlist.toString().equals("[]")) {
                        thridlist = new ArrayList();
                        thridlist.add(name2);
                    }

                    Intent intent = new Intent(CarTypeActivity.this, CarTypeThridActivity.class);
                    intent.putExtra("name2", name2);
                    intent.putCharSequenceArrayListExtra("thridlist", thridlist);
                    startActivity(intent);
                    finish();
                }

            }
        });

        carName.setText(name);

    }
}
