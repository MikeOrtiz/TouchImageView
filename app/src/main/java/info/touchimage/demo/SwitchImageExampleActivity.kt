package info.touchimage.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import info.touchimage.demo.databinding.ActivitySwitchImageExampleBinding

class SwitchImageExampleActivity : AppCompatActivity() {

    private var index = 0
    private lateinit var binding: ActivitySwitchImageExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivitySwitchImageExampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Set first image
        savedInstanceState?.getInt("index")?.let(this::index::set)
        binding.imageSwitch.setImageResource(images[index])

        // Set next image with each button click
        binding.imageSwitch.setOnClickListener {
            index = (index + 1) % images.size
            binding.imageSwitch.setImageResource(images[index])
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("index", index)
    }

    companion object {
        private val images = intArrayOf(R.drawable.nature_1, R.drawable.nature_4, R.drawable.nature_6, R.drawable.nature_7, R.drawable.nature_8)
    }
}
