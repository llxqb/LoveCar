package com.bhxx.lovecar.activity;
/**
 * 品牌车型的第三层
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.entity.CarTypeentity;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.SQLCar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@InjectLayer(R.layout.activity_car_type_thrid)
public class CarTypeThridActivity extends BasicActivity {

    private ArrayList thridlist;
    private String name2, name3;
    //    private boolean isnull = false;//判断品牌是否为空
    private SQLiteDatabase db;
    private Cursor cursor;
    String price = null;
    String c_level = null;
    String PriceAndLevel;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView cartypethrid_back;
        ListView listview;
        TextView car_title;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        thridlist = getIntent().getParcelableArrayListExtra("thridlist");
        name2 = getIntent().getStringExtra("name2");
        if (!TextUtils.isEmpty(name2)) {
            v.car_title.setText(name2);
        }

        v.listview.setAdapter(new ArrayAdapter<String>(CarTypeThridActivity.this, R.layout.dialog_list_item_text, thridlist));

        v.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name3 = thridlist.get(position).toString();

                PriceAndLevel = searchThridcar(name3);
                String[] aa = PriceAndLevel.split("#");
                price = aa[0];
                c_level = aa[1];

                LogUtils.i("price=" + price + "  c_level=" + c_level);

                if (price.equals("")) {
                    price = "null";
                }
                EventBus.getDefault().post(new CarTypeentity(name3 + "-" + price + "-" + c_level, "addcar"));
                finish();
            }
        });
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.cartypethrid_back:
                finish();
                break;
        }
    }

    private String searchThridcar(String thridword) {
        String price = null;
        String c_level = null;
        Set set = new HashSet();
        //打开数据库输出流
        SQLCar s = new SQLCar();
        db = s.openDatabase(getApplicationContext());
        //查询数据库中testid=1的数据
        cursor = db.rawQuery("select * from acpg_car_config where car_name=" + "'" + thridword + "'", null);

        if (cursor.moveToNext()) {
            price = cursor.getString(cursor.getColumnIndex("factory_price"));
            c_level = cursor.getString(cursor.getColumnIndex("c_level"));
        }
        cursor.close();
        return price + "#" + c_level;
    }
}
