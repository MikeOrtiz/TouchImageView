package info.touchimage.demo

import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZoomTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<AnimateZoomActivity>()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun zoom() {
        Thread.sleep(WAIT)
        onView(ViewMatchers.isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-1-init")
        onView(withId(R.id.current_zoom)).perform(ViewActions.click())
        Thread.sleep(WAIT)
        onView(ViewMatchers.isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-2-reset")
        onView(withId(R.id.current_zoom)).perform(ViewActions.click())
        Thread.sleep(WAIT)
        onView(ViewMatchers.isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-3-zoom")
        onView(withId(R.id.current_zoom)).perform(ViewActions.click())
        Thread.sleep(WAIT)
        onView(ViewMatchers.isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-4-end")
    }

    companion object {
        const val WAIT = 600L
    }

}
