package com.bhxx.lovecar.activity;
/**
 * 车友圈-搜索
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.MyMementsTuijianAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.CircleModel;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

public class SearchMomentsActivity extends BasicActivity implements View.OnClickListener {

    private Drawable mIconSearchClear;
    //缓存上一次文本框内是否为空
    private boolean isnull = true;
    private String searchContent = "";

    PullToRefreshLayout my_pull;
    PullableListView my_list;
    EditText search_text;
    ImageView back;
    List<CircleModel> list;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_moments);
        initView();
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);

        my_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                if (!TextUtils.isEmpty(searchContent)) {
                    page = 1;
                    searchlist(searchContent);
                }
                my_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                my_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                if (!TextUtils.isEmpty(searchContent)) {
                    page++;
                    searchlist(searchContent);
                }
                my_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                my_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
                showToast(Constant.FINISH);
            }
        });

    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        my_pull = (PullToRefreshLayout) findViewById(R.id.my_pull);
        my_list = (PullableListView) findViewById(R.id.my_list);

        search_text = (EditText) findViewById(R.id.search_text);
        search_text.setOnTouchListener(search_text_OnTouch);
        search_text.addTextChangedListener(search_text_OnChange);

        final Resources res = getResources();
        mIconSearchClear = res.getDrawable(R.mipmap.delete_search);
        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
            /*判断是否是“GO”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    searchContent = search_text.getText().toString();
                    if (searchContent.equals("")) {
                        showToast("请输入搜索内容");
                        return false;
                    }
                    showProgressDialog(SearchMomentsActivity.this);
                    //执行搜索
                    searchlist(searchContent);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    protected void click(View view) {
    }

    private void searchlist(String searchContent) {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(App.app.getData(UserPreferences.USER_ID))) {
            params.put("userId", App.app.getData(UserPreferences.USER_ID));
        }
        params.put("name", searchContent);
        params.put("pageNo", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.CIRCLE_TUIJIAN, 1, "search", params, new MyStringCallback());


    }

    private View.OnTouchListener search_text_OnTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    int curX = (int) event.getX();
                    if (curX > v.getWidth() - 38
                            && !TextUtils.isEmpty(search_text.getText())) {
                        search_text.setText("");
                        int cacheInputType = search_text.getInputType();// backup  the input type
                        search_text.setInputType(InputType.TYPE_NULL);// disable soft input
                        search_text.onTouchEvent(event);// call native handler
                        search_text.setInputType(cacheInputType);// restore input  type
                        return true;// consume touch even
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    };


    private TextWatcher search_text_OnChange = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(s)) {
                if (!isnull) {
                    //这里指删除输入框内容后执行
                    search_text.setCompoundDrawablesWithIntrinsicBounds(null,
                            null, null, null);
                    isnull = true;
                }
            } else {
                if (isnull) {
                    search_text.setCompoundDrawablesWithIntrinsicBounds(null,
                            null, mIconSearchClear, null);
                    isnull = false;
                }
            }
        }
    };

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast(Constant.ERROR_WEB);
            dismissProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<CircleModel> listBean = JsonUtils.getBean(response, CommonListBean.class, CircleModel.class);
                if (listBean.getResultCode().equals("0000")) {
                    dismissProgressDialog();
                    if(listBean.getRows().size()>0){
                        MyMementsTuijianAdapter myMementsTuijianAdapter = new MyMementsTuijianAdapter(listBean.getRows(), SearchMomentsActivity.this, R.layout.mymoments_tuijian_item);
                        my_list.setAdapter(myMementsTuijianAdapter);
                    }else {
                        showToast("没有查询到您所检索的结果");
                    }
                } else {
                    showToast(Constant.ERROR_WEB);
                }
            }
        }
    }

}
