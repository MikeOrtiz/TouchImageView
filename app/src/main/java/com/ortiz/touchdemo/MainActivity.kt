package com.ortiz.touchdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.touch.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        single_touchimageview_button.setOnClickListener { startActivity(Intent(this@MainActivity, SingleTouchImageViewActivity::class.java)) }
        viewpager_example_button.setOnClickListener { startActivity(Intent(this@MainActivity, ViewPagerExampleActivity::class.java)) }
        viewpager2_example_button.setOnClickListener { startActivity(Intent(this@MainActivity, ViewPager2ExampleActivity::class.java)) }
        mirror_touchimageview_button.setOnClickListener { startActivity(Intent(this@MainActivity, MirroringExampleActivity::class.java)) }
        switch_image_button.setOnClickListener { startActivity(Intent(this@MainActivity, SwitchImageExampleActivity::class.java)) }
        switch_scaletype_button.setOnClickListener { startActivity(Intent(this@MainActivity, SwitchScaleTypeExampleActivity::class.java)) }
        resize_button.setOnClickListener { startActivity(Intent(this@MainActivity, ChangeSizeExampleActivity::class.java)) }
        recycler_button.setOnClickListener { startActivity(Intent(this@MainActivity, RecyclerExampleActivity::class.java)) }
        animate_button.setOnClickListener { startActivity(Intent(this@MainActivity, AnimateZoomActivity::class.java)) }
    }
}