package com.ortiz.touchdemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.touch.R
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.activity_single_touchimageview.*


class AnimateZoomActivity : AppCompatActivity(), TouchImageView.OnZoomFinishedListener {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_touchimageview)

        current_zoom.setOnClickListener {
            imageSingle.setZoomAnimated(0.9f, 0.5f, 0f, this)
        }
    }

    override fun onZoomFinished() {
        scroll_position.text = "Zoom done"
    }

}
