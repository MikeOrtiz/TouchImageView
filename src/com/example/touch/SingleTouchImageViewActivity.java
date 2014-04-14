package com.example.touch;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class SingleTouchImageViewActivity extends Activity {
	
	private TouchImageView image;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_touchimageview);
		textView = (TextView) findViewById(R.id.textview);
		image = (TouchImageView) findViewById(R.id.img);
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setTextWithScrollPosition();
			}
		});
	}
	
	private void setTextWithScrollPosition() {
		PointF point = image.getScrollPosition();
		textView.setText("x: " + point.x + " y: " + point.y);
	}
}
