package info.touchimage.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import info.touchimage.demo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // https://developer.android.com/topic/libraries/view-binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.singleTouchimageviewButton.setOnClickListener { startActivity(Intent(this@MainActivity, SingleTouchImageViewActivity::class.java)) }
        binding.viewpagerExampleButton.setOnClickListener { startActivity(Intent(this@MainActivity, ViewPagerExampleActivity::class.java)) }
        binding.viewpager2ExampleButton.setOnClickListener { startActivity(Intent(this@MainActivity, ViewPager2ExampleActivity::class.java)) }
        binding.mirrorTouchimageviewButton.setOnClickListener { startActivity(Intent(this@MainActivity, MirroringExampleActivity::class.java)) }
        binding.switchImageButton.setOnClickListener { startActivity(Intent(this@MainActivity, SwitchImageExampleActivity::class.java)) }
        binding.switchScaletypeButton.setOnClickListener { startActivity(Intent(this@MainActivity, SwitchScaleTypeExampleActivity::class.java)) }
        binding.resizeButton.setOnClickListener { startActivity(Intent(this@MainActivity, ChangeSizeExampleActivity::class.java)) }
        binding.recyclerButton.setOnClickListener { startActivity(Intent(this@MainActivity, RecyclerExampleActivity::class.java)) }
        binding.animateButton.setOnClickListener { startActivity(Intent(this@MainActivity, AnimateZoomActivity::class.java)) }
        binding.glideButton.setOnClickListener { startActivity(Intent(this@MainActivity, GlideExampleActivity::class.java)) }
    }
}