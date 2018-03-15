package com.bhxx.lovecar.activity;
/**
 * 主页-搜索
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.HomeActivityCarAdapter;
import com.bhxx.lovecar.beans.CarModel;
import com.bhxx.lovecar.beans.CommonListBean;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.JsonUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class SearchActivity extends BasicActivity implements View.OnClickListener {

    private Drawable mIconSearchClear;
    //缓存上一次文本框内是否为空
    private boolean isnull = true;
    private String searchContent = "";

    PullToRefreshLayout my_pull;
    ListView my_list;
    TextView search_back;
    EditText search_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);

    }

    private void initView() {
        my_list = (ListView) findViewById(R.id.search_listview);

        search_back = (TextView) findViewById(R.id.search_back);
        search_back.setOnClickListener(this);
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

                    showProgressDialog(SearchActivity.this, "正在搜索...");
                    //执行搜索
                    searchList(searchContent);
                    return true;
                }
                return false;
            }
        });
    }

    private void searchList(String searchContent) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("carName", searchContent);
        params.put("isPublish", "0");
        params.put("sign", SingUtils.getMd5SignMsg(params));

        MyOkHttp.postMap(GlobalValues.HOME_SEARCH, 1, "search", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast(Constant.ERROR_WEB);
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<CarModel> searchlistBean = JsonUtils.getBeans(response, CommonListBean.class, CarModel.class);

                dismissProgressDialog();
                if (searchlistBean.getResultCode().equals("0000")) {
                    if (searchlistBean.getRows().size() > 0) {
                        HomeActivityCarAdapter adapter = new HomeActivityCarAdapter(searchlistBean.getRows(), SearchActivity.this, R.layout.home_car_item);
                        my_list.setAdapter(adapter);

                    } else {
                        showToast("没有查询到您所检索的结果");
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_back:
                finish();
                break;
        }
    }

    @Override
    protected void click(View view) {
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


}
