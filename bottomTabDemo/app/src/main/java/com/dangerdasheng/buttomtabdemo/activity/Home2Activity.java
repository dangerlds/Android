package com.dangerdasheng.buttomtabdemo.activity;

import android.view.KeyEvent;

import com.dangerdasheng.buttomtabdemo.R;
import com.dangerdasheng.buttomtabdemo.base.BaseActivity;
import com.dangerdasheng.buttomtabdemo.fragment.HomeFragment;
import com.dangerdasheng.buttomtabdemo.fragment.MessageFragment;
import com.dangerdasheng.buttomtabdemo.fragment.MineFragment;
import com.dangerdasheng.buttomtabdemo.util.ToastUtil;
import com.dangerdasheng.buttomtabdemo.view.widget.AlphaTabsIndicator;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;




public class Home2Activity extends BaseActivity {

    private AlphaTabsIndicator alphaTabsIndicator;
    private long exitTime = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home2_layout;
    }

    @Override
    protected void initView() {
        ViewPager mViewPger = (ViewPager) findViewById(R.id.mViewPager);
        Home2Activity.HomeAdapter mainAdapter = new Home2Activity.HomeAdapter(getSupportFragmentManager());
        mViewPger.setAdapter(mainAdapter);
        mViewPger.addOnPageChangeListener(mainAdapter);

        alphaTabsIndicator = (AlphaTabsIndicator) findViewById(R.id.alphaIndicator);
        alphaTabsIndicator.setViewPager(mViewPger);

        alphaTabsIndicator.getTabView(0).showNumber(10);
        alphaTabsIndicator.getTabView(1).showNumber(100);
        alphaTabsIndicator.getTabView(2).showPoint();
    }

    @Override
    protected void initData() {

    }


    private class HomeAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        private List<Fragment> fragments = new ArrayList<>();
        private String[] titles = {"首页", "消息", "我的"};

        public HomeAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(HomeFragment.newInstance(titles[0]));
            fragments.add(MessageFragment.newInstance(titles[1]));
            fragments.add(MineFragment.newInstance(titles[2]));
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (0 == position) {
                alphaTabsIndicator.getCurrentItemView().removeShow();
            } else if (2 == position) {
                alphaTabsIndicator.getCurrentItemView().removeShow();
            } else if (3 == position) {
                alphaTabsIndicator.removeAllBadge();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showToast("再按一次退出应用");
                exitTime = System.currentTimeMillis();
            } else {
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                startActivity(intent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
