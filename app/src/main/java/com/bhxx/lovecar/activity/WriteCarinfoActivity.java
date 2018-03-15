package com.bhxx.lovecar.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.activity.photo.PhotoAlbumActivity;
import com.bhxx.lovecar.activity.photo.ViewPagerActivity;
import com.bhxx.lovecar.activity.photo.ViewPagerDeleteActivity;
import com.bhxx.lovecar.adapter.photo.AddImageGridAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.CommonBean;
import com.bhxx.lovecar.beans.Item;
import com.bhxx.lovecar.entity.CarEntity;
import com.bhxx.lovecar.entity.CarTypeentity;
import com.bhxx.lovecar.entity.Cityentity;
import com.bhxx.lovecar.entity.ImageDeleteStatusEntity;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CheckUtils;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.DateUtils;
import com.bhxx.lovecar.utils.FileUtils;
import com.bhxx.lovecar.utils.IntentUtil;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.PictureManageUtil;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.ActionSheetDialog;
import com.bhxx.lovecar.views.UserItem1;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

@InjectLayer(R.layout.activity_write_carinfo)
public class WriteCarinfoActivity extends BasicActivity {
    /* 用来标识请求照相功能的activity */
    private final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private final int PHOTO_PICKED_WITH_DATA = 3021;
    // GridView预览删除页面过来
    private final int PIC_VIEW_CODE = 2016;
    /* 拍照的照片存储位置 */
    private final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory()
                    + "/lovecar");
    private File mCurrentPhotoFile;// 照相机拍照得到的图片
    // 用来显示预览图
    private ArrayList<Bitmap> microBmList = new ArrayList<Bitmap>();
    // 所选图的信息(主要是路径)
    private ArrayList<Item> photoList = new ArrayList<Item>();
    private AddImageGridAdapter imgAdapter;
    private Bitmap addNewPic;
    private GridView gridView;//显示所有上传图片
    private String photoListpath = "";

    private TimePickerView timePickerView;//时间选择器
    private String bsxtype;//变速箱类型
    private CarModel carModel;
    private boolean isclear = false;//判断是否清空

    private String picId;//图片ID
    private int isState = 0;//isState=0增加 ，isState=1是修改
    private String bsx; // 0 1 2
    private String factory_price;//出厂价格
    private String c_level;//汽车类型

    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        private ImageView write_carinfo_back;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        private RelativeLayout writecarinfo_cartype_rel, writecarinfo_licensetime_rel, writecarinfo_city_rel, writecarinfo_transmission_case_rel;
        private TextView writecarinfo_cartype_tv, writecarinfo_licensetime_tv, writecarinfo_city_tv, writecarinfo_transmission_case_tv;
        private EditText writecarinfo_kmnum_et, writecarinfo_saletime_et, writecarinfo_phone_et, writecarinfo_expectation_price_et, writecarinfo_describe_et;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button submit_btn;
        ScrollView write_scrollview;
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.write_carinfo_back:
                finish();
                break;
            case R.id.writecarinfo_cartype_rel:
                //品牌
                IntentUtil.setIntent(WriteCarinfoActivity.this, CarTypeActivity.class);
                break;
            case R.id.writecarinfo_licensetime_rel:
                //上牌时间
                setBirthday();
                break;
            case R.id.writecarinfo_city_rel:
                //车辆所在地
                IntentUtil.setIntent(WriteCarinfoActivity.this, CityListActivity.class);
                break;
            case R.id.writecarinfo_transmission_case_rel:
                //变速箱
                showbsxDialog();
                break;
            case R.id.submit_btn:
                //提交
                if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
                    showToast("请先登录");
                    IntentUtil.setIntent(WriteCarinfoActivity.this, LoginActivity.class);
                    return;
                }
                //品牌车型
                if (TextUtils.isEmpty(v.writecarinfo_cartype_tv.getText().toString())) {
                    showToast("请选择车型");
                    return;
                }

                if (TextUtils.isEmpty(v.writecarinfo_licensetime_tv.getText().toString())) {
                    showToast("请选择上牌时间");
                    return;
                }

                if (TextUtils.isEmpty(v.writecarinfo_city_tv.getText().toString())) {
                    showToast("请选择车辆所在地");
                    return;
                }

                if (TextUtils.isEmpty(v.writecarinfo_kmnum_et.getText().toString())) {
                    showToast("请输入行驶里程");
                    return;
                }

                if (TextUtils.isEmpty(v.writecarinfo_saletime_et.getText().toString())) {
                    showToast("请输入过户次数");
                    return;
                }

                if (TextUtils.isEmpty(v.writecarinfo_transmission_case_tv.getText().toString())) {
                    showToast("请选择变速箱类型");
                    return;
                }

                if (TextUtils.isEmpty(v.writecarinfo_phone_et.getText().toString())) {
                    showToast("请输入手机号");
                    return;
                }
                if (!CheckUtils.checkMobile(v.writecarinfo_phone_et.getText().toString())) {
                    showToast("手机号格式不正确");
                    return;
                }

                if (TextUtils.isEmpty(v.writecarinfo_expectation_price_et.getText().toString())) {
                    showToast("请输入期望价");
                    return;
                }

                if (photoList.size() == 0) {
                    showToast("请添加图片");
                    return;
                }

                showProgressDialog(WriteCarinfoActivity.this, "上传中...");

                submitImg();

                break;
        }
    }

    private void submitImg() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            showToast("请先登录");
            IntentUtil.setIntent(WriteCarinfoActivity.this, LoginActivity.class);
        } else {
            params.put("UserId", App.app.getData(UserPreferences.USER_ID));
        }
        if (isState == 1) {
            if (!TextUtils.isEmpty(carModel.getAcpgPictures().getPictureId())) {
                params.put("pictureId", carModel.getAcpgPictures().getPictureId());
            }
        }
        params.put("uploadType", "0");
        for (Item item : photoList) {
            photoListpath = photoListpath + item.getPhotoPath() + ";";
        }
        Map<String, File> map = new HashMap<>();
        String[] imgload = photoListpath.split(";");
        for (int i = 0; i < imgload.length; i++) {
            String paramName = FileUtils.getFileName(imgload[i]);
            File f = FileUtils.getUploadImageFile(imgload[i]);
            map.put(paramName, f);
        }
        MyOkHttp.multiPostFile(GlobalValues.UPLOAD_IMAGE, "UPLOADIMG", params, map, "myPic", new MyImageCallback());
    }

    private void submit(String s, String s1, String s2, String s3, String s4, String s5, String s6, String s7) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        String url = null;
        if (isState == 0) {
            //增加  车辆信息
            url = GlobalValues.ASSESS_ADD_CAR;
        } else {
            //修改  车辆信息
            url = GlobalValues.ASSESS_UPDATE_CAR;
            params.put("carId", carModel.getCarId() + "");

        }
        params.put("userId", App.app.getData(UserPreferences.USER_ID));
        params.put("carName", s);
        params.put("carLicenseTime", s1);
        params.put("carAddress", s2);
        params.put("kmNumber", s3);
        params.put("saleTimes", s4);
        params.put("transmissionCase", s5);
        params.put("mobile", s6);
        params.put("expectationPrice", s7);
        if (!TextUtils.isEmpty(factory_price)) {
            params.put("factoryPrice", factory_price);
        }
        if (!TextUtils.isEmpty(c_level)) {
            params.put("cLevel", c_level);
        }
        if (!TextUtils.isEmpty(v.writecarinfo_describe_et.getText().toString())) {
            params.put("extend1", v.writecarinfo_describe_et.getText().toString());
        }
        if (!TextUtils.isEmpty(picId)) {
            params.put("pictureId", picId);
        }

        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(url, "add", params, new MyStringCallback());

    }

    private class MyImageCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response=" + response);
            String picUrl;
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (bean.getResultCode().equals("0000")) {
                    picUrl = bean.getRows().toString();
                    LogUtils.i("picUrl=" + picUrl);
                    if (TextUtils.isEmpty(picUrl)) {
                        showToast("图片上传失败");
                        return;
                    }
                    submit(v.writecarinfo_cartype_tv.getText().toString(), v.writecarinfo_licensetime_tv.getText().toString(), v.writecarinfo_city_tv.getText().toString(),
                            v.writecarinfo_kmnum_et.getText().toString(), v.writecarinfo_saletime_et.getText().toString(), bsx,
                            v.writecarinfo_phone_et.getText().toString(), v.writecarinfo_expectation_price_et.getText().toString());
                }
            }

        }
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response=" + response);
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (bean.getResultCode().equals("0000")) {
                    showToast(bean.getResultDesc());
                    showSuccess();
                } else {
                    showToast("上传失败");
                }
            }

        }
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        EventBus.getDefault().register(this);
        v.write_scrollview.fullScroll(ScrollView.FOCUS_UP);//scrollview 默认调到顶部
        if (getIntent() != null) {
            carModel = (CarModel) getIntent().getSerializableExtra("writeCarinfo");
            if (carModel != null) {
                //指编辑状态
                isState = 1;
                if (carModel.getAcpgPictures() != null) {
                    picId = carModel.getAcpgPictures().getPictureId();
                }
                initView(carModel);
            } else {
                if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_MOBILE))) {
                    v.writecarinfo_phone_et.setText(App.app.getData(UserPreferences.USER_MOBILE));
                }
                //指恢复界面状态
//                carModel = (CarModel) App.app.readObjData("carModel");
//                if (carModel != null) {
//                    initView(carModel);
//                }
            }
        }

        if (!(PHOTO_DIR.exists() && PHOTO_DIR.isDirectory())) {
            PHOTO_DIR.mkdirs();
        }
        //添加图片
        gridView = (GridView) findViewById(R.id.my_addaction_photo);
        //加号图片
        addNewPic = BitmapFactory.decodeResource(this.getResources(), R.mipmap.photos_up);
        microBmList.add(addNewPic);
        imgAdapter = new AddImageGridAdapter(this, microBmList);
        gridView.setAdapter(imgAdapter);
        //事件监听，点击GridView里的图片时，在ImageView里显示出来
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == (photoList.size())) {
                    if (photoList.size() >= 9) {
                        showToast("已达到最大上传数");
                        return;
                    }
                    new ActionSheetDialog(WriteCarinfoActivity.this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                            .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    String status = Environment.getExternalStorageState();
                                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                                        //判断是否有SD卡
                                        doTakePhoto();// 用户点击了从照相机获取
                                    } else {
                                        showToast("没有SD卡");
                                    }
                                }
                            }).addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            //打开选择图片界面
                            doPickPhotoFromGallery();
                        }
                    }).show();

                } else {
                    Intent intent = new Intent(WriteCarinfoActivity.this, ViewPagerDeleteActivity.class);
                    intent.putParcelableArrayListExtra("files", photoList);
                    intent.putExtra(ViewPagerActivity.CURRENT_INDEX, position);
                    startActivityForResult(intent, PIC_VIEW_CODE);
                }
            }
        });
    }

    public static void start(Context context, CarModel carModel) {
        Intent intent = new Intent(context, WriteCarinfoActivity.class);
        intent.putExtra("writeCarinfo", carModel);
        context.startActivity(intent);
    }

    private void initView(CarModel carModel) {
        if (!TextUtils.isEmpty(carModel.getCarName())) {
            v.writecarinfo_cartype_tv.setText(carModel.getCarName());
        }
        if (!TextUtils.isEmpty(carModel.getCarLicenseTime())) {
            v.writecarinfo_licensetime_tv.setText(carModel.getCarLicenseTime());
        }
        if (!TextUtils.isEmpty(carModel.getCarAddress())) {
            v.writecarinfo_city_tv.setText(carModel.getCarAddress());
        }
        if (carModel.getKmNumber() != null) {
            v.writecarinfo_kmnum_et.setText(carModel.getKmNumber() + "");
        }
        if (carModel.getSaleTimes() != null) {
            v.writecarinfo_saletime_et.setText(carModel.getSaleTimes() + "");
        }
        if (!TextUtils.isEmpty(carModel.getTransmissionCase())) {
            switch (carModel.getTransmissionCase()) {
                case "0":
                    v.writecarinfo_transmission_case_tv.setText("手动");
                    break;
                case "1":
                    v.writecarinfo_transmission_case_tv.setText("自动");
                    break;
                case "2":
                    v.writecarinfo_transmission_case_tv.setText("手自一体");
                    break;
            }

        }
        if (!TextUtils.isEmpty(carModel.getMobile())) {
            v.writecarinfo_phone_et.setText(carModel.getMobile());
        }
        if (carModel.getExpectationPrice() != null) {
            v.writecarinfo_expectation_price_et.setText(carModel.getExpectationPrice() + "");
        }
        if (!TextUtils.isEmpty(carModel.getExtend1())) {
            v.writecarinfo_describe_et.setText(carModel.getExtend1());
        }

    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkPermission = ContextCompat.checkSelfPermission(WriteCarinfoActivity.this, Manifest.permission.CAMERA);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    (new AlertDialog.Builder(WriteCarinfoActivity.this)).setMessage("您需要在设置里打开相机权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(WriteCarinfoActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                        }
                    }).setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null).create().show();
                    return;
                }
            }
            // 创建照片的存储目录
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            showToast("找不到相机");
        }
    }

    public String getPhotoFileName() {
        UUID uuid = UUID.randomUUID();
        return uuid + ".jpg";
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkPermission = ContextCompat.checkSelfPermission(WriteCarinfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    (new AlertDialog.Builder(WriteCarinfoActivity.this)).setMessage("您需要在设置里打开存储空间权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(WriteCarinfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                        }
                    }).setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null).create().show();
                    return;
                }
            }
            final ProgressDialog dialog;
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //设置为圆形
            dialog.setMessage("数据加载中...");
            dialog.setIndeterminate(false);//
            dialog.show();
            Window window = dialog.getWindow();
            View view = window.getDecorView();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //初始化提示框
                    dialog.dismiss();
                }

            }, 1000);
            final Intent intent = new Intent(WriteCarinfoActivity.this, PhotoAlbumActivity.class);
            intent.putExtra("photonum", photoList.size() + "");
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "图库中找不到照片", Toast.LENGTH_LONG).show();
            showToast("图库中找不到照片");
        }
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    imgAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 删除gridview图片
     */
    protected void onEventMainThread(ImageDeleteStatusEntity entity) {
        photoList.remove(entity.getKey());
        imgAdapter.notifyDataSetChanged();
    }

    /**
     * 获取品牌车型
     */
    protected void onEventMainThread(CarTypeentity entity) {
        if (entity.getKey2().equals("addcar")) {
            if (entity.getKey1().contains("-")) {
                String name = entity.getKey1().split("-")[0];
                String price = entity.getKey1().split("-")[1];
                c_level = entity.getKey1().split("-")[2];
                LogUtils.i("name=" + name + " price=" + price + " c_level=" + c_level);
                v.writecarinfo_cartype_tv.setText(name);
//                if (price.equals("null")) {
//                    factory_price = "";
//                } else {
//                }
                factory_price = price;

            }
        }

    }

    /**
     * 获取车辆所在地
     */
    protected void onEventMainThread(Cityentity entity) {
        v.writecarinfo_city_tv.setText(entity.getKey());
    }


    /**
     * 处理其他页面返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: {
                // 调用Gallery返回的
                ArrayList<Item> tempFiles = new ArrayList<Item>();
                if (data == null)
                    return;
                tempFiles = data.getParcelableArrayListExtra("fileNames");

                if (tempFiles == null) {
                    return;
                }
                microBmList.remove(addNewPic);
                Bitmap bitmap;
                for (int i = 0; i < tempFiles.size(); i++) {
                    bitmap = MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(), tempFiles.get(i).getPhotoID(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
                    int rotate = PictureManageUtil.getCameraPhotoOrientation(tempFiles.get(i).getPhotoPath());
                    bitmap = PictureManageUtil.rotateBitmap(bitmap, rotate);
                    microBmList.add(bitmap);
                    photoList.add(tempFiles.get(i));
                }
                microBmList.add(addNewPic);
                imgAdapter.notifyDataSetChanged();
                break;
            }
            case CAMERA_WITH_DATA: {
                Long delayMillis = 0L;
                if (mCurrentPhotoFile == null) {
                    delayMillis = 500L;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                        //去掉GridView里的加号
                        microBmList.remove(addNewPic);
                        Item item = new Item();
                        item.setPhotoPath(mCurrentPhotoFile.getAbsolutePath());
                        photoList.add(item);
                        //根据路径，得到一个压缩过的Bitmap（宽高较大的变成500，按比例压缩）
                        Bitmap bitmap = PictureManageUtil.getCompressBm(mCurrentPhotoFile.getAbsolutePath());
                        //获取旋转参数
                        int rotate = PictureManageUtil.getCameraPhotoOrientation(mCurrentPhotoFile.getAbsolutePath());
                        //把压缩的图片进行旋转
                        bitmap = PictureManageUtil.rotateBitmap(bitmap, rotate);
                        microBmList.add(bitmap);
                        microBmList.add(addNewPic);
                        Message msg = handler.obtainMessage(1);
                        msg.sendToTarget();
                    }
                }, delayMillis);
                break;
            }
            case PIC_VIEW_CODE: {
                ArrayList<Integer> deleteIndex = data.getIntegerArrayListExtra("deleteIndexs");
                if (deleteIndex.size() > 0) {
                    for (int i = deleteIndex.size() - 1; i >= 0; i--) {
                        microBmList.remove((int) deleteIndex.get(i));
                        photoList.remove((int) deleteIndex.get(i));
                    }
                }
                imgAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void setBirthday() {
        //时间选择器
        timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH);
        //控制时间范围
        Calendar calendar = Calendar.getInstance();
        timePickerView.setRange(calendar.get(Calendar.YEAR) - 100, calendar.get(Calendar.YEAR));//要在setTime 之前才有效果哦
        timePickerView.setTime(new Date());
        timePickerView.setCyclic(false);
        timePickerView.setCancelable(true);
        //时间选择后回调
        timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                showToast(DateUtils.getStringDate(date, "yyyy-MM"));
                String licensetime = DateUtils.getStringDate(date, "yyyy-MM");
                String aa = licensetime.replace("-", "年");
                String bb = aa + "月";
                v.writecarinfo_licensetime_tv.setText(bb);
            }
        });
        //弹出时间选择器
        timePickerView.show();
    }

    //变速箱弹框
    private void showbsxDialog() {
        OptionsPickerView pickerView = new OptionsPickerView(this);
        final ArrayList<String> ageList = new ArrayList<String>();
        String[] ages = {"手动", "自动", "手自一体"};
        for (int i = 0; i < ages.length; i++) {
            ageList.add(ages[i]);
        }
        pickerView.setPicker(ageList);
        pickerView.setTitle("变速箱");
        pickerView.setCyclic(false);
        pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                String age = ageList.get(options1);
                switch (options1) {
                    case 0:
                        bsxtype = "手动";
                        bsx = "0";
                        break;
                    case 1:
                        bsxtype = "自动";
                        bsx = "1";
                        break;
                    case 2:
                        bsxtype = "手自一体";
                        bsx = "2";
                        break;
                }
                v.writecarinfo_transmission_case_tv.setText(bsxtype);
            }
        });
        pickerView.show();
    }

    /**
     * 添加车辆成功弹窗
     */
    private void showSuccess() {
        View view = LayoutInflater.from(WriteCarinfoActivity.this).inflate(R.layout.assess_submit_success, null);
        final Dialog log = new Dialog(WriteCarinfoActivity.this, R.style.transparentFrameWindowStyle);
        ImageView apply_success_close = (ImageView) view.findViewById(R.id.apply_success_close);
        TextView apply_success_ok = (TextView) view.findViewById(R.id.apply_success_ok);
        log.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = log.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示位置
        log.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        log.setCanceledOnTouchOutside(false);
        log.setCancelable(false);
        log.show();
        int measureWidth = getResources().getDisplayMetrics().widthPixels * 4 / 5;
        window.setLayout(measureWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        apply_success_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
                isclear = true;
                App.app.deleteData("carModel");
                finish();
            }
        });
        apply_success_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
                isclear = true;
                App.app.deleteData("carModel");
                finish();

                EventBus.getDefault().post(new CarEntity("writeCarinfo"));
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
