package com.bhxx.lovecar.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.CarDetailActivity;
import com.bhxx.lovecar.activity.WriteCarinfoActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by bhxx on 2016/12/2.
 */
public class AssessListAdapter extends CommonAdapter<CarModel> {
    public AssessListAdapter(List<CarModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final CarModel data) {
        if (data.getAcpgPictures() != null) {
            if (data.getAcpgPictures().getUrl().contains(";")) {
//                imageUrl = data.getAcpgPictures().getUrl().split(";")[0];
                ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getAcpgPictures().getUrl().split(";")[0], (ImageView) holder.getView(R.id.assess_car_img), LoadImage.getDefaultOptions());
            } else {
                ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getAcpgPictures().getUrl(), (ImageView) holder.getView(R.id.assess_car_img), LoadImage.getDefaultOptions());
            }
        }

        if (!TextUtils.isEmpty(data.getIsAssess())) {
            if (data.getIsAssess().equals("0")) {
                if (!TextUtils.isEmpty(data.getIsPublish())) {
                    if (data.getIsPublish().equals("0")) {
                        holder.setVisible(R.id.assess_car_publish, false);
                    } else {
                        holder.setVisible(R.id.assess_car_publish, true);
                    }
                } else {
                    holder.setVisible(R.id.assess_car_publish, true);
                }
            } else {
                holder.setVisible(R.id.assess_car_publish, true);
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
        if (data.getAssessPice() != null) {
            holder.setText(R.id.assess_car_assessprice, "估价 " + data.getAssessPice() + "万");
        } else {
            holder.setVisible(R.id.assess_car_publish, false);
        }

        holder.getView(R.id.assess_car_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteCarinfoActivity.start(context, data);
            }
        });
        holder.getView(R.id.assess_car_publish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog(data);
            }
        });
        holder.getView(R.id.assess_car_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CarDetailActivity.class);
                intent.putExtra("carModel", data);
                context.startActivity(intent);
            }
        });

    }

    /**
     * 发布弹窗界面
     */
    private void dialog(final CarModel data) {
        View view = LayoutInflater.from(context).inflate(R.layout.assesscar_publish_btn, null);
        final Dialog log = new Dialog(context, R.style.transparentFrameWindowStyle);
        ImageView publish_car_close = (ImageView) view.findViewById(R.id.publish_car_close);
        TextView publish_car_cancel = (TextView) view.findViewById(R.id.publish_car_cancel);
        TextView publish_car_sure = (TextView) view.findViewById(R.id.publish_car_sure);

        log.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = log.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示位置
        log.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        log.setCanceledOnTouchOutside(false);
        log.setCancelable(false);
        log.show();
        int measureWidth = context.getResources().getDisplayMetrics().widthPixels * 4 / 5;
        window.setLayout(measureWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        publish_car_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
            }
        });
        publish_car_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
            }
        });
        publish_car_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                LogUtils.i("data.getCarId()=" + data.getCarId());
                params.put("carId", data.getCarId() + "");
                params.put("isPublish", "0");
                params.put("sign", SingUtils.getMd5SignMsg(params));
                MyOkHttp.postMap(GlobalValues.ASSESS_LIST_CAR_PUBLISH, "publish", params, new MyStringCallback());
            }
        });
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                showToast(bean.getResultDesc());
                AssessListAdapter.this.notifyDataSetChanged();
            }
        }
    }
}
