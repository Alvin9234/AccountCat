package com.alvin.AccountCat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import com.alvin.fragment.DummyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alvin on 2015/4/10.
 */
public class DetailFragmentActivity extends FragmentActivity {
    private ViewPager viewPager_detailFragment;
    private PagerTabStrip pagerTabStrip_detailFragment;
    private List<Fragment> list = null;
    private String[] arrTabTitles = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);
        initData();
    }
    /**
     *  通用初始化方法
     */
    private void initData() {
        viewPager_detailFragment = (ViewPager) findViewById(R.id.viewPager_detailFragment);
        pagerTabStrip_detailFragment = (PagerTabStrip) findViewById(R.id.pagerTabStrip_detailFragment);
        list = new ArrayList<Fragment>();
        arrTabTitles = new String[]{"收入","支出"};
        for (int i = 0; i < arrTabTitles.length; i++) {
            DummyFragment fragment = new DummyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("tabIndex", i);
            fragment.setArguments(bundle);
            list.add(fragment);
        }
        pagerTabStrip_detailFragment.setTextColor(Color.BLUE);
        pagerTabStrip_detailFragment.setDrawFullUnderline(true);// 画标签的下划线
        pagerTabStrip_detailFragment.setTabIndicatorColor(Color.RED);//标签下面的线颜色
        viewPager_detailFragment.setAdapter(new MyPagerAdapter(
                getSupportFragmentManager(), list,arrTabTitles));
    }

    class MyPagerAdapter extends FragmentPagerAdapter{
        private List<Fragment> list = null;
        private String[] arrTitles =  null;
        public MyPagerAdapter(FragmentManager fm, List<Fragment> list,String[] arrTitles) {
            super(fm);
            this.list = list;
            this.arrTitles = arrTitles;
        }
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            // 获取标签的标题
            return arrTitles[position];
        }
    }
}