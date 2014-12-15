package com.example.gowhere;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MainActivity extends Activity  implements OnFocusChangeListener{

	public static final int SHOW_TEXT = 0;
	private HomeListViewAdapter adapter = null;
	private double latitude = 0;
	private double longitude = 0;
	public double getLatitude(){
		return latitude;
	}
	public double getLongitude(){
		return longitude;
	}
	public static String path = Environment.getExternalStorageDirectory()
			.toString() + File.separator + "testdata" + File.separator;
	
	StringBuffer sb = new StringBuffer(256);
	Button locationButton;
	Button onedayButton;
	Button twodayButton;
	Button sevendayButton;
	Button guessButton;
	
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home_layout);

		adapter = new HomeListViewAdapter(MainActivity.this,
				R.layout.home_list_view_item, null);
		ListView listView = (ListView) findViewById(R.id.home_list_view);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){
		
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	
					HomeListViewItem homeListViewItem = adapter.getPosition(position);
	
					Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
					intent.putExtra("name", homeListViewItem.getName());
					intent.putExtra("id", homeListViewItem.getId());
					intent.putExtra("imageUrl", homeListViewItem.getImageUrl());
					startActivity(intent); 
					//Toast.makeText(MainActivity.this, homeListViewItem.getName(), Toast.LENGTH_SHORT).show(); 
					} 
			}
		);
		locationButton = (Button) findViewById(R.id.geo_button);
		locationButton.setOnClickListener(new Button.OnClickListener(){//创建监听     
			public void onClick(View v) {   
				Intent intent = new Intent(MainActivity.this, LocationActivity.class);
				intent.putExtra("location", sb.toString());
				startActivity(intent);  
			}    
		});  
		
		onedayButton = (Button) findViewById(R.id.one_day_button);
		onedayButton.setOnClickListener(new Button.OnClickListener(){//创建监听     
			public void onClick(View v) {   
				Log.e("onClick", "Click One Day");
				adapter.setViewType(1);
				//onedayButton.requestFocus(); 
				  
			}    
		});  
		
		twodayButton = (Button) findViewById(R.id.two_day_button);
		twodayButton.setOnClickListener(new Button.OnClickListener(){//创建监听     
			public void onClick(View v) {   
				Log.e("onClick", "Click Two Day");
				adapter.setViewType(2);
				//twodayButton.requestFocus(); 
				  
			}    
		});  
		
		sevendayButton = (Button) findViewById(R.id.seven_day_button);
		sevendayButton.setOnClickListener(new Button.OnClickListener(){//创建监听     
			public void onClick(View v) {   
				Log.e("onClick", "Click Seven Day");
				adapter.setViewType(7);
				//sevendayButton.requestFocus();  
			}    
		});  
		
		guessButton = (Button) findViewById(R.id.guess_like_button);
		guessButton.setOnClickListener(new Button.OnClickListener(){//创建监听     
			public void onClick(View v) {   
				Log.e("onClick", "Click Guess Day");
				adapter.setViewType(0);
				//guessButton.requestFocus();
				  
			}    
			
		});  
		
		guessButton.requestFocus();
		
		onedayButton.setOnFocusChangeListener(this);  
		twodayButton.setOnFocusChangeListener(this);  
		sevendayButton.setOnFocusChangeListener(this);  
		guessButton.setOnFocusChangeListener(this);  
		
		onedayButton.setFocusableInTouchMode(true);
		twodayButton.setFocusableInTouchMode(true);
		sevendayButton.setFocusableInTouchMode(true);
		guessButton.setFocusableInTouchMode(true);
		
		File destDir = new File(path);
		if (!destDir.exists()) {
			destDir.mkdirs();
		} else {
			File[] childFiles = destDir.listFiles();
			for (int i = 0; i < childFiles.length; i++) {
				childFiles[i].delete();
			}
		}
		
		mLocationClient = new LocationClient(getApplicationContext()); //声明LocationClient类
		mLocationClient.registerLocationListener(myListener); //注册监听函数
		setLocationOption();
		mLocationClient.start();
		//sendRequestWithHttpURLConnection();
		

	}
	
	//according to: http://blog.csdn.net/a497393102/article/details/8600096
	public void onFocusChange(View v, boolean hasFocus) {
	    if (hasFocus && v.isFocusableInTouchMode()) {  
	    	((Button)v).performClick();  
	    }
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_TEXT:
				
				 String rspString = (String)msg.obj; 
				 //Toast.makeText(MainActivity.this, String.valueOf(rspString), Toast.LENGTH_SHORT).show(); 
				 List<HomeListViewItem> items = parseJsonWithJsonObject(rspString);
				 adapter.putListItems(items);
				 //Toast.makeText(MainActivity.this, String.valueOf(items.size()), Toast.LENGTH_SHORT).show(); 
				/*
				List<HomeListViewItem> items = initListViews();
				adapter.putListItems(items);
				Toast.makeText(MainActivity.this, "handleMessage OK!",
						Toast.LENGTH_SHORT).show();
				*/
				break;
			}
		}
	};

	private List<HomeListViewItem> initListViews(){
    	List<HomeListViewItem> items = new ArrayList<HomeListViewItem>();
    	items.add(new HomeListViewItem("真功夫","http://t1.s2.dpfile.com/pc/mc/e443e13e48cd450439b98d5f240b86e8(220c164)/thumb.jpg","2014111510401", 40.093307, 116.28595));
    	
    	items.add(new HomeListViewItem("香草香草云南原生态火锅", "http://t2.s2.dpfile.com/pc/mc/72d81cbbb658e5688f9de94b3b2a3e25(220c164)/thumb.jpg", "2014111510442", 39, 116));
     	items.add(new HomeListViewItem("南京大牌档","http://t3.s2.dpfile.com/pc/mc/9e4f6d58e4f6fbfb458120ea8521f8cd(220c164)/thumb.jpg","2014111510471", 27, 100));

    	items.add(new HomeListViewItem("耀莱成龙国际影城", "http://t3.s2.dpfile.com/pc/mc/8ef0ab86b6baa9085bb7e3b0633eba59(220c164)/thumb.jpg","2014111510551", 20,100));
    	/*
    	items.add(new HomeListViewItem("巴黎贝甜","http://t2.s2.dpfile.com/pc/mc/d07c9bdab25c026c95c0183ae9b4a514(220c164)/thumb.jpg","2014111510561"));
    	items.add(new HomeListViewItem("美嘉欢乐影城", "http://t3.s1.dpfile.com/pc/mc/d119e6c6fbb978988110df2055f4332b(220c164)/thumb.jpg", "2014111510562"));
     	items.add(new HomeListViewItem("金鼎轩","http://t2.s2.dpfile.com/pc/mc/fa5bb5c1431ab9f3a9fd91bacbd1e9a4(220c164)/thumb.jpg","2014111510571"));
    	items.add(new HomeListViewItem("A GOGO量贩式KTV", "http://t2.s2.dpfile.com/pc/mc/b569c272a392e7f94f0d80eb6aca0c47(220c164)/thumb.jpg", "2014111510581"));
     	items.add(new HomeListViewItem("俏江南","http://t3.s2.dpfile.com/pc/mc/4cfc33a534a373134049794b2d607143(220c164)/thumb.jpg","2014111510591"));
    	items.add(new HomeListViewItem("羲和", "http://t2.s2.dpfile.com/pc/mc/de7250d32abb9c207b6bed0e9d31383a(220c164)/thumb.jpg", "2014111510592"));
 */
    	return items;
	}

	private List<HomeListViewItem> parseJsonWithJsonObject(String jsonData) {
		List<HomeListViewItem> items = new ArrayList<HomeListViewItem>();
		String id;
		String name;
		String imageUrl;
		double latitude;
		double longitude;
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			Log.e("parseJsonWithJsonObject", String.valueOf(jsonArray.length()));
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

	private void sendRequestWithHttpURLConnection() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					
					//http://172.22.10.71:3000/Android/Dianping?lat=39.98072&lon=116.36224&radius=5,10
					///URL url = new URL("http://172.22.10.71:3000/Android/Dianping?start=1&size=6&filter=title,image_url,s_image_url,businesses"); 
					URL url = new URL("http://172.22.10.71:3000/Android/Business?lon=114.0578&lat=22.54346&start=1&size=5");
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					 
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in)); 
					StringBuilder sb = new StringBuilder();
					 
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
					msg.what = SHOW_TEXT;
					msg.obj = rspString;
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
			return ;
			
			if (sb.length() > 0)
				sb.delete(0, sb.length() - 1);
			
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			sb.append("\ncity :");
			sb.append(location.getCity());
			}
			
			//Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show(); 
			
			String city = location.getCity();
			if (city == null)
				city = "北京市";
			String city1 = city.substring(0,2);
			
			locationButton.setText(city1);
			if (location.getLatitude() > 1)
				adapter.setLocation(location.getLatitude(), location.getLongitude());
			//sendRequestWithHttpURLConnection();
			adapter.setViewType(0);
		}
		
	}
	
	/**
     * 设置相关参数
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
   
        option.setOpenGps(true);
        option.setCoorType("gcj02");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(500000);//设置发起定位请求的间隔时间为5000ms

        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }
	

}
