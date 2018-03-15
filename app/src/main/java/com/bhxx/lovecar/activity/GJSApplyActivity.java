package com.bhxx.lovecar.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.ActivityUtils;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.DialogUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bigkoo.pickerview.OptionsPickerView;
import com.makeapp.android.util.AndroidUtil;
import com.makeapp.android.util.ToastUtil;
import com.makeapp.javase.file.FileUtil;
import com.makeapp.javase.lang.StringUtil;
import com.makeapp.javase.util.DataUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.Call;

/**
 * Created by @dpy on 2017/1/10.
 * 我的-申请成为估价师
 *
 * @qq289513149.
 */

public class GJSApplyActivity extends BasicActivity implements View.OnClickListener, DialogUtils.DialogCallBack {

    private static final String TAG = GJSApplyActivity.class.getSimpleName();
    public static final String INTENT_REASON = "reason";
    public static final int REQUEST_USERNAME = 0x11;
    private View viewName;//估价师姓名
    private TextView nameTextView;
    private View viewEducation;//估价师学历
    private TextView educationTextView;
    private View viewLevel;//估价师等级
    private TextView levelTextView;
    private int gjsLevel;
    private int gjsExperience;
    private View viewExperience;//估价师经验年限
    private TextView experienceTextView;
    private TextView messageTextView;
    private ImageView imageView;//估价师证书图片
    private File cameraFile;//照相或拍照的文件对象
    private String serverUrl = "";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bitmap bitmap = BitmapFactory.decodeFile(cameraFile.getAbsolutePath());
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                ToastUtil.show(GJSApplyActivity.this, getResources().getString(R.string.operation_sdcard_inenough));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gjs_apply);
        initView();
        initEvent();
        String reason = getIntent().getStringExtra(INTENT_REASON);
        if (StringUtil.isValid(reason)) {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(String.format(getResources().getString(R.string.op_gjs_apply_error), reason));
        } else {
            messageTextView.setVisibility(View.GONE);
        }
        //查看评估师申请信息详情
        startGetAssessInfo();
    }

    //初始化界面控件
    private void initView() {
        viewName = this.findViewById(R.id.llName);
        viewEducation = this.findViewById(R.id.llEducation);
        viewLevel = this.findViewById(R.id.llLevel);
        viewExperience = this.findViewById(R.id.llExperience);
        imageView = (ImageView) this.findViewById(R.id.ivCertificate);
        nameTextView = (TextView) this.findViewById(R.id.tvUserName);
        educationTextView = (TextView) this.findViewById(R.id.tvEducation);
        levelTextView = (TextView) this.findViewById(R.id.tvLevel);
        experienceTextView = (TextView) this.findViewById(R.id.etExperience);
        messageTextView = (TextView) this.findViewById(R.id.tvMessage);
    }

    //初始化控件事件
    private void initEvent() {
        viewName.setOnClickListener(this);
        viewEducation.setOnClickListener(this);
        viewLevel.setOnClickListener(this);
        viewExperience.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    public void onBackClick(View view) {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.llName: {//姓名
                String title = getResources().getString(R.string.mine_detail_name);
                Intent intent = new Intent(GJSApplyActivity.this, SmallTextActivity.class);
                intent.putExtra(SmallTextActivity.REQUEST_TITLE, title);
                intent.putExtra(SmallTextActivity.REQUEST_CONTENT, nameTextView.getText().toString());
                startActivityForResult(intent, REQUEST_USERNAME);
            }
            break;
            case R.id.llEducation: {//学历
                selectedEducation();
            }
            break;
            case R.id.llLevel: {//等级
                selectedLevel();
            }
            break;
            case R.id.llExperience: {//经验年限
                selectedExperience();
            }
            break;
            case R.id.ivCertificate: {//证书
                cameraFile = AndroidUtil.getApplicationTempFile(GJSApplyActivity.this, "tmp", "jpg");
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(GJSApplyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(GJSApplyActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(GJSApplyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(GJSApplyActivity.this, Manifest.permission.CAMERA)) {
                            showDialog(getResources().getString(R.string.op_permission_title), getResources().getString(R.string.op_permission_con_camera),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            GJSApplyActivity.super.cancelAlertDialog();
                                            //请求权限
                                            ActivityCompat.requestPermissions(GJSApplyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
                                        }
                                    }, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            GJSApplyActivity.super.cancelAlertDialog();
                                        }
                                    }, getResources().getString(R.string.operation_confirm), getResources().getString(R.string.operation_cancel));
                        } else {
                            //请求权限
                            ActivityCompat.requestPermissions(GJSApplyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 100);
                        }
                    } else {
                        cameraFile = DialogUtils.showImageDialog(GJSApplyActivity.this,
                                getResources().getString(R.string.op_title_photo),
                                getResources().getString(R.string.operation_take_camera),
                                getResources().getString(R.string.operation_take_photo), "photo", this);
                    }
                } else {
                    cameraFile = DialogUtils.showImageDialog(GJSApplyActivity.this,
                            getResources().getString(R.string.op_title_photo),
                            getResources().getString(R.string.operation_take_camera),
                            getResources().getString(R.string.operation_take_photo), "photo", this);
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_USERNAME && resultCode == SmallTextActivity.RESULT_CONTENT) {
            nameTextView.setText(data.getStringExtra(SmallTextActivity.RESPONSE_CONTENT));
        } else if (requestCode == ActivityUtils.REQUEST_CODE_CAMERA && FileUtil.isValid(cameraFile)) { //拍照
            if (!cameraFile.exists()) {
                boolean b = FileUtil.create(cameraFile);
                if (b) {
                    startUploadCertificate(cameraFile);
                }
            } else {
                startUploadCertificate(cameraFile);
            }
        } else if (requestCode == ActivityUtils.REQUEST_CODE_IMAGE && data != null) { //选择图片
            Uri uri = data.getData();
            String[] strings = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = this.managedQuery(uri, strings, null,
                    null, null);
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor
                    .getString(actual_image_column_index);
            cameraFile = new File(img_path);

            if (!cameraFile.exists()) {
                boolean b = FileUtil.create(cameraFile);
                if (b) {
                    startUploadCertificate(cameraFile);
                }
            } else {
                startUploadCertificate(cameraFile);
            }
        }
    }

    //选择学历
    private void selectedEducation() {
        OptionsPickerView pickerView = new OptionsPickerView(this);
        final ArrayList<String> educationList = new ArrayList<String>();
        String[] educations = getResources().getStringArray(R.array.education);
        for (int i = 0; i < educations.length; i++) {
            educationList.add(educations[i]);
        }
        pickerView.setPicker(educationList);
        pickerView.setTitle(getResources().getString(R.string.mine_hint_education));
        pickerView.setCyclic(false);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String education = educationList.get(options1);
                educationTextView.setText(education);

            }
        });
        pickerView.show();
    }

    //选择估价师等级等级
    private void selectedLevel() {
        OptionsPickerView pickerView = new OptionsPickerView(this);
        final ArrayList<String> levelList = new ArrayList<String>();
        String[] levels = getResources().getStringArray(R.array.gjs_level);
        for (int i = 0; i < levels.length; i++) {
            levelList.add(levels[i]);
        }
        pickerView.setPicker(levelList);
        pickerView.setTitle(getResources().getString(R.string.mine_hint_gjs_level));
        pickerView.setCyclic(false);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String level = levelList.get(options1);
                levelTextView.setText(level);
                gjsLevel = options1;
            }
        });
        pickerView.show();
    }

    //选择估价师年限
    private void selectedExperience() {
        OptionsPickerView pickerView = new OptionsPickerView(this);
        final ArrayList<String> experiencelList = new ArrayList<String>();
        String[] experiences = getResources().getStringArray(R.array.gjs_experience);
        for (int i = 0; i < experiences.length; i++) {
            experiencelList.add(experiences[i]);
        }
        pickerView.setPicker(experiencelList);
        pickerView.setTitle(getResources().getString(R.string.mine_hint_gjs_experience));
        pickerView.setCyclic(false);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String experience = experiencelList.get(options1);
                experienceTextView.setText(experience);
                gjsExperience = options1;
            }
        });
        pickerView.show();
    }

    //上传估价师证书异步
    public void startUploadCertificate(File file) {
        showProgressDialog(GJSApplyActivity.this, getResources().getString(R.string.loading));
        MyOkHttp.uploadFileParams(GlobalValues.PICTURE_SINGLEUPLOAD, "s_picture", new HashMap<String, String>(), file, "myPic", new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissProgressDialog();
                showToast(Constant.ERROR_WEB);
                Log.e(TAG, "startUploadCertificate e----->" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            showToast(jsonObject.optString("resultDesc"));
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

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

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
                    cameraFile = DialogUtils.showImageDialog(GJSApplyActivity.this,
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
            ActivityUtils.takePicture(GJSApplyActivity.this, cameraFile);
        }
    }

    @Override
    public void onBottomClick(String type) {
        if ("photo".equals(type)) {//照片
            ActivityUtils.selectPicture(GJSApplyActivity.this);
        }
    }

    //查看评估师信息详情
    private void startGetAssessInfo() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        Log.e(TAG, "assessId--->" + App.app.getData(UserPreferences.USER_ASSESSID));
        params.put(Constant.CAR_USER_ASSESSID, App.app.getData(UserPreferences.USER_ASSESSID));
        params.put(Constant.CAR_KEY, SingUtils.getMd5SignMsg(params));
        MyOkHttp.postMap(GlobalValues.ASSESS_SELECTASSESS, "info", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "response--->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            JSONObject rowJSONObject = jsonObject.optJSONObject(Constant.CAR_ROWS);
                            String assessName = rowJSONObject.optString(Constant.CAR_ASSESSNAME);
                            nameTextView.setText(assessName);
                            String background = rowJSONObject.optString(Constant.CAR_BACKGROUND);
                            educationTextView.setText(background);
                            String grade = rowJSONObject.optString(Constant.CAR_GRADE);
                            String[] levels = getResources().getStringArray(R.array.gjs_level);
                            gjsLevel = DataUtil.getInt(grade, 0);
                            levelTextView.setText(levels[gjsLevel]);
                            String workingAge = rowJSONObject.optString(Constant.CAR_WORKINGAGE);
                            String[] experiences = getResources().getStringArray(R.array.gjs_experience);
                            gjsExperience = DataUtil.getInt(workingAge, 0);
                            experienceTextView.setText(experiences[gjsExperience]);
                            String avatar = rowJSONObject.optString(Constant.CAR_USER_AVATAR);
                            ImageLoader.getInstance().displayImage(GlobalValues.IP1 + avatar, imageView, LoadImage.getDefaultOptions());
                            serverUrl = rowJSONObject.optString(Constant.CAR_EXTEND1);
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

    //提交估价师信息异步请求
    public void startSubmitGJSInfoTask(View view) {
        String name = nameTextView.getText().toString();
        if (StringUtil.isInvalid(name)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_hint_rname));
            return;
        }
        String education = educationTextView.getText().toString();
        if (StringUtil.isInvalid(education)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_hint_education));
            return;
        }
        String level = levelTextView.getText().toString();
        if (StringUtil.isInvalid(level)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_hint_gjs_level));
            return;
        }
        String experience = experienceTextView.getText().toString();
        if (StringUtil.isInvalid(experience)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_hint_gjs_experience));
            return;
        }
        if (StringUtil.isInvalid(serverUrl)) {
            ToastUtil.show(this, getResources().getString(R.string.mine_gjs_upload_zs));
            return;
        }

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_ASSESSNAME, name);
        params.put(Constant.CAR_BACKGROUND, education);
        params.put(Constant.CAR_GRADE, gjsLevel + "");
        params.put(Constant.CAR_WORKINGAGE, gjsExperience + "");
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_EXTEND1, serverUrl);

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_ASSESSNAME, name);
        hashMap.put(Constant.CAR_BACKGROUND, education);
        hashMap.put(Constant.CAR_GRADE, gjsLevel + "");
        hashMap.put(Constant.CAR_WORKINGAGE, gjsExperience + "");
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_EXTEND1, serverUrl);
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startSubmitGJSInfoTask hashMap----->" + hashMap);
        String assessStatus = App.app.getData(UserPreferences.USER_ASSESSSTATUS);
        if ("2".equals(assessStatus)) {//失败重新提交就是修改接口
            MyOkHttp.postMap(GlobalValues.ASSESS_UPDATEASSESS, "list", hashMap, new CommonCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "startSubmitGJSInfoTask e---->" + e);
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e(TAG, "startSubmitGJSInfoTask response---->" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (null != jsonObject) {
                            String resultCode = jsonObject.optString("resultCode");
                            if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                                showToast(jsonObject.optString("resultDesc"));
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
        } else {//新增一条申请信息接口
            MyOkHttp.postMap(GlobalValues.ASSESS_SAVEASSESS, "list", hashMap, new CommonCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.e(TAG, "startSubmitGJSInfoTask e---->" + e);
                }

                @Override
                public void onResponse(String response, int id) {
                    Log.e(TAG, "startSubmitGJSInfoTask response---->" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (null != jsonObject) {
                            String resultCode = jsonObject.optString("resultCode");
                            if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));
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
    }
}
