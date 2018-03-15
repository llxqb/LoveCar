package com.bhxx.lovecar.activity;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.gjsDetailCommentAdapter;
import com.bhxx.lovecar.beans.CarAssessInfoModel;
import com.bhxx.lovecar.beans.DynamicCommentModel;
import com.bhxx.lovecar.beans.GJSModel;
import com.bhxx.lovecar.beans.GJSPublishServiceModel;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

@InjectLayer(R.layout.activity_gjsdetail)
public class GJSDetailActivity extends BasicActivity {

    private GJSPublishServiceModel gjsModel;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView gjs_detail_back, gjsDetail_avatar_background, gjsDetail_avatar;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView gjs_detail_title, gjsDetail_name, gjsDetail_lookcar;
        TextView gjsDetail_profession, gjsDetail_price, gjsDetail_assessNum, gjsDetail_city, gjsDetail_describe;
        RatingBar gjsDetail_level;
        MyListView gjsDetail_listview;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        if (getIntent() != null) {
            gjsModel = (GJSPublishServiceModel) getIntent().getSerializableExtra("gjsModel");
            if (gjsModel != null) {
                initView();
            }
        }
        initAssessList();
    }


    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.gjs_detail_back:
                finish();
                break;
            case R.id.gjsDetail_lookcar:
                Intent intent = new Intent(GJSDetailActivity.this, LookCarActivity.class);
                intent.putExtra("gjsModel", gjsModel);
                startActivity(intent);
                break;
        }
    }


    private void initView() {
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + gjsModel.getAvatar(), (ImageView) v.gjsDetail_avatar, LoadImage.getHeadImgOptions());
        ImageLoader.getInstance().displayImage(GlobalValues.IP1 + gjsModel.getAvatar(), (ImageView) v.gjsDetail_avatar_background, LoadImage.getDefaultOptions());

        v.gjs_detail_title.setText(gjsModel.getAssessName());
        v.gjsDetail_name.setText(gjsModel.getAssessName());
//        if (gjsModel.getServicePrice() != null) {
//        }
        v.gjsDetail_price.setText(gjsModel.getServicePrice() + "");
        if (gjsModel.getAssessTimes() == null) {
            v.gjsDetail_assessNum.setText(Html.fromHtml("已评估" + "<font color='red'>" + 0 + "</font>" + "次"));
        } else {
            v.gjsDetail_assessNum.setText(Html.fromHtml("已评估" + "<font color='red'>" + gjsModel.getAssessTimes() + "</font>" + "次"));
        }
        v.gjsDetail_level.setRating(Float.parseFloat(gjsModel.getGrade() + 1));
        v.gjsDetail_describe.setText(gjsModel.getDescription());
        v.gjsDetail_profession.setText(gjsModel.getCarTypeName());

    }

    private void initAssessList() {
        List<DynamicCommentModel> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DynamicCommentModel commentModel = new DynamicCommentModel();
            commentModel.setAvatar("http://www.chinagirlol.cc/data/attachment/forum/201412/03/233758hw7o7h08kkozkcwi.jpg");
            commentModel.setFullName("希达");
            commentModel.setCmContent("专业的评估师，专业的评估师，全世界就你最叼，专业的评估师，专业的评估师，全世界就你最叼");
            commentModel.setCreateTime("2017-01-01 12:00:00");//yyyy-MM-dd HH:mm:ss
            commentModel.setStarNum(5);
            list.add(commentModel);
        }
        gjsDetailCommentAdapter gjsDetailCommentAdapter = new gjsDetailCommentAdapter(list, GJSDetailActivity.this, R.layout.assessdetail_comment_item);
        v.gjsDetail_listview.setAdapter(gjsDetailCommentAdapter);
    }
}
