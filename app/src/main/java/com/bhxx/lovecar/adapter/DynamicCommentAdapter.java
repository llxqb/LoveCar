package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.DynamicCommentModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.utils.DateUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by bhxx on 2016/12/8.
 */
public class DynamicCommentAdapter extends CommonAdapter<DynamicCommentModel>{
    public DynamicCommentAdapter(List<DynamicCommentModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, DynamicCommentModel data) {

        if(!TextUtils.isEmpty(data.getFullName())){
            holder.setText(R.id.username,data.getFullName());
        }
        if(!TextUtils.isEmpty(data.getAvatar())){
            ImageLoader.getInstance().displayImage(GlobalValues.IP1+data.getAvatar(),(CircleImageView)holder.getView(R.id.circleImg), LoadImage.getHeadImgOptions());
        }

        if(!TextUtils.isEmpty(data.getCmContent())){
            holder.setText(R.id.comment_content,data.getCmContent());
        }
        if(!TextUtils.isEmpty(data.getCreateTime())){
            holder.setText(R.id.comment_creat_time, DateUtils.friendly_time(data.getCreateTime()));
        }
        if(!TextUtils.isEmpty(data.getCareCount())){
            holder.setText(R.id.car_num,data.getCareCount());
        }

        holder.setOnClickListener(R.id.care_num_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("已点赞");
                holder.setImageResource(R.id.car_iv,R.mipmap.icon_fabulous_ed);
            }
        });
    }
}
