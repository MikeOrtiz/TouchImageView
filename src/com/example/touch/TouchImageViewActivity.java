package com.example.touch;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class TouchImageViewActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TouchImageView img = new TouchImageView(this);
       // ImageView img = new ImageView(this);
        Bitmap snoop = BitmapFactory.decodeResource(getResources(), R.drawable.snoopy);
        img.setImageBitmap(snoop);
        img.setMaxZoom(4f);
        setContentView(img);
    }
}