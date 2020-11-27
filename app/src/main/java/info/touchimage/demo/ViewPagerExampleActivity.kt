package info.touchimage.demo

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.ortiz.touchview.TouchImageView
import info.touchimage.demo.databinding.ActivityViewpagerExampleBinding


class ViewPagerExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewpagerExampleBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityViewpagerExampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.viewPager.adapter = TouchImageAdapter()
    }

    private class TouchImageAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return images.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            return TouchImageView(container.context).apply {
                setImageResource(images[position])
                container.addView(this, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        companion object {

            private val images = intArrayOf(R.drawable.nature_1, R.drawable.nature_2, R.drawable.nature_3, R.drawable.nature_4, R.drawable.nature_5)
        }

    }
}
