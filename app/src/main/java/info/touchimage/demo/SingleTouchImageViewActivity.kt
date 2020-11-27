package info.touchimage.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touchview.TouchImageView
import info.touchimage.demo.databinding.ActivitySingleTouchimageviewBinding
import java.text.DecimalFormat


class SingleTouchImageViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingleTouchimageviewBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivitySingleTouchimageviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // DecimalFormat rounds to 2 decimal places.
        val df = DecimalFormat("#.##")

        // Set the OnTouchImageViewListener which updates edit texts with zoom and scroll diagnostics.
        binding.imageSingle.setOnTouchImageViewListener(object : TouchImageView.OnTouchImageViewListener {
            override fun onMove() {
                val point = binding.imageSingle.scrollPosition
                val rect = binding.imageSingle.zoomedRect
                val currentZoom = binding.imageSingle.currentZoom
                val isZoomed = binding.imageSingle.isZoomed
                binding.scrollPosition.text = "x: " + df.format(point.x.toDouble()) + " y: " + df.format(point.y.toDouble())
                binding.zoomedRect.text = ("left: " + df.format(rect.left.toDouble()) + " top: " + df.format(rect.top.toDouble())
                        + "\nright: " + df.format(rect.right.toDouble()) + " bottom: " + df.format(rect.bottom.toDouble()))
                binding.currentZoom.text = "getCurrentZoom(): $currentZoom isZoomed(): $isZoomed"
            }
        }
        )
    }
}
