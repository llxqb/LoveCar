package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.ZiXunDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.utils.LoadImage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-收藏 资讯适配器
 *
 * @qq289513149.
 */
public class CollectZXCarAdapter extends CommonAdapter<ZiXunModel> {
    public CollectZXCarAdapter(List<ZiXunModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final ZiXunModel data) {

        if (!TextUtils.isEmpty(data.getTitle())) {
            holder.setText(R.id.tvCarName, data.getTitle());
        }
        if (!TextUtils.isEmpty(data.getCreateTime())) {
            holder.setText(R.id.tvTime, data.getCreateTime());
        }
        if (!TextUtils.isEmpty(data.getTypeName())) {
            holder.setText(R.id.tvType, data.getTypeName());
        }
        Log.e("CollectZXCarAdapter", "data.getPicture():" + data.getPicture());
        if (!TextUtils.isEmpty(data.getPicture())) {
            ImageLoader.getInstance().displayImage(data.getPicture(), (ImageView) holder.getView(R.id.ivCarImg), LoadImage.getDefaultOptions());
        }
        holder.setOnClickListener(R.id.llCollectZX, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ZiXunDetailActivity.class);
                intent.putExtra("zixunId", data.getMessageId() + "");
                context.startActivity(intent);
            }
        });
    }
}
