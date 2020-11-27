package info.touchimage.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import info.touchimage.demo.custom.AdapterImages
import info.touchimage.demo.databinding.ActivityRecyclerviewBinding


class RecyclerExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityRecyclerviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        with(binding.recycler) {
            adapter = AdapterImages(images)
            PagerSnapHelper().attachToRecyclerView(this)
        }
    }

    companion object {

        private val images = intArrayOf(R.drawable.nature_1, R.drawable.nature_2, R.drawable.nature_3, R.drawable.nature_4, R.drawable.nature_5, R.drawable.nature_6, R.drawable.nature_7, R.drawable.nature_8)
    }

}
