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
import com.makeapp.javase.lang.StringUtil;


/**
 * Created by @dpy on 2016/11/23.
 * listview item，带一个 icon 和一行文字 以及一个箭头
 *
 * @qq289513149.
 */

public class UserItem1 extends FrameLayout {

    private TextView mText;
    private TextView mContent;

    public UserItem1(Context context) {
        super(context);
    }

    public UserItem1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserItem1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.layout_user_item_1, this);
        mText = (TextView) findViewById(R.id.title);
        mContent = (TextView) findViewById(R.id.content);


        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.UserItem1);
        String title = array.getString(R.styleable.UserItem1_userTitle);
        String content = array.getString(R.styleable.UserItem1_userContent);

        boolean center = array.getBoolean(R.styleable.UserItem1_userCenter, false);
        boolean arrow = array.getBoolean(R.styleable.UserItem1_userArrow, false);
        array.recycle();

        if (title == null) title = "";
        mText.setText(title);
        if (StringUtil.isValid(content)) {
            mContent.setText(content);
        }
        if (center) {
            findViewById(R.id.arrow).setVisibility(GONE);

            LinearLayout.LayoutParams layoutParams;
            layoutParams = (LinearLayout.LayoutParams) mText.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.weight = 0;
            ((LinearLayout) findViewById(R.id.rootUserLayout)).setGravity(Gravity.CENTER);
        }
        if (arrow) {
            findViewById(R.id.arrow).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.arrow).setVisibility(GONE);
        }
    }

    public void setText(String s) {
        if (s == null) {
            return;
        }

        mText.setText(s);
    }

    public void setContent(String s) {
        if (s == null) {
            return;
        }

        mContent.setText(s);
    }

    public String getContent() {
        return mContent.getText().toString();
    }
}
