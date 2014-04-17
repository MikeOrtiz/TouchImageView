package com.ortiz.touch;

import android.app.Activity;
import android.os.Bundle;

import com.example.touch.R;
import com.ortiz.touch.TouchImageView.OnTouchImageViewListener;

public class MirroringExampleActivity extends Activity {
	
	TouchImageView topImage;
	TouchImageView bottomImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mirroring_example);
		topImage = (TouchImageView) findViewById(R.id.topImage);
		bottomImage = (TouchImageView) findViewById(R.id.bottomImage);
		topImage.setOnTouchImageViewListener(new OnTouchImageViewListener() {
			
			@Override
			public void onMove() {
				bottomImage.setZoom(topImage);
			}
		});
		
		bottomImage.setOnTouchImageViewListener(new OnTouchImageViewListener() {
			
			@Override
			public void onMove() {
				topImage.setZoom(bottomImage);
			}
		});
	}
}
