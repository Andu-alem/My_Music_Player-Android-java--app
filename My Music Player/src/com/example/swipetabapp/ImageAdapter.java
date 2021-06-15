package com.example.swipetabapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

public class ImageAdapter extends BaseAdapter {

	//private ArrayList<Integer> pics = new ArrayList<Integer>(
	//		Arrays.asList(R.drawable.im1,R.drawable.im2,R.drawable.im3,R.drawable.im4,R.drawable.im5,R.drawable.im6,R.drawable.im7,R.drawable.im8,R.drawable.im9,
		//			R.drawable.im10,R.drawable.im11,R.drawable.im12,R.drawable.im13,R.drawable.im14,R.drawable.im15,R.drawable.im16,R.drawable.im17,R.drawable.im18));
	
	public Integer[] pics = {R.drawable.im5, R.drawable.im18,R.drawable.im14,R.drawable.im15,R.drawable.im17,R.drawable.im18,R.drawable.imteddy,
			R.drawable.imgy, R.drawable.im};
	
	private static final int padding = 8;
	private static final int width = 250;
	private static final int height = 250;
	private static int index;
	private Context mContext;
	//private List<Integer> pics;
	
	public ImageAdapter(Context c){
		mContext = c;
	}
	
	@Override
	public int getCount() {
		//return pics.size();
		return pics.length;
	}

	@Override
	public Object getItem(int position) {
		//return null;
		return pics[position];
	}

	@Override
	public long getItemId(int position) {
		// Will get called to provide the ID that
		// is passed to OnItemClickListener.onItemClick()
		//return pics.get(position);
		return 0;
	}

	@Override
	public int getItemViewType(int arg0) {
		// TODO Auto-generated method stub
		return 0;
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
		index = position;
		imgView.setImageResource(pics[position]);
		/*
		imgView.setClickable(true);
		imgView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "img resource "+index, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(mContext,GridImageViewActivity.class);
				intent.putExtra("imgIndex", index);
				intent.putExtra("imagePath",pics);
				mContext.startActivity(intent);
			}
		});*/
		return imgView;
	}

}
