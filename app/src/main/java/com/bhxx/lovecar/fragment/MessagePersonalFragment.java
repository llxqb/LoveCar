package com.bhxx.lovecar.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.pc.util.Handler_Inject;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.MessageAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.DateUtils;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.PullToRefreshLayout;
import com.bhxx.lovecar.views.PullableListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/5.
 * 我的-消息-个人消息
 *
 * @qq289513149.
 */

public class MessagePersonalFragment extends BaseFragment {


    @Override
    protected void init() {

    }

    @Override
    protected void click(View view) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_message_personal, container, false);

        io.rong.imkit.fragment.ConversationListFragment fragment = (io.rong.imkit.fragment.ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);

        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();

        fragment.setUri(uri);


        return view;
    }
}
