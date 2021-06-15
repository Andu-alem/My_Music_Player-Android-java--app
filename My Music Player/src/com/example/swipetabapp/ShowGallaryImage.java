package com.example.swipetabapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowGallaryImage extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_activity);
		
		String imgId = getIntent().getStringExtra("ImageResource");
		
		ImageView imgView = (ImageView) findViewById(R.id.image1);
		imgView.setImageURI(Uri.parse(imgId));
		
	}

}
