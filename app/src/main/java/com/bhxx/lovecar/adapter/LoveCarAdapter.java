package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.MarkActivity;
import com.bhxx.lovecar.activity.WriteCarinfoActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.utils.LoadImage;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by @dpy on 2016/12/2.
 * 我的-爱车适配器
 *
 * @qq289513149.
 */
public class LoveCarAdapter extends CommonAdapter<CarModel> {
    private Context context;

    public LoveCarAdapter(List<CarModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
        this.context = context;
    }

    @Override
    public void convert(ViewHolder holder, final CarModel data) {

        if (!TextUtils.isEmpty(data.getCarName())) {
            holder.setText(R.id.tvCarName, data.getCarName());
        }
        if (!TextUtils.isEmpty(data.getCarLicenseTime())) {
            holder.setText(R.id.tvCarOther, data.getCarLicenseTime() + " | " + data.getKmNumber() + "公里 | " + data.getCarAddress());
        }
        if (!TextUtils.isEmpty(data.getCarImg())) {
            ImageLoader.getInstance().displayImage(data.getCarImg(), (ImageView) holder.getView(R.id.ivCarImg), LoadImage.getDefaultOptions());
        }
        String isAssess = data.getIsAssess();//0估价的1未估价
        if ("0".equals(isAssess)) {
            if (!TextUtils.isEmpty(data.getAssessPice() + "") && !"null".equals(data.getAssessPice() + "")) {
                holder.setText(R.id.tvCarPrice, data.getAssessPice() + "万");
            }
        } else {
            holder.setText(R.id.tvCarPrice, "等待估价");
        }
//        String isPublish = data.getIsPublish();
//        if ("1".equals(isPublish)&&"0".equals(isAssess)) {//已经发布了
//            holder.setVisibility(R.id.)
//        }else{//没发布
//
//        }

        holder.setOnClickListener(R.id.ivEdit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteCarinfoActivity.start(context, data);
            }
        });
    }
}
