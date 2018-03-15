package com.bhxx.lovecar.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.makeapp.android.util.PackageUtil;
import com.makeapp.android.util.ToastUtil;
import com.umeng.social.tool.UMImageMark;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.SocializeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by @dpy on 2016/12/5.
 * 我的-关于我们
 *
 * @qq289513149.
 */

public class AboutActivity extends BasicActivity implements View.OnClickListener, Handler.Callback {

    private static final String TAG = AboutActivity.class.getSimpleName();
    private static final int MSG_TOAST = 1;
    private static final int MSG_ACTION_CCALLBACK = 2;//分享成功/分享取消/分享错误
    private static String CONTENT = "爱车估价，估出梦想，估出好心情！";
    private static final int MSG_CANCEL_NOTIFY = 3;
    private static String TITLE = "";
    private TextView versionTextView;
    private UMImage imagelocal;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        initEvent();
        TITLE = getResources().getString(R.string.app_name);
        initMedia();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(AboutActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(AboutActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("权限提示", "需要访问SD卡功能,否则不能使用相关功能", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAlertDialog();
                            //请求权限
                            ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 100);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAlertDialog();
                        }
                    }, "开启", "取消");
                } else {
                    //请求权限
                    ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 100);
                }
            }
        }

    }

    //初始化界面控件
    public void initView() {
        versionTextView = (TextView) this.findViewById(R.id.tvVersion);
        versionTextView.setText(PackageUtil.getPackageVersionName(this));
    }

    //初始化控件事件
    public void initEvent() {

    }

    public void onBackClick(View view) {
        finish();
    }

    //版本更新按钮
    public void onUpdateVersion(View view) {
        Log.e(TAG, "==onUpdateVersion==");
        ToastUtil.show(this, "当前已经是最新版本！");
    }

    //推荐给好友按钮
    public void onRecFriend(View view) {
        Log.e(TAG, "==onRecFriend==");
        showShareDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    showDialog("权限提示", "需要访问SD卡功能,否则不能使用相关功能", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAlertDialog();
                            //请求权限
                            ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 100);
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAlertDialog();
                            AboutActivity.this.finish();
                        }
                    }, "开启", "取消");
                }
            }
            break;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {

    }


    public void showShareDialog() {
        final Dialog dialog = new Dialog(AboutActivity.this, R.style.style_dialog);
        dialog.setContentView(R.layout.layout_third_group);
        dialog.setCanceledOnTouchOutside(true);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (v.getId()) {
                    case R.id.btnWechat:
                        new ShareAction(AboutActivity.this)
                                .withText(CONTENT)
                                .withMedia(imagelocal)
                                .withTargetUrl("http://www.baidu.com/")
                                .withTitle(TITLE)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(shareListener).share();
                        break;
                    case R.id.btnQQ:
                        new ShareAction(AboutActivity.this)
                                .withText(CONTENT)
                                .withMedia(imagelocal)
                                .withTargetUrl("http://www.baidu.com/")
                                .withTitle(TITLE)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(shareListener).share();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.btnWechat).setOnClickListener(listener);
        dialog.findViewById(R.id.btnQQ).setOnClickListener(listener);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(listener);
        dialog.show();
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_TOAST: {
                Toast.makeText(AboutActivity.this, String.valueOf(msg.obj), Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_ACTION_CCALLBACK: {
                switch (msg.arg1) {
                    case 1: { // 成功, successful notification
                        showNotification(2000, "分享成功");
                    }
                    break;
                    case 2: { // 失败, fail notification
                        String expName = msg.obj.getClass().getSimpleName();
                        if ("WechatClientNotExistException".equals(expName)
                                || "WechatTimelineNotSupportedException".equals(expName)
                                || "WechatFavoriteNotSupportedException".equals(expName)) {
                            showNotification(2000, "目前您的微信版本过低或未安装微信，需要安装微信才能使用");
                        } else {
                            showNotification(2000, "分享失败");
                        }
                    }
                    break;
                    case 3: { // 取消, cancel notification
                        showNotification(2000, "分享已取消");
                    }
                    break;
                }
            }
            break;
            case MSG_CANCEL_NOTIFY: {
                NotificationManager nm = (NotificationManager) msg.obj;
                if (nm != null) {
                    nm.cancel(msg.arg1);
                }
            }
            break;
        }
        return false;
    }

    // 在状态栏提示分享操作,the notification on the status bar
    private void showNotification(long cancelTime, String text) {
        try {
            NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            final int id = Integer.MAX_VALUE / 13 + 1;
            nm.cancel(id);

            Notification.Builder notification = new Notification.Builder(this);
            notification.setAutoCancel(true);
            notification.setTicker(getString(R.string.app_name) + text);
            PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(), 0);
            notification.setContentIntent(pi);
            notification.setSmallIcon(R.mipmap.icon_logo);
            Notification n = notification.getNotification();
            nm.notify(id, n);

            if (cancelTime > 0) {
                Message msg = new Message();
                msg.what = MSG_CANCEL_NOTIFY;
                msg.obj = nm;
                msg.arg1 = id;
                //取消通知
//                UIHandler.sendMessageDelayed(msg, cancelTime, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMedia() {
        UMImageMark umImageMark = new UMImageMark();
        umImageMark.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        umImageMark.setMarkBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_logo));
        imagelocal = new UMImage(this, R.mipmap.icon_logo);
        imagelocal.setThumb(new UMImage(this, R.mipmap.icon_logo));

        Log.e(TAG, "this.getFilesDir():" + this.getFilesDir());
        file = new File(this.getFilesDir() + "test.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (SocializeUtils.File2byte(file).length <= 0) {
            String content = "爱车评估分享";
            byte[] contentInBytes = content.getBytes();
            try {
                FileOutputStream fop = new FileOutputStream(file);
                fop.write(contentInBytes);
                fop.flush();
                fop.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(AboutActivity.this, "成功了", Toast.LENGTH_LONG).show();
            Log.e(TAG, "成功了");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Log.e(TAG, "失败" + t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(AboutActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };
}
