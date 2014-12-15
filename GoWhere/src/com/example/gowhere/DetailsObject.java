package com.example.gowhere;

public class DetailsObject {
	private String image_url;
	private String telephone;
	private String address;
	private String details;
	private String price;
	private String similar1;
	private String similar2;
	private String similar3;
	private String similar4;
	
	public DetailsObject(){
		
	}
	
	public String getImageUrl(){
		return image_url;
	}
	
	public String getTelephone(){
		return telephone;
	}
	public String getAddress(){
		return address;
	}
	public String getDetails(){
		return details;
	}
	public String getPrice(){
		return price;
	}
	
	
	public void setImageUrl(String image_url){
		this.image_url = image_url;
	}
	
	public void setTelephone(String telephone){
		this.telephone = telephone;
	}
	public void setAddress(String address){
		this.address = address;
	}
	public void setDetails(String details){
		this.details = details;
	}
	public void setPrice(String price){
		this.price = price;
	}
	
}
