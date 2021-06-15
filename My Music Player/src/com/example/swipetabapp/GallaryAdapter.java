package com.example.swipetabapp;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class GallaryAdapter extends BaseAdapter{
	private Context mContext;
	private static final int padding = 8;
	public static final int width = 250;
	public static final int height = 250;	
	ArrayList<String> images = imageUriList();
	
	
	public ArrayList<String> imageUriList(){
		ArrayList<String> img = new ArrayList<String>();
		String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String targetPath = ExternalStorageDirectoryPath + "/Download";
		File targetDirectory = new File(targetPath);
		File[] files = targetDirectory.listFiles();
		//String file = files[0].getAbsolutePath();
			
		for (File file: files){
			img.add(file.getAbsolutePath());
		}
		return img;
	}
	
	public GallaryAdapter(Context c){
		mContext = c;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int position) {
		return images.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
		//return images.get(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView imgView = (ImageView) convertView; 
		//if convertview's not recycled, initialize some attributes
		if(imgView == null){
			imgView = new ImageView(mContext);
			imgView.setLayoutParams(new GridView.LayoutParams(width,height));
			imgView.setPadding(padding, padding, padding, padding);
			imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		}
		imgView.setImageURI(Uri.parse(images.get(position)));
		//imgView.setTag(images.get(position));
		imgView.setTag(position);
		imgView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int index = (Integer) v.getTag();
				
				Intent intent = new Intent(mContext,GallaryActivity.class);
				intent.putExtra("imgIndex", index);
				intent.putExtra("imagePath",images);
				mContext.startActivity(intent);
			}
		});
		return imgView;
	}

}
