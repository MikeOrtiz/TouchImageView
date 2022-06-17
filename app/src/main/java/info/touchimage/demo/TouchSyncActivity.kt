package info.touchimage.demo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ortiz.touchview.OnTouchImageViewListener
import info.touchimage.demo.databinding.ActivityTouchSyncBinding

class TouchSyncActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTouchSyncBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTouchSyncBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.touchImageOverlay.setOnTouchImageViewListener(object : OnTouchImageViewListener {
            override fun onMove() {
                binding.touchImage1.setZoom(binding.touchImageOverlay)
                binding.touchImage2.setZoom(binding.touchImageOverlay)
            }
        })

        // Works
        //  val img = getBitmap(this, R.drawable.ic_baseline_aspect_ratio_24)

        /** HERE IS THE PROBLEM **/
        // Does not work (after zooming in, dinosaur head is unreachable)
        val img = getBitmap(this, R.drawable.corgosaurus)
        binding.touchImage2.setImageBitmap(img)

        // Also does not work (after zooming in, dinosaur head is unreachable)
//        binding.touchImage2.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.corgosaurus))
    }

    private fun getBitmap(context: Context, drawableRes: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableRes) ?: return null
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }
}
