package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.DynamicCommentModel;
import com.bhxx.lovecar.utils.DateUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by bhxx on 2016/12/8.
 */
public class gjsDetailCommentAdapter extends CommonAdapter<DynamicCommentModel> {
    public gjsDetailCommentAdapter(List<DynamicCommentModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, DynamicCommentModel data) {

        if (!TextUtils.isEmpty(data.getFullName())) {
            holder.setText(R.id.username, data.getFullName());
        }
        if (!TextUtils.isEmpty(data.getAvatar())) {
            ImageLoader.getInstance().displayImage(data.getAvatar(), (CircleImageView) holder.getView(R.id.circleImg), LoadImage.getHeadImgOptions());
        }

        if (!TextUtils.isEmpty(data.getCmContent())) {
            holder.setText(R.id.comment_content, data.getCmContent());
        }
        if (!TextUtils.isEmpty(data.getCreateTime())) {
            holder.setText(R.id.comment_creat_time, DateUtils.friendly_time(data.getCreateTime()));
        }

        if (!TextUtils.isEmpty(data.getStarNum() + "")) {
            holder.setRating(R.id.gjs_level, data.getStarNum());
        }
    }
}
