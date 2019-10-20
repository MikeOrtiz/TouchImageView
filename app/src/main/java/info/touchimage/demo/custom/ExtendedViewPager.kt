package info.touchimage.demo.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.ortiz.touchview.TouchImageView


class ExtendedViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun canScroll(view: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        return if (view is TouchImageView) {
            view.canScrollHorizontally(-dx)
        } else {
            super.canScroll(view, checkV, dx, x, y)
        }
    }

}
