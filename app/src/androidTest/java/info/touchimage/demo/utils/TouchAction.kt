package info.touchimage.demo.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

class TouchAction(private val x: Float, private val y: Float) : ViewAction {

    override fun getConstraints(): Matcher<View> = ViewMatchers.isDisplayed()

    override fun getDescription() = "Send touch events"

    override fun perform(uiController: UiController, view: View) {
        // Get view absolute position
        val location = IntArray(2)
        view.getLocationOnScreen(location)

        // Offset coordinates by view position
        val coordinates = floatArrayOf(x + location[0], y + location[1])
        val precision = floatArrayOf(1f, 1f)

        // Send down event, pause, and send up
        val down = MotionEvents.sendDown(uiController, coordinates, precision).down
        uiController.loopMainThreadForAtLeast(200)
        MotionEvents.sendUp(uiController, down, coordinates)
    }
}
