package com.ortiz.touchdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.ortiz.touchdemo.custom.AdapterImages
import kotlinx.android.synthetic.main.activity_recyclerview.*


class RecyclerExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.touch.R.layout.activity_recyclerview)

        recycler.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        recycler.adapter = AdapterImages(images)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recycler)
    }

    companion object {

        private val images = intArrayOf(com.example.touch.R.drawable.nature_1, com.example.touch.R.drawable.nature_2, com.example.touch.R.drawable.nature_3, com.example.touch.R.drawable.nature_4, com.example.touch.R.drawable.nature_5, com.example.touch.R.drawable.nature_6, com.example.touch.R.drawable.nature_7, com.example.touch.R.drawable.nature_8)
    }

}
