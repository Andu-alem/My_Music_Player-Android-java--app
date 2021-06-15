package com.example.swipetabapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.RemoteViews;

public class MediaPlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
				MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,MediaPlayer.OnPreparedListener,
				MediaPlayer.OnSeekCompleteListener,AudioManager.OnAudioFocusChangeListener,MediaPlayerControl{
	
	private Handler myHandler = new Handler();
    private MediaPlayer mediaPlayer;
    public static MediaPlayer mpObject;
    private AudioManager audioManager;
    private static int resumePosition;
    private boolean ongoingCall = false, isRandom;
    private static boolean isPaused = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private ArrayList<SongListModel> songList;
    private SongListModel sModel;
    private static int audioIndex = -1;
    SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	
	public static final String ACTION_PLAY="com.example.swipetabapp.ACTION_PLAY";
	public static final String ACTION_PAUSE="com.example.swipetabapp.ACTION_PAUSE";
	public static final String ACTION_PREVIOUS="com.example.swipetabapp.ACTION_PREVIOUS";
	public static final String ACTION_NEXT="com.example.swipetabapp.ACTION_NEXT";
	public static final String ACTION_STOP="com.example.swipetabapp.ACTION_STOP";
	
	private static NotificationManager nManager;
	private NotificationCompat.Builder nBuilder;
	private RemoteViews remoteView;

	//MediaSession
	//private MediaSessionManager mediaSessionManager;
	//private MediaSessionCompat mediaSession;
	//private MediaController.MediaPlayerControl transportControls;
    
    private final IBinder iBinder = new LocalBinder();
	@Override
	public IBinder onBind(Intent intent) {
		return iBinder;
	}
	@Override
	public boolean onUnbind(Intent intent){
		super.onUnbind(intent);
		//stopSelf();
		return false;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		
		// Manage incoming phone call during playback
		// pause MediaPlayer on incoming call
		// Resume on hangup
		callStateListener();
		
		// ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
		registerBecomingNoisyReceiver();
		
		// Listen for new Audio to play -- BroadcastReceiver
		register_playNewAudio();
		
		// listen for if repeat mode is set
		register_loopingState();
	}
	
	// The system calls this method when an activity requests the service be started
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		if(intent.getAction()== null){
			try{
				// an audio file is passed to the service through putExtra()
				Bundle bundle = intent.getExtras();
				songList = bundle.getParcelableArrayList("parcelSongList");
				audioIndex = sharedPref.getInt("serviceAudioIndex", -1);
				if(audioIndex != -1 && audioIndex < songList.size()){
					sModel= songList.get(audioIndex);
				}else{
					stopSelf();
				}
			}catch(NullPointerException e){
				stopSelf();
			}
			if(sModel != null){
				initMediaPlayer();
				buildNotification(PlaybackStatus.PLAYING);
				//mpObject = mediaPlayer;
			}
		}else{
			handleIncomingActions(intent);
		}
		
		// request audio focus
		if(requestAudioFocus() == false){
			stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mediaPlayer != null){
			//pause();
			//resumeMedia();
			//stopMedia();
			//mediaPlayer.release();
			stopSelf();
		}
		removeAudioFocus();
		// Disable the PhoneStateListener
		if(phoneStateListener != null){
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		
		removeNotification();
		
		// unregister BroadcastReceivers
		unregisterReceiver(becomingNoisyReceiver);
		unregisterReceiver(playNewAudio);
		
	}
	
	@Override
	public void onAudioFocusChange(int focusChange) {
		// invoked when the audio focus of the system is updated
		switch(focusChange){
		case AudioManager.AUDIOFOCUS_GAIN:
			
			if(mediaPlayer == null) initMediaPlayer();
			else if(!mediaPlayer.isPlaying()) resumeMedia();
			//mediaPlayer.start();
			mediaPlayer.setVolume(1.0f, 1.0f);
			break;
		case AudioManager.AUDIOFOCUS_LOSS:
			if(mediaPlayer.isPlaying()){
				pause();
			}
			break;
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			//lost focus for short time, but we have to stop playback
			if(mediaPlayer.isPlaying()) mediaPlayer.pause();
			break;
		case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
			//lost focus for short time, but it is ok to keep playing
			// at an attunated level
			if(mediaPlayer.isPlaying()){
				mediaPlayer.setVolume(0.1f, 0.1f);
			}
			break;
		}
	}
	
	private boolean requestAudioFocus(){
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
			//Focus gained
			return true;
		}
		return false;
	}
	private boolean removeAudioFocus(){
		return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
	}
	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// invoked indicating the completion of a seek operation
		
	}
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// invoked to communicate some info
		return false;
	}
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// invoked when there has been an error during an asynchronous operation
		switch(what){
		case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
			break;
		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
			break;
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:
			break;
		}
		return false;
	}
	@Override
	public void onCompletion(MediaPlayer mp) {
		//invoked when playback of a media source has completed
		skeepToNext();
		
	}
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		//invoked idicating buffering status of a media resource being 
		//streamed over the network	
	}
	
	public class LocalBinder extends Binder{
		public MediaPlayerService getService(){
			return MediaPlayerService.this;
		}
	}
	@Override
	public void onPrepared(MediaPlayer mp) {
		// invoked when the media source is ready for playback
		if(!mediaPlayer.isPlaying()){
			mediaPlayer.start();
		}
		isPaused = false;
		editor.putInt("lastPlayedIndex", sharedPref.getInt("serviceAudioIndex", -1));
		editor.commit();
		initiatePlayerInfo();
	}
	public void initiatePlayerInfo(){
		Intent bIntent = new Intent(MusicPlayerActivity.Broadcast_Player_Info);
	  	// bIntent.putExtra("currentIndex", audioIndex);
	  	sendBroadcast(bIntent);
	}
	private void initMediaPlayer(){
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		mediaPlayer.setOnInfoListener(this);
		// Reset so that the MediaPlayer is not pointing to another data source
		mediaPlayer.reset();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		
		try {
			mediaPlayer.setDataSource(sModel.getSongPath());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			stopSelf();
		}
		mediaPlayer.prepareAsync();		
	}
	
	private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// pause audio on ACTION_AUDIO_BECOMING_NOISY
			resumePosition = mediaPlayer.getCurrentPosition();
			mediaPlayer.pause();
			buildNotification(PlaybackStatus.PAUSED);
		}
	};
	private void registerBecomingNoisyReceiver(){
		// register after getting audio focus
		IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		registerReceiver(becomingNoisyReceiver, intentFilter);
	}
	
	//Handle incoming phone calls
	private void callStateListener(){
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		//statrting listening for phonestate changes
		phoneStateListener = new PhoneStateListener(){
			@Override
			public void onCallStateChanged(int state, String incomingNumber){
				switch(state){
				//if at least one call exists or the phone is ringing
				// pause the MediaPlayer
				case TelephonyManager.CALL_STATE_OFFHOOK:
				case TelephonyManager.CALL_STATE_RINGING:
					if(mediaPlayer != null){
						resumePosition = mediaPlayer.getCurrentPosition();
						mediaPlayer.pause();
						ongoingCall = true;
					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					if(mediaPlayer != null){
						if(ongoingCall){
							ongoingCall = false;
							mediaPlayer.seekTo(resumePosition);
							mediaPlayer.start();
						}
					}
				}
			}
		};
		// Register the listener with the telephony manager
		// listen for changes to the device call state
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	private BroadcastReceiver playNewAudio = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// Get the new media index form sharedpreferences
			int indx = sharedPref.getInt("serviceAudioIndex", -1);
			/*if(audioIndex != -1 && audioIndex < songList.size()){
				sModel = songList.get(audioIndex);
			}else{
				stopSelf();
			}*/
			sModel = songList.get(indx);
			// A play new audio action received 
			//reset mediaPlayer to play the new audio
			//mediaPlayer.stop();
			stopMedia();
			mediaPlayer.reset();
			initMediaPlayer();
			buildNotification(PlaybackStatus.PLAYING);			
		}	
	};
	private void register_playNewAudio(){
		// Register playNewMedia receiver
		IntentFilter filter = new IntentFilter(MusicPlayerActivity.Broadcast_PLAY_NEW_AUDIO);
		registerReceiver(playNewAudio,filter);
	}
	
	private BroadcastReceiver loopingState = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// Get the new media index form sharedpreferences
			boolean looping = sharedPref.getBoolean("isLooping", false);
			if(looping){
				mediaPlayer.setLooping(true);
			}else{
				mediaPlayer.setLooping(false);
			}
		}	
	};
	private void register_loopingState(){
		// Register playNewMedia receiver
		IntentFilter filter = new IntentFilter(MusicPlayerActivity.Broadcast_IS_LOOPING);
		registerReceiver(loopingState,filter);
	}

	protected void buildNotification(PlaybackStatus playbackStatus) {
		if(playbackStatus == PlaybackStatus.PLAYING){
			
		}else if(playbackStatus == PlaybackStatus.PAUSED){
			
		}

		audioIndex = sharedPref.getInt("serviceAudioIndex", -1);
		nBuilder = new NotificationCompat.Builder(this);
		nBuilder.setContentTitle("Music");
		nBuilder.setSmallIcon(R.drawable.plyy);
		nBuilder.setOngoing(true);
		
		remoteView = new RemoteViews(getPackageName(),R.layout.notification_view);
		setListeners(remoteView);
		nBuilder.setContent(remoteView);
		
		nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//nManager.notify(10, nBuilder.build());
		startForeground(10,nBuilder.build());
	}

	public void setListeners(RemoteViews view){
		Bitmap songImage;
		   try{
			   MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
			   metaRetriever.setDataSource(this, Uri.parse(songList.get(audioIndex).getSongPath()));
			   byte[] art = metaRetriever.getEmbeddedPicture();
			   if(art!=null){
				   songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
				   view.setImageViewBitmap(R.id.imageView1,songImage);						   
			   }
		   }catch(Exception e){
			   }
		view.setTextViewText(R.id.title, songList.get(audioIndex).getSongTitle());
		view.setTextViewText(R.id.artist, songList.get(audioIndex).getSongArtist());
		Intent cancel = new Intent(this,MediaPlayerService.class);
		//cancel.putExtra("Msg","cancel");
		cancel.setAction(ACTION_STOP);
		PendingIntent btn1 = PendingIntent.getService(this, 0, cancel, 0);
		view.setOnClickPendingIntent(R.id.cancel,btn1);
		
		Intent next = new Intent(this,MediaPlayerService.class);
		//next.putExtra("Msg","next");
		next.setAction(ACTION_NEXT);
		PendingIntent btn2 = PendingIntent.getService(this,1,next,0);
		view.setOnClickPendingIntent(R.id.next,btn2);
		
		Intent pausePlay = new Intent(this,MediaPlayerService.class);
		//pausePlay.putExtra("Msg","pausePlay");
		if(isPaused){
			pausePlay.setAction(ACTION_PLAY);
			songImage = BitmapFactory.decodeResource(getResources(), R.drawable.plyy);
			view.setImageViewBitmap(R.id.play, songImage);
		}else{
			pausePlay.setAction(ACTION_PAUSE);
			songImage = BitmapFactory.decodeResource(getResources(), R.drawable.paause);
			view.setImageViewBitmap(R.id.play, songImage);
		}	
		PendingIntent btn3 = PendingIntent.getService(this,2,pausePlay,0);
		view.setOnClickPendingIntent(R.id.play,btn3);
		
		Intent prev = new Intent(this,MediaPlayerService.class);
		//next.putExtra("Msg","next");
		prev.setAction(ACTION_PREVIOUS);
		PendingIntent btn4 = PendingIntent.getService(this,3,prev,0);
		view.setOnClickPendingIntent(R.id.prev,btn4);
		
		Intent nowPlaying = new Intent(this,MusicPlayerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putBoolean("isFromNowPlaying", true);
		bundle.putParcelableArrayList("parcelSongList", songList);
		bundle.putInt("songIndex", -1);
		nowPlaying.putExtras(bundle);
		//nowPlaying.setAction(ACTION_PAUSE);
		PendingIntent imgIntent = PendingIntent.getActivity(getApplicationContext(), 4, nowPlaying, 0);
		view.setOnClickPendingIntent(R.id.imageView1, imgIntent);
			
	}
	public void removeNotification(){
		//cancel notification
		//nManager.cancel(10);	
		stopForeground(true);
	}
	private void handleIncomingActions(Intent playbackAction){
		if(playbackAction == null || playbackAction.getAction() == null) return;
		String action = playbackAction.getAction();
		if(action.equalsIgnoreCase(ACTION_PLAY)){
			resumeMedia();
			//isPaused = false;
		}else if(action.equalsIgnoreCase(ACTION_PAUSE)){
			this.pause();
			//isPaused = true;
		}else if(action.equalsIgnoreCase(ACTION_PREVIOUS)){
			skeepToPrevious();
			
		}else if(action.equalsIgnoreCase(ACTION_NEXT)){
			skeepToNext();	
		}else if(action.equalsIgnoreCase(ACTION_STOP)){
			resumePosition = mediaPlayer.getCurrentPosition();
			mediaPlayer.pause();
			removeNotification();
			//stopSelf();
		}
	}
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBufferPercentage() {
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		return mediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return mediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		resumePosition = mediaPlayer.getCurrentPosition();
		mediaPlayer.pause();
		isPaused = true;
		buildNotification(PlaybackStatus.PAUSED);
	}

	@Override
	public void seekTo(int position) {
		mediaPlayer.seekTo(position);
	}

	@Override
	public void start() {
		mediaPlayer.start();
	}
	public void resumeMedia(){
		mediaPlayer.seekTo(resumePosition);
		mediaPlayer.start();
		isPaused = false;
		buildNotification(PlaybackStatus.PLAYING);
	}
	
	public void stopMedia(){
		if(mediaPlayer == null) return;
		if(mediaPlayer.isPlaying()){
			//resumePosition = mediaPlayer.getCurrentPosition();
			mediaPlayer.stop();
		}
	}
	
	public void skeepToNext(){
		//audioIndex = sharedPref.getInt("serviceAudioIndex", -1);
		isRandom = sharedPref.getBoolean("isShuffled", false);
		audioIndex = sharedPref.getInt("serviceAudioIndex", -1);
		if(isRandom){
			audioIndex = (int)(songList.size()*Math.random()+1);
		}else{
			if(audioIndex == songList.size() - 1){
				audioIndex = 0;
			}else{
				audioIndex = audioIndex + 1;
			}
		}
		
		sModel = songList.get(audioIndex);
		editor.putInt("serviceAudioIndex", audioIndex);
		editor.commit();
		
		mediaPlayer.stop();
		//mediaPlayer.release();
		mediaPlayer.reset();
		initMediaPlayer();
		buildNotification(PlaybackStatus.PLAYING);
		Intent broadcastIntent = new Intent(MusicPlayerActivity.Broadcast_ChangeFromService);
		broadcastIntent.putExtra("changedFromService", true);
		sendBroadcast(broadcastIntent);
	}
	public void skeepToPrevious(){
		isRandom = sharedPref.getBoolean("isShuffled", false);
		audioIndex = sharedPref.getInt("serviceAudioIndex", -1);
		if(isRandom){
			audioIndex = (int)(songList.size()*Math.random()+1);
		}else{
			if(audioIndex == 0){
				audioIndex = songList.size() - 1;
			}else{
				audioIndex = audioIndex - 1;
			}
		}
		sModel = songList.get(audioIndex);
		
		editor.putInt("serviceAudioIndex", audioIndex);
		editor.commit();
		
		mediaPlayer.stop();
		mediaPlayer.reset();
		initMediaPlayer();
		buildNotification(PlaybackStatus.PLAYING);
		Intent broadcastIntent = new Intent(MusicPlayerActivity.Broadcast_ChangeFromService);
		broadcastIntent.putExtra("changedFromService", true);
		sendBroadcast(broadcastIntent);
	}


}
