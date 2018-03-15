package com.bhxx.lovecar.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.ImageDisplayActivity;
import com.bhxx.lovecar.activity.PostDetailActivity;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.MultiTypeSupport;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.beans.DynamicModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.DateUtils;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

/**
 * liuli
 * 圈子动态适配器
 */
public class DynamicAdapter extends CommonAdapter<DynamicModel> {
    private boolean isfocus = false;//是否关注
    private boolean isconnection = false;//是否收藏
    private boolean islike = false;//是否点赞

    public DynamicAdapter(List<DynamicModel> dataList, Context context) {
        super(dataList, context, new DynamicItemSupport());
    }

    @Override
    public void convert(final ViewHolder holder, final DynamicModel data) {

        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + data.getAvatar(), (ImageView) holder.getView(R.id.dynamic_user_pic), LoadImage.getHeadImgOptions());

        if (!TextUtils.isEmpty(data.getCollectStatus())) {
            if (data.getCollectStatus().equals("0")) {
                holder.setImageResource(R.id.dynamic_connection_icon, R.mipmap.icon_collection_ed);
            } else {
                holder.setImageResource(R.id.dynamic_connection_icon, R.mipmap.icon_collection);
            }
        }
        if (!TextUtils.isEmpty(data.getHitStatus())) {
            if (data.getHitStatus().equals("0")) {
                holder.setImageResource(R.id.dynamic_like_icon, R.mipmap.icon_fabulous_ed);
            } else if (data.getHitStatus().equals("1")) {
                holder.setImageResource(R.id.dynamic_like_icon, R.mipmap.icon_fabulous);
            }
        }

        if (!TextUtils.isEmpty(data.getCareStatus())) {
            if (data.getCareStatus().equals("0")) {
                holder.setText(R.id.focus_tv, "已关注");
            }
        }

        if (!TextUtils.isEmpty(data.getFullName())) {
            holder.setText(R.id.dynamic_user_name, data.getFullName());
        }
        if (!TextUtils.isEmpty(data.getCreateTime())) {
            holder.setText(R.id.dynamic_create_time, DateUtils.friendly_time(data.getCreateTime()));
        }


        if (!TextUtils.isEmpty(data.getCity())) {
            holder.setText(R.id.dynamic_city, data.getCity());
        }
        if (!TextUtils.isEmpty(data.getContent())) {
            holder.setText(R.id.dynamic_content, data.getContent());
        }

        if (data.getCommentCount() != null) {//评论
            holder.setText(R.id.dynamic_msg_num, data.getCommentCount() + "");
        }
        if (data.getCareCount() != null) {//点赞
            holder.setText(R.id.dynamic_like_num, data.getCareCount() + "");
        }
        if (data.getFavoriteCount() != null) {//收藏数
            holder.setText(R.id.dynamic_connection_num, data.getFavoriteCount() + "");
        }


        //关注
        holder.setOnClickListener(R.id.focus_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getText(R.id.focus_tv).toString().equals("已关注")) {
                    showToast("已关注，请别重复关注");
                    return;
                }
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    return;
                } else {
                    if (Integer.parseInt(App.app.getData(UserPreferences.USER_ID)) == data.getUserId()) {
                        showToast("不能关注自己");
                        return;
                    }
                    params.put("userId", App.app.getData(UserPreferences.USER_ID));
                    params.put("objectId", data.getUserId() + "");
                    params.put("createType", "0");
                    params.put("sign", SingUtils.getMd5SignMsg(params));

                    MyOkHttp.postMap(GlobalValues.FOCUS_PEOPLE, 1, "focus_people", params, new MyStringCallback(holder));
                }
            }
        });

        //点赞
        holder.setOnClickListener(R.id.dynamic_like_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getHitStatus().equals("0")) {
                    showToast("已点赞");
                    return;
                }
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    return;
                }
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("userId", App.app.getData(UserPreferences.USER_ID));
                if (data.getDynamicId() != null) {
                    params.put("objectId", data.getDynamicId() + "");
                }
                params.put("createType", "2");
                params.put("sign", SingUtils.getMd5SignMsg(params));

                MyOkHttp.postMap(GlobalValues.DYNAMIC_LIKE, 2, "like", params, new MyStringCallback(holder));
            }
        });
        //收藏
        holder.setOnClickListener(R.id.dynamic_connection_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getCollectStatus().equals("0")) {
                    showToast("已收藏");
                    return;
                }
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    return;
                }
                LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                params.put("userId", App.app.getData(UserPreferences.USER_ID));
                if (data.getDynamicId() != null) {
                    params.put("dynamicId", data.getDynamicId() + "");
                }
                params.put("createType", "2");
                params.put("sign", SingUtils.getMd5SignMsg(params));

                MyOkHttp.postMap(GlobalValues.DYNAMIC_CONNECTION, 3, "connection", params, new MyStringCallback(holder));
            }
        });

        switch (holder.getLayoutId()) {
            case R.layout.dynamic_no_pic_item:
                holder.setOnClickListener(R.id.dynamic_no_pic_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostDetailActivity.start(context, data, 0);
                    }
                });
                break;
            case R.layout.dynamic_single_pic_item:
                if (data.getAcpgPictures() != null) {
                    final String[] single = data.getAcpgPictures().getUrl().split(";");
                    if (!TextUtils.isEmpty(single[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + single[0], (ImageView) holder.getView(R.id.dynamic_single_img), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.dynamic_single_img, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<String> singUrl = new ArrayList<String>();
                                singUrl.add(single[0]);
                                ImageDisplayActivity.start(context, singUrl, 0);
                            }
                        });
                    }
                }
                holder.setOnClickListener(R.id.dynamic_single_pic_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostDetailActivity.start(context, data, 1);
                    }
                });
                break;
            case R.layout.dynamic_double_pic_item:
                if (data.getAcpgPictures() != null) {
                    final String[] doublePic = data.getAcpgPictures().getUrl().split(";");
                    final ArrayList<String> doubleUrl = new ArrayList<String>();
                    doubleUrl.add(doublePic[0]);
                    doubleUrl.add(doublePic[1]);
                    if (!TextUtils.isEmpty(doublePic[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + doublePic[0], (ImageView) holder.getView(R.id.dynamic_double_img_1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.dynamic_double_img_1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + doublePic[1], (ImageView) holder.getView(R.id.dynamic_double_img_2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.dynamic_double_img_2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 1);
                            }
                        });
                    }
                }
                holder.setOnClickListener(R.id.dynamic_double_pic_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostDetailActivity.start(context, data, 2);
                    }
                });
                break;
            case R.layout.dynamic_list_pics_item:
                if (data.getAcpgPictures() != null) {
                    String[] pics = data.getAcpgPictures().getUrl().split(";");
                    final ArrayList<String> listPic = new ArrayList<String>();
                    for (int i = 0; i < pics.length; i++) {
                        listPic.add(pics[i]);
                    }
                    holder.setAdapter(R.id.dynamic_pic_gv, new CommonAdapter<String>(listPic, context, R.layout.dynamic_pics_item) {
                        @Override
                        public void convert(ViewHolder holders, String img) {
                            if (!TextUtils.isEmpty(img)) {
                                ImageLoader.getInstance().displayImage(GlobalValues.IP1 + img, (ImageView) holders.getView(R.id.dynamic_pics_img), LoadImage.getDefaultOptions());
                            }
                        }
                    });

                    holder.setOnItemClickListener(R.id.dynamic_pic_gv, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ImageDisplayActivity.start(context, listPic, position);
                        }
                    });
                }
                holder.setOnClickListener(R.id.dynamic_list_pics_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostDetailActivity.start(context, data, 3);
                    }
                });
                break;
        }

    }

    private class MyStringCallback extends CommonCallback {
        private ViewHolder holder;

        private MyStringCallback() {
        }

        private MyStringCallback(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {

            LogUtils.i("response=" + response);
            switch (id) {
                case 1:
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            showToast("关注成功");
                            holder.setText(R.id.focus_tv, "已关注");
                        }
                    } else {
                        showToast(Constant.ERROR_WEB);
                    }
                    break;
                case 2:
                    //点赞
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            showToast("点赞成功");
                            holder.setImageResource(R.id.dynamic_like_icon, R.mipmap.icon_fabulous_ed);
                        }
                    } else {
                        showToast(Constant.ERROR_WEB);
                    }
                    break;
                case 3:
                    //收藏
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            showToast("收藏成功");
                            holder.setImageResource(R.id.dynamic_connection_icon, R.mipmap.icon_collection_ed);
                        }
                    } else {
                        showToast(Constant.ERROR_WEB);
                    }
                    break;
            }

        }
    }
}
