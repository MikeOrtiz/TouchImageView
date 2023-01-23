package info.touchimage.demo

import androidx.test.core.app.takeScreenshot
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.touchimage.demo.utils.MultiTouchDownEvent
import info.touchimage.demo.utils.TouchAction
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TouchTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<SingleTouchImageViewActivity>()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun testSingleTouch() {
        onView(withId(R.id.imageSingle)).perform(TouchAction(4f, 8f))
        onView(withId(R.id.imageSingle)).captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-touch1")
        onView(withId(R.id.imageSingle)).perform(TouchAction(40f, 80f))
        Thread.sleep(300)
        onView(withId(R.id.imageSingle)).captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-touch2")
    }

    @Test
    @Ignore("It is flaky")
    fun testMultiTouch() {
        onView(ViewMatchers.isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-before")
        val touchList: Array<Pair<Float, Float>> = listOf(
            Pair(4f, 8f),
            Pair(40f, 80f),
            Pair(30f, 70f)
        ).toTypedArray()
        onView(withId(R.id.imageSingle)).perform(MultiTouchDownEvent(touchList))
        onView(ViewMatchers.isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-after")
    }
}
