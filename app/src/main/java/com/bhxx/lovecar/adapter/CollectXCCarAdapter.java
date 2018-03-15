package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.CarDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.utils.LoadImage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-收藏 选车适配器
 *
 * @qq289513149.
 */
public class CollectXCCarAdapter extends CommonAdapter<CarModel> {
    public CollectXCCarAdapter(List<CarModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final CarModel data) {

        if (!TextUtils.isEmpty(data.getCarName())) {
            holder.setText(R.id.tvCarName, data.getCarName());
        }
        if (!TextUtils.isEmpty(data.getCarLicenseTime())) {
            holder.setText(R.id.tvTimeKilo, data.getCarLicenseTime() + "/" + data.getKmNumber() + "万里");
        }
        if (data.getAssessPice() > 0) {
            holder.setText(R.id.tvPrice, data.getAssessPice() + "万");
        }
        Log.e("CollectXCCarAdapter", "data.getCarImg():" + data.getCarImg());
        if (!TextUtils.isEmpty(data.getCarImg())) {
            ImageLoader.getInstance().displayImage(data.getCarImg(), (ImageView) holder.getView(R.id.ivCarImg), LoadImage.getDefaultOptions());
        }

        holder.setOnClickListener(R.id.llCollectXC, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CarDetailActivity.class);
                intent.putExtra("carId", data.getCarId() + "");
                context.startActivity(intent);
            }
        });
    }
}
