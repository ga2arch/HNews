package com.gabriele.hnews.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gabriele.hnews.fragments.ItemListFragment;
import com.viewpagerindicator.TitleProvider;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter implements TitleProvider {

	private String[] frags;
	
	public MyFragmentPagerAdapter(FragmentManager fm, String[] frags) {
		super(fm);
		this.frags = frags;
	}

	@Override
	public Fragment getItem(int position) {
		Bundle bundle = new Bundle();
		bundle.putString("location", frags[position]);
		ItemListFragment ilf = new ItemListFragment();
		ilf.setArguments(bundle);
		return ilf;
	}

	@Override
	public int getCount() {
		return frags.length;
	}

	@Override
	public String getTitle(int position) {
		return frags[position];
	}

}
