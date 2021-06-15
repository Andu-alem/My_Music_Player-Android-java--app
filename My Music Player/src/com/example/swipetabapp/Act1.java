package com.example.swipetabapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Act1 extends Activity{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		//getActionBar().setDisplayShowHomeEnabled(true);
		
	}
	
	@Override
	public boolean onNavigateUp(){
		//to enable actionbar backbutton
		finish();
		return true;
	}
	// another option for the above code
	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/
	public boolean onCreateOptionMenu(Menu menu){
		getMenuInflater().inflate(R.menu.activity_swipe, menu);
		return true;
	}
	public boolean onOptionItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			//Toast.makeText(this,"back button clicked",Toast.LENGTH_SHORT).show();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/*
	@Override
	public void onBackPressed(){
	   //the below command used to totally close the app when a back button is pressed
		moveTaskToBack(true);
		Act1.this.finish();
	}*/
	public void play(View view){
		Intent i = new Intent(this, PlayActivity.class);
		startActivity(i);
	}
}
