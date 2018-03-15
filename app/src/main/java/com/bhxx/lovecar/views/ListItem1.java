package com.bhxx.lovecar.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bhxx.lovecar.R;


/**
 * Created by @dpy on 2016/11/23.
 * listview item，带一个 icon 和一行文字 以及一个箭头
 * @qq289513149.
 */

public class ListItem1 extends FrameLayout {
    private View mIcon;
    private TextView mText;

    public ListItem1(Context context) {
        super(context);
    }

    public ListItem1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ListItem1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_list_item_1, this);
        mIcon = findViewById(R.id.icon);
        mText = (TextView) findViewById(R.id.title);


        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ListItem1);
        String title = array.getString(R.styleable.ListItem1_itemTitle);
        int icon = array.getResourceId(R.styleable.ListItem1_itemIcon, R.mipmap.icon_logo);

        boolean center = array.getBoolean(R.styleable.ListItem1_itemCenter, false);
        boolean arrow = array.getBoolean(R.styleable.ListItem1_itemArrow, false);
        array.recycle();

        if (title == null) title = "";
        mText.setText(title);
        mIcon.setBackgroundResource(icon);

        if (center) {
            findViewById(R.id.arrow).setVisibility(GONE);

            LinearLayout.LayoutParams layoutParams;
            layoutParams = (LinearLayout.LayoutParams) mText.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.weight = 0;
            ((LinearLayout) findViewById(R.id.rootLayout)).setGravity(Gravity.CENTER);
        }
        if(arrow){
            findViewById(R.id.arrow).setVisibility(VISIBLE);
        }else{
            findViewById(R.id.arrow).setVisibility(GONE);
        }
    }

    public void setText(String s) {
        if (s == null) {
            return;
        }

        mText.setText(s);
    }
}
