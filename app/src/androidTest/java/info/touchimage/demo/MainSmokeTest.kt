package info.touchimage.demo

import android.Manifest
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.moka.utils.Screenshot
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainSmokeTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun smokeTestSimplyStart() {
        Screenshot.takeScreenshot("Start")
    }

    @Test
    fun testSingleTouch() {
        Espresso.onView(withId(R.id.single_touchimageview_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(SingleTouchImageViewActivity::class.java.name))
        Screenshot.takeScreenshot("testSingleTouch")
    }

    @Test
    fun testViewPager() {
        Espresso.onView(withId(R.id.viewpager_example_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(ViewPagerExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testViewPager")
    }

    @Test
    fun testView2Pager() {
        Espresso.onView(withId(R.id.viewpager2_example_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(ViewPager2ExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testView2Pager")
    }

    @Test
    fun testMirroring() {
        Espresso.onView(withId(R.id.mirror_touchimageview_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(MirroringExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testMirroring")
    }

    @Test
    fun testSwitchImage() {
        Espresso.onView(withId(R.id.switch_image_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(SwitchImageExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testSwitchImage")
    }

    @Test
    fun testSwitchScale() {
        Espresso.onView(withId(R.id.switch_scaletype_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(SwitchScaleTypeExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testSwitchScale")
    }

    @Test
    fun testChangeSize() {
        Espresso.onView(withId(R.id.resize_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(ChangeSizeExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testChangeSize")
    }

    @Test
    fun testRecycler() {
        Espresso.onView(withId(R.id.recycler_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(RecyclerExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testRecycler")
    }

    @Test
    fun testAnimateZoom() {
        Espresso.onView(withId(R.id.animate_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(AnimateZoomActivity::class.java.name))
        Screenshot.takeScreenshot("testAnimateZoom")
    }

    @Test
    @Ignore("Error performing 'single click' on view 'Animations or transitions are enabled on the target device.")
    fun testGlide() {
        Espresso.onView(withId(R.id.glide_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(GlideExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testGlide")
    }

    @Test
    fun makeScreenshotOfShapedImage() {
        Espresso.onView(withId(R.id.shaped_image_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(ShapedExampleActivity::class.java.name))
        Screenshot.takeScreenshot("testShapedImage")
    }
}
