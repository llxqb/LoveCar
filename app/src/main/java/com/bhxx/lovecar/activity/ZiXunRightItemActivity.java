package com.bhxx.lovecar.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.lovecar.R;
import com.bhxx.lovecar.adapter.DragAdapter;
import com.bhxx.lovecar.adapter.ZiXunRightItemAdapter;
import com.bhxx.lovecar.application.App;
import com.bhxx.lovecar.beans.ZiXunModel;
import com.bhxx.lovecar.beans.ZiXunRightItemBean;
import com.bhxx.lovecar.db.ZiXunItemDao;
import com.bhxx.lovecar.entity.ObjectEntity;
import com.bhxx.lovecar.entity.ZiXunRightItemEntity;
import com.bhxx.lovecar.utils.ActivityCollector;
import com.bhxx.lovecar.utils.LogUtils;
import com.bhxx.lovecar.utils.UserPreferences;
import com.bhxx.lovecar.views.DragGridView;
import com.bhxx.lovecar.views.MyGridView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZiXunRightItemActivity extends BasicActivity implements AdapterView.OnItemClickListener {

    private MyGridView mOtherGv;
    private DragGridView mUserGv;
    private List<ZiXunRightItemBean> mUserList = new ArrayList<>();
    private List<ZiXunRightItemBean> mOtherList = new ArrayList<>();
    private static ZiXunRightItemAdapter mOtherAdapter;
    private DragAdapter mUserAdapter;
    private static TextView sure;
    private ImageView back;
    //    private static List<ZiXunRightItemBean> listmAll = null;
    private static List<ZiXunRightItemBean> listmUser = null;
    private List<ZiXunRightItemBean> listmOtherGv = null;
    private String CurrentCheckTitle;
    private boolean issure = false;//记录是否点击确定按钮

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void click(View view) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zi_xun_right_item);

        CurrentCheckTitle = App.app.getData("CurrentCheckTitle");

        initView();
    }


    public void initView() {
        listmUser = ZiXunItemDao.queryAllType("mime");
        listmOtherGv = ZiXunItemDao.queryAllType("other");
        if (listmUser != null) {
            for (int i = 0; i < listmUser.size(); i++) {
                ZiXunRightItemBean itemBean = new ZiXunRightItemBean();
                itemBean.setType("mime");
                itemBean.setContentId(listmUser.get(i).getContentId());
                itemBean.setContent(listmUser.get(i).getContent());
                mUserList.add(itemBean);
            }
        }
        if (listmOtherGv != null) {
            for (int i = 0; i < listmOtherGv.size(); i++) {
                ZiXunRightItemBean itemBean = new ZiXunRightItemBean();
                itemBean.setType("other");
                itemBean.setContent(listmOtherGv.get(i).getContent());
                itemBean.setContentId(listmOtherGv.get(i).getContentId());
                mOtherList.add(itemBean);
//                mOtherList.add(listmOtherGv.get(i).getContent().toString());
            }
        }

        back = (ImageView) findViewById(R.id.back);
        sure = (TextView) findViewById(R.id.sure);
        mUserGv = (DragGridView) findViewById(R.id.userGridView);
        mOtherGv = (MyGridView) findViewById(R.id.otherGridView);
        mUserAdapter = new DragAdapter(this, mUserList, true, Integer.parseInt(CurrentCheckTitle));
        mOtherAdapter = new ZiXunRightItemAdapter(this, mOtherList, false);
        mUserGv.setAdapter(mUserAdapter);
        mOtherGv.setAdapter(mOtherAdapter);
        mUserGv.setOnItemClickListener(this);
        mOtherGv.setOnItemClickListener(this);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserAdapter.setIsShowDelete(false);
                //变化界面
                change();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void onEventMainThread(ObjectEntity entity) {
        if (!TextUtils.isEmpty(entity.getKey())) {
            finish();
            EventBus.getDefault().post(new ZiXunRightItemEntity(entity.getKey()));
        }
    }

    private void changeList(List<ZiXunRightItemBean> list0, String z_type, List<ZiXunRightItemBean> list1) {
        for (int i = 0; i < list0.size(); i++) {
            ZiXunRightItemBean bean = new ZiXunRightItemBean();
            bean.setType(z_type);
            bean.setContentId(list0.get(i).getContentId());
            bean.setContent(list0.get(i).getContent());
            list1.add(bean);
        }
    }


    /**
     * 获取点击的Item的对应View，
     * 因为点击的Item已经有了自己归属的父容器MyGridView，所有我们要是有一个ImageView来代替Item移动
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     * 用于存放我们移动的View
     */
    private ViewGroup getMoveViewGroup() {
        //window中最顶层的view
        ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(this);
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final String moveChannel,
                          final GridView clickGridView, final boolean isUser) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标/
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // 判断点击的是DragGrid还是OtherGridView
                if (isUser) {
                    mOtherAdapter.setVisible(true);
                    mOtherAdapter.notifyDataSetChanged();
                    mUserAdapter.remove();
                } else {
                    mUserAdapter.setVisible(true);
                    mUserAdapter.notifyDataSetChanged();
                    mOtherAdapter.remove();
                    change();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        LogUtils.i("sss");
        switch (parent.getId()) {
            case R.id.userGridView:
                //position为 0，1 的不可以进行任何操作
                if (position != 0) {
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final ZiXunRightItemBean channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                        mOtherAdapter.setVisible(false);
                        //添加到最后一个
                        mOtherAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    //获取终点的坐标
                                    mOtherGv.getChildAt(mOtherGv.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, channel.getContent(), mUserGv, true);
                                    mUserAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
                break;
            case R.id.otherGridView:
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ZiXunRightItemBean channel = ((ZiXunRightItemAdapter) parent.getAdapter()).getItem(position);
                    mUserAdapter.setVisible(false);
                    //添加到最后一个
                    mUserAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                mUserGv.getChildAt(mUserGv.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel.getContent(), mOtherGv, false);
                                mOtherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);
                }
                break;
            default:
                break;
        }
    }

    //增加其他栏目的item
    public static void addOthergv(ZiXunRightItemBean additem) {
        mOtherAdapter.addItem(additem);
    }

    //显示删除布局
    public static void showsure(boolean isShowDelete) {
        sure.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);//设置删除按钮是否显示
    }

    private void change() {
        issure = true;
        listmUser = new ArrayList<>();
        listmOtherGv = new ArrayList<>();

        changeList(mUserList, "mime", listmUser);
        changeList(mOtherList, "other", listmOtherGv);
        ZiXunItemDao.clearTable();
        ZiXunItemDao.insertTypes(listmUser);
        ZiXunItemDao.insertTypes(listmOtherGv);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (issure) {
            EventBus.getDefault().post(new ZiXunRightItemEntity(CurrentCheckTitle));
        }
        super.onDestroy();
    }
}





