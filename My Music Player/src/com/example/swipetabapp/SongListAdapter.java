package com.example.swipetabapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SongListAdapter extends ArrayAdapter{
		public static String[] arrPath;
		List<SongListModel> songList;
		SongListModel sModel = new SongListModel();
		Context applicationContext;
		public String[] songPathArray;
		public SongListAdapter(Context context, List<SongListModel> titles, String[] pathArray) {
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
		return songList.get(position);
	}
}
