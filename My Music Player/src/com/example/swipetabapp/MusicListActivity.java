package com.example.swipetabapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicListActivity extends Activity{
	public ArrayList<SongListModel> songList;
	String[] arrPath;
	public static MediaPlayer playerObj = null;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	SongListModel sModel;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		
		songList = path_list();
		ArrayList<String> mList = new ArrayList<String>(Arrays.asList(arrPath));
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mList);
		ListView lv = (ListView) findViewById(R.id.lV1);
		MusicListAdapter adapter = new MusicListAdapter(this,songList,arrPath);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				sModel = (SongListModel) arg0.getItemAtPosition(arg2);
				//Toast.makeText(applicationContext, "Click is working  ", Toast.LENGTH_SHORT).show();
				if(MusicPlayer.mpObject != null){
					//Toast.makeText(getApplicationContext(), "Yes there is Mediaplayer object", Toast.LENGTH_SHORT).show();
					MusicPlayer.mpObject.stop();
					//MusicPlayer.mpObject.release();
				}
				if(NowPlayingActivity.mpObject != null){
					NowPlayingActivity.mpObject.stop();
				}
				
				Intent intent = new Intent(getApplicationContext(),MusicPlayer.class);
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("parcelSongList", songList);
				bundle.putInt("songIndex", arg2);
				intent.putExtras(bundle);
				startActivity(intent);
				
			}
		});
		registerForContextMenu(lv);
	}
	@Override
	public boolean onNavigateUp(){
		//to enable actionbar backbutton
		finish();
		return true;
	}
	@Override
	public void onResume(){
		super.onResume();
		//Toast.makeText(getApplicationContext(), "Yes there is Mediaplayer object: onResume", Toast.LENGTH_SHORT).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player_menu, menu);
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("This is context menu title");
		menu.add(0,v.getId(),0,"Delete");
		menu.add(0,v.getId(),0,"Sahre");
		menu.add(0,v.getId(),0,"Open");
	}
	@Override
	public boolean onContextItemSelected(MenuItem item){
		if(item.getTitle() == "Delete"){
			/*String dataPath;
			File file = new File(dataPath);
		    file.delete();*/
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setMessage("You sure You want to delete this item?");
			dialogBuilder.setPositiveButton("No",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "No has pressed", Toast.LENGTH_SHORT).show();
					
				}
			});
			dialogBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Yes has pressed", Toast.LENGTH_SHORT).show();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.nowPlaying:
			Intent intent = new Intent(this,NowPlayingActivity.class);
			Bundle bundle = new Bundle();
			if(MusicPlayer.mpObject != null){
				playerObj = MusicPlayer.mpObject;
				MusicPlayer.mpObject.stop();
				if(NowPlayingActivity.mpObject != null){
					NowPlayingActivity.mpObject.stop();
				}
				if(sharedPref.getBoolean("isNowPlaying", false) == true){
					bundle.putInt("songIndex", NowPlayingActivity.index);
					bundle.putInt("currentPosition", NowPlayingActivity.mpObject.getCurrentPosition());
				}else{
					bundle.putInt("songIndex", MusicPlayer.index);
					bundle.putInt("currentPosition", MusicPlayer.mpObject.getCurrentPosition());
				}
				//intent.putExtra("songPathList",arrPath);			
			}	
			bundle.putParcelableArrayList("parcelSongList", songList);
			intent.putExtras(bundle);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
		
	//public MediaMetadataRetriever metaRetriever;
	public ArrayList<SongListModel> path_list(){
		String [] pathArray;
		//MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		songList = new ArrayList<SongListModel>();
		Boolean isSDPresent =  android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		//If SD card is present then use this:
		if(isSDPresent){
				final String[] columns ={ MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST};
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
	public class MusicListAdapter extends ArrayAdapter{
		List<SongListModel> songList;
		SongListModel sModel = new SongListModel();
		Context applicationContext;
		public String[] songPathArray;
		public MusicListAdapter(Context context, List<SongListModel> titles, String[] pathArray) {
			super(context, R.layout.list_of_music, R.id.textView1,titles);
			// TODO Auto-generated constructor stub
			this.songList = titles;
			this.songPathArray = pathArray;
			applicationContext = context;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			//Inflating the layout
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View row = inflater.inflate(R.layout.list_of_music, parent, false);
			
			//Get the reference to the view objects
			sModel = songList.get(position);
			String title = sModel.getSongTitle();
			String artist = sModel.getSongArtist();
			
			
			TextView songTitle = (TextView) row.findViewById(R.id.textView1);
			TextView songArtist = (TextView) row.findViewById(R.id.textView2);
			String[] musicNameArray = sModel.getSongPath().split("/");
		    String musicName = musicNameArray[musicNameArray.length-1];
		    
	    	if(title != ""){
	    		songTitle.setText(title);
	    		songArtist.setText(artist);
	    	}else{
	    		int index = musicName.lastIndexOf(".");
	    		songTitle.setText(musicName.substring(0, index));
	    	    songArtist.setText("");
	    	}
			songArtist.setClickable(false);
			songTitle.setTag(sModel.getSongPath());
			songTitle.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					/*
					String musicPath = (String) arg0.getTag();
					if(MusicPlayer.mpObject != null){
						//Toast.makeText(getApplicationContext(), "Yes there is Mediaplayer object", Toast.LENGTH_SHORT).show();
						MusicPlayer.mpObject.stop();
						//MusicPlayer.mpObject.release();
					}
					if(NowPlayingActivity.mpObject != null){
						NowPlayingActivity.mpObject.stop();
					}
					
					Intent intent = new Intent(getApplicationContext(),MusicPlayer.class);
					intent.putExtra("musicPath",musicPath);
					intent.putExtra("pathArray", songPathArray);
					startActivity(intent);*/
					
				}
			});
			songTitle.setClickable(false);
			
			return row;
		}
		
		@Override
		public Object getItem(int position){
			return songList.get(position).getSongPath();
		}
	}
}


