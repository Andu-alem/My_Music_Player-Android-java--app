package com.example.swipetabapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowImageActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_activity);
		
		Integer imgId = getIntent().getIntExtra("ImageResource", 0);
		
		ImageView imgView = (ImageView) findViewById(R.id.image1);
		imgView.setImageResource(imgId);
		
	}

}
