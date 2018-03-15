package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.GJSDetailActivity;
import com.bhxx.lovecar.activity.GJSWorkDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.GJSModel;
import com.bhxx.lovecar.beans.GJSPublishServiceModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by bhxx on 2016/11/29.
 * 估价师适配器
 */
public class GJSAdapter extends CommonAdapter<GJSPublishServiceModel> {
    private String detailType;

    public GJSAdapter(List<GJSPublishServiceModel> dataList, Context context, int layoutId, String detailType) {
        super(dataList, context, layoutId);
        this.detailType = detailType;
    }

    @Override
    public void convert(ViewHolder holder, final GJSPublishServiceModel data) {

        if (!TextUtils.isEmpty(data.getAssessName())) {
            holder.setText(R.id.gjs_name, data.getAssessName());
        }
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getAvatar(), (ImageView) holder.getView(R.id.gjs_avatar), LoadImage.getHeadImgOptions());
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getAvatar(), (ImageView) holder.getView(R.id.gjs_avatar_background), LoadImage.getDefaultOptions());

        if (!TextUtils.isEmpty(data.getGrade())) {
            holder.setRating(R.id.gjs_level, Float.parseFloat(data.getGrade() + 1));
        }
        if (!TextUtils.isEmpty(data.getServiceRegion())) {
            holder.setText(R.id.gjs_address, data.getServiceRegion());
        }

        if (data.getServicePrice() != null) {
            holder.setText(R.id.gjs_assess_price, "￥" + data.getServicePrice());
        }
        if (!TextUtils.isEmpty(data.getCarTypeName())) {
            holder.setText(R.id.gjs_assess_cartype, data.getCarTypeName());
        }
        if (data.getAssessTimes() == null) {
            String number = "已估价" + "<font color='red'>" + 0 + "</font>" + "次";
            holder.setText(R.id.gjs_assess_number, Html.fromHtml(number));
        } else {
            String number = "已估价" + "<font color='red'>" + data.getAssessTimes() + "</font>" + "次";
            holder.setText(R.id.gjs_assess_number, Html.fromHtml(number));
        }

        holder.setOnClickListener(R.id.gjs_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GJSDetailActivity.class);
                intent.putExtra("gjsModel", data);
                context.startActivity(intent);
            }
        });


    }
}
