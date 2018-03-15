package com.bhxx.lovecar.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.values.GlobalValues;
import com.makeapp.android.util.AndroidUtil;
import com.makeapp.android.util.PackageUtil;
import com.makeapp.android.util.PreferencesUtil;
import com.makeapp.javase.lang.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import leeon.mobile.BBSBrowser.NetworkException;
import leeon.mobile.BBSBrowser.utils.HTTPUtil;

/**
 * Created by @dpy on 2016/12/19.
 * app更新
 *
 * @qq289513149.
 */

public class AppUpdateUtil {

    public static final String TAG = AppUpdateUtil.class.getSimpleName();

    //自动更新下载的app name
    public static final String APP_NAME = "lovecar.apk";
    //自动更新下载的app version 文件
    public static final String APP_VERSION_FILE = "androidversion";
    //自动更新本地保存目录
    public static String APP_LOCAL_URL = Environment.getExternalStorageDirectory() + "/" + APP_NAME;
    //最新的app version 及 发布信息
    //自动更新下载的app version 用于比较
    public static String CURRENT_APP_VERSION = "";//本地app的版本VersionName
    public static String SERVER_APP_VERSION = "";//服务器最新版本
    public static String APP_UPDATE_VERSION_FILE_URL = GlobalValues.IP + "upgrade/" + APP_VERSION_FILE;//txt路径
    public static String SPLIT_SYMBOL = ";";//分隔符号
    public static String serverApkUrl = "";//服务器apk的路径
    public static String serverApkContent = "";//升级内容

    //创建一个实例
    public static AppUpdateUtil newInstance(Context context) {
        return new AppUpdateUtil();
    }

    //版本说明书txt必须符合规范
    public static void checkVersion(final Context context, final Handler mHandler, final int what) {
        initVersion(context);
        PreferencesUtil.put(context, "updatetime", System.currentTimeMillis());
        new Thread() {
            public void run() {
                try {
                    Looper.prepare();
                    //删除升级完不用的文件
                    File f = new File(APP_LOCAL_URL);
                    if (f.exists()) f.delete();
                    String message = HTTPUtil.viewTextFile(APP_UPDATE_VERSION_FILE_URL, "gbk");
                    Log.e(TAG, "下载txt路径url---->" + APP_UPDATE_VERSION_FILE_URL + ";message---->" + message);

                    if (StringUtil.isValid(message)) {
                        String[] strings = message.split(SPLIT_SYMBOL);
                        if (strings != null && strings.length > 0) {
                            SERVER_APP_VERSION = strings[0];//版本
                            serverApkUrl = strings[1];//apk下载地址
                            serverApkContent = strings[2];//这个apk更新内容
                            if (!SERVER_APP_VERSION.equals(AppUpdateUtil.CURRENT_APP_VERSION)) {//版本不同就去升级或者还原
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.create();
                                builder.setTitle(context.getResources().getString(R.string.operation_upgrade_title));
                                builder.setCancelable(false);
                                if (StringUtil.isValid(serverApkContent)) {
                                    TextView textView = new TextView(context);
                                    textView.setText(serverApkContent);
                                    builder.setView(textView);
                                }
                                builder.setPositiveButton(context.getResources().getString(R.string.operation_ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (mHandler != null) {
                                            mHandler.sendEmptyMessage(what);
                                        }
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton(context.getResources().getString(R.string.operation_cancel), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            } else {
                                Log.e(TAG, "服务器版本与客户端版本一样,不用升级!");
                            }
                        }
                    }
                } catch (NetworkException e) {
                    Log.e(TAG, "upgrade e-->" + e.getMessage());
                    showErrorToast(context);
                }
                Looper.loop();
            }
        }.start();
    }

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "gbk");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
//                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //init version from manifest
    private static void initVersion(Context context) {
        AppUpdateUtil.CURRENT_APP_VERSION = PackageUtil.getPackageVersionName(context);
    }

    /**
     * 共用show error 对话框
     *
     * @param context
     */
    public static void showErrorToast(Context context) {
        Toast.makeText(context, context.getResources().getString(R.string.operation_upgrade_error), Toast.LENGTH_LONG).show();
    }

    //下载最新的apk包
    public void downloadFile(final Context context) {
        //带进度条的下载
        AndroidUtil.showVersionUpgrade(context, serverApkUrl);
    }

    //安装apk的方法
    private void install(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(APP_LOCAL_URL)), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
