package info.touchimage.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.ortiz.touchview.TouchImageView;

public class MainActivity325 extends Activity {

    TouchImageView iv_target_1;
    TouchImageView iv_target_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main325);

        iv_target_1 = findViewById(R.id.iv_1);
        Bitmap mIcon_1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.lastcurrent_c2);
        iv_target_1.setImageBitmap(mIcon_1);

        iv_target_2 = findViewById(R.id.iv_2);
        Bitmap mIcon_2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.lastcurrent_c2);
        iv_target_2.setImageBitmap(mIcon_2);
    }

}
