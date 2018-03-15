package com.bhxx.lovecar.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.bhxx.lovecar.R;

/**
 * Created by @dpy on 2017/1/12.
 *
 * @qq289513149.
 */

public class CustomDialog {
    private static void dialogTitleLineColor(Context context, Dialog dialog, int color) {
        String dividers[] = {
                "android:id/titleDividerTop", "android:id/titleDivider"
        };

        for (int i = 0; i < dividers.length; ++i) {
            int divierId = context.getResources().getIdentifier(dividers[i], null, null);
            View divider = dialog.findViewById(divierId);
            if (divider != null) {
                divider.setBackgroundColor(color);
            }
        }
    }

    public static void dialogTitleLineColor(Context context, Dialog dialog) {
        if (dialog != null) {
            dialogTitleLineColor(context, dialog, context.getResources().getColor(R.color.com_line_bg));
        }
    }
}
