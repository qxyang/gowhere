package com.example.gowhere;

import android.util.Log;

public class LatLongDistance {
	 private static final double EARTH_RADIUS = 6378137.0;  
	 // 返回单位是Km
	 public static double getDistance(double longitude1, double latitude1,  
	   double longitude2, double latitude2) {  
	  double Lat1 = rad(latitude1);  
	  double Lat2 = rad(latitude2);  
	  double a = Lat1 - Lat2;  
	  double b = rad(longitude1) - rad(longitude2);  
	  double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)  
	    + Math.cos(Lat1) * Math.cos(Lat2)  
	    * Math.pow(Math.sin(b / 2), 2)));  
	  s = s * EARTH_RADIUS/1000;  
	  Log.e("LatLongDistance2", String.valueOf(s));
	  s = (double)(Math.round(s * 10)) / 10;  
	  Log.e("LatLongDistance2", String.valueOf(s));
	  return s;  
	 }  
	 private static double rad(double d) {  
	  return d * Math.PI / 180.0;  
	 }  
}
