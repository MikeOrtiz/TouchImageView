package com.example.touch;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class TouchImageViewActivity extends Activity {
    /** Called when the activity is first created. */
    
	private TouchImageView img;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        img = (TouchImageView) findViewById(R.id.snoop);
        img.setImageResource(R.drawable.snoopy);
        img.setMaxZoom(4f);
    }
    
    public void printPinPosition(View view) {
		ZoomablePinView pin = img.getPin();
		if (pin != null) {
			PointF pinPos = pin.getPositionInPixels();
			Toast.makeText(this, "pin position: " + pinPos.x + ", " + pinPos.y, Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(this, "no pin", Toast.LENGTH_SHORT).show();
		}
	}
}