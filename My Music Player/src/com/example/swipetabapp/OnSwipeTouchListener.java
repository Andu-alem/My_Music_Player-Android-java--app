package com.example.swipetabapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

	public class OnSwipeTouchListener implements OnTouchListener, OnItemClickListener{
		private GestureDetector gestureDetector;
		public SongListModel listObj;
		
		public OnSwipeTouchListener(Context c) {
			gestureDetector = new GestureDetector(c, new GestureListener());
		}
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			return gestureDetector.onTouchEvent(motionEvent);
		}
		
		private final class GestureListener extends GestureDetector.SimpleOnGestureListener{
					private static final int SWIPE_THRESHOLD = 100;
					private static final int SWIPE_VELOCITY_THRESHOLD = 100;
					
					@Override
			public boolean onDown(MotionEvent e){
				return true;
			}
			@Override
			public boolean onSingleTapUp ( MotionEvent e )
			{
			         onClick ();
			return super .onSingleTapUp ( e );
			}
			@Override
			public boolean onDoubleTap ( MotionEvent e ) {
			onDoubleClick ();
			return super .onDoubleTap ( e );
			}
			@Override
			public void onLongPress ( MotionEvent e ) {
			onLongClick ();
			super .onLongPress ( e );
			}
			@Override
			public boolean onFling ( MotionEvent e1 ,MotionEvent e2 , float velocityX , float velocityY ) {
				try {
					float diffY = e2 .getY() - e1 . getY();
					float diffX = e2 .getX() - e1 . getX();
					if ( Math .abs( diffX) > Math .abs( diffY)) {
						if ( Math. abs( diffX) > SWIPE_THRESHOLD&& Math .abs( velocityX) > SWIPE_VELOCITY_THRESHOLD ) {
							if ( diffX > 0) {
								onSwipeRight ();
							} else {
								onSwipeLeft ();
							}
						}
					}
					else {
						if ( Math. abs( diffY) > SWIPE_THRESHOLD&& Math .abs( velocityY) > SWIPE_VELOCITY_THRESHOLD ) {
							if ( diffY > 0) {
								onSwipeDown ();
							} else {
								onSwipeUp ();
							}
						}
					}
				}
			    catch ( Exception exception ) {
			    	exception . printStackTrace ();
			    }
				return false ;
			  }
		}
		public void onSwipeRight () {
		}
		public void onSwipeLeft () {
		}
		private void onSwipeUp () {
		}
		private void onSwipeDown () {
		}
		private void onClick () {
		}
		private void onDoubleClick () {
		}
		private void onLongClick () {
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
					
		}
	
}

