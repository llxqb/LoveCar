package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.LoginActivity;
import com.bhxx.lovecar.activity.MomentsDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CircleModel;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.entity.Circleentity;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.IntentUtil;
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
 * 车友圈-圈子推荐适配器
 */
public class MyMementsTuijianAdapter extends CommonAdapter<CircleModel> {
    public MyMementsTuijianAdapter(List<CircleModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, final CircleModel data) {
        if (!TextUtils.isEmpty(data.getExtend1())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getExtend1(), (ImageView) holder.getView(R.id.circleImg), LoadImage.getDefaultOptions());
        }

        if (!TextUtils.isEmpty(data.getIsAdd())) {
            if (data.getIsAdd().equals("0")) {
                holder.setText(R.id.circle_add, "已添加");
                holder.setBackgroundRes(R.id.circle_add, R.drawable.search_round_btn);
            } else {
                holder.setText(R.id.circle_add, "添加");
                holder.setBackgroundRes(R.id.circle_add, R.drawable.round_moment_btn);
            }
        }
        if (!TextUtils.isEmpty(data.getName())) {
            holder.setText(R.id.circleName, data.getName());
        }
        if (!TextUtils.isEmpty(data.getCircleCount() + "")) {
            holder.setText(R.id.circle_count, "圈友" + data.getCircleCount());
        }
        if (!TextUtils.isEmpty(data.getPostCount() + "")) {
            holder.setText(R.id.circle_post_count, "帖子" + data.getPostCount());
        }
        if (!TextUtils.isEmpty(data.getBrief())) {
            holder.setText(R.id.circle_brief, data.getBrief());
        }

        holder.setOnClickListener(R.id.circle_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getText(R.id.circle_add).equals("已添加")) {
                    showToast("已添加");
                } else {
                    if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                        IntentUtil.setIntent(context, LoginActivity.class);
                    } else {
                        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                        params.put("userId", App.app.getData(UserPreferences.USER_ID));
                        params.put("circleId", data.getCircleId() + "");
                        params.put("name", data.getName());
                        params.put("sign", SingUtils.getMd5SignMsg(params));

                        MyOkHttp.postMap(GlobalValues.CIRCLE_ADD, "add_circle", params, new MyStringCallback());
                    }
                }
            }
        });
        holder.setOnClickListener(R.id.moments_rel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MomentsDetailActivity.start(context, data);
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
                //刷新界面
                EventBus.getDefault().post(new Circleentity(1));
            }
        }
    }

}
