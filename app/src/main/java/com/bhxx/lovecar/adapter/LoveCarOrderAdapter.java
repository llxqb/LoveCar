package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.MarkActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.utils.LoadImage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-爱车-订单适配器
 *
 * @qq289513149.
 */
public class LoveCarOrderAdapter extends CommonAdapter<CarModel> {
    private Context context;

    public LoveCarOrderAdapter(List<CarModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
        this.context = context;
    }

    @Override
    public void convert(ViewHolder holder, CarModel data) {

        if (!TextUtils.isEmpty(data.getCarName())) {
            holder.setText(R.id.tvCarName, data.getCarName());
        }
        if (!TextUtils.isEmpty(data.getAssessTime())) {
            holder.setText(R.id.tvCarOther, data.getAssessTime() + " | " + data.getKmNumber() + "公里 | " + data.getCarLicenseAddress());
        }
        if (!TextUtils.isEmpty(data.getCarImg())) {
            ImageLoader.getInstance().displayImage(data.getCarImg(), (ImageView) holder.getView(R.id.ivCarImg), LoadImage.getDefaultOptions());
        }
        holder.setOnClickListener(R.id.btnPJ, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MarkActivity.class));
            }
        });
    }
}
