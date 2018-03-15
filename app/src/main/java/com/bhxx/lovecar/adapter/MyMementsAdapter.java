package com.bhxx.lovecar.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.MyMomentsDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CircleModel;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.entity.Circleentity;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

/**
 * liuli
 * 我加入的圈子适配器
 */
public class MyMementsAdapter extends CommonAdapter<CircleModel> {
    public MyMementsAdapter(List<CircleModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final CircleModel data) {
        if (!TextUtils.isEmpty(data.getExtend1())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getExtend1(), (ImageView) holder.getView(R.id.circleImg), LoadImage.getDefaultOptions());
        }
        if (!TextUtils.isEmpty(data.getName())) {
            holder.setText(R.id.circleName, data.getName());
        }

        holder.setOnClickListener(R.id.my_moments_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMomentsDetailActivity.start(context, data);
            }
        });

        //长按退出圈子
        holder.setOnLongClickListener(R.id.my_moments_layout, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("是否退出该圈子"); //设置内容
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitCircle(data);
                        dialog.dismiss(); //关闭dialog
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //参数都设置完成了，创建并显示出来
                builder.create().show();

                return false;
            }
        });

    }

    private void exitCircle(CircleModel data) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("circleId", data.getCircleId() + "");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.EXITCIRCLE, 1, "exitcircle", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response=" + response);
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (bean.getResultCode().equals("0000")) {
                    showToast("退出成功");

                    EventBus.getDefault().post(new Circleentity(1));
                }
            }
        }
    }
}
