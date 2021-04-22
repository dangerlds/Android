package com.dangerdasheng.buttomtabdemo.view.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.dangerdasheng.buttomtabdemo.listener.OnTabChangedListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;


/**
 * =============================
 * Author:   liudasheng
 * Version:  1.0
 * DateTime: 2020/08/14
 * Function: 底部标签栏通用视图
 * =============================
 */

public class AlphaTabsIndicator extends LinearLayout {
    private ViewPager mViewPager;
    private OnTabChangedListener mListener;
    private List<AlphaTabView> mTabViews;
    private boolean ISINIT;

    /**
     * 子View数量
     */
    private int mChildCounts;

    /**
     * 当前的条目索引
     */
    private int mCurrentItem = 0;

    public AlphaTabsIndicator(Context context) {
        this(context, null);
    }

    public AlphaTabsIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlphaTabsIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        post(new Runnable() {
            @Override
            public void run() {
                isInit();
            }
        });
    }

    public void setViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
        init();
    }

    public void setOnTabChangedListner(OnTabChangedListener listner) {
        this.mListener = listner;
        isInit();
    }


    public AlphaTabView getCurrentItemView() {
        isInit();
        return mTabViews.get(mCurrentItem);
    }

    public AlphaTabView getTabView(int tabIndex) {
        isInit();
        return mTabViews.get(tabIndex);
    }

    public void removeAllBadge() {
        isInit();
        for (AlphaTabView alphaTabView : mTabViews) {
            alphaTabView.removeShow();
        }
    }

    public void setTabCurrenItem(int tabIndex) {
        if (tabIndex < mChildCounts && tabIndex > -1) {
            mTabViews.get(tabIndex).performClick();
        } else {
            throw new IllegalArgumentException("IndexOutOfBoundsException");
        }
    }


    private void isInit(){
        if (!ISINIT){
            init();
        }
    }

    private void init(){
        ISINIT = true;
        mTabViews = new ArrayList<>();
        mChildCounts = getChildCount();
        if (null != mViewPager){
            if (null == mViewPager.getAdapter()){
                throw  new NullPointerException("viewpager的adapter为null");
            }
            if (mViewPager.getAdapter().getCount() != mChildCounts){
                throw new IllegalArgumentException("子view数量必须和ViewPager" +
                        "条目数量一致");
            }
            mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        }
        for (int i = 0; i < mChildCounts ; i++){
            if (getChildAt(i) instanceof AlphaTabView){
                AlphaTabView tabView = (AlphaTabView)getChildAt(i);
                mTabViews.add(tabView);
                tabView.setOnClickListener(new MyOnclickListener(i));
            }else {
                throw new IllegalArgumentException("TabIndicator的子View必须是TabView");
            }
        }
        mTabViews.get(mCurrentItem).setIconAlpha(1.0f);
    }

    private class MyOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //滑动时的透明度动画
            if (positionOffset > 0) {
                mTabViews.get(position).setIconAlpha(1 - positionOffset);
                mTabViews.get(position + 1).setIconAlpha(positionOffset);
            }
            //滑动时保存当前按钮索引
            mCurrentItem = position;
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            resetState();
            mTabViews.get(position).setIconAlpha(1.0f);
            mCurrentItem = position;

        }
    }

    private class MyOnclickListener implements OnClickListener{

        private int currentIndex;

        public MyOnclickListener(int i){
            this.currentIndex = i;
        }
        @Override
        public void onClick(View v) {
            resetState();
            mTabViews.get(currentIndex).setIconAlpha(1.0f);
            if (null != mListener){
                mListener.onTabSelected(currentIndex);
            }
            if (null != mViewPager){
                //不能使用平滑滚动，否者颜色改变会乱
                mViewPager.setCurrentItem(currentIndex,false);
            }
            //点击是保存当前按钮索引
            mCurrentItem = currentIndex;
        }
    }
    /**
     * 重置所有按钮的状态
     */
    private void resetState() {
        for (int i = 0; i < mChildCounts; i++) {
            mTabViews.get(i).setIconAlpha(0);
        }
    }

    private static final String STATE_INSTANCE = "instance_state";
    private static final String STATE_ITEM = "state_item";

    /**
     * @return 当view被销毁的时候，保存数据
     */

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE,super.onSaveInstanceState());
        bundle.putInt(STATE_ITEM,mCurrentItem);
        return bundle;
    }

    /**
     * @param  state 用于恢复数据使用
     */

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle)state;
            mCurrentItem = bundle.getInt(STATE_ITEM);
            if (null == mTabViews || mTabViews.isEmpty()){
                super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
                return;
            }
            //重置所有按钮状态
            resetState();;
            //恢复点击的条目
            mTabViews.get(mCurrentItem).setIconAlpha(1.0f);
            super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
        }else {
            super.onRestoreInstanceState(state);
        }
    }
}
