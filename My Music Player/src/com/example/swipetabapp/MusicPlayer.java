package com.example.swipetabapp;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayer extends Activity {
	   
	   RelativeLayout layout;
	   public double seekMax;
	   public TextView songTitle,startTimeField,endTimeField, pathData,random,repeat,songArtist,songAlbum;
	   public ImageView imv;
	   private MediaPlayer mediaPlayer;
	   private double startTime = 0;
	   private double finalTime = 0;
	   public static int duration = 0;
	   private Handler myHandler = new Handler();;
	   private int forwardTime = 5000; 
	   private int backwardTime = 5000;
	   public static MediaPlayer mpObject=null;
	   public static String music_path;
	   public static int index, listLength;
	   public static boolean isRandom, isLooping;
	   private SeekBar seekbar;
	   public static Bitmap songImage = null;
	   private Button playButton,pauseButton,nextButton, previousButton;
	   public static int oneTimeOnly = 0;
	   public static int indexer = 0;
	   ArrayList<SongListModel> songModelList;
	   SongListModel sModelObj;
	   SharedPreferences sharedPref;
	   SharedPreferences.Editor editor;
		
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_play);
	      	      
	      Bundle bundle = getIntent().getExtras();
	      int songIndex = bundle.getInt("songIndex");
	      songModelList = bundle.getParcelableArrayList("parcelSongList");
	      listLength = songModelList.size();
	      
	      sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
	      editor = sharedPref.edit();
	      editor.putBoolean("isNowPlaying", false);
	   	  editor.commit();
	      
	      imv = (ImageView) findViewById(R.id.imageView1);
	      songTitle = (TextView)findViewById(R.id.textView4);
	      songArtist = (TextView)findViewById(R.id.textView5);
	      songAlbum = (TextView)findViewById(R.id.textView6);
	      startTimeField =(TextView)findViewById(R.id.textView1);
	      random =(TextView)findViewById(R.id.random);
	      repeat =(TextView)findViewById(R.id.repeat);
	      endTimeField =(TextView)findViewById(R.id.textView2);
	      seekbar = (SeekBar)findViewById(R.id.seekBar1);
	      playButton = (Button)findViewById(R.id.button2);
	      pauseButton = (Button)findViewById(R.id.button4);
	      nextButton = (Button)findViewById(R.id.button3);
	      previousButton = (Button)findViewById(R.id.button1);
	      
	      if(sharedPref.contains("isShuffled")){
			  if(sharedPref.getBoolean("isShuffled", false)){
				  isRandom = true;
				  random.setText("Shuffle On");
			  }else{
				  isRandom = false;
				  random.setText("Shuffle Off");
			  }
		  }else{
			  isRandom = false;
		  }
	      if(sharedPref.contains("isLooping")){
			  if(sharedPref.getBoolean("isLooping", false)){
				  isLooping = true;
				  repeat.setText("Repeat On");
			  }else{
				  isLooping = false;
				  repeat.setText("Repeat Off");
			  }
		  }
	      startMediaPlayer(songIndex); 
	      
	      layout = (RelativeLayout) findViewById(R.id.relativeLayout);
	      layout.setOnTouchListener(new OnSwipeTouchListener(MusicPlayer.this)
	      {
	    	@Override
	    	public void onSwipeLeft(){
	    		super.onSwipeLeft();
	    		nextButton.callOnClick();
	    	}
	    	@Override
	    	public void onSwipeRight(){
	    		super.onSwipeRight();
	    		previousButton.callOnClick();
	    	}
	      });

	   }
	   
	   public void startMediaPlayer(int position){
		   sModelObj = songModelList.get(position);
		   music_path = sModelObj.getSongPath();
		   songTitle.setText(sModelObj.getSongTitle());
		   if(sModelObj.getSongArtist().equals("<unknown>")){
			   String[] arr = music_path.split("/");
			   String songName = arr[arr.length - 1];
			   songArtist.setText(songName.replace(".mp3", ""));
		   }else{
			   songArtist.setText(sModelObj.getSongArtist());   
		   }
		   songAlbum.setText(sModelObj.getSongAlbum());
		   mediaPlayer = MediaPlayer.create(this, Uri.parse(music_path));
		   pauseButton.setEnabled(false);
		   getActionBar().setDisplayHomeAsUpEnabled(true);
		   playButton.callOnClick();
		   mpObject = mediaPlayer;
		   index = position;
		   editor.putInt("lastPlayedIndex", index);
		   editor.commit();
		   MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		   metaRetriever.setDataSource(this, Uri.parse(music_path));
		   try{
			   byte[] art = metaRetriever.getEmbeddedPicture();
			   songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
			   imv.setImageBitmap(songImage);
			   imv.setClickable(false);
		   }catch(Exception e){
			    	//do something here
		   }
			   
		   mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {				
			   @Override
				public void onCompletion(MediaPlayer arg0) {
					nextButton.callOnClick();
					
				}
		   });
		   if(isLooping){
		     mediaPlayer.setLooping(true);
		   }	 
		   if(mediaPlayer.isLooping()){
			   playButton.setEnabled(false);
			   pauseButton.setEnabled(true);
		   }
		   seekbar.setClickable(true);
		   seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		    	public int position = 0;
		    	public double currentPos= 0;
				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					mediaPlayer.seekTo(position);			
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					//arg0.getThumbOffset();			
				}
				
				@Override
				public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					position = arg1;
					if(mediaPlayer.isPlaying()){
						currentPos = mediaPlayer.getDuration();
					}
					if(mediaPlayer.getDuration()== (double) arg1){
						playButton.setEnabled(true);
						pauseButton.setEnabled(false);
					}		
				}
		   });
	   }
		@Override
		public boolean onNavigateUp(){
			//to enable actionbar backbutton
			finish();
			return true;
		}

		public void play(View view){
		      mediaPlayer.start();
		      finalTime = mediaPlayer.getDuration();
		      duration = (int) finalTime;
		      startTime = mediaPlayer.getCurrentPosition();
		      if(oneTimeOnly == 0){
		         seekbar.setMax((int) finalTime);
		         oneTimeOnly = 1;
		      } 
		      seekbar.setMax((int) finalTime);
	
		      endTimeField.setText(String.format("%d min, %d sec", 
		         TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
		         TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - 
		         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
		         toMinutes((long) finalTime)))
		      );
		      startTimeField.setText(String.format("%d min, %d sec", 
		         TimeUnit.MILLISECONDS.toMinutes((long) startTime),
		         TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
		         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
		         toMinutes((long) startTime)))
		      );
		      seekbar.setProgress((int)startTime);
		      myHandler.postDelayed(UpdateSongTime,100);
		      pauseButton.setEnabled(true);
		      playButton.setEnabled(false);
		      duration = (int) (finalTime - startTime);
	   }

	   private Runnable UpdateSongTime = new Runnable() {
	      public void run() {
	         startTime = mediaPlayer.getCurrentPosition();
	         startTimeField.setText(String.format("%d min, %d sec", 
	            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
	            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
	            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
	            toMinutes((long) startTime)))
	         );
	         seekbar.setProgress((int)startTime);
	         if((int) startTime == duration){
	        	 //nextButton.callOnClick();
	         }
	         myHandler.postDelayed(this, 100);
	      }
	   };
	   public void pause(View view){
	      mediaPlayer.pause();
	      pauseButton.setEnabled(false);
	      playButton.setEnabled(true);
	   }
	   public void random(View view){
		   isRandom = !isRandom;
		   if(isRandom){
			   random.setText("Shuffle On");
			   editor.putBoolean("isShuffled", true);
			   editor.commit();
			   /*random.setBackgroundColor();
			   random.setTextColor(R.color.qnBgd);*/
		   }else{
			   random.setText("Shuffle Off");
			   editor.putBoolean("isShuffled", false);
			   editor.commit();
			   /*random.setBackgroundColor(R.color.qnBgd);
			   random.setTextColor(R.color.black);*/
		   }
	   }
	   public void next(View view){
		   mediaPlayer.stop();
		   isRandom = sharedPref.getBoolean("isShuffled", false);
		   if(isRandom){
			   index = (int)(listLength*Math.random()+1);
		   }else{
			   if(index == (songModelList.size()-1)){
				   index = 0;
			   }else{
				   index = index + 1;
			   }
		   }
		  startMediaPlayer(index);
	   }
	   public void previous(View view){
		   mediaPlayer.stop();
		   isRandom = sharedPref.getBoolean("isShuffled", false);
		   if(isRandom){
			   index = (int) (listLength*Math.random()+1);
		   }else{
			   if(index == 0){
				   index = songModelList.size()-1;
			   }else{
				   index = index - 1;   
			   }   
		   }
		   startMediaPlayer(index);
	   }
	   public void repeat(View view){
		   isLooping = !isLooping;
		   if(isLooping){
			   repeat.setText("Repeat On");
			   editor.putBoolean("isLooping", true);
			   mediaPlayer.setLooping(true);
			   editor.commit();
			   /*random.setBackgroundColor();
			   random.setTextColor(R.color.qnBgd);*/
		   }else{
			   repeat.setText("Repeat Off");
			   editor.putBoolean("isLooping", false);
			   mediaPlayer.setLooping(false);
			   editor.commit();
			   /*random.setBackgroundColor(R.color.qnBgd);
			   random.setTextColor(R.color.black);*/
		   }
	   }
	   public void forward(View view){
	      int temp = (int)startTime;
	      if((temp+forwardTime)<=finalTime){
	         startTime = startTime + forwardTime;
	         mediaPlayer.seekTo((int) startTime);
	      }
	      else{
	         Toast.makeText(getApplicationContext(), 
	         "Cannot jump forward 5 seconds", 
	         Toast.LENGTH_SHORT).show();
	      }
	   }
	   public void rewind(View view){
	      int temp = (int)startTime;
	      if((temp-backwardTime)>0){
	         startTime = startTime - backwardTime;
	         mediaPlayer.seekTo((int) startTime);
	      }
	      else{
	         Toast.makeText(getApplicationContext(), 
	         "Cannot jump backward 5 seconds",
	         Toast.LENGTH_SHORT).show();
	      }
	   }
	   @Override
	   public void onPause(){
		   super.onPause();
		   editor.putInt("lastPosition", mediaPlayer.getCurrentPosition());
		   editor.commit();
	   }
	   
	   @Override
	   public void onStop(){
		   super.onStop();
		   //mediaPlayer.stop();
		   //mediaPlayer.release();
	   }
	   
	   public int getIndex(String path, String[] songPathList){
		   int indx = 0;
		      for(int i = 0; i<songPathList.length;i++){
		    	  String s = songPathList[i];
		    	  if(path.equals(s)){
		    		  indx = i;
		    		  break;
		    	  }
		      }
		      return indx;
	   }
	 
	   @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
		   // Inflate the menu; this adds items to the action bar if it is present.
		   getMenuInflater().inflate(R.menu.activity_swipe, menu);
		   return true;
	   }
}
