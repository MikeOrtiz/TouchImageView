package info.touchimage.demo

import android.os.Bundle
import android.widget.ImageView.ScaleType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_switch_scaletype_example.*

class SwitchScaleTypeExampleActivity : AppCompatActivity() {

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_scaletype_example)

        // Set next scaleType with each button click
        imageScale.setOnClickListener {
            index = ++index % scaleTypes.size
            val currScaleType = scaleTypes[index]
            imageScale.scaleType = currScaleType
            Toast.makeText(this, "ScaleType: $currScaleType", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val scaleTypes = arrayOf(ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE, ScaleType.FIT_XY, ScaleType.FIT_CENTER)
    }

}
