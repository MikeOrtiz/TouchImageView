package com.ortiz.touchdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.touch.R
import com.ortiz.touchdemo.custom.AdapterImages
import kotlinx.android.synthetic.main.activity_viewpager2_example.*

class ViewPager2ExampleActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager2_example)
        view_pager2.adapter = AdapterImages(images)
    }

    companion object {

        private val images = intArrayOf(R.drawable.nature_1, R.drawable.nature_2, R.drawable.nature_3, R.drawable.nature_4, R.drawable.nature_5, R.drawable.nature_6, R.drawable.nature_7, R.drawable.nature_8)
    }
}
