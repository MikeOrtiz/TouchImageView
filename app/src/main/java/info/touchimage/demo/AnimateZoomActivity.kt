package info.touchimage.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.activity_single_touchimageview.*


class AnimateZoomActivity : AppCompatActivity(), TouchImageView.OnZoomFinishedListener {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_touchimageview)

        current_zoom.setOnClickListener {
            when {
                imageSingle.isZoomed -> imageSingle.resetZoomAnimated()
                imageSingle.isZoomed.not() -> imageSingle.setZoomAnimated(0.9f, 0.5f, 0f, this)
            }
        }
    }

    override fun onZoomFinished() {
        scroll_position.text = "Zoom done"
    }

}
