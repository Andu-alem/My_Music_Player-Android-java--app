package com.example.swipetabapp;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

public class NotificationResult extends Activity{
	SongListModel songListModel;
	static ArrayList<SongListModel> songList;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//NotificationMaker nMaker = new NotificationMaker(this);
		String msg = getIntent().getStringExtra("Msg");
		songList = path_list();
		if(msg.equals("cancel")){
			NotificationMaker.cancelNotification();
			moveTaskToBack(true);
			NotificationResult.this.finish();
			finish();
		}else if(msg.equals("next")){
			NotificationMaker nMaker = new NotificationMaker(this);
			//NowPlayingActivity.nextNotification();
			nMaker.next();
			//NotificationMaker.cancelNotification();
			//moveTaskToBack(true);
			//NotificationResult.this.finish();
			finish();
		}else if(msg.equals("pausePlay")){
			NotificationMaker.pausePlay();
			finish();
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
				//arrPath = pathArray;
		}
		return songList;	
	}
}
