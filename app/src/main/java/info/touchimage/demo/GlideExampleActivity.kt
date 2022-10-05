package info.touchimage.demo

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import info.touchimage.demo.databinding.ActivityGlideBinding


class GlideExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGlideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityGlideBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val start = System.currentTimeMillis()

        Glide.with(this)
            .load(GLIDE_IMAGE_URL)
            .into(object : CustomTarget<Drawable?>() {
                @SuppressLint("SetTextI18n")
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                    binding.imageGlide.setImageDrawable(resource)
                    binding.textLoaded.visibility = View.VISIBLE
                    binding.textLoaded.text = getString(R.string.loaded) + " within ${System.currentTimeMillis() - start} ms"
                    Log.d("GlideExampleActivity", binding.textLoaded.text as String)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit

            })
    }

    companion object {
        const val GLIDE_IMAGE_URL = "https://raw.githubusercontent.com/bumptech/glide/master/static/glide_logo.png"
    }
}
