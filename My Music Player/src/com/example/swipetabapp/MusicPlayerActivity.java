package com.example.swipetabapp;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.example.swipetabapp.GallaryActivity.NewFragment;
import com.example.swipetabapp.GallaryActivity.ViewPagerAdapter;
import com.example.swipetabapp.MediaPlayerService.LocalBinder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayerActivity extends FragmentActivity{

	   PlayerFragmentAdapter playerAdapter;
	   ViewPager mViewPager;
	   RelativeLayout layout;
	   public double seekMax;
	   public TextView songTitle,startTimeField,endTimeField, pathData,random,repeat,songArtist,songAlbum;
	   public ImageView imv;
	   private double startTime = 0;
	   private double finalTime = 0;
	   private Handler myHandler = new Handler();;
	   private int forwardTime = 5000; 
	   private int backwardTime = 5000;
	   //public static MediaPlayer mpObject=null;
	   public static String music_path;
	   public static int randomIndex = 0,  oneTimeOnly = 0;
	   public static boolean isRandom, isLooping, isPlayOn = false,isFromService = false, isFromNowPlaying = false;
	   private SeekBar seekbar;
	   public static Bitmap songImage = null;
	   private ImageButton playButton,nextButton,previousButton;
	   ArrayList<SongListModel> songModelList;
	   SongListModel sModelObj;
	   SharedPreferences sharedPref;
	   SharedPreferences.Editor editor;
	   public static MediaPlayerService player;
	   private boolean serviceBound = false;
	   
	   public static final String Broadcast_Player_Info = "com.example.swiptetabapp.PLAYER_INFO";
	   public static final String Broadcast_ChangeFromService = "com.example.swipetabapp.ChangeFromService";
	   public static final String Broadcast_IS_LOOPING= "com.example.swipetabapp.Looping";
	   public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.swipetabapp.PlayNewAudio";
	   
	   private ServiceConnection serviceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// we have bound to localService, cast the IBinder and get loaclService instance
			MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
			player = binder.getService();
			serviceBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			serviceBound = false;
		}
	   };
		
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_play_fragment);
	      
	      // BroadcastListeners
		  register_PlayerInfo();
		  register_ChangeFromService();
		  
	      
	      Bundle bundle = getIntent().getExtras();    
	      songModelList = bundle.getParcelableArrayList("parcelSongList");
	      isFromNowPlaying = bundle.getBoolean("isFromNowPlaying");
	      
	      sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
	      editor = sharedPref.edit();
	      //editor.putBoolean("isNowPlaying", false);
	   	  editor.commit();
	   	  
	   	  
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
	      getActionBar().setDisplayHomeAsUpEnabled(true);
	      
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
	      int songIndex;
	      if(isFromNowPlaying){
	    	  songIndex = sharedPref.getInt("serviceAudioIndex", -1);
	    	  if(player == null){
	    		  // if not the service is bound
	    		  mViewPager.setCurrentItem(songIndex);
	    		  playAudio(songIndex);
	    	  }else{
	    		  if(!player.isPlaying()){
	 	    		 player.resumeMedia();
	 	    	  }
	    		  mViewPager.setCurrentItem(songIndex);
	    		  player.initiatePlayerInfo();
	    	  }
	      }else{
	    	  songIndex = bundle.getInt("songIndex");
	    	  mViewPager.setCurrentItem(songIndex);
		      playAudio(songIndex);
	      }
	      randomIndex = songIndex;
	      mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					//mViewPager.setCurrentItem(arg0);
					if(!isFromService){
						int indx;
						isRandom = sharedPref.getBoolean("isShuffled", false);
						   if(isRandom){
							   indx = (int) (songModelList.size()*Math.random()+1);
							   /*if(indx >= songModelList.size()-1){
								   randomIndex = 0;
							   }else{
								   randomIndex = indx;
							   }*/
							   isFromService = true;
							   mViewPager.setCurrentItem(indx);
						   }else{
							   indx = arg0;
						   }
						 playAudio(indx);
					}else{
						isFromService = false;
					}
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub				
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {		
				}
			});	
	      
	      seekbar.setClickable(true);
		   seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		    	public int position = 0;
		    	//public double currentPos= 0;
				@Override
				public void onStopTrackingTouch(SeekBar arg0) {
					player.seekTo(position);			
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar arg0) {
					//arg0.getThumbOffset();			
				}
				
				@Override
				public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					position = arg1;
					if(player.getDuration()== (double) arg1){
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
	      
	      /*
	      layout = (RelativeLayout) findViewById(R.id.relativeLayout);
	      layout.setOnTouchListener(new OnSwipeTouchListener(MusicPlayerFragment.this)
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
	private void playAudio(int audioIndex){
		 editor.putInt("serviceAudioIndex", audioIndex);
		 editor.commit();
		 if(!serviceBound){
			if(player != null){
				if(player.isPlaying()){
					player.stopMedia();
				}
			}
			Intent playerIntent = new Intent(this, MediaPlayerService.class);
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList("parcelSongList", songModelList);
			playerIntent.putExtras(bundle);
			startService(playerIntent);
			bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		 }else{
			Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
			sendBroadcast(broadcastIntent);
		 }
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		savedInstanceState.putBoolean("ServiceState", serviceBound);
		super.onSaveInstanceState(savedInstanceState);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		serviceBound = savedInstanceState.getBoolean("ServiceState");
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
		   player.resumeMedia();
		   isPlayOn = true;
		   playButton.setImageResource(R.drawable.pause);
	}
	public void pause(){
		  player.pause();
		  isPlayOn = false;
		  playButton.setImageResource(R.drawable.play);
	}
	public void next(View view){
		player.skeepToNext();
	}
	public void previous(View view){
		player.skeepToPrevious();
	}

	private BroadcastReceiver playerInfo = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// Get the new media index form sharedpreferences
			int indx = sharedPref.getInt("serviceAudioIndex", -1);
			if(indx > -1 && indx < songModelList.size()){
				 sModelObj = songModelList.get(indx);
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
			}
			finalTime = player.getDuration();
		    startTime = player.getCurrentPosition();
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
		      //playButton.setEnabled(false);
		      isPlayOn = true;
		      playButton.setImageResource(R.drawable.pause);
		}	
	};
	private void register_PlayerInfo(){
		// Register playNewMedia receiver
		IntentFilter filter = new IntentFilter(Broadcast_Player_Info);
		registerReceiver(playerInfo,filter);
	}

   private Runnable UpdateSongTime = new Runnable() {
      public void run() {
         startTime = player.getCurrentPosition();
         startTimeField.setText(String.format("%d min, %d sec", 
            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
            TimeUnit.MILLISECONDS.toSeconds((long) startTime) - 
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
            toMinutes((long) startTime)))
         );
         seekbar.setProgress((int)startTime);
         if(player.isPlaying()){
        	 playButton.setImageResource(R.drawable.pause);
        	// isPlayOn = false;
         }else{
        	 playButton.setImageResource(R.drawable.play);
        	// isPlayOn = true;
         }
         if(player.getCurrentPosition() == player.getDuration()){
        	 //nextButton.callOnClick();
         }
         myHandler.postDelayed(this, 100);
      }
   };
	   
	private BroadcastReceiver  changeFromService = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
				isFromService = intent.getBooleanExtra("changeFromService", false);
				int indx = sharedPref.getInt("serviceAudioIndex", -1);
				mViewPager.setCurrentItem(indx);
		}	
	};
	private void register_ChangeFromService(){
		// Register playNewMedia receiver
		IntentFilter filter = new IntentFilter(Broadcast_ChangeFromService);
		registerReceiver(changeFromService,filter);
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

   public void repeat(View view){
	   isLooping = !isLooping;
	   if(isLooping){
		   repeat.setText("Repeat On");
		   /*random.setBackgroundColor();
		   random.setTextColor(R.color.qnBgd);*/
	   }else{
		   repeat.setText("Repeat Off");
		  
		   /*random.setBackgroundColor(R.color.qnBgd);
		   random.setTextColor(R.color.black);*/
	   }
	   editor.putBoolean("isLooping", isLooping);
	   editor.commit();
	   Intent broadcastIntent = new Intent(Broadcast_IS_LOOPING);
	   sendBroadcast(broadcastIntent);
   }
   public void forward(View view){
      int temp = (int)startTime;
      if((temp+forwardTime)<=finalTime){
         startTime = startTime + forwardTime;
         player.seekTo((int) startTime);
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
         player.seekTo((int) startTime);
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
	  // editor.putInt("lastPosition", player.getCurrentPosition());
	   //editor.commit();
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
			
		}//int m = mViewPager.getCurrentItem();
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

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(serviceBound){
			finish();
			if(!player.isPlaying()){
				player.removeNotification();
				player.stopSelf();
				unbindService(serviceConnection);
			}
		}
	}
			
}