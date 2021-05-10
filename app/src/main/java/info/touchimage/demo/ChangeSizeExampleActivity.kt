package info.touchimage.demo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.ortiz.touchview.FixedPixel
import com.ortiz.touchview.TouchImageView
import info.touchimage.demo.databinding.ActivityChangeSizeBinding
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

    private lateinit var binding: ActivityChangeSizeBinding

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

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityChangeSizeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.imageChangeSize.setBackgroundColor(Color.LTGRAY)
        binding.imageChangeSize.minZoom = TouchImageView.AUTOMATIC_MIN_ZOOM
        binding.imageChangeSize.setMaxZoomRatio(6.0f)

        binding.left.setOnClickListener(SizeAdjuster(-1, 0))
        binding.right.setOnClickListener(SizeAdjuster(1, 0))
        binding.up.setOnClickListener(SizeAdjuster(0, -1))
        binding.down.setOnClickListener(SizeAdjuster(0, 1))

        resizeAdjuster = SizeBehaviorAdjuster(false, "resize: ")
        rotateAdjuster = SizeBehaviorAdjuster(true, "rotate: ")
        binding.resize.setOnClickListener(resizeAdjuster)
        binding.rotate.setOnClickListener(rotateAdjuster)

        binding.switchScaletypeButton.setOnClickListener {
            scaleTypeIndex = (scaleTypeIndex + 1) % scaleTypes.size
            processScaleType(scaleTypes[scaleTypeIndex], true)
        }

        findViewById<View>(R.id.switch_image_button).setOnClickListener {
            imageIndex = (imageIndex + 1) % images.size
            binding.imageChangeSize.setImageResource(images[imageIndex])
        }

        savedInstanceState?.let { savedState ->
            scaleTypeIndex = savedState.getInt("scaleTypeIndex")
            resizeAdjuster.setIndex(findViewById<View>(R.id.resize) as Button, savedState.getInt("resizeAdjusterIndex"))
            rotateAdjuster.setIndex(findViewById<View>(R.id.rotate) as Button, savedState.getInt("rotateAdjusterIndex"))
            imageIndex = savedState.getInt("imageIndex")
            binding.imageChangeSize.setImageResource(images[imageIndex])
        }

        binding.imageChangeSize.post { processScaleType(scaleTypes[scaleTypeIndex], false) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            putInt("scaleTypeIndex", scaleTypeIndex)
            putInt("resizeAdjusterIndex", resizeAdjuster.index)
            putInt("rotateAdjusterIndex", rotateAdjuster.index)
            putInt("imageIndex", imageIndex)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun processScaleType(scaleType: ImageView.ScaleType, resetZoom: Boolean) {
        when (scaleType) {
            ImageView.ScaleType.FIT_END -> {
                binding.switchScaletypeButton.text = ImageView.ScaleType.CENTER.name + " (with " + ImageView.ScaleType.CENTER_CROP.name + " zoom)"
                binding.imageChangeSize.scaleType = ImageView.ScaleType.CENTER
                val widthRatio = binding.imageChangeSize.measuredWidth.toFloat() / binding.imageChangeSize.drawable.intrinsicWidth
                val heightRatio = binding.imageChangeSize.measuredHeight.toFloat() / binding.imageChangeSize.drawable.intrinsicHeight
                if (resetZoom) {
                    binding.imageChangeSize.setZoom(max(widthRatio, heightRatio))
                }
            }
            ImageView.ScaleType.FIT_START -> {
                binding.switchScaletypeButton.text = ImageView.ScaleType.CENTER.name + " (with " + ImageView.ScaleType.FIT_CENTER.name + " zoom)"
                binding.imageChangeSize.scaleType = ImageView.ScaleType.CENTER
                val widthRatio = binding.imageChangeSize.measuredWidth.toFloat() / binding.imageChangeSize.drawable.intrinsicWidth
                val heightRatio = binding.imageChangeSize.measuredHeight.toFloat() / binding.imageChangeSize.drawable.intrinsicHeight
                if (resetZoom) {
                    binding.imageChangeSize.setZoom(min(widthRatio, heightRatio))
                }
            }
            else -> {
                binding.switchScaletypeButton.text = scaleType.name
                binding.imageChangeSize.scaleType = scaleType
                if (resetZoom) {
                    binding.imageChangeSize.resetZoom()
                }
            }
        }
    }

    private fun adjustImageSize() {
        val width = binding.imageContainer.measuredWidth * 1.1.pow(xSizeAdjustment.toDouble())
        val height = binding.imageContainer.measuredHeight * 1.1.pow(ySizeAdjustment.toDouble())
        xSizeAnimator.cancel()
        ySizeAnimator.cancel()
        xSizeAnimator = ValueAnimator.ofInt(binding.imageChangeSize.width, width.toInt())
        ySizeAnimator = ValueAnimator.ofInt(binding.imageChangeSize.height, height.toInt())
        xSizeAnimator.addUpdateListener { animation ->
            binding.imageChangeSize.updateLayoutParams {
                this.width = animation.animatedValue as Int
            }
        }
        ySizeAnimator.addUpdateListener { animation ->
            binding.imageChangeSize.updateLayoutParams {
                this.height = animation.animatedValue as Int
            }
        }
        xSizeAnimator.duration = 200
        ySizeAnimator.duration = 200
        xSizeAnimator.start()
        ySizeAnimator.start()
    }

    private inner class SizeAdjuster constructor(var dx: Int, var dy: Int) : View.OnClickListener {

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

    private inner class SizeBehaviorAdjuster(private val forOrientationChanges: Boolean, private val buttonPrefix: String) : View.OnClickListener {
        private val values = FixedPixel.values()
        var index = 0
            private set

        override fun onClick(v: View) {
            setIndex(v as Button, (index + 1) % values.size)
        }

        @SuppressLint("SetTextI18n")
        fun setIndex(b: Button, index: Int) {
            this.index = index
            if (forOrientationChanges) {
                binding.imageChangeSize.orientationChangeFixedPixel = values[index]
            } else {
                binding.imageChangeSize.viewSizeChangeFixedPixel = values[index]
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
