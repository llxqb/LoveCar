package com.bhxx.lovecar.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.AccountActivity;
import com.bhxx.lovecar.activity.CollectionCarActivity;
import com.bhxx.lovecar.activity.FocusActivity;
import com.bhxx.lovecar.activity.GJSApplyActivity;
import com.bhxx.lovecar.activity.GJSInfoActivity;
import com.bhxx.lovecar.activity.LoveCarActivity;
import com.bhxx.lovecar.activity.MessageCenterActivity;
import com.bhxx.lovecar.activity.SetupActivity;
import com.bhxx.lovecar.activity.UserDetailActivity;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.bhxx.lovecar.views.ListItem1;
import com.makeapp.android.util.ToastUtil;
import com.makeapp.javase.lang.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class MinePageFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MinePageFragment.class.getSimpleName();
    private Activity mContext;
    private CircleImageView userImageView;//用户头像
    private TextView nicknameTextView;//昵称
    private ListItem1 loveCarListItem1;//爱车
    private ListItem1 estimatorListItem1;//估价师认证
    private ListItem1 accountListItem1;//账户中心
    private ListItem1 messageListItem1;//消息
    private ListItem1 focusListItem1;//关注
    private ListItem1 collectListItem1;//收藏
    private ListItem1 setupListItem1;//设置
    private View rootView;
    private boolean isRefresh = true;//是否刷新true去刷新,false不刷新
    private String identity = "";//0会员,1估价师
    private String assessFailMessage = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mine_page, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        initView();
        initEvent();
        if (isRefresh) {
            refreshData();
            isRefresh = false;
        }
    }

    //初始化控件
    private void initView() {
        userImageView = (CircleImageView) rootView.findViewById(R.id.civUserHead);
        nicknameTextView = (TextView) rootView.findViewById(R.id.tvNickName);
        loveCarListItem1 = (ListItem1) rootView.findViewById(R.id.liLoveCar);
        estimatorListItem1 = (ListItem1) rootView.findViewById(R.id.liEstimator);
        accountListItem1 = (ListItem1) rootView.findViewById(R.id.liAccount);
        messageListItem1 = (ListItem1) rootView.findViewById(R.id.lvMessage);

        focusListItem1 = (ListItem1) rootView.findViewById(R.id.liFocus);
        collectListItem1 = (ListItem1) rootView.findViewById(R.id.liCollect);
        setupListItem1 = (ListItem1) rootView.findViewById(R.id.liSetup);
    }

    //控件添加事件
    private void initEvent() {
        userImageView.setOnClickListener(this);
        loveCarListItem1.setOnClickListener(this);
        estimatorListItem1.setOnClickListener(this);
        accountListItem1.setOnClickListener(this);
        messageListItem1.setOnClickListener(this);
        focusListItem1.setOnClickListener(this);
        collectListItem1.setOnClickListener(this);
        setupListItem1.setOnClickListener(this);
    }

    //刷新界面部分数据
    private void refreshData() {
        //头像刷新
        String avatar = App.app.getData(UserPreferences.USER_AVATAR);
        if (StringUtil.isValid(avatar) && !"null".equals(avatar)) {
            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + avatar, userImageView, LoadImage.getDefaultOptions());
        }
        //姓名刷新
        String fullName = App.app.getData(UserPreferences.USER_NAME);
        if (StringUtil.isValid(fullName) && !"null".equals(fullName)) {
            nicknameTextView.setText(fullName);
        } else {//没有姓名就显示手机号，并且保护用户信息
            String mobilePhone = App.app.getData(UserPreferences.USER_MOBILE);
            String frontChar = mobilePhone.substring(0, 3);//前三位
            String lastChar = mobilePhone.substring(7, 11);//后四位
            nicknameTextView.setText(frontChar + "*****" + lastChar);
        }
        //估价师刷新
        identity = App.app.getData(UserPreferences.USER_IDENTITY);//0会员,1估价师
        if (StringUtil.isValid(identity) && "1".equals(identity)) {
            estimatorListItem1.setText(getResources().getString(R.string.mine_estimator_center));
        } else {
            estimatorListItem1.setText(getResources().getString(R.string.mine_estimator_authentication));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        startGetUserInfo();
        if (isRefresh) {
            refreshData();
        } else {
            isRefresh = true;//接下去可以无限刷新只要不停地来回切换到别的页面
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.civUserHead: {//编辑用户信息,后面改到点击下面列表中的设置里面的个人信息
                startActivity(new Intent(mContext, UserDetailActivity.class));
            }
            break;
            case R.id.liLoveCar: {//爱车
                startActivity(new Intent(mContext, LoveCarActivity.class));
            }
            break;
            case R.id.liEstimator: {//估价师个人中心/申请成为估价师
                if (StringUtil.isValid(identity) && "1".equals(identity)) {//估价师个人中心
                    startActivity(new Intent(mContext, GJSInfoActivity.class));
                } else {//申请成为估价师
                    String assessStatus = App.app.getData(UserPreferences.USER_ASSESSSTATUS);
                    if ("1".equals(assessStatus)) {//审核中
                        ToastUtil.show(mContext, getResources().getString(R.string.mine_gjs_checking));
                    } else if ("2".equals(assessStatus) && StringUtil.isValid(assessFailMessage)) {//失败
                        Intent intent = new Intent(mContext, GJSApplyActivity.class);
                        intent.putExtra(GJSApplyActivity.INTENT_REASON, assessFailMessage);
                        startActivity(intent);
                    } else if ("0".equals(assessStatus) && StringUtil.isValid(assessFailMessage)) {//失败
                        startActivity(new Intent(mContext, GJSInfoActivity.class));
                    } else {//还没有提交过任何审核
                        Intent intent = new Intent(mContext, GJSApplyActivity.class);
                        intent.putExtra(GJSApplyActivity.INTENT_REASON, "");
                        startActivity(intent);
                    }
                }
            }
            break;
            case R.id.liAccount: {//账户中心
                startActivity(new Intent(mContext, AccountActivity.class));
            }
            break;
            case R.id.lvMessage: {//消息
                startActivity(new Intent(mContext, MessageCenterActivity.class));
            }
            break;
            case R.id.liFocus: {//关注
                startActivity(new Intent(mContext, FocusActivity.class));
            }
            break;
            case R.id.liCollect: {//收藏
                startActivity(new Intent(mContext, CollectionCarActivity.class));
            }
            break;
            case R.id.liSetup: {//设置
                startActivity(new Intent(mContext, SetupActivity.class));
            }
            break;

        }
    }

    @Override
    protected void click(View view) {

    }

    @Override
    protected void init() {
    }


    //查看会员信息详情
    private void startGetUserInfo() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.USERINFO, "info", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));
                            JSONObject rowJSONObject = jsonObject.optJSONObject(Constant.CAR_ROWS);
                            String userAssessStatus = rowJSONObject.optString(Constant.CAR_USER_ASSESSSTATUS);
                            String userAssessiD = rowJSONObject.optString(Constant.CAR_USER_ASSESSID);
                            Log.e(TAG, "userAssessStatus---->" + userAssessStatus);
                            App.app.setData(UserPreferences.USER_ASSESSID, userAssessiD);
                            App.app.setData(UserPreferences.USER_ASSESSSTATUS, userAssessStatus);
                            assessFailMessage = rowJSONObject.optString(Constant.CAR_USER_SYSTEMREPLY);
                            //刷新我的-估价师字符串相关
                            if (StringUtil.isValid(userAssessStatus) && "0".equals(userAssessStatus)) {
                                estimatorListItem1.setText(getResources().getString(R.string.mine_estimator_center));
                            } else {
                                estimatorListItem1.setText(getResources().getString(R.string.mine_estimator_authentication));
                            }
                        } else {
                            showToast(jsonObject.optString("resultDesc"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
