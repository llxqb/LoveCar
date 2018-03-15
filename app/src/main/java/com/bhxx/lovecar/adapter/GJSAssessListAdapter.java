package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.CarDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by bhxx on 2016/12/2.
 */
public class GJSAssessListAdapter extends CommonAdapter<CarModel> {

    private boolean ischeck = false;

    public GJSAssessListAdapter(List<CarModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, final CarModel data) {

        if (data.getAcpgPictures() != null) {
            if (data.getAcpgPictures().getUrl().contains(";")) {
                ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getAcpgPictures().getUrl().split(";")[0], (ImageView) holder.getView(R.id.assess_car_img), LoadImage.getDefaultOptions());
            } else {
                ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getAcpgPictures().getUrl(), (ImageView) holder.getView(R.id.assess_car_img), LoadImage.getDefaultOptions());
            }
        }


        if (!TextUtils.isEmpty(data.getCarName())) {
            holder.setText(R.id.assess_car_name, data.getCarName());
        }
        if (!TextUtils.isEmpty(data.getCarLicenseTime())) {
            holder.setText(R.id.assess_car_license_time, data.getCarLicenseTime());
        }
        if (data.getKmNumber() != null) {
            holder.setText(R.id.assess_car_km, data.getKmNumber() + "万公里");
        }
        if (!TextUtils.isEmpty(data.getCarAddress())) {
            holder.setText(R.id.assess_car_city, data.getCarAddress());
        }

        holder.getView(R.id.assess_car_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CarDetailActivity.class);
                intent.putExtra("carModel", data);
                context.startActivity(intent);
            }
        });

        holder.setOnClickListener(R.id.assess_car_check_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ischeck) {
                    holder.setImageResource(R.id.assess_car_check, R.mipmap.icon_xuanzehui);
                    ischeck = false;
                } else {
                    holder.setImageResource(R.id.assess_car_check, R.mipmap.icon_xuanzehuang);
                    ischeck = true;
                }
            }
        });


    }

}
