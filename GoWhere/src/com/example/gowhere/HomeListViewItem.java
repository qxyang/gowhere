package com.example.gowhere;

public class HomeListViewItem {
	private String name;
	private String imageUrl;
	private String id;
	private double latitude;
	private double longitude;
	public HomeListViewItem(){
		
	}
	public HomeListViewItem(String name, String imageUrl, String id, double latitude, double longitude){
		this.name = name;
		this.imageUrl = imageUrl;
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	public HomeListViewItem(String name, String imageUrl, String id){
		this.name = name;
		this.imageUrl = imageUrl;
		this.id = id;
	}
	public String getName(){
		return name;
	}
	public String getImageUrl(){
		return imageUrl;
	}
	public String getId(){
		return id;
	}
	public double getLatitude(){
		return latitude;
	}
	public double getLongitude(){
		return longitude;
	}
	public void setName(String name){
		this.name = name;
	}
	public void setImageUrl(String imageUrl){
		this.imageUrl = imageUrl;
	}
	public void setId(String id){
		this.id = id;
	}
	public void setLatitude(double latitude){
		this.latitude = latitude;
	}
	public void setLongitude(double longitude){
		this.longitude = longitude;
	}
}
