package com.ortiz.touchdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.touch.R;
import com.ortiz.touchview.TouchImageView;

public class SwitchImageExampleActivity extends Activity {
	
	private TouchImageView image;
	private static int[] images = { R.drawable.nature_1, R.drawable.nature_4, R.drawable.nature_6, R.drawable.nature_7, R.drawable.nature_8 };
	int index;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch_image_example);
		image = (TouchImageView) findViewById(R.id.img);
		index = 0;
		//
		// Set first image
		//
		setCurrentImage();
		//
		// Set next image with each button click
		//
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setCurrentImage();
			}
		});
	}
	
	private void setCurrentImage() {
		image.setImageResource(images[index]);
		index = (++index % images.length);
	}

}
