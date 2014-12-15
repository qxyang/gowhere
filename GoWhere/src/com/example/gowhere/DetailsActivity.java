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
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailsActivity extends Activity {
	ImageView detailImage;
	TextView telephone;
	TextView address;
	TextView information;
	TextView price;
	
	TextView similar1;
	TextView similar2;
	TextView similar3;
	TextView similar4;
	public static String path = Environment.getExternalStorageDirectory().toString() + File.separator + "testdata" + File.separator;
	HomeListViewItem viewItem = null;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.details_layout);
		Intent intent = getIntent();
		String title = intent.getStringExtra("name");
		String id = intent.getStringExtra("id");
		String imageUrl = intent.getStringExtra("imageUrl");
		viewItem = new HomeListViewItem(title, imageUrl, id);
		TextView textView = (TextView) findViewById(R.id.details_name);
		Button backButton = (Button) findViewById(R.id.return_button);
		textView.setText(title);
		backButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				finish();
				
			}
		});		
		detailImage = (ImageView) findViewById(R.id.details_picture);
		telephone = (TextView) findViewById(R.id.telephone_content);
		address = (TextView) findViewById(R.id.address_content);
		information = (TextView) findViewById(R.id.info_content);
		price = (TextView) findViewById(R.id.price_content);
		
		similar1 = (TextView) findViewById(R.id.similar_match_item1);
		similar2 = (TextView) findViewById(R.id.similar_match_item2);
		similar3 = (TextView) findViewById(R.id.similar_match_item3);
		similar4 = (TextView) findViewById(R.id.similar_match_item4);
		sendRequestWithHttpURLConnection(id);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:
				/*
				 * String rspString = (String)msg.obj; List<HomeListViewItem>
				 * items = parseJsonWithJsonObject(rspString);
				 * adapter.putListItems(items);
				 */
				String rspString = (String)msg.obj;
				DetailsObject detailsObject = parseJsonWithJsonObject(rspString);
				
				
				File file = new File(path + viewItem.getId() + "_big.jpg");
				if (file.exists()){
					//detailImage.setImageURI(Uri.fromFile(new File(path + viewItem.getId() + "_big.jpg")));
					Bitmap bmp = BitmapFactory.decodeFile(path + viewItem.getId() + "_big.jpg");  
					detailImage.setImageBitmap(bmp);
				}else
					getImageWithHttpURLConnection(detailsObject.getImageUrl(), viewItem.getId());
				telephone.setText(detailsObject.getTelephone());
				address.setText(detailsObject.getAddress());
				information.setText(detailsObject.getDetails());
				price.setText("单价：￥" + detailsObject.getPrice());
				similar1.setText("故宫");
				similar2.setText("长城");
				similar3.setText("颐和园");
				similar4.setText("圆明园");
				
				//Toast.makeText(DetailsActivity.this, rspString, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				File file2 = new File(path + viewItem.getId() + "_big.jpg");
				if (file2.exists())
					detailImage.setImageURI(Uri.fromFile(new File(path + viewItem.getId() + "_big.jpg")));
				break;
			}
		}
	};
	
	private void sendRequestWithHttpURLConnection(final String id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					
					URL url = new URL("http://mytestproj.duapp.com/Android/Business/" + id); 
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					 
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder sb = new StringBuilder();
					 
					String line = null; 
					try {
						while ((line =reader.readLine()) != null) { 
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
					msg.what = 2;
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
    				
    				OutputStream output = new FileOutputStream(path + idString + "_big.jpg");
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
    		         
    				bitmap.compress(CompressFormat.JPEG, 100, new FileOutputStream(path + idString + "_big.jpg"));  
    				
    				Message msg = new Message();
    				msg.what = 3;
    				msg.obj = path + idString + "_big.jpg";
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
    
	private DetailsObject parseJsonWithJsonObject(String jsonData) {
		
		DetailsObject detailsObject = new DetailsObject();
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			detailsObject.setDetails(jsonObject.getString("name"));
			detailsObject.setPrice(jsonObject.getString("avg_price"));
			detailsObject.setImageUrl(jsonObject.getString("photo_url"));
			
			
		    
			String address = jsonObject.getString("city") + jsonObject.getString("address");
			detailsObject.setAddress(address);
			detailsObject.setTelephone(jsonObject.getString("telephone"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return detailsObject;
	}
	
}


