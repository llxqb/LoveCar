package com.bhxx.lovecar.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bhxx.lovecar.utils.LogUtils;
import com.makeapp.javase.lang.StringUtil;

/**
 * Created by @dpy on 2016/12/28.
 * 极光推送的广播接受，全局配置文件注册，包括点击等监听的拦截
 *
 * @qq289513149.
 */

public class MyJpushBroadcast extends BroadcastReceiver {

    public static final String REGISTRATION = "cn.jpush.android.intent.REGISTRATION";
    public static final String MESSAGE_RECEIVED = "cn.jpush.android.intent.MESSAGE_RECEIVED";
    public static final String NOTIFICATION_RECEIVED = "cn.jpush.android.intent.NOTIFICATION_RECEIVED";
    public static final String NOTIFICATION_OPENED = "cn.jpush.android.intent.NOTIFICATION_OPENED";
    public static final String ACTION_RICHPUSH_CALLBACK = "cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK";
    public static final String CONNECTION = "cn.jpush.android.intent.CONNECTION";
    public String messageServer = "";
    public static final String splitSymbol = ",";//服务器推送数据返回分割形式
//    public static PersonalPushListener personalPushListener;

    public MyJpushBroadcast() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        Log.e("MyJpushBroadcast", "onReceive action----->" + action);
        LogUtils.i("action=" + action);
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            messageServer = bundle.getString("cn.jpush.android.ALERT");
            LogUtils.i("messageServer=" + messageServer);
            Log.e("MyJpushBroadcast", "onReceive cn.jpush.android.ALER messageServer----->" + messageServer);
        }
        if (StringUtil.isValid(action) && StringUtil.isValid(messageServer)) {
            if (REGISTRATION.equals(action)) {
            } else if (MESSAGE_RECEIVED.equals(action)) {
            } else if (NOTIFICATION_RECEIVED.equals(action)) {//接受极光推送的广播过来
            } else if (NOTIFICATION_OPENED.equals(action)) {//点击通知栏中极光推送的广播

                if (StringUtil.isValid(messageServer)) {
                    String[] contentArray = messageServer.split(splitSymbol);
                    int len = contentArray.length;
//                    Intent infoIntent = new Intent(context, UserWebViewActivity.class);
//                    infoIntent.setFlags(268435456);
                    String pushUrl = "";
                    if (len == 2) {//长度2个是全局的数据结构
//                        infoIntent.putExtra(OrderStateActivity.UURL, contentArray[1]);//全局的链接地址
//                        infoIntent.putExtra(MainActivity.MY_URL, contentArray[1]);//全局的链接地址
                        pushUrl = contentArray[1];
                    } else if (len == 3) {//长度3个是个人的数据结构
//                        infoIntent.putExtra(OrderStateActivity.UURL, contentArray[2]);//个人信息的链接地址
//                        infoIntent.putExtra(MainActivity.MY_URL, contentArray[2]);//个人信息的链接地址
                        pushUrl = contentArray[2];
                    }
//                    context.startActivity(infoIntent);
//                    Log.e("MyJpushBroadcast", "this.personalPushListener----->" + this.personalPushListener);
//                    if (this.personalPushListener != null) {
//                        this.personalPushListener.pushUrlResult(pushUrl);
//                    }
                }

            } else if (ACTION_RICHPUSH_CALLBACK.equals(action)) {
            } else if (CONNECTION.equals(action)) {
            }
        }
    }

//    public static class MyPushUrlActivity extends Activity {
//        public void setPushUrlListener(PersonalPushListener pushListener) {
//            personalPushListener = pushListener;
//        }
//    }
}
