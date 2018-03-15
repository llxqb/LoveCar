package com.bhxx.lovecar.rongcloud;
/**
 * 启动融云会执行
 */


import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.beans.UserModel;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.GlobalValues;

import java.util.LinkedHashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;

public class CustomerUserInfoProvider implements RongIM.UserInfoProvider {
    private UserInfo uInfo;

    @Override
    public UserInfo getUserInfo(String userId) {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("userId", userId);
        params.put("sign", SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.USERINFO, "USER_INFO", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                LogUtils.i("response="+response);
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<UserModel> user = JsonUtils.getBean(response, CommonBean.class, UserModel.class);

                    if (user.getResultCode().equals("0000")) {
                        if(user.getRows()!=null){
                            UserModel model = user.getRows();
                            String uId = model.getUserId() + "";
                            String uName = model.getFullName();
                            String uPic = model.getAvatar();
                            if (!TextUtils.isEmpty(uPic)) {
                                uInfo = new UserInfo(uId, uName, Uri.parse(GlobalValues.IP1 + uPic));
                            } else {
                                uInfo = new UserInfo(uId, uName, null);
                            }
                            //能及时刷新会话列表  刷新用户信息
                            RongIM.getInstance().refreshUserInfoCache(uInfo);
                        }
                    }
                }
            }
        });
        return uInfo;
    }
}
