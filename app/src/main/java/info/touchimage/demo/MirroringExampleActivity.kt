package info.touchimage.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_mirroring_example.*

class MirroringExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mirroring_example)
        //
        // Each image has an OnTouchImageViewListener which uses its own TouchImageView
        // to set the other TIV with the same zoom variables.
        //
        topImage.setOnTouchImageViewListener { bottomImage.setZoom(topImage) }

        bottomImage.setOnTouchImageViewListener { topImage.setZoom(bottomImage) }
    }
}
