package com.example.swipetabapp;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;

public class ListImageActivity extends Activity{
	//private ArrayList<File> fileList = new ArrayList<File>();
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_grid);
		
		ArrayList<String> fileList = new ArrayList<String>();
		GridView gdView = (GridView)findViewById(R.id.gridView1);
		gdView.setAdapter(new GallaryAdapter(this));
		String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		String targetFile = ExternalStorageDirectoryPath + "/Download/addis.jpg";
		/*
		String targetPath = ExternalStorageDirectoryPath + "/Download";
		File targetDirectory = new File(targetPath);
		File[] files = targetDirectory.listFiles();
		for(File file:files){
			fileList.add(file.getAbsolutePath());
		}
		
		
		ListView lv = (ListView) findViewById(R.id.listView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,fileList);
		lv.setAdapter(adapter);*/
		ImageView im = (ImageView) findViewById(R.id.imageView1);
		TextView tv = (TextView) findViewById(R.id.textView1);
		im.setImageURI(Uri.parse(targetFile));
		tv.setText(targetFile);
		/*
		File root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
				fileList = getfile(root);
				for (int i = 0; i < fileList.size(); i++) {
				    String stringFile = fileList.get(i).getName();
				    //Do whatever you want to do with each filename here...
				}
				*/
		
	}
	/*
	public ArrayList<File> getfile(File dir) {
	    File listFile[] = dir.listFiles();
	    if (listFile != null && listFile.length > 0) {
	    for (int i = 0; i < listFile.length; i++) {
	        if (listFile[i].getName().endsWith(".png")
	                || listFile[i].getName().endsWith(".jpg")
	                || listFile[i].getName().endsWith(".jpeg")
	                || listFile[i].getName().endsWith(".gif"))
	            {
	                String temp = listFile[i].getPath().substring(0, listFile[i].getPath().lastIndexOf('/'));
	                fileList.add(temp);
	            }
	        }
	    }
	    return fileList;
	}*/
}
