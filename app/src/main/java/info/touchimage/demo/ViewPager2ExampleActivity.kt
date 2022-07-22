package info.touchimage.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import info.touchimage.demo.custom.AdapterImages
import info.touchimage.demo.custom.AdapterStringImages
import info.touchimage.demo.databinding.ActivityViewpager2ExampleBinding


class ViewPager2ExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewpager2ExampleBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityViewpager2ExampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.viewPager2.adapter = AdapterStringImages(images)
    }

    companion object {
        private val images = arrayListOf(
            GlideExampleActivity.GLIDE_IMAGE_URL,
            GlideExampleActivity.GLIDE_IMAGE_URL,
            GlideExampleActivity.GLIDE_IMAGE_URL,
            "https://test.error.png"
        )
    }
}
