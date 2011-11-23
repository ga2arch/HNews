package com.gabriele.hnews;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.Window;

import com.gabriele.hnews.adapters.MyFragmentPagerAdapter;
import com.gabriele.hnews.request.IHackernewsApi;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends FragmentActivity {
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_ITEM_TEXT);
        setContentView(R.layout.main);
        
        
        FragmentManager fm = getSupportFragmentManager();
        
        String[] frags = new String[]{IHackernewsApi.API_NEW,
        							  IHackernewsApi.API_PAGE,
        							  IHackernewsApi.API_ASK};
        
        MyFragmentPagerAdapter mAdapter = new MyFragmentPagerAdapter(fm, frags);

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        mPager.setAdapter(mAdapter);
        indicator.setViewPager(mPager);

    }
}