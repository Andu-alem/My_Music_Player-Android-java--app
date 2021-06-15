package com.example.swipetabapp;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.example.swipetabapp.MusicPlayerFragment.PlayerFragment;
import com.example.swipetabapp.MusicPlayerFragment.PlayerFragmentAdapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class NowPlayingActivity extends FragmentActivity{
	   public double seekMax;
	   RelativeLayout layout;
	   public TextView songTitle,songArtist,songAlbum,startTimeField,endTimeField,random,repeat;
	   ImageView imv;
	   private MediaPlayer mediaPlayer;
	   private double startTime = 0;
	   private double finalTime = 0;
	   public static int duration = 0;
	   private Handler myHandler = new Handler();;
	   private int forwardTime = 5000; 
	   private int backwardTime = 5000;
	   public static MediaPlayer mpObject=null;
	   public static String song_name="";
	   public static String music_path;
	   private SeekBar seekbar;
	   private ImageButton playButton;
	   private ImageButton previousButton, nextButton;
	   public static int oneTimeOnly = 0;
	   public String musicName;
	   public static boolean isRandom, isLooping;
	   public static Bitmap songImage = null;
	   public static int index, listLength,lastPosition;
	   public static boolean isDflt = false,isPlayOn = false;
	   ArrayList<SongListModel> songModelList;
	   SongListModel sModelObj;
	   SharedPreferences sharedPref;
	   SharedPreferences.Editor editor;
	    public PlayerFragmentAdapter playerAdapter;
	    ViewPager mViewPager;
	   
	   
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_play_fragment);
	      
	      playerAdapter = new PlayerFragmentAdapter(getSupportFragmentManager(),this);
		  mViewPager = (ViewPager) findViewById(R.id.pager);
		  mViewPager.setAdapter(playerAdapter);
		 	      
	      //imv = (ImageView) findViewById(R.id.imageView1);
	      songTitle = (TextView)findViewById(R.id.title);
	      songArtist = (TextView)findViewById(R.id.artist);
	      songAlbum = (TextView)findViewById(R.id.album);
	      startTimeField =(TextView)findViewById(R.id.textView1);
	      random =(TextView)findViewById(R.id.random);
	      repeat =(TextView)findViewById(R.id.repeat);
	      endTimeField =(TextView)findViewById(R.id.textView2);
	      seekbar = (SeekBar)findViewById(R.id.seekBar1);
	      playButton = (ImageButton)findViewById(R.id.imageButton3);
	      nextButton = (ImageButton) findViewById(R.id.imageButton1);	 
	      previousButton = (ImageButton)findViewById(R.id.imageButton2);
	      
	      sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
	      editor = sharedPref.edit();
		  editor.putBoolean("isNowPlaying", true);
		  editor.commit();
		  
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
		  
	      Bundle bundle = getIntent().getExtras();
	      songModelList = bundle.getParcelableArrayList("parcelSongList");
	      listLength = songModelList.size();
	      if(SwipeActivity.playerObj == null){
	    	  startMediaPlayer(0,true,false);
	      }else{
	    	  int songIndex = bundle.getInt("songIndex");
	    	  mViewPager.setCurrentItem(songIndex);
	    	  startMediaPlayer(songIndex,false,true);
	      }
	      mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					//mViewPager.setCurrentItem(arg0);
					int indx;
					isRandom = sharedPref.getBoolean("isShuffled", false);
					   if(isRandom){
						   indx = (int) (listLength*Math.random()+1);
					   }else{
						   indx = arg0;
					   }

					 mediaPlayer.stop();
					 startMediaPlayer(indx,false,false);
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub				
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {		
				}
			});	
	      
	      
	      /*layout = (RelativeLayout) findViewById(R.id.relativeLayout);
	      layout.setOnTouchListener(new OnSwipeTouchListener(NowPlayingActivity.this)
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
	      });*/
	   }	
	   
	   public void startMediaPlayer(int position, boolean isDefault, boolean isseekToNedded){
		  
		   if(isDefault){
			   int lastPlayedIndex = sharedPref.getInt("lastPlayedIndex", 0);
			   lastPosition = sharedPref.getInt("lastPosition", 0);
			   sModelObj = songModelList.get(lastPlayedIndex);
			   index = lastPlayedIndex;
			   mViewPager.setCurrentItem(lastPlayedIndex);
		       isDflt = true;
		       
		   }else{
			   sModelObj = songModelList.get(position);
			   index = position;
		   }
		   
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
		   if(isDflt){
			   mediaPlayer.seekTo(lastPosition);
		   }
		   if(isseekToNedded){
			   int currentPos = getIntent().getIntExtra("currentPosition",0);
			   mediaPlayer.seekTo(currentPos);
		   }
		   //mediaPlayer.setAudioStreamType(2);
		   editor.putInt("lastPlayedIndex", index);
		   editor.commit();
		   /*
		   MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
		   metaRetriever.setDataSource(this, Uri.parse(music_path));
		   try{
			   byte[] art = metaRetriever.getEmbeddedPicture();
			   songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
			   imv.setImageBitmap(songImage);
			   imv.setClickable(false);
		   }catch(Exception e){
				    	//do something here
		   }*/
		   
		   getActionBar().setDisplayHomeAsUpEnabled(true);
		   play();
		   mpObject = mediaPlayer;
		   MusicPlayer.mpObject = mediaPlayer;
		   if(isLooping){
			   mediaPlayer.setLooping(true);
		   }
		   if(mediaPlayer.isLooping()){
			   isPlayOn = true;
			   playButton.setImageResource(R.drawable.pause);
		   }
		   mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer arg0) {
					nextButton.callOnClick();
					
				}
			});
		      
		  seekbar.setClickable(true);
		  seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		    	  public int position = 0;
		    	  public double currentPos= 0;
				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					//Toast.makeText(getApplicationContext(), "progress "+position+"  and  "+currentPos, Toast.LENGTH_SHORT).show();
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
						if(isLooping){
							isPlayOn = true;
							playButton.setImageResource(R.drawable.pause);
						}else{
							isPlayOn = false;
							playButton.setImageResource(R.drawable.play);
						}
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
		
	   public void playPause(View view){
		   if(isPlayOn){
			   pause();
			   isPlayOn = false;
		   }else{
			   play();
			   isPlayOn = true;
		   }
	   }
	   public void play(){
		      mediaPlayer.start();
		      finalTime = mediaPlayer.getDuration();
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
		      //pauseButton.setEnabled(true);
		     // playButton.setEnabled(false);
		      isPlayOn = true;
		      playButton.setImageResource(R.drawable.pause);
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
	        	 if(isDflt){
	        		 //playButton.setEnabled(true);
	        		 playButton.setImageResource(R.drawable.play);
	        		 isPlayOn = false;
		        	 //pauseButton.setEnabled(false);
		        	 isDflt = false;
	        	 }else{
	        		 //nextButton.callOnClick();
	        	 }	        	 	        	 
	         }
	         myHandler.postDelayed(this, 100);
	      }
	   };
	   public void pause(){
	      mediaPlayer.pause();
	      isPlayOn = false;
	      playButton.setImageResource(R.drawable.play);
	      //pauseButton.setEnabled(false);
	      //playButton.setEnabled(true);
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
		 // startMediaPlayer(index,false,false);
		  mViewPager.setCurrentItem(index);
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
		   //startMediaPlayer(index,false,false);
		   mViewPager.setCurrentItem(index);
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
			   mediaPlayer.setLooping(false);
			   editor.putBoolean("isLooping", false);
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
		   NotificationMaker nMaker = new NotificationMaker(this);
		   nMaker.makeNotification();
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
	   
	   public class PlayerFragmentAdapter extends FragmentPagerAdapter{
			Context applicationContext;

			public PlayerFragmentAdapter(FragmentManager fm,Context c) {
				super(fm);
				applicationContext = c;
			}

			@Override
			public Fragment getItem(int position) {
				Fragment fragment = new PlayerFragment(position, applicationContext);
			    /*Bundle args = new Bundle();
				args.putString("imgUri", images.get(position));
				args.putInt("imageIndex",position);
				fragment.setArguments(args);*/
				return fragment;
			}

			@Override
			public int getCount() {
				return songModelList.size();
			}
			
		}
		
		
		public class PlayerFragment extends Fragment{
			int position;
			String songPath;
			Uri imageUri;
			Context context;
			
			public PlayerFragment(int pos, Context c){
				position = pos;
				context = c;
				sModelObj = songModelList.get(position);
				songPath = sModelObj.getSongPath();
				
			}
			//int m = mViewPager.getCurrentItem();
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {			
				   MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
				   metaRetriever.setDataSource(context, Uri.parse(songPath));
				   try{
					   View view = inflater.inflate(R.layout.fragment_image_view, container, false);
					   ImageView img = (ImageView) view.findViewById(R.id.image1);
					   byte[] art = metaRetriever.getEmbeddedPicture();
					   if(art!=null){
						   songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
						   if(songImage == null){
							   img.setImageResource(R.drawable.imgx);
						   }else{
							   img.setImageBitmap(songImage);   
						   } 						   
					   }else{
						   img.setImageResource(R.drawable.imgx);
					   }
					   img.setClickable(false);
					   return view;
				   }catch(Exception e){
					   return null;
				   }
			}

		}
		
		public void nextNotification(){
			//NowPlayingActivity npa = new NowPlayingActivity();
			nextButton.callOnClick();
		}

}
