package com.bhxx.lovecar.adapter;

import android.text.TextUtils;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.common.MultiTypeSupport;
import com.bhxx.lovecar.beans.DynamicModel;
import com.bhxx.lovecar.utils.LogUtils;

public class DynamicItemSupport implements MultiTypeSupport<DynamicModel> {
    private static final int ITEM_TYPE_NO_PIC = 0;
    private static final int ITEM_TYPE_SING_PIC = 1;
    private static final int ITEM_TYPE_DOUBLE_PIC = 2;
    private static final int ITEM_TYPE_LIST_PICS = 3;

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position, DynamicModel data) {
        //获取所有图片  得到数组
        if (data.getAcpgPictures()!=null) {
            String[] pics = data.getAcpgPictures().getUrl().split(";");
            if (pics.length == 1) {
                return ITEM_TYPE_SING_PIC;
            } else if (pics.length == 2) {
                return ITEM_TYPE_DOUBLE_PIC;
            } else if (pics.length > 2) {
                return ITEM_TYPE_LIST_PICS;
            } else {
                return ITEM_TYPE_NO_PIC;
            }
        } else {
            return ITEM_TYPE_NO_PIC;
        }
    }

    @Override
    public int getLayoutId(int viewType) {
        switch (viewType) {
            case ITEM_TYPE_NO_PIC:
                return R.layout.dynamic_no_pic_item;
            case ITEM_TYPE_SING_PIC:
                return R.layout.dynamic_single_pic_item;
            case ITEM_TYPE_DOUBLE_PIC:
                return R.layout.dynamic_double_pic_item;
            case ITEM_TYPE_LIST_PICS:
                return R.layout.dynamic_list_pics_item;
            default:
                break;
        }
        return 0;
    }
}
