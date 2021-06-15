package com.example.swipetabapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.swipetabapp.MusicListActivity.MusicListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
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
import android.widget.AdapterView.AdapterContextMenuInfo;

public class CustomMusicActivity extends Activity{
	public static MediaPlayer playerObj = null;
	final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
	final int cacheSize = maxMemory/8;
	private ArrayList<SongListModel> songList;
	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	SongListModel sModel;
	CustomMusicListAdapter songListAdapter;
	public MediaPlayer mp;
	

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		songList = path_list();
		sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		if(!sharedPref.contains("isShuffled")){
			editor.putBoolean("isShuffled", false);
			editor.putBoolean("isLooping",false);
			editor.commit();
		}
		//mp = MediaPlayerService.mpObject;
		//playAudio(3);
		//ArrayList<String> mList = new ArrayList<String>(Arrays.asList(arrPath));
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,mList);
		ListView lv = (ListView) findViewById(R.id.lV1);
		CustomMusicListAdapter adapter = new CustomMusicListAdapter(this,songList);
		lv.setAdapter(adapter);	
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent(getApplicationContext(),MusicPlayerActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("isFromNowPlaying", false);
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
		default:
			return super.onOptionsItemSelected(item);
		}
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
					sModel = (SongListModel) songListAdapter.getItem(info.position);
					String dataPath = sModel.getSongPath();
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
	
	public ArrayList<SongListModel> path_list(){
		byte[] art;
		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		songList = new ArrayList<SongListModel>();
		Boolean isSDPresent =  android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		//If SD card is present then use this:
		if(isSDPresent){
				final String[] columns ={ MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.ALBUM};
				final String orderBy = MediaStore.Audio.Media.TITLE;

				Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null,null, orderBy);

				int count = cursor.getCount();

				for (int i = 0; i < count; i++) {
					SongListModel songListM = new SongListModel();
				    cursor.moveToPosition(i);
				    int dataColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
				    int titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
				    int artistColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
				    int albumColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);

				    String dataPath= cursor.getString(dataColumnIndex);		

				  
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
		}
		return songList;	
	}
		
	
	public class CustomMusicListAdapter extends ArrayAdapter{
		Context applicationContext;
		String[] titleArray;
		Holder holder;
		final int maxMemory = (int)(Runtime.getRuntime().maxMemory()/1024);
		final int cacheSize = maxMemory/8;		
		List<SongListModel> songList;
		SongListModel sModel = new SongListModel();
		MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		
		public CustomMusicListAdapter(Context context, List<SongListModel> titles) {
			super(context, R.layout.custom_music_list, R.id.textView1,titles);
			// TODO Auto-generated constructor stub
			this.songList = titles;
			applicationContext = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			ImageCache lruCache = new ImageCache();
			byte[] art;
			String uriPath;
			holder = new Holder();
			//Inflating the layout
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.custom_music_list, parent, false);					    
		    
		    sModel = songList.get(position);
			String title = sModel.getSongTitle();
			String artist = sModel.getSongArtist();
			uriPath = sModel.getSongPath();
			
			
			TextView songTitle = (TextView) view.findViewById(R.id.textView1);
			TextView songArtist = (TextView) view.findViewById(R.id.textView2);
			ImageView im = (ImageView) view.findViewById(R.id.imageView1);
			String[] musicNameArray = uriPath.split("/");
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
			songTitle.setClickable(false);    
	    	
	    	 try{
	    		 metaRetriever.setDataSource(getApplicationContext(), Uri.parse(uriPath));
	    		 art = metaRetriever.getEmbeddedPicture();
	    		 if(art != null){
	    			 BitmapFactory.Options options = new BitmapFactory.Options();
		    		 Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length,options);
		    		 lruCache.put(uriPath, songImage);
		    		 if(songImage != null){
			    			//lruCache.put(uriPath, songImg);
					 	    im.setImageBitmap(lruCache.get(uriPath));
					 	    //im.setImageBitmap(songImage);
		    		 }else{
			    			im.setImageResource(R.drawable.imgx);
		    		 }
	    		 }else{
	    			 im.setImageResource(R.drawable.imgx);
	    		 }
	    		 im.setClickable(false);
	    	 }catch(Exception e){
	    		 
	    	 }
		    
		    /*try{
		    	metaRetriever.setDataSource(applicationContext, Uri.parse(titleArray[position]));
		    	art = metaRetriever.getEmbeddedPicture();
		    	//title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		    	//artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST); 
		    	Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
		    	holder.im.setImageBitmap(songImage);
		    	holder.im.setClickable(false);
		    	// subTitle.setText(artist);
				//subTitle.setClickable(false);
		    }catch(Exception e){
		    	//do something here
		    }	*/   		
			return view;
		}
		@Override
		public Object getItem(int position){
			return songList.get(position);
		}
	}
}
class Holder{
	public ImageView im;
	public TextView songTitle;
}


