package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.GJSDetailActivity;
import com.bhxx.lovecar.activity.GJSWorkDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.GJSModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by bhxx on 2016/11/28.
 */
public class HomeActivitygjsAdapter extends CommonAdapter<GJSModel> {
    public HomeActivitygjsAdapter(List<GJSModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final GJSModel data) {

        if (!TextUtils.isEmpty(data.getAvatar())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getAvatar(), (ImageView) holder.getView(R.id.home_gjs_grid_avatar), LoadImage.getDefaultOptions());
        }

        if (!TextUtils.isEmpty(data.getAssessName())) {
            holder.setText(R.id.home_gjs_grid_name, data.getAssessName());
        }

        if (!TextUtils.isEmpty(data.getGrade())) {
            holder.setRating(R.id.home_gjs_grid_level, Float.parseFloat(data.getGrade()));
        }

        holder.setOnClickListener(R.id.home_gjs_item_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GJSWorkDetailActivity.class);
                intent.putExtra("gjsModel", data);
                context.startActivity(intent);
            }
        });

    }
}
