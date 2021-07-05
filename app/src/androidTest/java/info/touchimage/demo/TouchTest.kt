package info.touchimage.demo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moka.utils.Screenshot
import info.touchimage.demo.utils.MultiTouchDownEvent
import info.touchimage.demo.utils.TouchAction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TouchTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(SingleTouchImageViewActivity::class.java)

    @Test
    fun testSingleTouch() {
        onView(withId(R.id.imageSingle)).perform(TouchAction(4f, 8f))
        Screenshot.takeScreenshot("touch1")
        onView(withId(R.id.imageSingle)).perform(TouchAction(40f, 80f))
        Screenshot.takeScreenshot("touch2")
    }

    @Test
    fun testMultiTouch() {
        val touchList: Array<Pair<Float, Float>> = listOf(
            Pair(4f, 8f),
            Pair(40f, 80f),
            Pair(30f, 70f)
        ).toTypedArray()
        onView(withId(R.id.imageSingle)).perform(MultiTouchDownEvent(touchList))
        Screenshot.takeScreenshot("multiTouch")
    }
}
