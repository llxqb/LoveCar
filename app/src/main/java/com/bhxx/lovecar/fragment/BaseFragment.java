package com.bhxx.lovecar.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.inject.InjectInit;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.views.CustomProgressDialog;
import com.makeapp.javase.lang.StringUtil;

public abstract  class BaseFragment extends Fragment {
    private Toast toast;
    public CustomProgressDialog progressDialog = null;
    private AlertDialog alertDialog;

    @InjectInit
    protected abstract void init();

    protected abstract void click(View view);

    protected void showToast(int resId) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
        }
        toast.show();
    }

    protected void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);
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

    /*
* 页面"确定""取消" 对话框
* */
    protected void showDialog(Context context,String title, String msg, View.OnClickListener clickOk,
                              View.OnClickListener clickCancel,
                              String okButton,
                              String cancelButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.from(context).inflate(R.layout.layout_alertdialog, null);
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
