package com.ortiz.touchdemo

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.RecyclerView
import com.ortiz.touchview.TouchImageView

class AdapterImages(private val photoList: IntArray) : RecyclerView.Adapter<AdapterImages.ViewHolder>() {

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
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imagePlace.setImageResource(photoList[position])
    }

    override fun getItemViewType(i: Int): Int {
        return 0
    }

}
