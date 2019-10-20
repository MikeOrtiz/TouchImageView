package info.touchimage.demo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.activity_change_size.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * An example Activity for how to handle a TouchImageView that might be resized.
 *
 * If you want your image to look like it's being cropped or sliding when you resize it, instead of
 * changing its zoom level, you probably want ScaleType.CENTER. Here's an example of how to use it:
 *
 * imageChangeSize.setScaleType(CENTER);
 * imageChangeSize.setMinZoom(TouchImageView.AUTOMATIC_MIN_ZOOM);
 * imageChangeSize.setMaxZoomRatio(3.0f);
 * float widthRatio = (float) imageChangeSize.getMeasuredWidth() / imageChangeSize.getDrawable().getIntrinsicWidth();
 * float heightRatio = (float) imageChangeSize.getMeasuredHeight() / imageChangeSize.getDrawable().getIntrinsicHeight();
 * imageChangeSize.setZoom(Math.max(widthRatio, heightRatio));  // For an initial view that looks like CENTER_CROP
 * imageChangeSize.setZoom(Math.min(widthRatio, heightRatio));  // For an initial view that looks like FIT_CENTER
 *
 * That code is run when the button displays "CENTER (with X zoom)".
 *
 * You can use other ScaleTypes, but for all of them, the size of the image depends somehow on the
 * size of the TouchImageView, just like it does in ImageView. You can thus expect your image to
 * change magnification as its View changes sizes.
 */
class ChangeSizeExampleActivity : AppCompatActivity() {

    private var xSizeAnimator = ValueAnimator()
    private var ySizeAnimator = ValueAnimator()
    private var xSizeAdjustment = 0
    private var ySizeAdjustment = 0
    private var scaleTypeIndex = 0
    private var imageIndex = 0

    private lateinit var resizeAdjuster: SizeBehaviorAdjuster
    private lateinit var rotateAdjuster: SizeBehaviorAdjuster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_size)

        imageChangeSize.setBackgroundColor(Color.LTGRAY)
        imageChangeSize.minZoom = TouchImageView.AUTOMATIC_MIN_ZOOM
        imageChangeSize.setMaxZoomRatio(6.0f)

        left.setOnClickListener(SizeAdjuster(-1, 0))
        right.setOnClickListener(SizeAdjuster(1, 0))
        up.setOnClickListener(SizeAdjuster(0, -1))
        down.setOnClickListener(SizeAdjuster(0, 1))

        resizeAdjuster = SizeBehaviorAdjuster(false, "resize: ")
        rotateAdjuster = SizeBehaviorAdjuster(true, "rotate: ")
        resize.setOnClickListener(resizeAdjuster)
        rotate.setOnClickListener(rotateAdjuster)

        switch_scaletype_button.setOnClickListener {
            scaleTypeIndex = (scaleTypeIndex + 1) % scaleTypes.size
            processScaleType(scaleTypes[scaleTypeIndex], true)
        }

        findViewById<View>(R.id.switch_image_button).setOnClickListener {
            imageIndex = (imageIndex + 1) % images.size
            imageChangeSize.setImageResource(images[imageIndex])
        }

        if (savedInstanceState != null) {
            scaleTypeIndex = savedInstanceState.getInt("scaleTypeIndex")
            resizeAdjuster.setIndex(findViewById<View>(R.id.resize) as Button, savedInstanceState.getInt("resizeAdjusterIndex"))
            rotateAdjuster.setIndex(findViewById<View>(R.id.rotate) as Button, savedInstanceState.getInt("rotateAdjusterIndex"))
            imageIndex = savedInstanceState.getInt("imageIndex")
            imageChangeSize.setImageResource(images[imageIndex])
        }

        imageChangeSize.post { processScaleType(scaleTypes[scaleTypeIndex], false) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("scaleTypeIndex", scaleTypeIndex)
        outState.putInt("resizeAdjusterIndex", resizeAdjuster.index)
        outState.putInt("rotateAdjusterIndex", rotateAdjuster.index)
        outState.putInt("imageIndex", imageIndex)
    }

    @SuppressLint("SetTextI18n")
    private fun processScaleType(scaleType: ImageView.ScaleType, resetZoom: Boolean) {
        if (scaleType == ImageView.ScaleType.FIT_END) {
            switch_scaletype_button.text = ImageView.ScaleType.CENTER.name + " (with " + ImageView.ScaleType.CENTER_CROP.name + " zoom)"
            imageChangeSize.scaleType = ImageView.ScaleType.CENTER
            val widthRatio = imageChangeSize.measuredWidth.toFloat() / imageChangeSize.drawable.intrinsicWidth
            val heightRatio = imageChangeSize.measuredHeight.toFloat() / imageChangeSize.drawable.intrinsicHeight
            if (resetZoom) {
                imageChangeSize.setZoom(max(widthRatio, heightRatio))
            }
        } else if (scaleType == ImageView.ScaleType.FIT_START) {
            switch_scaletype_button.text = ImageView.ScaleType.CENTER.name + " (with " + ImageView.ScaleType.FIT_CENTER.name + " zoom)"
            imageChangeSize.scaleType = ImageView.ScaleType.CENTER
            val widthRatio = imageChangeSize.measuredWidth.toFloat() / imageChangeSize.drawable.intrinsicWidth
            val heightRatio = imageChangeSize.measuredHeight.toFloat() / imageChangeSize.drawable.intrinsicHeight
            if (resetZoom) {
                imageChangeSize.setZoom(min(widthRatio, heightRatio))
            }
        } else {
            switch_scaletype_button.text = scaleType.name
            imageChangeSize.scaleType = scaleType
            if (resetZoom) {
                imageChangeSize.resetZoom()
            }
        }
    }

    private fun adjustImageSize() {
        val width = image_container.measuredWidth * 1.1.pow(xSizeAdjustment.toDouble())
        val height = image_container.measuredHeight * 1.1.pow(ySizeAdjustment.toDouble())
        xSizeAnimator.cancel()
        ySizeAnimator.cancel()
        xSizeAnimator = ValueAnimator.ofInt(imageChangeSize.width, width.toInt())
        ySizeAnimator = ValueAnimator.ofInt(imageChangeSize.height, height.toInt())
        xSizeAnimator.addUpdateListener { animation ->
            val layoutParams = imageChangeSize.layoutParams
            layoutParams.width = animation.animatedValue as Int
            imageChangeSize.layoutParams = layoutParams
        }
        ySizeAnimator.addUpdateListener { animation ->
            val layoutParams = imageChangeSize.layoutParams
            layoutParams.height = animation.animatedValue as Int
            imageChangeSize.layoutParams = layoutParams
        }
        xSizeAnimator.duration = 200
        ySizeAnimator.duration = 200
        xSizeAnimator.start()
        ySizeAnimator.start()
    }

    private inner class SizeAdjuster internal constructor(internal var dx: Int, internal var dy: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            val newXScale = min(0, xSizeAdjustment + dx)
            val newYScale = min(0, ySizeAdjustment + dy)
            if (newXScale == xSizeAdjustment && newYScale == ySizeAdjustment) {
                return
            }
            xSizeAdjustment = newXScale
            ySizeAdjustment = newYScale
            adjustImageSize()
        }
    }

    private inner class SizeBehaviorAdjuster internal constructor(private val forOrientationChanges: Boolean, private val buttonPrefix: String) : View.OnClickListener {
        private val values = TouchImageView.FixedPixel.values()
        var index = 0
            private set

        override fun onClick(v: View) {
            setIndex(v as Button, (index + 1) % values.size)
        }

        @SuppressLint("SetTextI18n")
        internal fun setIndex(b: Button, index: Int) {
            this.index = index
            if (forOrientationChanges) {
                imageChangeSize.orientationChangeFixedPixel = values[index]
            } else {
                imageChangeSize.viewSizeChangeFixedPixel = values[index]
            }
            b.text = buttonPrefix + values[index].name
        }
    }

    companion object {

        //
        // Two of the ScaleTypes are stand-ins for CENTER with different initial zoom levels. This is
        // special-cased in processScaleType.
        //
        private val scaleTypes = arrayOf(ImageView.ScaleType.CENTER, ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.FIT_START, // stand-in for CENTER with initial zoom that looks like FIT_CENTER
                ImageView.ScaleType.FIT_END, // stand-in for CENTER with initial zoom that looks like CENTER_CROP
                ImageView.ScaleType.CENTER_INSIDE, ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_CENTER)

        private val images = intArrayOf(R.drawable.nature_1, R.drawable.nature_2, R.drawable.nature_6, R.drawable.nature_7, R.drawable.nature_8)
    }
}
