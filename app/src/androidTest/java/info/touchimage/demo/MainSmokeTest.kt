package info.touchimage.demo

import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moka.lib.assertions.WaitingAssertion
import org.hamcrest.CoreMatchers.containsString
import org.junit.*
import org.junit.rules.TestName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainSmokeTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule
    var nameRule = TestName()

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
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testSingleTouch() {
        Espresso.onView(withId(R.id.single_touchimageview_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(SingleTouchImageViewActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testViewPager() {
        Espresso.onView(withId(R.id.viewpager_example_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(ViewPagerExampleActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testView2Pager() {
        Espresso.onView(withId(R.id.viewpager2_example_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(ViewPager2ExampleActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testMirroring() {
        Espresso.onView(withId(R.id.mirror_touchimageview_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(MirroringExampleActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testSwitchImage() {
        Espresso.onView(withId(R.id.switch_image_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(SwitchImageExampleActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testSwitchScale() {
        Espresso.onView(withId(R.id.switch_scaletype_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(SwitchScaleTypeExampleActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testChangeSize() {
        Espresso.onView(withId(R.id.resize_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(ChangeSizeExampleActivity::class.java.name))
        Thread.sleep(500)
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testRecycler() {
        Espresso.onView(withId(R.id.recycler_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(RecyclerExampleActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testAnimateZoom() {
        Espresso.onView(withId(R.id.animate_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(AnimateZoomActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun testGlide() {
        Espresso.onView(withId(R.id.glide_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(GlideExampleActivity::class.java.name))

        WaitingAssertion.checkAssertion(R.id.textLoaded, isDisplayed(), 1500)
        Espresso.onView(withId(R.id.textLoaded)).check( matches(withText(containsString(" ms"))))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }

    @Test
    fun makeScreenshotOfShapedImage() {
        Espresso.onView(withId(R.id.shaped_image_button)).perform(ViewActions.click())
        Intents.intended(hasComponent(ShapedExampleActivity::class.java.name))
        Espresso.onView(isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")
    }
}
