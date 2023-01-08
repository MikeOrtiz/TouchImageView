package info.touchimage.demo.utils

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher

class MultiTouchDownEvent(private val locations: Array<Pair<Float, Float>>) : ViewAction {

    override fun getDescription() = "Multi Touch Event"

    override fun getConstraints(): Matcher<View> = isDisplayed()

    override fun perform(uiController: UiController, view: View) {

        val screenPos = IntArray(2)
        view.getLocationOnScreen(screenPos)

        val coordinatesList = buildCoordinatesList(screenPos)
        val pointerProperties = buildPointerPropertiesList()
        println(coordinatesList)
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis()

        for (i in coordinatesList.indices) {
            val pointerCount = i + 1

            val coordinatesSlice = coordinatesList.subList(0, pointerCount)
            val propertiesSlice = pointerProperties.subList(0, pointerCount)

            val eventType = pointerDownEventType(pointerCount)

            val event = MotionEvent.obtain(
                downTime,
                eventTime,
                eventType,
                pointerCount,
                propertiesSlice.toTypedArray(),
                coordinatesSlice.toTypedArray(),
                0,
                0,
                1f,
                1f,
                0,
                0,
                InputDevice.SOURCE_UNKNOWN,
                0
            )

            uiController.injectMotionEvent(event)

            event.recycle()
        }
    }

    private fun buildCoordinatesList(screenPosition: IntArray): List<MotionEvent.PointerCoords> {
        return locations.map {
            val coordinate = MotionEvent.PointerCoords()
            coordinate.x = it.first + screenPosition[0]
            coordinate.y = it.second + screenPosition[1]


            coordinate.pressure = 1f
            coordinate.size = 1f
            coordinate
        }
    }

    private fun buildPointerPropertiesList(): List<MotionEvent.PointerProperties> {
        return IntArray(locations.count()) { it }.map {
            val pointer = MotionEvent.PointerProperties()
            pointer.id = it
            pointer
        }
    }

    private fun pointerDownEventType(numberOfPointers: Int): Int {
        if (numberOfPointers < 1) return -1

        var eventType = if (numberOfPointers == 1) MotionEvent.ACTION_DOWN else MotionEvent.ACTION_POINTER_DOWN
        eventType += (numberOfPointers shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)

        return eventType
    }
}
