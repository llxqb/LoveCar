package com.bhxx.lovecar.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by @dpy on 2016/12/24.
 *
 * @qq289513149.
 */
public class AutoHeightListView extends ListView {
    public AutoHeightListView(Context context) {
        super(context);
    }

    public AutoHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoHeightListView(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getLayoutParams() != null && getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            // 使ListView支持wrap_content的高度
            // see http://www.jayway.com/2012/10/04/how-to-make-the-height-of-a-gridview-wrap-its-content/
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}


