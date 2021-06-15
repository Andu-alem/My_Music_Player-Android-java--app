package com.example.swipetabapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.swipetabapp.MusicListActivity.MusicListAdapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class SwipeActivity extends FragmentActivity implements
		ActionBar.TabListener {
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	public static ArrayList<SongListModel> songList;
	public static String[] arrPath;
	public ListView lv;
	SongListModel songModel;
	static SongListAdapter songListAdapter;
	SectionsPagerAdapter mSectionsPagerAdapter;
	public static MediaPlayer playerObj = null;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_swipe);

		sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		if(!sharedPref.contains("isShuffled")){
			editor.putBoolean("isShuffled", false);
			editor.putBoolean("isLooping",false);
			editor.commit();
		}
		// Set up the action bar.
		ColorDrawable color = new ColorDrawable(Color.parseColor("#2e78a5"));
		final ActionBar actionBar = getActionBar();
		actionBar.setBackgroundDrawable(color);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		songList = path_list();
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(),this);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.nowPlaying:
			Intent intent = new Intent(getApplicationContext(),MusicPlayerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("isFromNowPlaying", true);
			bundle.putParcelableArrayList("parcelSongList", songList);
			bundle.putInt("songIndex", 22);
			intent.putExtras(bundle);
			startActivity(intent);
			return true;
			
		case R.id.another:
			Intent i = new Intent(this,Act1.class);
			startActivity(i);
			return true;
			
		case R.id.gridView:
			Intent intentGrid = new Intent(this,ActivityGrid.class);
			startActivity(intentGrid);
			return true;
	    case R.id.customMusic:
			Intent intt = new Intent(this,CustomMusicActivity.class);
			startActivity(intt);
		         return true;
		case R.id.item3:
			   //Toast.makeText(this, "Selection Three", Toast.LENGTH_SHORT).show();
			Intent int4 = new Intent(this,ListImageActivity.class);
			startActivity(int4);
			return true;
        case R.id.item4:
	          //Toast.makeText(this, "Selection Four", Toast.LENGTH_SHORT).show();
        	Intent fileIntent = new Intent(this, ListFileActivity.class);
        	startActivity(fileIntent);
	          return true;
	    default:
	    	return super.onOptionsItemSelected(item);
        }  
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Delete Song");
		menu.add(0,v.getId(),0,"Delete");
		menu.add(0,v.getId(),0,"Sahre");
		menu.add(0,v.getId(),0,"Open");
	}
	@Override
	public boolean onContextItemSelected(MenuItem item){
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if(item.getTitle() == "Delete"){
			/*String dataPath;
			File file = new File(dataPath);
		    file.delete();*/
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setMessage("The song will permanently deleted");
			dialogBuilder.setPositiveButton("Cance",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "No has pressed", Toast.LENGTH_SHORT).show();
					
				}
			});
			dialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					songModel = (SongListModel) songListAdapter.getItem(info.position);
					String dataPath = songModel.getSongPath();
					File file = new File(dataPath);
				    file.delete();
					Toast.makeText(getApplicationContext(), "One song is deleted", Toast.LENGTH_SHORT).show();
					songListAdapter.remove(songListAdapter.getItem(info.position));
					songListAdapter.notifyDataSetChanged();
				}
			});
			AlertDialog dialog = dialogBuilder.create();
			dialog.show();
		}
		else if(item.getTitle() == "Share"){
			
		}
		else if(item.getTitle() == "Open"){
			
		}
		return true;
	}
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		Context applicationContext;

		public SectionsPagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			applicationContext = context;
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment(applicationContext);
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String tabLabel = null;
			switch (position) {
			case 0:
				tabLabel = 	getString(R.string.title_section1).toUpperCase();
				break;
			case 2:
				tabLabel = getString(R.string.title_section2).toUpperCase();
				break;
			case 3:
				tabLabel = getString(R.string.title_section3).toUpperCase();
				break;
			case 1:
				tabLabel = getString(R.string.title_section4).toUpperCase();
				break;
			}
			return tabLabel;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		ListView lv;
		SearchView search;
		ArrayAdapter<String> adapter;
		MyCustomAdapter adaptr;
		Context applicationContext;
		SongListModel sModel = new SongListModel();
		String[] data = {"Bob Marley","Hileselasie","Bob Marley3","Bob Marley4"};
		String[] desc = {"Could you be loved", "Ethiopian King", "One Love", "Redemption Song"};
		int[] images = {R.drawable.im5, R.drawable.im18,R.drawable.im14,R.drawable.im15,R.drawable.im17,R.drawable.im18,R.drawable.imteddy,
				R.drawable.imgy, R.drawable.im};
		String[] data3 = {"Ethiopia","England","Netherland","Germen","Australia","America","China","UAE","Eriterea","Russia"};
		
		
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment(Context mContext) {
			applicationContext = mContext;
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
			int tabLayout = 0;
			boolean isCustom = false;
			String[] dataSet = {};
			switch(position){
			case 0:
				View view = inflater.inflate(R.layout.music_list, container, false);
				ArrayList<String> mList = new ArrayList<String>(Arrays.asList(arrPath));
				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mList);
				ListView lv = (ListView) view.findViewById(R.id.lV1);
				songListAdapter = new SongListAdapter(applicationContext,songList,arrPath);
				lv.setAdapter(songListAdapter);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
							Intent intent = new Intent(applicationContext,MusicPlayerActivity.class);
							Bundle bundle = new Bundle();
							bundle.putBoolean("isFromNowPlaying", false);
							bundle.putParcelableArrayList("parcelSongList", songList);
							bundle.putInt("songIndex", arg2);
							intent.putExtras(bundle);
							startActivity(intent);						
					}
				});
				registerForContextMenu(lv);
				return view;
			case 2:
				tabLayout = R.layout.tab_1;
				isCustom = true;
				break;
			case 3:
				tabLayout = R.layout.tab_3;
				dataSet = data3;
				break;
			case 1:
				View v = inflater.inflate(R.layout.grid_act, container, false);
				GridView gd = (GridView) v.findViewById(R.id.gridView1);
				gd.setAdapter(new ImageAdapter(getActivity()));
				gd.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View v, int position,
							long id) {
						Intent intent = new Intent(applicationContext,GridImageViewActivity.class);
						intent.putExtra("imgIndex", position);
						intent.putExtra("imagePath",images);
						applicationContext.startActivity(intent);						
					}
				});
				return v;
				
			}
			View view = inflater.inflate(tabLayout, container, false);
			lv  = (ListView) view.findViewById(R.id.listView1);
			if(isCustom==true){
				adaptr = new MyCustomAdapter(getActivity(),data,desc,images);
				lv.setAdapter(adaptr);
			}else{
				adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,dataSet);
				lv.setAdapter(adapter);
			}	
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					String dataset = (String) arg0.getItemAtPosition(position);
					Toast.makeText(getActivity(), "Item clicked"+dataset, Toast.LENGTH_SHORT).show();
					
				}
			});
			return view;
		}
	}		

	public ArrayList<SongListModel> path_list(){
		String [] pathArray;
		//MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		songList = new ArrayList<SongListModel>();
		Boolean isSDPresent =  android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		//If SD card is present then use this:
		if(isSDPresent){
				final String[] columns ={ MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.ALBUM};
				final String orderBy = MediaStore.Audio.Media.TITLE;
				Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null,null, orderBy);
				//Total number of files
				int count = cursor.getCount();
				//Create an array to store path to all the pathes
				pathArray = new String[count];
				for (int i = 0; i < count; i++) {
					SongListModel songListM = new SongListModel();
				    cursor.moveToPosition(i);
				    int dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
				    int titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
				    int artistColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
				    int albumColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
				    //Store the path of the image
				    String dataPath= cursor.getString(dataColumnIndex);	
				    pathArray[i] = dataPath;
				   
				    String title = cursor.getString(titleColumnIndex);
				    String artist = cursor.getString(artistColumnIndex);
				    String album = cursor.getString(albumColumnIndex);
				    
				    songListM.setSongPath(dataPath);
				    songListM.setSongTitle(title);
				    songListM.setSongArtist(artist);
				    songListM.setSongAlbum(album);
				    songList.add(songListM);
				    
				}
				cursor.close();
				arrPath = pathArray;
		}
		return songList;	
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
}
class MyCustomAdapter extends ArrayAdapter{
	Context applicationContext;
	int[] imageArray;
	String[] titleArray;
	String[] descArray;
	public MyCustomAdapter(Context context, String[] titles1, String[] descriptions1,int[] img1) {
		super(context, R.layout.custom_list, R.id.idTitle,titles1);
		// TODO Auto-generated constructor stub
		this.imageArray = img1;
		this.titleArray = titles1;
		this.descArray = descriptions1;
		applicationContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		//Inflating the layout
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.custom_list, parent, false);
		
		//Get the reference to the view objects
		ImageView myImage = (ImageView) row.findViewById(R.id.idPic);
		TextView myTitle = (TextView) row.findViewById(R.id.idTitle);
		TextView myDesc = (TextView) row.findViewById(R.id.idDescription);
		
		//Providing the element of an array
		myImage.setImageResource(imageArray[position]);
		myTitle.setText(titleArray[position]);
		myDesc.setText(descArray[position]);
		
		myImage.setTag(imageArray[position]);
		myImage.setOnClickListener(new View.OnClickListener() {
			
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Integer imgResource = (Integer) v.getTag();
			  //Toast.makeText(applicationContext, "you have pressed "+pos,Toast.LENGTH_SHORT ).show();
				
				Intent intent = new Intent(applicationContext,ShowImageActivity.class);
				intent.putExtra("ImageResource", imgResource);
				applicationContext.startActivity(intent);
			}
		});
		
		return row;
	}	
	
}
