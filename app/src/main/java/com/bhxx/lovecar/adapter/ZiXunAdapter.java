package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.ZiXunDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by bhxx on 2016/11/28.
 * 资讯适配器  要匹配资讯类的实体 暂时用的Car实体
 */
public class ZiXunAdapter extends CommonAdapter<ZiXunModel> {
    public ZiXunAdapter(List<ZiXunModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final ZiXunModel data) {
        if (!TextUtils.isEmpty(data.getPicture())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getPicture(), (ImageView) holder.getView(R.id.zixun_item_img), LoadImage.getDefaultOptions());
        }

        if (!TextUtils.isEmpty(data.getTitle())) {
            holder.setText(R.id.zixun_item_name, data.getTitle());
        }
        if (!TextUtils.isEmpty(data.getCreateTime())) {
            holder.setText(R.id.zixun_item_date, data.getCreateTime());
        }

//        ZiXunTypeModel ziXunTypeModel =data.getAcpgMessageType();
        if (!TextUtils.isEmpty(data.getTypeName())) {
            holder.setText(R.id.zixun_item_type, data.getTypeName());
        }

        holder.setOnClickListener(R.id.zixun_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ZiXunDetailActivity.class);
//                intent.putExtra("zixunId", data.getMessageId() + "");
                intent.putExtra("zixunModel",data);
                context.startActivity(intent);
            }
        });
    }
}
