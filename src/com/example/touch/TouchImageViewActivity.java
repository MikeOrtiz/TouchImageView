package com.example.touch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class TouchImageViewActivity extends Activity {
	
	TouchImageView img;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        img = (TouchImageView) findViewById(R.id.img);
        img.setMaxZoom(4);
        img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				img.setImageResource(R.drawable.icon);
			}
		});
    }
}