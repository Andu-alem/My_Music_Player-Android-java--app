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

public class GallaryActivity extends FragmentActivity{
	
	ViewPager mViewPager;
	ArrayList<String> imgPath;
	ViewPagerAdapter viewPagerAdapter;
	public static int m;
	public static boolean isDflt;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallary);
		
		imgPath = imageUriList();
		int imgIndex = getIntent().getIntExtra("imgIndex", 0);
		viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
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
	
	public class ViewPagerAdapter extends FragmentPagerAdapter{
		Context applicationContext;

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new NewFragment(position);
		    /*Bundle args = new Bundle();
			args.putString("imgUri", images.get(position));
			args.putInt("imageIndex",position);
			fragment.setArguments(args);*/
			return fragment;
		}

		@Override
		public int getCount() {
			return imgPath.size();
		}
		
	}
	
	
	public class NewFragment extends Fragment{
		int position;
		Uri imageUri;
		public NewFragment(int pos){
			position = pos;
			imageUri = Uri.parse(imgPath.get(position));
		}
		//int m = mViewPager.getCurrentItem();
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View view = inflater.inflate(R.layout.image_activity, container, false);
			ImageView img = (ImageView) view.findViewById(R.id.image1);
			img.setImageURI(imageUri);
			return view;
		}

	}
	public ArrayList<String> imageUriList(){
		ArrayList<String> img = new ArrayList<String>();
		String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String targetPath = ExternalStorageDirectoryPath + "/Download";
		File targetDirectory = new File(targetPath);
		File[] files = targetDirectory.listFiles();
		//String file = files[0].getAbsolutePath();
			
		for (File file: files){
			img.add(file.getAbsolutePath());
		}
		return img;
	}
}
