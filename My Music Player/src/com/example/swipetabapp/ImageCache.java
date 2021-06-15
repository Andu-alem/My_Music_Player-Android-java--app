package com.example.swipetabapp;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageCache extends LruCache<String, Bitmap>{
	//int memClass = ((ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	//int cacheSize;
	public ImageCache(int maxSize) {
		super(maxSize);
		// TODO Auto-generated constructor stub
	}
	public ImageCache(){
		super(1024);
	}
	@Override
	protected int sizeOf(String key, Bitmap bitmap){
		return bitmap.getByteCount()/1024;
	}
	@Override
	protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue){
		oldValue.recycle();
	}
	//add bitmap to the memory cache
	/*public void addBitmapToCache(String key, Bitmap bitmap){
		if(getBitmap(key)==null){
			lruCache.put(key,bitmap);
		}
	}
	public Bitmap getBitmap(String key){
		return lruCache.get(key);
	
	}*/

}
