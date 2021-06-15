package com.example.swipetabapp;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListFileActivity extends Activity{
	//private ArrayList<File> fileList = new ArrayList<File>();
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_file_activity);			
		//ArrayList<String> fileList = new ArrayList<String>();
		GridView gdView = (GridView)findViewById(R.id.gridView1);
		gdView.setAdapter(new ImageGridAdapter(this));		
	}
	
	public class ImageGridAdapter extends BaseAdapter{
		private static final int width = 250;
		private static final int height = 250;
		private static final int padding = 8;

		String[] arrPath = path_list();
		
		private Context mContext;
		public ImageGridAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrPath.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arrPath[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageCache lruCache = new ImageCache();
			ArrayList<String> pathList = new ArrayList<String>(Arrays.asList(arrPath));
			ImageView imgView = (ImageView) convertView; 
			//if convertview's not recycled, initialize some attributes
			if(imgView == null){
				imgView = new ImageView(mContext);
				imgView.setLayoutParams(new GridView.LayoutParams(width,height));
				imgView.setPadding(padding, padding, padding, padding);
				imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
			String songPath = arrPath[position];
			/*
			try{		    	
		    	BitmapFactory.Options options = new BitmapFactory.Options();
		    	//options.inJustDecodeBounds =  true;
		    	//options.inSampleSize = 4;
		    	Bitmap songImg = BitmapFactory.decodeFile(songPath);
		    	lruCache.put(songPath, songImg);
				//imgView.setImageBitmap(lruCache.get(songPath));	
				imgView.setImageBitmap(songImg);
		    }catch(Exception e){
		    	//do something here
		    	Toast.makeText(mContext, "error has occured", Toast.LENGTH_SHORT).show();
		    }*/
			imgView.setImageURI(Uri.parse(songPath));
			imgView.setTag(songPath);
		 	imgView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String imgResource = (String) v.getTag();
				    //Toast.makeText(mContext, "you have pressed "+imgResource,Toast.LENGTH_SHORT ).show();					
					Intent intent = new Intent(mContext,ShowGallaryImage.class);
					intent.putExtra("ImageResource", imgResource);
					mContext.startActivity(intent);
				}
			});
			return imgView;
		}
		
		public String[] path_list(){
			Boolean isSDPresent =  android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			//If SD card is present then use this:
			if(isSDPresent){
					final String[] columns ={ MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
					final String orderBy = MediaStore.Images.Media._ID;
					//Stores all the images from the gallery in Cursor
					Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,null, orderBy);
					//Total number of images
					int count = cursor.getCount();
					//Create an array to store path to all the images
					arrPath = new String[count];
					for (int i = 0; i < count; i++) {
					    cursor.moveToPosition(i);
					    int dataColumnIndex = cursor.getColumnIndex
					(MediaStore.Images.Media.DATA);
					    //Store the path of the image
					    arrPath[i]= cursor.getString(dataColumnIndex);
					    //Log.i("PATH", arrPath[i]);
					}
					cursor.close();
			}
			return arrPath;	
		}
		
	}


}
