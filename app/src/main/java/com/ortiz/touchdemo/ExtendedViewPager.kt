package com.ortiz.touchdemo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.ortiz.touchview.TouchImageView


class ExtendedViewPager : ViewPager {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun canScroll(view: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        return if (view is TouchImageView) {
            // canScrollHorizontally is not supported for Api < 14. To get around this issue,
            // ViewPager is extended and canScrollHorizontallyFroyo, a wrapper around
            // canScrollHorizontally supporting Api >= 8, is called.
            view.canScrollHorizontallyFroyo(-dx)

        } else {
            super.canScroll(view, checkV, dx, x, y)
        }
    }

}
