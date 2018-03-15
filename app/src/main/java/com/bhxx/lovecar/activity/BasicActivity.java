package com.bhxx.lovecar.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectInit;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.views.CustomProgressDialog;
import com.makeapp.javase.lang.StringUtil;

public abstract class BasicActivity extends AppCompatActivity {
    protected Toast toast;
    protected NesConnectionChangeReceiver changeReceiver;
    public CustomProgressDialog progressDialog = null;
    private AlertDialog alertDialog;

    @InjectInit
    protected abstract void init();

    protected abstract void click(View view);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();

    }


    protected void showToast(int resId) {
        if (toast == null) {
            toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
        }
        toast.show();
    }

    protected void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param msg
     */
    protected void showToast(String msg, int duration) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, duration);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 方法说明 对话框显示
     *
     * @param
     * @return
     * @author
     */
    protected void showProgressDialog(Context context) {
        if (null == progressDialog) {
            progressDialog = CustomProgressDialog.createDialog(context);
        }
        progressDialog.show();
    }

    protected void showProgressDialog(Context context, String messager) {
        if (null == progressDialog) {
            progressDialog = CustomProgressDialog.createDialog(context);
        }
        progressDialog.setMessage(messager);
        progressDialog.show();
    }

    /**
     * 方法说明 对话框关闭
     *
     * @param
     * @return
     * @author
     */
    protected void dismissProgressDialog() {
        if (null != progressDialog && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 设置可见
     *
     * @param view
     */
    protected void setVisible(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 设置不可见
     *
     * @param view
     */
    protected void setGone(View view) {
        view.setVisibility(View.GONE);
    }

    /**
     * 设置暂不可见
     *
     * @param view
     */
    protected static void setInVisible(View view) {
        view.setVisibility(View.INVISIBLE);
    }


    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        changeReceiver = new NesConnectionChangeReceiver();
        this.registerReceiver(changeReceiver, filter);
    }

    private void unRegisterReceiver() {
        this.unregisterReceiver(changeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterReceiver();
    }

    protected class NesConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
                // 改变背景或者 处理网络的全局变量'
                showToast("网络不可用");
            } else {
                // showToast(R.string.ok_net);
            }
        }
    }

    protected boolean isLogin(Context mContext) {
        //判断当前用户是否有效即是否已经登录
        String userId = App.app.getData(UserPreferences.USER_ID);
        if (StringUtil.isValid(userId)) {//用户存在
            return true;
        }
        return false;
    }

    /*
    * 页面"确定""取消" 对话框
    * */
    protected void showDialog(String title, String msg, View.OnClickListener clickOk,
                              View.OnClickListener clickCancel,
                              String okButton,
                              String cancelButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.from(this).inflate(R.layout.layout_alertdialog, null);
        View titleLine = view.findViewById(R.id.titleLine);
        View btnLine = view.findViewById(R.id.btnLine);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        if (StringUtil.isValid(title)) {
            titleView.setText(title);
        } else {
            titleView.setVisibility(View.GONE);
            titleLine.setVisibility(View.GONE);
        }
        TextView messageView = (TextView) view.findViewById(R.id.message);
        messageView.setText(msg);
        TextView okView = (TextView) view.findViewById(R.id.ok);
        okView.setText(okButton);

        TextView cancelView = (TextView) view.findViewById(R.id.cancel);
        if (StringUtil.isValid(cancelButton)) {
            cancelView.setText(cancelButton);
        } else {
            cancelView.setVisibility(View.GONE);
            btnLine.setVisibility(View.GONE);
        }

        alertDialog = builder.setView(view).show();
        if (clickOk != null) {
            okView.setOnClickListener(clickOk);
        } else {
            okView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
        }
        if (clickCancel != null) {
            cancelView.setOnClickListener(clickCancel);
        } else {
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
            });
        }
    }

    /*
    * 取消dialog
    * */
    public void cancelAlertDialog() {
        if (alertDialog != null) {
            alertDialog.cancel();
            alertDialog = null;
        }
    }

}
