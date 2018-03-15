package com.bhxx.lovecar.adapter;
/**
 * 资讯右侧item
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.ZiXunActivity;
import com.bhxx.lovecar.activity.ZiXunRightItemActivity;
import com.bhxx.lovecar.beans.ZiXunRightItemBean;
import com.bhxx.lovecar.entity.ObjectEntity;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.LogUtils;

import java.util.List;

public class DragAdapter extends BaseAdapter {
    /**
     * TAG
     */
    private final static String TAG = "DragAdapter";
    /**
     * 是否显示底部的ITEM
     */
    private boolean isItemShow = false;
    private Context context;
    /**
     * 控制的postion
     */
    private int holdPosition;
    /**
     * 是否改变
     */
    private boolean isChanged = false;
    /**
     * 列表数据是否改变
     */
    private boolean isListChanged = false;
    /**
     * 是否可见
     */
    boolean isVisible = true;
    /**
     * 可以拖动的列表（即用户选择的频道列表）
     */
    public List<ZiXunRightItemBean> channelList;
    /**
     * TextView 频道内容
     */
    private TextView item_text;
    private RelativeLayout rl_subscribe;
    private ImageView icon_delete;
    /**
     * 要删除的position
     */
    public int remove_position = -1;
    /**
     * 是否是用户频道
     */
    private boolean isUser = false;
    public static boolean isShowDelete = false;//根据这个变量来判断是否显示删除图标，true是显示，false是不显示
    private int CurrentCheckTitle;

    public DragAdapter(Context context, List<ZiXunRightItemBean> channelList, boolean isUser, int CurrentCheckTitle) {
        this.context = context;
        this.channelList = channelList;
        this.isUser = isUser;
        this.CurrentCheckTitle = CurrentCheckTitle;
        LogUtils.i("CurrentCheckTitle=" + CurrentCheckTitle);
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        ZiXunRightItemActivity.showsure(isShowDelete);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public ZiXunRightItemBean getItem(int position) {
        // TODO Auto-generated method stub
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_mygridview_item, null);
        item_text = (TextView) view.findViewById(R.id.text_item);
        rl_subscribe = (RelativeLayout) view.findViewById(R.id.rl_subscribe);
        icon_delete = (ImageView) view.findViewById(R.id.icon_delete);

        rl_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowDelete) {
                    return;
                } else {
                    EventBus.getDefault().post(new ObjectEntity(position + ""));
                }

            }
        });

        rl_subscribe.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onlongclick();
                return false;
            }
        });


        icon_delete.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);//设置删除按钮是否显示

        icon_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //position为 0，1 的不可以进行任何操作
                if (position != 0) {
                    setRemove(position);
                    remove();

                }
            }
        });

        ZiXunRightItemBean channel = getItem(position);
        if (position == CurrentCheckTitle) {
            item_text.setBackgroundResource(R.drawable.bg_round_corner_com_app_gray);
            item_text.setTextColor(Color.parseColor("#ffffff"));
        }
        item_text.setText(channel.getContent());
        if (isUser) {
            if ((position == 0) || (position == 1)) {
                item_text.setEnabled(false);
            }
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            isChanged = false;
        }
        if (!isVisible && (position == -1 + channelList.size())) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
        }
        if (remove_position == position) {
            item_text.setText("");
        }
        return view;
    }

    /**
     * 添加频道列表
     */
    public void addItem(ZiXunRightItemBean ziXunRightItemBean) {
        channelList.add(ziXunRightItemBean);
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 拖动变更频道排序
     */
    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        ZiXunRightItemBean dragItem = getItem(dragPostion);
        Log.d(TAG, "startPostion=" + dragPostion + ";endPosition=" + dropPostion);
        if (dragPostion < dropPostion) {

            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }
        isChanged = true;
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 获取频道列表
     */
    public List<ZiXunRightItemBean> getChannnelLst() {
        return channelList;
    }

    /**
     * 设置删除的position
     */
    public void setRemove(int position) {
        remove_position = position;
        //删除我的频道同时 增加更多频道的item
        ZiXunRightItemActivity.addOthergv(channelList.get(position));
        notifyDataSetChanged();
    }

    /**
     * 删除频道列表
     */
    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        isListChanged = true;
        notifyDataSetChanged();
    }

    /**
     * 设置频道列表
     */
    public void setListDate(List<ZiXunRightItemBean> list) {
        channelList = list;
    }

    /**
     * 获取是否可见
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 排序是否发生改变
     */
    public boolean isListChanged() {
        return isListChanged;
    }

    /**
     * 设置是否可见
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    /**
     * 显示放下的ITEM
     */
    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }


    private void onlongclick() {
//        if (isShowDelete) {
//            isShowDelete = false;
//        } else {
//            isShowDelete = true;
//        }

        if (isShowDelete == false) {
            isShowDelete = true;
        }
        setIsShowDelete(isShowDelete);

    }

}