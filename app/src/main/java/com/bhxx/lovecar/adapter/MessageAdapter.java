package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.makeapp.javase.util.MapUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by @dpy on 2016/12/5.
 * 我的-消息公共适配器
 *
 * @qq289513149.
 */

public class MessageAdapter extends BaseAdapter {
    public static final String TAG = MessageAdapter.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private Context mContext;
    private List<Map> mapList;

    public MessageAdapter(List<Map> maps, Context mContext) {
        this.mapList = maps;
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
        View root = layoutInflater.inflate(R.layout.layout_message_item, null);
        TextView content = (TextView) root.findViewById(R.id.tvContent);
        TextView time = (TextView) root.findViewById(R.id.tvTime);
        TextView detail = (TextView) root.findViewById(R.id.tvDetail);
        content.setText(MapUtil.getString(map, "content"));
        time.setText(MapUtil.getString(map, "time"));
        detail.setText(MapUtil.getString(map, "detail"));
        return root;
    }
}
