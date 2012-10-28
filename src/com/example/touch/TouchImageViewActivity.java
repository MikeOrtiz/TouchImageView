package com.example.touch;

import android.app.Activity;
import android.os.Bundle;


public class TouchImageViewActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TouchImageView img = (TouchImageView) findViewById(R.id.snoop);
        img.setImageResource(R.drawable.snoopy);
        img.setMaxZoom(4f);
    }
}