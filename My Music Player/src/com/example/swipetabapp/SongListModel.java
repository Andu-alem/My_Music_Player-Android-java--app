package com.example.swipetabapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class SongListModel implements Parcelable{
	private Bitmap songImage;
	private String songTitle;
	private String songPath;
	private String songArtist;
	private String songAlbum;
	
	public SongListModel(){
		this.songImage = null;
		this.songTitle = "";
		this.songPath = "";
		this.songArtist = "";
		this.songAlbum = "";
	}
	
	public SongListModel(Parcel parcel){
		songTitle = parcel.readString();
		songPath = parcel.readString();
		songArtist = parcel.readString();
		songAlbum = parcel.readString();
	}
	
	public void setSongImage(Bitmap Image){
		this.songImage = Image;
	}
	public void setSongTitle(String title){
		this.songTitle = title;
	}
	public void setSongPath(String path){
		this.songPath = path;
	}
	public void setSongArtist(String artist){
		this.songArtist = artist;
	}
	public void setSongAlbum(String album){
		this.songAlbum = album;
	}
	public Bitmap getSongImage(){
		return songImage;
	}
	public String getSongTitle(){
		return songTitle;
	}
	public String getSongPath(){
		return songPath;
	}
	public String getSongArtist(){
		return songArtist;
	}
	public String getSongAlbum(){
		return  songAlbum;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(songTitle);
		dest.writeString(songPath);
		dest.writeString(songArtist);
		dest.writeString(songAlbum);
	}
	
	public static final Parcelable.Creator<SongListModel> CREATOR = new Parcelable.Creator<SongListModel>() {

		@Override
		public SongListModel createFromParcel(Parcel parcel) {
			// TODO Auto-generated method stub
			return new SongListModel(parcel);
		}

		@Override
		public SongListModel[] newArray(int size) {
			// TODO Auto-generated method stub
			return new SongListModel[size];
		}
	};
}
