package info.touchimage.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import info.touchimage.demo.custom.AdapterImages
import kotlinx.android.synthetic.main.activity_recyclerview.*


class RecyclerExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        with(recycler) {
            adapter = AdapterImages(images)
            PagerSnapHelper().attachToRecyclerView(this)
        }
    }

    companion object {

        private val images = intArrayOf(R.drawable.nature_1, R.drawable.nature_2, R.drawable.nature_3, R.drawable.nature_4, R.drawable.nature_5, R.drawable.nature_6, R.drawable.nature_7, R.drawable.nature_8)
    }

}
