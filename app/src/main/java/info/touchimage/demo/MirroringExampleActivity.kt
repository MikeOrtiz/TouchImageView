package info.touchimage.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ortiz.touchview.OnTouchImageViewListener
import info.touchimage.demo.databinding.ActivityMirroringExampleBinding

class MirroringExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMirroringExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityMirroringExampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Each image has an OnTouchImageViewListener which uses its own TouchImageView
        // to set the other TIV with the same zoom variables.

        binding.topImage.setOnTouchImageViewListener(object : OnTouchImageViewListener {
            override fun onMove() {
                binding.bottomImage.setZoom(binding.topImage)
            }
        })
        binding.bottomImage.setOnTouchImageViewListener(object : OnTouchImageViewListener {
            override fun onMove() {
                binding.topImage.setZoom(binding.bottomImage)
            }
        })
    }
}
