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
	int index = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_switch_image_example);
		image = (TouchImageView) findViewById(R.id.img);
		//
		// Set first image
		//
		if (savedInstanceState != null) {
			index = savedInstanceState.getInt("index");
		}
		image.setImageResource(images[index]);
		//
		// Set next image with each button click
		//
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				index = (index + 1) % images.length;
				image.setImageResource(images[index]);
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("index", index);
	}
}
