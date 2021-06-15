package com.example.swipetabapp;

import com.example.swipetabapp.SwipeActivity.DummySectionFragment;
import com.example.swipetabapp.SwipeActivity.SectionsPagerAdapter;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;

public class Act2 extends FragmentActivity{
	GridView gridView;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	AudioManager ma;
	//String[] lists = {"Name","age","Blen","Strong","More","Often","Height","Makbez","Othello"};
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.active);
	//	gridView = (GridView) findViewById(R.id.gridView1);
		//ArrayAdapter adapter = new ArrayAdapter(this,1,lists);
		//gridView.setAdapter(adapter);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		ma = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		ma.playSoundEffect(AudioManager.FX_KEY_CLICK);
	}
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	//ma = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	//ma.playSoundEffect(AudioManager.FX_KEY_CLICK);
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		int[] images = {R.drawable.im5, R.drawable.im18, R.drawable.im15, R.drawable.im6};
		
		
		
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Create a new TextView and set its text to the fragment's section
			// number argument value.
			/*TextView textView = new TextView(getActivity());
			textView.setGravity(Gravity.CENTER);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return textView;*/
			Bundle args = getArguments();
			int position = args.getInt(ARG_SECTION_NUMBER);
			String[] dataSet = {};
			
			View view = inflater.inflate(R.layout.act_fragment, container, false);
			ImageView image = (ImageView) view.findViewById(R.id.imageView11);
			image.setImageResource(images[2]);
			
			
			//adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,dataSet);										
			return view;
		}
	}

}
