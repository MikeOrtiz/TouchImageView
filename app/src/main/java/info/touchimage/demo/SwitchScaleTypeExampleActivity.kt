package info.touchimage.demo

import android.os.Bundle
import android.widget.ImageView.ScaleType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import info.touchimage.demo.databinding.ActivitySwitchScaletypeExampleBinding

class SwitchScaleTypeExampleActivity : AppCompatActivity() {

    private var index = 0
    private lateinit var binding: ActivitySwitchScaletypeExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivitySwitchScaletypeExampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        // Set next scaleType with each button click
        binding.imageScale.setOnClickListener {
            index = ++index % scaleTypes.size
            val currScaleType = scaleTypes[index]
            binding.imageScale.scaleType = currScaleType
            Toast.makeText(this, "ScaleType: $currScaleType", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val scaleTypes = arrayOf(ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE, ScaleType.FIT_XY, ScaleType.FIT_CENTER)
    }

}
