package com.example.touch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

public class TouchImageViewActivity extends Activity implements
		OnLongClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TouchImageView img = (TouchImageView) findViewById(R.id.snoop);
		img.setImageResource(R.drawable.snoopy);
		img.setMaxZoom(4f);
		img.setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onLongClick", Toast.LENGTH_SHORT).show();
		return true;
	}
}