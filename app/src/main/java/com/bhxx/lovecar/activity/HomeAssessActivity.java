package com.bhxx.lovecar.activity;
/**
 * 主页的估价
 */
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.IntentUtil;

@InjectLayer(R.layout.activity_home_assess)
public class HomeAssessActivity extends BasicActivity {

    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView addcar_back;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView addcar_tv;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.addcar_back:
                finish();
                break;
            case R.id.addcar_tv:
                IntentUtil.setIntent(this,WriteCarinfoActivity.class);
                break;
        }
    }
}
