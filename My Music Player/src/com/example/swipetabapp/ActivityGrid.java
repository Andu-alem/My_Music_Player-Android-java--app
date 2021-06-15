package com.example.swipetabapp;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ActivityGrid extends Activity{
	
	protected static final String EXTRA_RES_ID = "POS";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grid_act);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		GridView gdView = (GridView)findViewById(R.id.gridView1);
		gdView.setAdapter(new ImageAdapter(this));
		
		gdView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Intent intent = new Intent(getApplicationContext(),ShowImageActivity.class);
				intent.putExtra("ImageResource", parent.getItemIdAtPosition(position));
				startActivity(intent);
				
			}
		});
	}
	
	@Override
	public boolean onNavigateUp(){
		//to enable actionbar backbutton
		finish();
		return true;
	}
}
