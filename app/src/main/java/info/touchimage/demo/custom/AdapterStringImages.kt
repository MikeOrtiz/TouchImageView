package info.touchimage.demo.custom

import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ortiz.touchview.TouchImageView

class AdapterStringImages(private val photoList: List<String>) : RecyclerView.Adapter<AdapterStringImages.ViewHolder>() {

    override fun getItemCount(): Int {
        return photoList.size
    }

    class ViewHolder(view: TouchImageView) : RecyclerView.ViewHolder(view) {
        val imagePlace = view
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TouchImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            setOnTouchListener { view, event ->
                var result = true
                //can scroll horizontally checks if there's still a part of the image
                //that can be scrolled until you reach the edge
                if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && canScrollHorizontally(-1)) {
                    //multi-touch event
                    result = when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            // Disallow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(true)
                            // Disable touch on view
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            // Allow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(false)
                            true
                        }
                        else -> true
                    }
                }
                result
            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.imagePlace.context)
            .load(photoList[position])
            .into(holder.imagePlace)
    }

    override fun getItemViewType(i: Int): Int {
        return 0
    }

}
