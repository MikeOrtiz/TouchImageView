package info.touchimage.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_switch_image_example.*

class SwitchImageExampleActivity : AppCompatActivity() {

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_switch_image_example)

        // Set first image
        savedInstanceState?.getInt("index")?.let(this::index::set)
        imageSwitch.setImageResource(images[index])

        // Set next image with each button click
        imageSwitch.setOnClickListener {
            index = (index + 1) % images.size
            imageSwitch!!.setImageResource(images[index])
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
