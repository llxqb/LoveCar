package com.bhxx.lovecar.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.makeapp.android.util.AndroidUtil;
import com.makeapp.android.util.DialogUtil;

import java.io.File;

/**
 * Created by @dpy on 2016/11/30.
 *
 * @qq289513149.
 */

public class DialogUtils extends DialogUtil {

    public static Dialog createBottomDialog(Context context, int layoutId) {
        return setBottomDialog(new Dialog(context, R.style.style_dialog_full_screen), context, layoutId);
    }

    public static void showBottomDialog(final Activity context, final String title, final String ok,
                                        final String cancel, final String type, final DialogCallBack dialogCallBack) {
        final Dialog dialog = createBottomDialog(context, R.layout.setting_dialog_bottom);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.dialog_btn_top) {
                    dialogCallBack.onTopClick(type);
                } else if (id == R.id.dialog_btn_bottom) {
                    dialogCallBack.onBottomClick(type);
                }
                dialog.dismiss();
            }
        };
        dialog.findViewById(R.id.dialog_btn_top).setOnClickListener(listener);
        ((Button) (dialog.findViewById(R.id.dialog_btn_top))).setText(ok);
        dialog.findViewById(R.id.dialog_btn_bottom).setOnClickListener(listener);
        ((Button) (dialog.findViewById(R.id.dialog_btn_bottom))).setText(cancel);
        dialog.findViewById(R.id.dialog_btn_cancel).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.dialog_tv_title)).setText(title);
        dialog.show();
    }

    public static File showImageDialog(final Activity context, final String title, final String ok,
                                       final String cancel, final String type, final DialogCallBack dialogCallBack) {
        final File file = AndroidUtil.getApplicationTempFile(context, ".tmp", "jpg");
        final Dialog dialog = createBottomDialog(context, R.layout.setting_dialog_bottom);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.dialog_btn_top) {
                    dialogCallBack.onTopClick(type);
//                    ActivityUtils.takePicture(context, file);
                } else if (id == R.id.dialog_btn_bottom) {
                    dialogCallBack.onBottomClick(type);
//                    ActivityUtils.selectPicture(context);
                }
                dialog.dismiss();
            }
        };
        dialog.findViewById(R.id.dialog_btn_top).setOnClickListener(listener);
        ((Button) (dialog.findViewById(R.id.dialog_btn_top))).setText(ok);
        dialog.findViewById(R.id.dialog_btn_bottom).setOnClickListener(listener);
        ((Button) (dialog.findViewById(R.id.dialog_btn_bottom))).setText(cancel);
        dialog.findViewById(R.id.dialog_btn_cancel).setOnClickListener(listener);
        ((TextView) dialog.findViewById(R.id.dialog_tv_title)).setText(title);
        dialog.show();
        return file;
    }

    public static Dialog setBottomDialog(Dialog dialog, Context context, int layoutId) {
        View layout = LayoutInflater.from(context).inflate(layoutId, null);
        layout.setMinimumWidth(10000);

        Window w = dialog.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = -1000;
        lp.gravity = Gravity.BOTTOM;

        dialog.onWindowAttributesChanged(lp);
        dialog.setContentView(layout);
        dialog.setCanceledOnTouchOutside(true);

        return dialog;
    }

    public interface DialogCallBack {
        public void onTopClick(String type);

        public void onBottomClick(String type);
    }
}
