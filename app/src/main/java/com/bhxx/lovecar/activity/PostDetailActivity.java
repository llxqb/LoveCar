package com.bhxx.lovecar.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.ioc.view.listener.OnTouch;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.DynamicCommentAdapter;
import com.bhxx.lovecar.adapter.common.CommonAdapter;
import com.bhxx.lovecar.adapter.common.ViewHolder;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.AcpgPicture;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.beans.DynamicCommentModel;
import com.bhxx.lovecar.beans.DynamicModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.utils.ActivityCollector;
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
import com.bhxx.lovecar.views.CircleImageView;
import com.bhxx.lovecar.views.ExpandGridView;
import com.bhxx.lovecar.views.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

/**
 * 动态详情
 */
@InjectLayer(R.layout.activity_post_detail)
public class PostDetailActivity extends BasicActivity {

    @InjectAll
    private Views v;
    private DynamicModel dynamicModel;
    private static int layoutnum = -1;
    private List<DynamicCommentModel> commentlist = new ArrayList<>();

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView back;
        CircleImageView dynamic_user_pic;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView dynamic_user_name, dynamic_create_time, dynamic_city, dynamic_content,
                dynamic_msg_num, dynamic_like_num, dynamic_collection_num, sendPost, focus_tv, hidetv;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        LinearLayout focus_layout, dynamic_single_layout, dynamic_double_layout,
                dynamic_msg_layout, dynamic_like_layout, dynamic_collection_layout,
                msg_listview_layout;
        RelativeLayout post_detail_buttom;
        ImageView dynamic_single_img, dynamic_double_img_1, dynamic_double_img_2,
                dynamic_collection_icon, dynamic_like_icon;
        ExpandGridView dynamic_pic_gv;
        MyListView post_detail_listview;
        EditText comment_content_et;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        dynamicModel = (DynamicModel) getIntent().getSerializableExtra("dynamicModel");

        initdata();
        initmsgList();

    }

    private void initmsgList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("dynamicId", dynamicModel.getDynamicId() + "");
        params.put("createType", "00");
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.DYNAMIC_COMMENT_LIST, 0, "comment_list", params, new MyStringCallback());
    }

    private void initdata() {
        v.dynamic_user_name.setText(dynamicModel.getFullName());
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + dynamicModel.getAvatar(), (ImageView) v.dynamic_user_pic, LoadImage.getHeadImgOptions());

        v.dynamic_create_time.setText(DateUtils.friendly_time(dynamicModel.getCreateTime()));
        v.dynamic_city.setText(dynamicModel.getCity());
        v.dynamic_content.setText(dynamicModel.getContent());

        if (dynamicModel.getCommentCount() != null) {
            v.dynamic_msg_num.setText(dynamicModel.getCommentCount() + "");
        }
        if (dynamicModel.getCareCount() != null) {
            v.dynamic_like_num.setText(dynamicModel.getCareCount() + "");
        }
        if (dynamicModel.getFavoriteCount() != null) {
            v.dynamic_collection_num.setText(dynamicModel.getFavoriteCount() + "");
        }

        if (!TextUtils.isEmpty(dynamicModel.getCollectStatus())) {
            if (dynamicModel.getCollectStatus().equals("0")) {
                v.dynamic_collection_icon.setImageResource(R.mipmap.icon_collection_ed);
            } else {
                v.dynamic_collection_icon.setImageResource(R.mipmap.icon_collection);
            }
        }
        if (!TextUtils.isEmpty(dynamicModel.getHitStatus())) {
            if (dynamicModel.getHitStatus().equals("0")) {
                v.dynamic_like_icon.setImageResource(R.mipmap.icon_fabulous_ed);
            } else if (dynamicModel.getHitStatus().equals("1")) {
                v.dynamic_like_icon.setImageResource(R.mipmap.icon_fabulous);
            }
        }

        if (!TextUtils.isEmpty(dynamicModel.getIsFriend())) {
            if (dynamicModel.getIsFriend().equals("0")) {
                v.focus_layout.setVisibility(View.INVISIBLE);
            }
        }
//        if (!TextUtils.isEmpty(dynamicModel.getCareStatus())) {
//            if (dynamicModel.getCareStatus().equals("0")) {
//                v.focus_tv.setText("已关注");
//            }
//        }
        switch (layoutnum) {
            case -1:
                break;
            case 0:
                v.dynamic_single_layout.setVisibility(View.GONE);
                v.dynamic_double_layout.setVisibility(View.GONE);
                v.dynamic_pic_gv.setVisibility(View.GONE);
                break;
            case 1:
                v.dynamic_single_layout.setVisibility(View.VISIBLE);
                v.dynamic_double_layout.setVisibility(View.GONE);
                v.dynamic_pic_gv.setVisibility(View.GONE);

                if (dynamicModel.getAcpgPictures() != null) {
                    final String[] single = dynamicModel.getAcpgPictures().getUrl().split(";");
                    if (!TextUtils.isEmpty(single[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + single[0], v.dynamic_single_img, LoadImage.getDefaultOptions());
                        v.dynamic_single_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<String> singUrl = new ArrayList<String>();
                                singUrl.add(single[0]);
                                ImageDisplayActivity.start(PostDetailActivity.this, singUrl, 0);
                            }
                        });
                    }
                }
                break;
            case 2:
                v.dynamic_single_layout.setVisibility(View.GONE);
                v.dynamic_double_layout.setVisibility(View.VISIBLE);
                v.dynamic_pic_gv.setVisibility(View.GONE);
                if (dynamicModel.getAcpgPictures() != null) {
                    final String[] doublePic = dynamicModel.getAcpgPictures().getUrl().split(";");
                    final ArrayList<String> doubleUrl = new ArrayList<String>();
                    doubleUrl.add(doublePic[0]);
                    doubleUrl.add(doublePic[1]);
                    if (!TextUtils.isEmpty(doublePic[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + doublePic[0], v.dynamic_double_img_1, LoadImage.getDefaultOptions());
                        v.dynamic_double_img_1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(PostDetailActivity.this, doubleUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + doublePic[1], v.dynamic_double_img_2, LoadImage.getDefaultOptions());
                        v.dynamic_double_img_2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(PostDetailActivity.this, doubleUrl, 1);
                            }
                        });
                    }
                }
                break;
            case 3:
                v.dynamic_single_layout.setVisibility(View.GONE);
                v.dynamic_double_layout.setVisibility(View.GONE);
                v.dynamic_pic_gv.setVisibility(View.VISIBLE);
                if (dynamicModel.getAcpgPictures() != null) {
                    String[] pics = dynamicModel.getAcpgPictures().getUrl().split(";");
                    final ArrayList<String> listPic = new ArrayList<String>();
                    for (int i = 0; i < pics.length; i++) {
                        listPic.add(pics[i]);
                    }

                    v.dynamic_pic_gv.setAdapter(new CommonAdapter<String>(listPic, PostDetailActivity.this, R.layout.dynamic_pics_item) {
                        @Override
                        public void convert(ViewHolder holders, String img) {
                            if (!TextUtils.isEmpty(img)) {
                                ImageLoader.getInstance().displayImage(GlobalValues.IP1 + img, (ImageView) holders.getView(R.id.dynamic_pics_img), LoadImage.getDefaultOptions());
                            }
                        }
                    });

                    v.dynamic_pic_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ImageDisplayActivity.start(PostDetailActivity.this, listPic, position);
                        }
                    });
                }
                break;

        }
    }

    @Override
    protected void click(View view) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.dynamic_msg_layout:
                v.msg_listview_layout.setVisibility(View.VISIBLE);
                v.post_detail_buttom.setVisibility(View.VISIBLE);
                break;
            case R.id.sendPost:
                //发送评论
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                } else {
                    if (TextUtils.isEmpty(v.comment_content_et.getText().toString())) {
                        showToast("请输入评论内容");
                        return;
                    }
                    sendPost(v.comment_content_et.getText().toString());
                }
                break;
            case R.id.focus_layout:

                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    return;
                } else {
                    if (Integer.parseInt(App.app.getData(UserPreferences.USER_ID)) == dynamicModel.getUserId()) {
                        showToast("不能添加自己为好友");
                        return;
                    }
                    params.put("userId", App.app.getData(UserPreferences.USER_ID));
                    params.put("toUser", dynamicModel.getUserId() + "");
//                    params.put("describe", "0");
                    params.put("sign", SingUtils.getMd5SignMsg(params));

                    MyOkHttp.postMap(GlobalValues.ADDFRIEND, 2, "addFriend", params, new MyStringCallback());
                }
                break;
            case R.id.dynamic_collection_layout:
                //收藏
                if (dynamicModel.getCollectStatus().equals("0")) {
                    showToast("已收藏");
                    return;
                }
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    return;
                }
                params.put("userId", App.app.getData(UserPreferences.USER_ID));
                if (dynamicModel.getDynamicId() != null) {
                    params.put("dynamicId", dynamicModel.getDynamicId() + "");
                }
                params.put("createType", "2");
                params.put("sign", SingUtils.getMd5SignMsg(params));

                MyOkHttp.postMap(GlobalValues.DYNAMIC_CONNECTION, 3, "connection", params, new MyStringCallback());
                break;
            case R.id.dynamic_like_layout:
                //点赞
                if (dynamicModel.getHitStatus().equals("0")) {
                    showToast("已点赞");
                    return;
                }
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    return;
                }
                params.put("userId", App.app.getData(UserPreferences.USER_ID));
                if (dynamicModel.getDynamicId() != null) {
                    params.put("objectId", dynamicModel.getDynamicId() + "");
                }
                params.put("createType", "2");
                params.put("sign", SingUtils.getMd5SignMsg(params));

                MyOkHttp.postMap(GlobalValues.DYNAMIC_LIKE, 4, "like", params, new MyStringCallback());
                break;
        }
    }

    private void sendPost(String content) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("dynamicId", dynamicModel.getDynamicId() + "");
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("cmContent", content);
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.DYNAMIC_COMMENT, 1, "dynamic_comment", params, new MyStringCallback());
    }

    public static void start(Context context, DynamicModel dynamicModel, int layout) {
        layoutnum = layout;
        Intent intent = new Intent(context, PostDetailActivity.class);
        intent.putExtra("dynamicModel", dynamicModel);
        context.startActivity(intent);
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 0:
                    //查看评论列表
                    LogUtils.i("response 0=" + response);
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<DynamicCommentModel> bean = JsonUtils.getBean(response, CommonListBean.class, DynamicCommentModel.class);
                        if (bean.getResultCode().equals("0000")) {
                            if (bean.getRows() != null && bean.getRows().size() > 0) {
                                v.hidetv.setVisibility(View.GONE);
                                DynamicCommentAdapter dynamicCommentAdapter = new DynamicCommentAdapter(bean.getRows(), PostDetailActivity.this, R.layout.dynamic_comment_item);
                                v.post_detail_listview.setAdapter(dynamicCommentAdapter);
                            } else {
                                v.hidetv.setVisibility(View.VISIBLE);
                            }
                        } else {
                            showToast(Constant.ERROR_WEB);
                        }
                    }
                    break;
                case 1:
                    //发表评论
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        showToast(bean.getResultDesc());
                        v.comment_content_et.setText("");
                        initmsgList();
                    }
                    break;
                case 2:
                    //加好友
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            showToast("添加好友请求成功");
                            v.focus_tv.setText("已发送添加好友请求");
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
                            v.dynamic_collection_icon.setImageResource(R.mipmap.icon_collection_ed);
                        }
                    } else {
                        showToast(Constant.ERROR_WEB);
                    }
                    break;
                case 4:
                    //点赞
                    //点赞
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                        if (bean.getResultCode().equals("0000")) {
                            showToast("点赞成功");
                            v.dynamic_like_icon.setImageResource(R.mipmap.icon_fabulous_ed);
                        }
                    } else {
                        showToast(Constant.ERROR_WEB);
                    }
                    break;
            }

        }
    }
}
