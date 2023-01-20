package info.touchimage.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touchview.OnZoomFinishedListener
import info.touchimage.demo.databinding.ActivitySingleTouchimageviewBinding


class AnimateZoomActivity : AppCompatActivity(), OnZoomFinishedListener {

    private lateinit var binding: ActivitySingleTouchimageviewBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivitySingleTouchimageviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imageSingle.setImageResource(R.drawable.numbers)

        binding.currentZoom.setOnClickListener {
            when {
                binding.imageSingle.isZoomed -> binding.imageSingle.resetZoomAnimated()
                binding.imageSingle.isZoomed.not() -> binding.imageSingle.setZoomAnimated(3f, 0.75f, 0.75f, this)
            }
        }

        // It's not needed, but help to see if it proper centers on bigger screens (or smaller images)
        binding.imageSingle.setZoom(1.1f, 0f, 0f)
    }

    @SuppressLint("SetTextI18n")
    override fun onZoomFinished() {
        binding.scrollPosition.text = "Zoom done"
    }

}
