package info.touchimage.demo

import android.graphics.Outline
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import info.touchimage.demo.databinding.ActivityShapedExampleBinding

internal class ShapedExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityShapedExampleBinding.inflate(layoutInflater).apply {
            val view = root

            val outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            view.resources.getDimension(R.dimen.grid_2)
                    )
                }
            }
            imageView.outlineProvider = outlineProvider
            imageView.clipToOutline = true

            setContentView(view)
        }
    }
}