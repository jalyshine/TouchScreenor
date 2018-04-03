package com.jaly.touchscreenor;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainFragmentPageAdapter extends FragmentPagerAdapter{

	private List<Fragment> mFragments; 
	
	public MainFragmentPageAdapter(FragmentManager fm, List<Fragment> mFragments) {
		super(fm);
		this.mFragments = mFragments;
	}

	@Override
	public Fragment getItem(int position) { 
		return mFragments.get(position);
	}

	@Override
	public int getCount() { 
		return mFragments.size();
	}

}
