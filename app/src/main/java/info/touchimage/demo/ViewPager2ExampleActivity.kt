package info.touchimage.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import info.touchimage.demo.custom.AdapterImages
import info.touchimage.demo.databinding.ActivityViewpager2ExampleBinding


class ViewPager2ExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewpager2ExampleBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityViewpager2ExampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.viewPager2.adapter = AdapterImages(images)
    }

    companion object {

        private val images = intArrayOf(R.drawable.nature_1, R.drawable.nature_2, R.drawable.nature_3, R.drawable.nature_4, R.drawable.nature_5, R.drawable.nature_6, R.drawable.nature_7, R.drawable.nature_8)
    }
}
