package com.example.gowhere;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeListViewAdapter extends ArrayAdapter<HomeListViewItem>{
	private int resourceId;
	private double latitude = 39.990541;
	private double longitude = 116.319706;
	private int startPage;
	private int viewType = -1;
	private int days;
	private String city = "%E5%8C%97%E4%BA%AC";//±±æ©

	private Context cont;
	public static String path = Environment.getExternalStorageDirectory().toString()  + File.separator + "testdata" + File.separator; 
	protected List<HomeListViewItem> listViews = new ArrayList<HomeListViewItem>();
	private boolean listFullFlag = false;
	public HomeListViewAdapter (Context context, int viewResourceId, List<HomeListViewItem> objects){
		super(context, viewResourceId, objects);
		cont = context;
		resourceId =viewResourceId;
		if (objects != null)
			this.listViews.addAll(objects);
		startPage = 1;
	}

	public void putListItems(List<HomeListViewItem> items){
		if(items == null || this.listViews == null)
			return;
		
		startPage = startPage + 1;
		this.listViews.addAll(items);
		this.notifyDataSetChanged();
	}
	
	public void putListItem(HomeListViewItem item){
		if(item == null || this.listViews == null)
			return;
		List<HomeListViewItem> lists = new ArrayList<HomeListViewItem>();
		lists.add(item);
		putListItems(lists);
	}
	
	public HomeListViewItem getPosition(int position){
		return listViews.get(position);
	}
	@Override
	public int getCount(){
		return listViews.size();
	}
	
	public void setLocation(double latitude, double longitude, String city){
		Log.e("setLocation Lat", String.valueOf(latitude));
		Log.e("setLocation Long", String.valueOf(longitude));
		this.latitude = latitude;
		this.longitude = longitude;
		try{
			this.city = URLEncoder.encode(city, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		HomeListViewItem listViewItem = listViews.get(position);
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		ImageView itemView = (ImageView) view.findViewById(R.id.home_list_view_image);
		TextView  itemText = (TextView) view.findViewById(R.id.home_list_view_text);
		TextView  itemDistance = (TextView) view.findViewById(R.id.home_list_view_distance);
		
		itemText.setText(listViewItem.getName());
		
		double distance = LatLongDistance.getDistance(listViewItem.getLongitude(), listViewItem.getLatitude(), longitude, latitude);
		itemDistance.setText("æ‡¿Î£∫" + Double.toString(distance) + "Km");
		File file = new File(path + listViewItem.getId() + ".jpg");
		if (file.exists()){
			//itemView.setImageURI(Uri.fromFile(new File(path + listViewItem.getId() + ".jpg")));
			Bitmap bmp = BitmapFactory.decodeFile(path + listViewItem.getId() + ".jpg");  
			itemView.setImageBitmap(bmp);
		} else
			getImageWithHttpURLConnection(listViewItem.getImageUrl(), listViewItem.getId());
		
		if (position == getCount() - 1 && listFullFlag == false)
			sendRequestWithHttpURLConnection();
		
		return view;
	}
	public void doneProcess(){
		this.notifyDataSetChanged();
	}
	private Handler handler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what){
			case 18:
				doneProcess();
				break;
			case 19:
				Log.e("handler", "Receive Response Msg!");
				String rspString = (String)msg.obj; 
				List<HomeListViewItem> items = parseJsonWithJsonObject(rspString);
				if (items.size() > 0)
					putListItems(items);
				else
					listFullFlag = true;
				break;
			case 20:
				Toast.makeText(cont, (String)msg.obj, Toast.LENGTH_SHORT).show(); 
				break;
			}
		}
	};
	private List<HomeListViewItem> parseJsonWithJsonObject(String jsonData) {
		List<HomeListViewItem> items = new ArrayList<HomeListViewItem>();
		String id;
		String name;
		String imageUrl;
		double latitude;
		double longitude;
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				id = jsonObject.getString("_id");
				name = jsonObject.getString("name");
				imageUrl = jsonObject.getString("s_photo_url");
				latitude = jsonObject.getDouble("latitude");
				longitude = jsonObject.getDouble("longitude");
				items.add(new HomeListViewItem(name, imageUrl, id, latitude, longitude));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

    private void getImageWithHttpURLConnection(final String urlString, final String idString) {
    	new Thread(new Runnable(){
    		@Override
    		public void run() {
    			HttpURLConnection connection = null;
    			try {
    				//URL url = new URL("http://mytestproj.duapp.com" + urlString);
    				URL url = new URL(urlString);
    				connection = (HttpURLConnection) url.openConnection();
    				connection.setRequestMethod("GET");
    				connection.setConnectTimeout(8000);
    				connection.setReadTimeout(8000);
    				/*
    				InputStream input = connection.getInputStream();  
   
    				OutputStream output = new FileOutputStream(path + idString + ".jpg");
    				byte[] img = new byte[1024];
    				while (input.read(img) != -1) {             
    					output.write(img, 0, img.length);         
    				}           
    				output.flush();         
    				output.close();         
    				input.close(); 
					*/
    				InputStream input = connection.getInputStream(); 
    				byte[] data = readInputStream(input);
    				input.close();
    				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  
    		         
    				bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(path + idString + ".jpg"));  
    				Message msg = new Message();
    				msg.what = 18;
    				msg.obj = path + idString + ".jpg";
    				handler.sendMessage(msg);
    			}catch (Exception e){		
    				e.printStackTrace();
    			}finally{
    				if(connection != null){
    					connection.disconnect();
    				}
    			}
    		}
    	}).start();
    }
    
    public static byte[] readInputStream(InputStream inStream) throws Exception{    
    	ByteArrayOutputStream outSteam = new ByteArrayOutputStream();    
    	byte[] buffer = new byte[4096];    
    	int len = 0;    
    	while( (len = inStream.read(buffer)) != -1 ){    
    		outSteam.write(buffer, 0, len);    
    	}    
    		outSteam.close();    
    	return outSteam.toByteArray();    
    }  
    
	private void sendRequestWithHttpURLConnection() {
		Log.e("sendRequestWithHttpURLConnection", String.valueOf(startPage));
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					
					//http://172.22.10.71:3000/Android/Dianping?lat=39.98072&lon=116.36224&radius=5,10
					//URL url = new URL("http://mytestproj.duapp.com/Android/Business?lon=" + longitude + "&lat=" + latitude + "&minDistance=" + minDistance + "&maxDistance=" + maxDistance + "&start="+ startPage + "&size=5");
					String strUrl = "http://mytestproj.duapp.com/Android/Business?start="+ startPage + "&size=5&find=city," + city + ",days," + days + "&lon=" + longitude + "&lat=" + latitude;
					Log.e("StrURL:", strUrl);
					URL url = new URL(strUrl); 
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					 
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new
					InputStreamReader(in)); StringBuilder sb = new
					StringBuilder();
					 
					String line = null; 
					try {
						while ((line = reader.readLine()) != null) { 
							sb.append(line + "\n"); 
						}
					} catch (IOException e) {
						e.printStackTrace(); 
					} finally {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace(); 
						} 
					} 
					String rspString = sb.toString();
					
					Message msg = new Message();
					msg.what = 19;
					msg.obj = rspString;
					handler.sendMessage(msg);
				} catch (Exception e) {
					//e.printStackTrace();
					Message msg = new Message();
    				msg.what = 20;
    				msg.obj = "«ÎºÏ≤ÈÕ¯¬Á£°";
    				handler.sendMessage(msg);
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	
	public void setViewType(int viewType){
		Log.e("setViewType", String.valueOf(viewType));
		this.listViews.clear();
		this.notifyDataSetChanged();
		startPage = 1;
		
		switch (viewType){
		case 0:
			days = 0;
			break;
		case 1:	
			days = 1;
			break;
		case 2:
			days = 2;
			break;	
		case 7:
			days = 7;
			break;
			
		}
		listFullFlag = false;
		sendRequestWithHttpURLConnection();		
	}

}
