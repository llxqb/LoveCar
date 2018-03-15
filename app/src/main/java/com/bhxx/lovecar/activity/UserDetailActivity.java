package com.bhxx.lovecar.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.ActivityUtils;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.DateUtils;
import com.bhxx.lovecar.utils.DialogUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.CircleImageView;
import com.bhxx.lovecar.views.UserItem1;
import com.bigkoo.pickerview.TimePickerView;
import com.makeapp.android.util.AndroidUtil;
import com.makeapp.android.util.ToastUtil;
import com.makeapp.javase.file.FileUtil;
import com.makeapp.javase.lang.StringUtil;
import com.makeapp.javase.util.DateUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.Call;

/**
 * Created by @dpy on 2016/11/30.
 * 用户详情
 *
 * @qq289513149.
 */

public class UserDetailActivity extends BasicActivity implements View.OnClickListener, DialogUtils.DialogCallBack {

    private static final String TAG = UserDetailActivity.class.getSimpleName();
    public static final int REQUEST_USERNAME = 0x11;
    public static final int REQUEST_BIRTHDAY = 0x12;
    public static final int REQUEST_USERCITY = 0x13;
    public static final int REQUEST_MOBILEPHONE = 0x14;
    private File cameraFile;//照相或拍照的文件对象
    private File cropFile;//裁切文件对象
    private View headView;
    private CircleImageView circleImageView;//用户头像
    private UserItem1 uiUserName;//用户姓名
    private UserItem1 uiUserSex;//性别
    private UserItem1 uiBirthday;//出生年月
    private UserItem1 uiUserCity;//所在城市
    private UserItem1 uiMobilePhone;//手机号
    private TimePickerView timePickerView;
    private Date birthdayDate;
    private String serverUrl = "";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = BitmapFactory.decodeFile(cropFile.getAbsolutePath());
            if (bitmap != null) {
                circleImageView.setImageBitmap(bitmap);
            } else {
                ToastUtil.show(UserDetailActivity.this, getResources().getString(R.string.operation_sdcard_inenough));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initView();
        initEvent();
        String mobile = App.app.getData(UserPreferences.USER_MOBILE);
        if (StringUtil.isValid(mobile)) {
            uiMobilePhone.setContent(mobile);
        }
        //显示本地保存的个人信息
        showUserInfo();
    }

    public void onBackClick(View view) {
        finish();
    }

    //显示本地保存的个人信息
    private void showUserInfo() {
        //本地头像显示
        serverUrl = App.app.getData(UserPreferences.USER_AVATAR);
        if (StringUtil.isValid(serverUrl) && !"null".equals(serverUrl)) {
            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + serverUrl, circleImageView, LoadImage.getDefaultOptions());
        }
        String fullName = App.app.getData(UserPreferences.USER_NAME);
        String sex = App.app.getData(UserPreferences.USER_SEX);
        String birthday = App.app.getData(UserPreferences.USER_BIRTHDAY);
        String city = App.app.getData(UserPreferences.USER_CITY);
        Log.e(TAG, "get----->fullName:" + fullName + "；sex:" + sex + "；birthday:" + birthday + "；city:" + city);
        if (StringUtil.isValid(fullName) && !"null".equals(fullName)) {
            uiUserName.setContent(fullName);
        }
        if ("0".equals(sex)) {
            uiUserSex.setContent(getResources().getString(R.string.mine_d_sex_male));
        } else if ("1".equals(sex)) {
            uiUserSex.setContent(getResources().getString(R.string.mine_d_sex_female));
        }else{
            uiUserSex.setContent("");
        }

        if (StringUtil.isValid(birthday) && !"null".equals(birthday)) {
            uiBirthday.setContent(birthday);
            birthdayDate = DateUtil.getDate(birthday, "yyyy-MM-dd");
        } else {
            uiBirthday.setContent("");
        }
        if (StringUtil.isValid(city) && !"null".equals(city)) {
            uiUserCity.setContent(city);
        }else{
            uiUserCity.setContent("");
        }
    }

    //初始化控件
    private void initView() {
        headView = this.findViewById(R.id.rlHead);
        circleImageView = (CircleImageView) this.findViewById(R.id.civHead);
        uiUserName = (UserItem1) this.findViewById(R.id.uiUserName);
        uiUserSex = (UserItem1) this.findViewById(R.id.uiUserSex);
        uiBirthday = (UserItem1) this.findViewById(R.id.uiBirthday);
        uiUserCity = (UserItem1) this.findViewById(R.id.uiCity);
        uiMobilePhone = (UserItem1) this.findViewById(R.id.uiMobilephone);
    }

    //初始化控件事件
    private void initEvent() {
        headView.setOnClickListener(this);
        uiUserName.setOnClickListener(this);
        uiUserSex.setOnClickListener(this);
        uiBirthday.setOnClickListener(this);
        uiUserCity.setOnClickListener(this);
//        uiMobilePhone.setOnClickListener(this);
    }


    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.rlHead: {
                cropFile = AndroidUtil.getApplicationTempFile(UserDetailActivity.this, "tmp", "jpg");
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(UserDetailActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(UserDetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(UserDetailActivity.this, Manifest.permission.CAMERA)) {
                            showDialog(getResources().getString(R.string.op_permission_title), getResources().getString(R.string.op_permission_con_camera),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UserDetailActivity.super.cancelAlertDialog();
                                            //请求权限
                                            ActivityCompat.requestPermissions(UserDetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
                                        }
                                    }, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            UserDetailActivity.super.cancelAlertDialog();
                                        }
                                    }, getResources().getString(R.string.operation_confirm), getResources().getString(R.string.operation_cancel));
                        } else {
                            //请求权限
                            ActivityCompat.requestPermissions(UserDetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
                        }
                    } else {
                        cameraFile = DialogUtils.showImageDialog(UserDetailActivity.this,
                                getResources().getString(R.string.op_title_photo),
                                getResources().getString(R.string.operation_take_camera),
                                getResources().getString(R.string.operation_take_photo), "photo", this);
                    }
                } else {
                    cameraFile = DialogUtils.showImageDialog(UserDetailActivity.this,
                            getResources().getString(R.string.op_title_photo),
                            getResources().getString(R.string.operation_take_camera),
                            getResources().getString(R.string.operation_take_photo), "photo", this);
                }
            }
            break;
            case R.id.uiUserName: {
                String title = getResources().getString(R.string.mine_detail_name);
                Intent intent = new Intent(UserDetailActivity.this, SmallTextActivity.class);
                intent.putExtra(SmallTextActivity.REQUEST_TITLE, title);
                String name = uiUserName.getContent();
                intent.putExtra(SmallTextActivity.REQUEST_CONTENT, name);
                startActivityForResult(intent, REQUEST_USERNAME);
            }
            break;
            case R.id.uiUserSex: {
                DialogUtils.showBottomDialog(UserDetailActivity.this,
                        getResources().getString(R.string.op_title_sex),
                        getResources().getString(R.string.mine_d_sex_male),
                        getResources().getString(R.string.mine_d_sex_female), "sex", this);
            }
            break;
            case R.id.uiBirthday: {
                setBirthday();
            }
            break;
            case R.id.uiCity: {
                String title = getResources().getString(R.string.mine_detail_city);
                Intent intent = new Intent(UserDetailActivity.this, UserCityActivity.class);
                intent.putExtra(UserCityActivity.REQUEST_TITLE, title);
                String city = uiUserCity.getContent();
                intent.putExtra(SmallTextActivity.REQUEST_CONTENT, city);
                startActivityForResult(intent, REQUEST_USERCITY);
            }
            break;
            case R.id.uiMobilephone: {
                String title = getResources().getString(R.string.mine_detail_mobilephone);
                Intent intent = new Intent(UserDetailActivity.this, SmallTextActivity.class);
                intent.putExtra(SmallTextActivity.REQUEST_TITLE, title);
//                String mobile = uiMobilePhone.getContent();
//                intent.putExtra(SmallTextActivity.REQUEST_CONTENT, mobile);
                intent.putExtra(SmallTextActivity.REQUEST_TYPE, true);
                startActivityForResult(intent, REQUEST_MOBILEPHONE);
            }
            break;

        }
    }

    private void setBirthday() {
        //时间选择器
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        timePickerView.setRange(calendar.get(Calendar.YEAR) - 100, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦
        if (birthdayDate != null) {
            timePickerView.setTime(birthdayDate);
        } else {
            timePickerView.setTime(new Date());

        }
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);
        //时间选择后回调
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                uiBirthday.setContent(DateUtils.getStringDate(date, "yyyy-MM-dd"));
            }
        });
        //弹出时间选择器
        timePickerView.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityUtils.REQUEST_CODE_CAMERA && FileUtil.isValid(cameraFile)) { //拍照
            if (!cropFile.exists()) {
                boolean b = FileUtil.create(cropFile);
                if (b) {
                    ActivityUtils.startPhotoCrop(UserDetailActivity.this, Uri.fromFile(cameraFile), 80, cropFile);
                }
            } else {
                ActivityUtils.startPhotoCrop(UserDetailActivity.this, Uri.fromFile(cameraFile), 80, cropFile);
            }
            cameraFile = null;
        } else if (requestCode == ActivityUtils.REQUEST_CODE_IMAGE && data != null) { //选择图片
            if (!cropFile.exists()) {
                boolean b = FileUtil.create(cropFile);
                if (b) {
                    ActivityUtils.startPhotoCrop(UserDetailActivity.this, data.getData(), 80, cropFile);
                }
            } else {
                ActivityUtils.startPhotoCrop(UserDetailActivity.this, data.getData(), 80, cropFile);
            }
        } else if (requestCode == ActivityUtils.REQUEST_CODE_CROP && data != null) { //裁剪图片
            //上传个人头像
            startUploadPortrait(cropFile);
        } else if (requestCode == REQUEST_USERNAME && resultCode == SmallTextActivity.RESULT_CONTENT) {
            uiUserName.setContent(data.getStringExtra(SmallTextActivity.RESPONSE_CONTENT));
        } else if (requestCode == REQUEST_BIRTHDAY && resultCode == SmallTextActivity.RESULT_CONTENT) {
            uiBirthday.setContent(data.getStringExtra(SmallTextActivity.RESPONSE_CONTENT));
        } else if (requestCode == REQUEST_USERCITY && resultCode == UserCityActivity.RESULT_CITY) {
            uiUserCity.setContent(data.getStringExtra(UserCityActivity.RESPONSE_CITY));
        } else if (requestCode == REQUEST_MOBILEPHONE && resultCode == SmallTextActivity.RESULT_CONTENT) {
            uiMobilePhone.setContent(data.getStringExtra(SmallTextActivity.RESPONSE_CONTENT));
        }
    }

    //android6.0以上的权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100: {//照片 系统给的权限提醒后 确认 长度>0 否则提示
                Log.e(TAG, "100");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//权限请求确认
                    cameraFile = DialogUtils.showImageDialog(UserDetailActivity.this,
                            getResources().getString(R.string.op_title_photo),
                            getResources().getString(R.string.operation_take_camera),
                            getResources().getString(R.string.operation_take_photo), "photo", this);
                } else {//权限请求取消
                    showToast(getResources().getString(R.string.op_permission_rfile));
                }
            }
        }
    }

    @Override
    public void onTopClick(String type) {
        if ("photo".equals(type)) {//拍照
            ActivityUtils.takePicture(UserDetailActivity.this, cameraFile);
        } else if ("sex".equals(type)) {//性别
            uiUserSex.setContent(getResources().getString(R.string.mine_d_sex_male));
        }
    }

    @Override
    public void onBottomClick(String type) {
        if ("photo".equals(type)) {//照片
            ActivityUtils.selectPicture(UserDetailActivity.this);
        } else if ("sex".equals(type)) {//性别
            uiUserSex.setContent(getResources().getString(R.string.mine_d_sex_female));
        }
    }

    //上传个人头像异步
    public void startUploadPortrait(File file) {
//        showProgressDialog(UserDetailActivity.this, getResources().getString(R.string.operation_submitting));
        MyOkHttp.uploadFileParams(GlobalValues.PICTURE_SINGLEUPLOAD, "s_picture", new HashMap<String, String>(), file, "myPic", new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
                dismissProgressDialog();
                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
//                dismissProgressDialog();
                Log.e(TAG, "response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
                            //头像上传成功后暂时替换本地变量，等最后点击"确定"
                            serverUrl = jsonObject.getString("rows");
                            mHandler.sendEmptyMessage(0x11);
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

    //修改个人信息异步请求
    public void startModifyTask(View view) {
        showProgressDialog(UserDetailActivity.this, getResources().getString(R.string.operation_submitting));
        String userId = App.app.getData(UserPreferences.USER_ID);
        String fullName = uiUserName.getContent();
        String sex = uiUserSex.getContent();
        String birthday = uiBirthday.getContent();
        String city = uiUserCity.getContent();
        if (StringUtil.isInvalid(fullName)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_toast_name));
            return;
        }
        if (StringUtil.isInvalid(birthday)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_toast_birthday));
            return;
        }
        if (StringUtil.isInvalid(sex)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_toast_sex));
            return;
        }
        if (StringUtil.isInvalid(city)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_toast_city));
            return;
        }
        String avatar;
        if (StringUtil.isValid(serverUrl)) {
            avatar = serverUrl;
        } else {
            avatar = App.app.getData(UserPreferences.USER_AVATAR);
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, userId);

        if (StringUtil.isValid(avatar) && !"null".equals(avatar)) {
            params.put(Constant.CAR_USER_AVATAR, avatar);
        }
        params.put(Constant.CAR_USER_FULLNAME, fullName);
        if (getResources().getString(R.string.mine_d_sex_male).equals(sex)) {
            params.put(Constant.CAR_USER_SEX, "0");
        } else if (getResources().getString(R.string.mine_d_sex_female).equals(sex)) {
            params.put(Constant.CAR_USER_SEX, "1");
        }
        params.put(Constant.CAR_USER_BIRTHDAY, birthday);
        params.put(Constant.CAR_USER_CITY, city);

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, userId);
        if (StringUtil.isValid(avatar) && !"null".equals(avatar)) {
            hashMap.put(Constant.CAR_USER_AVATAR, avatar);
        }
        hashMap.put(Constant.CAR_USER_FULLNAME, fullName);
        if (getResources().getString(R.string.mine_d_sex_male).equals(sex)) {
            hashMap.put(Constant.CAR_USER_SEX, "0");
        } else if (getResources().getString(R.string.mine_d_sex_female).equals(sex)) {
            hashMap.put(Constant.CAR_USER_SEX, "1");
        }
        hashMap.put(Constant.CAR_USER_BIRTHDAY, birthday);
        hashMap.put(Constant.CAR_USER_CITY, city);
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startModifyTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.MODIFY_INFO, "modify", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
                dismissProgressDialog();
                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                Log.e(TAG, "response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));
                            saveUserInfoPreference();
                            finish();
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

    //保存用户个人信息到本地
    private void saveUserInfoPreference() {
        String fullName = uiUserName.getContent();
        String sex = uiUserSex.getContent();
        String birthday = uiBirthday.getContent();
        String city = uiUserCity.getContent();

        Log.e(TAG, "save----->fullName:" + fullName + "；sex:" + sex + "；birthday:" + birthday + "；city:" + city);
        App.app.setData(UserPreferences.USER_NAME, fullName);
        if (getResources().getString(R.string.mine_d_sex_male).equals(sex)) {
            App.app.setData(UserPreferences.USER_SEX, "0");

        } else if (getResources().getString(R.string.mine_d_sex_female).equals(sex)) {
            App.app.setData(UserPreferences.USER_SEX, "1");
        }

        App.app.setData(UserPreferences.USER_BIRTHDAY, birthday);
        App.app.setData(UserPreferences.USER_CITY, city);
        //不点击确定提交最新的个人信息数据 都不更新本地头像
        if (StringUtil.isValid(serverUrl)) {
            App.app.setData(UserPreferences.USER_AVATAR, serverUrl);
        }
    }
}
