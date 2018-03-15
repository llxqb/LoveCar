package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.CarDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import io.rong.imkit.RongIM;

/**
 * Created by bhxx on 2016/11/25.
 */
public class SelectCarSearchAdapter extends CommonAdapter<CarModel> {
    private String imgUrl;

    public SelectCarSearchAdapter(List<CarModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, final CarModel data) {
        if (data.getAcpgPictures() != null) {
            if (!TextUtils.isEmpty(data.getAcpgPictures().getUrl())) {
                if (data.getAcpgPictures().getUrl().contains(";")) {
                    imgUrl = data.getAcpgPictures().getUrl().split(";")[0];
                } else {
                    imgUrl = data.getAcpgPictures().getUrl();
                }
                ImageLoader.getInstance().displayImage(GlobalValues.IP1 + imgUrl, (ImageView) holder.getView(R.id.home_car_img), LoadImage.getDefaultOptions());
            }
        }
        if (!TextUtils.isEmpty(data.getCarName())) {
            holder.setText(R.id.home_car_name, data.getCarName());
        }
        if (data.getExpectationPrice() != null) {
            holder.setText(R.id.home_car_price, data.getExpectationPrice() + "万");
        }
        holder.setText(R.id.home_car_date, data.getCarLicenseTime() + "年/" + data.getKmNumber() + "万公里");

        holder.setOnClickListener(R.id.home_car_more, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("data.getCarId()=" + data.getCarId());
                setonclickmore(data.getCarId(), true);
                holder.setVisible(R.id.home_car_img, false);
                holder.setVisible(R.id.home_car_more, false);
                holder.setVisible(R.id.home_car_more_lin, true);
            }
        });


        holder.setOnClickListener(R.id.home_car_rel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getonclickmore(data.getCarId())) {
                    holder.setVisible(R.id.home_car_img, true);
                    holder.setVisible(R.id.home_car_more, true);
                    holder.setVisible(R.id.home_car_more_lin, false);
                    setonclickmore(data.getCarId(), false);
                } else {
                    Intent intent = new Intent(context, CarDetailActivity.class);
                    intent.putExtra("carModel", data);
                    context.startActivity(intent);
                }
            }
        });

        holder.setOnClickListener(R.id.home_car_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CarDetailActivity.class);
                intent.putExtra("carModel", data);
                context.startActivity(intent);
            }
        });
        holder.setOnClickListener(R.id.chat, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请登录");
                    return;
                }
                if (App.app.getData(UserPreferences.USER_ID).equals(data.getUserId() + "")) {
                    showToast("请勿与自己聊天");
                    return;
                }
                if (RongIM.getInstance() != null) {
                    if (data.getUserId() != null) {
                        RongIM.getInstance().startPrivateChat(context, "" + data.getUserId() + "", data.getContacts());
                    }
                }
            }
        });
        holder.setOnClickListener(R.id.tel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(data.getMobile())) {
                    Intent call = new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + data.getMobile()));
                    context.startActivity(call);
                } else {
                    showToast("暂无号码");
                }
            }
        });
    }

    private void setonclickmore(int car_id, boolean ismore) {
        for (CarModel carModel : getDataList()) {
            if (carModel.getCarId() == car_id) {
                carModel.setIsmore(ismore);
            }
        }
    }

    private boolean getonclickmore(int car_id) {
        for (CarModel carModel : getDataList()) {
            if (carModel.getCarId() == car_id) {
                return carModel.getismore();
            }
        }
        return false;
    }
}
