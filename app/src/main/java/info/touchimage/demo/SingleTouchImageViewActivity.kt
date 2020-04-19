package info.touchimage.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.activity_single_touchimageview.*
import java.text.DecimalFormat


class SingleTouchImageViewActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_touchimageview)

        // DecimalFormat rounds to 2 decimal places.
        val df = DecimalFormat("#.##")

        // Set the OnTouchImageViewListener which updates edit texts with zoom and scroll diagnostics.
        imageSingle.setOnTouchImageViewListener(object : TouchImageView.OnTouchImageViewListener {
            override fun onMove() {
                val point = imageSingle.scrollPosition
                val rect = imageSingle.zoomedRect
                val currentZoom = imageSingle.currentZoom
                val isZoomed = imageSingle.isZoomed
                scroll_position.text = "x: " + df.format(point.x.toDouble()) + " y: " + df.format(point.y.toDouble())
                zoomed_rect.text = ("left: " + df.format(rect.left.toDouble()) + " top: " + df.format(rect.top.toDouble())
                        + "\nright: " + df.format(rect.right.toDouble()) + " bottom: " + df.format(rect.bottom.toDouble()))
                current_zoom.text = "getCurrentZoom(): $currentZoom isZoomed(): $isZoomed"
            }
        }
        )
    }
}
