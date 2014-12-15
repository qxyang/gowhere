package com.example.gowhere;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LocationActivity extends Activity{
	TextView locationView;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_layout);
		Intent intent = getIntent();
		String locationInfo = intent.getStringExtra("location");

		locationView = (TextView) findViewById(R.id.location_info);
		locationView.setText(locationInfo);
		Button backButton = (Button) findViewById(R.id.geo_return_button);
		
		backButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view){
				finish();
				
			}
		});		
	}

}
