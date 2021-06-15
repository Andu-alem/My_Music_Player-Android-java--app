package com.example.swipetabapp;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class NotificationMaker {
	private static NotificationManager nManager;
	private NotificationCompat.Builder nBuilder;
	private RemoteViews remoteView;
	private Context mContext;
	public ArrayList<SongListModel> lists;
	public static boolean isRandom = false;
	SharedPreferences sharedPref;
	public int index, listLength;
	MediaPlayer mediaPlayer;
	SongListModel sModel = new SongListModel();

	public NotificationMaker(Context c){
		this.mContext = c;
	}
	public void makeNotification(){
		nBuilder = new NotificationCompat.Builder(mContext);
		nBuilder.setContentTitle("Music");
		nBuilder.setSmallIcon(R.drawable.imgx);
		nBuilder.setOngoing(true);
		
		remoteView = new RemoteViews(mContext.getPackageName(),R.layout.notification_view);
		
		setListeners(remoteView);
		nBuilder.setContent(remoteView);
		
		nManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		nManager.notify(10, nBuilder.build());
	}
	public void setListeners(RemoteViews view){
		Intent cancel = new Intent(mContext,NotificationResult.class);
		cancel.putExtra("Msg","cancel");
		PendingIntent btn1 = PendingIntent.getActivity(mContext, 0, cancel, 0);
		view.setOnClickPendingIntent(R.id.cancel,btn1);
		
		Intent next = new Intent(mContext,NotificationResult.class);
		next.putExtra("Msg","next");
		PendingIntent btn2 = PendingIntent.getActivity(mContext,1,next,0);
		view.setOnClickPendingIntent(R.id.next,btn2);
		
		Intent pausePlay = new Intent(mContext,NotificationResult.class);
		pausePlay.putExtra("Msg","pausePlay");
		PendingIntent btn3 = PendingIntent.getActivity(mContext,2,pausePlay,0);
		view.setOnClickPendingIntent(R.id.play,btn3);
	}
	public static void cancelNotification(){
		//calncel notification
		NowPlayingActivity.mpObject.stop();
		nManager.cancel(10);			
	}
	public static void pausePlay(){
		if(NowPlayingActivity.isPlayOn){
			NowPlayingActivity.mpObject.pause();
			NowPlayingActivity.isPlayOn = false;
		}else{
			NowPlayingActivity.mpObject.start();
			NowPlayingActivity.isPlayOn = true;
		}
	}
	public void next(){
		lists = NotificationResult.songList;
		listLength = lists.size();
		NowPlayingActivity.mpObject.stop();
		isRandom = sharedPref.getBoolean("isShuffled", false);
		index = NowPlayingActivity.index;
		   if(isRandom){
			   index = (int)(listLength*Math.random()+1);
		   }else{
			   if(index == (lists.size()-1)){
				   index = 0;
			   }else{
				   index = index + 1;
			   }
		   }
		sModel = lists.get(index);
		String music_path = sModel.getSongPath();
		mediaPlayer = MediaPlayer.create(mContext, Uri.parse(music_path));
		mediaPlayer.start();
		//NowPlayingActivity.mpObject = mediaPlayer;

	}
	
}
