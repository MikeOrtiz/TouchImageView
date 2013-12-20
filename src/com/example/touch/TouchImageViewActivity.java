package com.example.touch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView.ScaleType;


public class TouchImageViewActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TouchImageView img = (TouchImageView) findViewById(R.id.img);
        img.setScaleType(ScaleType.CENTER_CROP);
        img.setMaxZoom(4);
    }
}