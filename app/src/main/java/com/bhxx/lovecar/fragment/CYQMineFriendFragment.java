package com.bhxx.lovecar.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.utils.CommonCallback;
import com.bhxx.lovecar.utils.LoadImage;
import com.bhxx.lovecar.utils.MyOkHttp;
import com.bhxx.lovecar.utils.SingUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.values.Constant;
import com.bhxx.lovecar.values.GlobalValues;
import com.bhxx.lovecar.views.AutoHeightListView;
import com.bhxx.lovecar.views.CircleImageView;
import com.bhxx.lovecar.views.FastLocationBarView;
import com.bhxx.lovecar.views.PinnedHeaderListView;
import com.bhxx.lovecar.views.SectionedBaseAdapter;
import com.makeapp.javase.lang.StringUtil;
import com.makeapp.javase.util.MapUtil;
import com.makeapp.javase.util.PingYinUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;
import okhttp3.Call;

/**
 * Created by @dpy on 2016/12/6.
 * 车友圈-好友-我的好友
 *
 * @qq289513149.
 */
public class CYQMineFriendFragment extends BaseFragment {

    private static final String TAG = CYQMineFriendFragment.class.getSimpleName();
    private Activity mActivity;
    private View rootView;
    private PinnedHeaderListView listView;
    private TextView dialog;
    private FastLocationBarView sideBar;
    private FriendSectionedAdapter friendSectionedAdapter;
    private AutoHeightListView autoHeightListView;
    private List<String> headList = new ArrayList<String>();//头部字母
    private List<Map> inviteList = new ArrayList<Map>();//新的好友申请列表集合
    private List<Map> friendList = new ArrayList<Map>();//我的好友列表
    private List<List<Map>> oKFriendList = new ArrayList<List<Map>>();//整理好A-Z我的好友列表
    private NewFriendListAdapter listAdapter;
    private View headInviteView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_cyq_minefriend, null);
        mActivity = getActivity();
        initView();
        initEvent();
        headInviteView = mActivity.getLayoutInflater().inflate(R.layout.layout_cyq_jy_invitelist, null);
        autoHeightListView = (AutoHeightListView) headInviteView.findViewById(R.id.autoHeightListView);
        listView.addHeaderView(headInviteView);
        startApplyFriendListTask();//申请人列表
        startFriendListTask();//自己的好友列表
        EventBus.getDefault().register(mActivity);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(getActivity());
    }

    private void onEventMainThread(Object o) {
        Log.e(TAG, "====CYQMineFriendFragment onEventMainThread===");
        startApplyFriendListTask();//申请人列表
        startFriendListTask();//自己的好友列表
    }


    //初始化控件
    private void initView() {
        listView = (PinnedHeaderListView) rootView.findViewById(R.id.pinnedListView);
        sideBar = (FastLocationBarView) rootView.findViewById(R.id.sidebar);
        dialog = (TextView) rootView.findViewById(R.id.dialog);
    }

    //初始化控件事件
    private void initEvent() {
        sideBar.setTextView(dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new FastLocationBarView.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = friendSectionedAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    listView.setSelection(position);
                }
            }
        });
    }

    @Override
    protected void init() {
    }

    @Override
    protected void click(View view) {

    }

    class FriendSectionedAdapter extends SectionedBaseAdapter implements PinnedHeaderListView.PinnedSectionedHeaderAdapter, SectionIndexer {


        @Override
        public Object getItem(int section, int position) {
            return oKFriendList.get(section).get(position);
        }

        @Override
        public long getItemId(int section, int position) {
            return position;
        }

        @Override
        public int getSectionCount() {//7个分段
            return headList.size();
        }

        @Override
        public int getCountForSection(int section) {//每个分段下面的数量
            List<Map> itemList = oKFriendList.get(section);
            return itemList.size();
        }


        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {
            LinearLayout layout = null;
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.layout_cyq_friend_item, null);
            } else {
                layout = (LinearLayout) convertView;
            }
            layout.setTag(oKFriendList.get(section).get(position));
            final Map map = oKFriendList.get(section).get(position);
            ImageLoader.getInstance().displayImage(MapUtil.getString(map, Constant.CAR_USER_AVATAR), (CircleImageView) layout.findViewById(R.id.civHead), LoadImage.getDefaultOptions());
            ((TextView) layout.findViewById(R.id.textItem)).setText(MapUtil.getString(map, Constant.CAR_USER_FULLNAME));

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (RongIM.getInstance() != null) {
                        if (!TextUtils.isEmpty(MapUtil.getString(map, Constant.CAR_TOUSER))) {
                            RongIM.getInstance().startPrivateChat(getActivity(), "" + MapUtil.getString(map, Constant.CAR_TOUSER) + "", MapUtil.getString(map, Constant.CAR_USER_FULLNAME));
                        }
                    }
                }
            });
            return layout;
        }

        @Override
        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {//设置每个分段的头部
            LinearLayout layout = null;
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.layout_cyq_friend_item_head, null);
            } else {
                layout = (LinearLayout) convertView;
            }
            layout.setTag(headList.get(section));
            ((TextView) layout.findViewById(R.id.textItem)).setText(headList.get(section));
            return layout;
        }


        @Override
        public Object[] getSections() {
            return new Object[0];
        }

        @Override
        public int getPositionForSection(int section) {
            int indexCount = 0;
            for (int i = 0; i < getSectionCount(); i++) {
                String sortStr = headList.get(i);
                indexCount += getCountForSection(i);
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return indexCount - getCountForSection(i) + i;
                }
            }
            return -1;
        }
    }

    class NewFriendListAdapter extends BaseAdapter {

        LayoutInflater inflater;

        public NewFriendListAdapter() {
            inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return inviteList.size();
        }

        @Override
        public Object getItem(int i) {
            return inviteList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Map map = inviteList.get(i);
            View layout = inflater.inflate(R.layout.layout_cyq_jy_newfrienditem, null);
            ImageLoader.getInstance().displayImage(MapUtil.getString(map, Constant.CAR_USER_AVATAR), (CircleImageView) layout.findViewById(R.id.civHead), LoadImage.getDefaultOptions());
            TextView nameTextView = (TextView) layout.findViewById(R.id.textItem);
            if (map != null) {
                nameTextView.setText(MapUtil.getString(map, Constant.CAR_USER_FULLNAME));
            }
            Button addButton = (Button) layout.findViewById(R.id.btnAdd);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(mActivity, mActivity.getResources().getString(R.string.operation_tip), String.format(mActivity.getResources().getString(R.string.op_add_to_friend), MapUtil.getString(map, Constant.CAR_USER_FULLNAME)),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cancelAlertDialog();
                                    String toUser = MapUtil.getString(map, Constant.CAR_USER_ID);
                                    startAddToUser(toUser);
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cancelAlertDialog();
                                    String toUser = MapUtil.getString(map, Constant.CAR_USER_ID);
                                    startRefuseToUser(toUser);
                                }
                            }, mActivity.getResources().getString(R.string.operation_accept), mActivity.getResources().getString(R.string.operation_refuse));

                }
            });
            return layout;
        }
    }

    //统一添加好友异步请求
    private void startAddToUser(String toUser) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_TOUSER, toUser);
        params.put(Constant.CAR_ISAGREE, "0");//0同意

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_TOUSER, toUser);
        hashMap.put(Constant.CAR_ISAGREE, "0");//0同意
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startAddToUser hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.FRIEND_AGREEFRIEND, "list", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startAddToUser response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
                            startApplyFriendListTask();//刷新申请人列表
                            startFriendListTask();//刷新自己的好友列表
                        } else {
//                            showToast(jsonObject.optString("resultDesc"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //拒绝好友异步请求
    private void startRefuseToUser(String toUser) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_TOUSER, toUser);
        params.put(Constant.CAR_ISAGREE, "1");//1不同意

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_TOUSER, toUser);
        hashMap.put(Constant.CAR_ISAGREE, "1");//1不同意
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startRefuseToUser hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.FRIEND_REFUSEFRIEND, "list", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startRefuseToUser response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));
                            startApplyFriendListTask();
                        } else {
//                            showToast(jsonObject.optString("resultDesc"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //申请人列表异步请求
    private void startApplyFriendListTask() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_TOUSER, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_ISAGREE, "1");//1未同意

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_TOUSER, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_ISAGREE, "1");//1未同意
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startApplyFriendListTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.FRIEND_LIST, "list", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startApplyFriendListTask response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));
                            JSONArray rowJSONArray = jsonObject.optJSONArray(Constant.CAR_ROWS);
                            inviteList.clear();
                            listAdapter = new NewFriendListAdapter();
                            autoHeightListView.setAdapter(listAdapter);//刷新申请的好友
                            listAdapter.notifyDataSetChanged();
                            if (rowJSONArray != null) {
                                int length = rowJSONArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject item = rowJSONArray.optJSONObject(i);
                                    Map inviteMap = new HashMap();
                                    inviteMap.put(Constant.CAR_USER_AVATAR, GlobalValues.IP1 + item.optString(Constant.CAR_USER_AVATAR));
                                    inviteMap.put(Constant.CAR_USER_FULLNAME, item.optString(Constant.CAR_USER_FULLNAME));
                                    inviteMap.put(Constant.CAR_TOUSER, item.optString(Constant.CAR_TOUSER));
                                    inviteMap.put(Constant.CAR_USER_ID, item.optString(Constant.CAR_USER_ID));
                                    inviteList.add(inviteMap);
                                }
                                listAdapter = new NewFriendListAdapter();
                                autoHeightListView.setAdapter(listAdapter);//刷新申请的好友
                            }

                        } else {
//                            showToast(jsonObject.optString("resultDesc"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //好友列表异步请求
    private void startFriendListTask() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        params.put(Constant.CAR_ISAGREE, "0");

        String sign = SingUtils.getMd5SignMsg(params);
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        hashMap.put(Constant.CAR_USER_ID, App.app.getData(UserPreferences.USER_ID));
        hashMap.put(Constant.CAR_ISAGREE, "0");
        hashMap.put(Constant.CAR_KEY, sign);
        Log.e(TAG, "startFriendListTask hashMap----->" + hashMap);
        MyOkHttp.postMap(GlobalValues.FRIEND_LIST, "list", hashMap, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e(TAG, "e---->" + e);
//                showToast(Constant.ERROR_WEB);
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e(TAG, "startFriendListTask response---->" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (null != jsonObject) {
                        String resultCode = jsonObject.optString("resultCode");
                        if (Constant.CAR_RESPONSE_OK.equals(resultCode)) {
//                            showToast(jsonObject.optString("resultDesc"));
                            JSONArray rowJSONArray = jsonObject.optJSONArray(Constant.CAR_ROWS);
                            headList.clear();
                            friendList.clear();
                            oKFriendList.clear();
                            friendSectionedAdapter = new FriendSectionedAdapter();
                            listView.setAdapter(friendSectionedAdapter);
                            friendSectionedAdapter.notifyDataSetChanged();
                            if (rowJSONArray != null) {
                                int length = rowJSONArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject item = rowJSONArray.optJSONObject(i);
                                    Map friendMap = new HashMap();
                                    friendMap.put(Constant.CAR_USER_AVATAR, GlobalValues.IP1 + item.optString(Constant.CAR_USER_AVATAR));
                                    friendMap.put(Constant.CAR_USER_FULLNAME, item.optString(Constant.CAR_USER_FULLNAME));
                                    friendMap.put(Constant.CAR_USER_ID, item.optString(Constant.CAR_USER_ID));
                                    friendMap.put(Constant.CAR_TOUSER, item.optString(Constant.CAR_TOUSER));
                                    friendList.add(friendMap);
//                                    if ("0".equals(item.optString(Constant.CAR_STATUS))) {//自己的好友
//                                        Map friendMap = new HashMap();
//                                        friendMap.put(Constant.CAR_USER_AVATAR, GlobalValues.IP1 + item.optString(Constant.CAR_USER_AVATAR));
//                                        friendMap.put(Constant.CAR_USER_FULLNAME, item.optString(Constant.CAR_USER_FULLNAME));
//                                        friendMap.put(Constant.CAR_TOUSER, item.optString(Constant.CAR_TOUSER));
//                                        friendList.add(friendMap);
//                                    } else if ("1".equals(item.optString(Constant.CAR_STATUS))) {//向自己申请的好友
//                                        Map inviteMap = new HashMap();
//                                        inviteMap.put(Constant.CAR_USER_AVATAR, GlobalValues.IP1 + item.optString(Constant.CAR_USER_AVATAR));
//                                        inviteMap.put(Constant.CAR_USER_FULLNAME, item.optString(Constant.CAR_USER_FULLNAME));
//                                        inviteMap.put(Constant.CAR_TOUSER, item.optString(Constant.CAR_TOUSER));
//                                        inviteList.add(inviteMap);
//                                    }
                                }
                                //整理自己的好友ABCDEFG
                                refreshFriendAZList();
                            }

                        } else {
//                            showToast(jsonObject.optString("resultDesc"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshFriendAZList() {
        List<Map> AMapList = new ArrayList<Map>();
        List<Map> BMapList = new ArrayList<Map>();
        List<Map> CMapList = new ArrayList<Map>();
        List<Map> DMapList = new ArrayList<Map>();
        List<Map> EMapList = new ArrayList<Map>();
        List<Map> FMapList = new ArrayList<Map>();
        List<Map> GMapList = new ArrayList<Map>();

        List<Map> HMapList = new ArrayList<Map>();
        List<Map> JMapList = new ArrayList<Map>();
        List<Map> KMapList = new ArrayList<Map>();
        List<Map> LMapList = new ArrayList<Map>();
        List<Map> MMapList = new ArrayList<Map>();
        List<Map> NMapList = new ArrayList<Map>();

        List<Map> OMapList = new ArrayList<Map>();
        List<Map> PMapList = new ArrayList<Map>();
        List<Map> QMapList = new ArrayList<Map>();
        List<Map> RMapList = new ArrayList<Map>();
        List<Map> SMapList = new ArrayList<Map>();
        List<Map> TMapList = new ArrayList<Map>();

        List<Map> WMapList = new ArrayList<Map>();
        List<Map> XMapList = new ArrayList<Map>();
        List<Map> YMapList = new ArrayList<Map>();
        List<Map> ZMapList = new ArrayList<Map>();
        int size = friendList.size();
        for (int i = 0; i < size; i++) {//先转拼音
            Map map = friendList.get(i);
            String name = MapUtil.getString(map, Constant.CAR_USER_FULLNAME);
            String pinyin = PingYinUtil.getPingYin(name);
            map.put("pinyin", pinyin);
            Log.e(TAG, "name-->" + name + " pinyin-->" + pinyin);
        }
        for (int i = 0; i < size; i++) {
            Map map = friendList.get(i);
            String pinyin = MapUtil.getString(map, "pinyin");
            String name = pinyin.substring(0, 1);

            if (StringUtil.isValid(name) && "A".equals(StringUtil.getFirstCharUpper(name))) {
                AMapList.add(map);
            } else if (StringUtil.isValid(name) && "B".equals(StringUtil.getFirstCharUpper(name))) {
                BMapList.add(map);
            } else if (StringUtil.isValid(name) && "C".equals(StringUtil.getFirstCharUpper(name))) {
                CMapList.add(map);
            } else if (StringUtil.isValid(name) && "D".equals(StringUtil.getFirstCharUpper(name))) {
                DMapList.add(map);
            } else if (StringUtil.isValid(name) && "E".equals(StringUtil.getFirstCharUpper(name))) {
                EMapList.add(map);
            } else if (StringUtil.isValid(name) && "F".equals(StringUtil.getFirstCharUpper(name))) {
                FMapList.add(map);
            } else if (StringUtil.isValid(name) && "G".equals(StringUtil.getFirstCharUpper(name))) {
                GMapList.add(map);
            } else if (StringUtil.isValid(name) && "H".equals(StringUtil.getFirstCharUpper(name))) {
                HMapList.add(map);
            } else if (StringUtil.isValid(name) && "J".equals(StringUtil.getFirstCharUpper(name))) {
                JMapList.add(map);
            } else if (StringUtil.isValid(name) && "K".equals(StringUtil.getFirstCharUpper(name))) {
                KMapList.add(map);
            } else if (StringUtil.isValid(name) && "L".equals(StringUtil.getFirstCharUpper(name))) {
                LMapList.add(map);
            } else if (StringUtil.isValid(name) && "M".equals(StringUtil.getFirstCharUpper(name))) {
                MMapList.add(map);
            } else if (StringUtil.isValid(name) && "N".equals(StringUtil.getFirstCharUpper(name))) {
                NMapList.add(map);
            } else if (StringUtil.isValid(name) && "O".equals(StringUtil.getFirstCharUpper(name))) {
                OMapList.add(map);
            } else if (StringUtil.isValid(name) && "P".equals(StringUtil.getFirstCharUpper(name))) {
                PMapList.add(map);
            } else if (StringUtil.isValid(name) && "Q".equals(StringUtil.getFirstCharUpper(name))) {
                QMapList.add(map);
            } else if (StringUtil.isValid(name) && "R".equals(StringUtil.getFirstCharUpper(name))) {
                RMapList.add(map);
            } else if (StringUtil.isValid(name) && "S".equals(StringUtil.getFirstCharUpper(name))) {
                SMapList.add(map);
            } else if (StringUtil.isValid(name) && "T".equals(StringUtil.getFirstCharUpper(name))) {
                TMapList.add(map);
            } else if (StringUtil.isValid(name) && "W".equals(StringUtil.getFirstCharUpper(name))) {
                WMapList.add(map);
            } else if (StringUtil.isValid(name) && "X".equals(StringUtil.getFirstCharUpper(name))) {
                XMapList.add(map);
            } else if (StringUtil.isValid(name) && "Y".equals(StringUtil.getFirstCharUpper(name))) {
                YMapList.add(map);
            } else if (StringUtil.isValid(name) && "Z".equals(StringUtil.getFirstCharUpper(name))) {
                ZMapList.add(map);
            }
        }
        if (AMapList.size() > 0) {
            headList.add("A");
            oKFriendList.add(AMapList);
        }
        if (BMapList.size() > 0) {
            headList.add("B");
            oKFriendList.add(BMapList);
        }
        if (CMapList.size() > 0) {
            headList.add("C");
            oKFriendList.add(CMapList);
        }
        if (DMapList.size() > 0) {
            headList.add("D");
            oKFriendList.add(DMapList);
        }
        if (EMapList.size() > 0) {
            headList.add("E");
            oKFriendList.add(EMapList);
        }
        if (FMapList.size() > 0) {
            headList.add("F");
            oKFriendList.add(FMapList);
        }
        if (GMapList.size() > 0) {
            headList.add("G");
            oKFriendList.add(GMapList);
        }
        if (HMapList.size() > 0) {
            headList.add("H");
            oKFriendList.add(HMapList);
        }
        if (JMapList.size() > 0) {
            headList.add("J");
            oKFriendList.add(JMapList);
        }
        if (KMapList.size() > 0) {
            headList.add("K");
            oKFriendList.add(KMapList);
        }
        if (LMapList.size() > 0) {
            headList.add("L");
            oKFriendList.add(LMapList);
        }
        if (MMapList.size() > 0) {
            headList.add("M");
            oKFriendList.add(MMapList);
        }
        if (NMapList.size() > 0) {
            headList.add("N");
            oKFriendList.add(NMapList);
        }
        if (PMapList.size() > 0) {
            headList.add("P");
            oKFriendList.add(PMapList);
        }
        if (OMapList.size() > 0) {
            headList.add("O");
            oKFriendList.add(OMapList);
        }
        if (RMapList.size() > 0) {
            headList.add("R");
            oKFriendList.add(RMapList);
        }
        if (SMapList.size() > 0) {
            headList.add("S");
            oKFriendList.add(SMapList);
        }
        if (TMapList.size() > 0) {
            headList.add("T");
            oKFriendList.add(TMapList);
        }
        if (WMapList.size() > 0) {
            headList.add("W");
            oKFriendList.add(WMapList);
        }
        if (XMapList.size() > 0) {
            headList.add("X");
            oKFriendList.add(XMapList);
        }
        if (YMapList.size() > 0) {
            headList.add("Y");
            oKFriendList.add(YMapList);
        }
        if (ZMapList.size() > 0) {
            headList.add("Z");
            oKFriendList.add(ZMapList);
        }
        Log.e(TAG, "headList.size():" + headList.size());
        Log.e(TAG, "oKFriendList.size():" + oKFriendList.size());

        friendSectionedAdapter = new FriendSectionedAdapter();
        listView.setAdapter(friendSectionedAdapter);
        friendSectionedAdapter.notifyDataSetChanged();
    }

}
