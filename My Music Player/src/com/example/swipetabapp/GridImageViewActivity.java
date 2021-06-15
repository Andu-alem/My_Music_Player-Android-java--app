package com.example.swipetabapp;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class GridImageViewActivity extends FragmentActivity{

	ViewPager mViewPager;
	int[] imgId;
	PagerViewAdapter viewPagerAdapter;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallary);
		
		imgId = getIntent().getIntArrayExtra("imagePath");
		int imgIndex = getIntent().getIntExtra("imgIndex", 0);
		Toast.makeText(getApplicationContext(), "img resource "+imgId[imgIndex], Toast.LENGTH_SHORT).show();
		viewPagerAdapter = new PagerViewAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(viewPagerAdapter);
		mViewPager.setCurrentItem(imgIndex);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub				
			}
		});	
	}
	
	public class PagerViewAdapter extends FragmentPagerAdapter{
		Context applicationContext;

		public PagerViewAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new NewGallaryFragment(position);
		    /*Bundle args = new Bundle();
			args.putString("imgUri", images.get(position));
			args.putInt("imageIndex",position);
			fragment.setArguments(args);*/
			return fragment;
		}

		@Override
		public int getCount() {
			return imgId.length;
		}
		
	}
	
	
	public class NewGallaryFragment extends Fragment{
		int imgPath;
		public NewGallaryFragment(int pos){
			imgPath = imgId[pos];
		}
		//int m = mViewPager.getCurrentItem();
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View view = inflater.inflate(R.layout.image_activity, container, false);
			ImageView img = (ImageView) view.findViewById(R.id.image1);
			img.setImageResource(imgPath);
			return view;
		}

	}
}
