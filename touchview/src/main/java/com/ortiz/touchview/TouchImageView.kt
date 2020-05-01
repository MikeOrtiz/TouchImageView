package com.ortiz.touchview

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import androidx.appcompat.widget.AppCompatImageView

@Suppress("unused")
class TouchImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatImageView(context, attrs, defStyle) {
    /**
     * Get the current zoom. This is the zoom relative to the initial
     * scale, not the original resource.
     *
     * @return current zoom multiplier.
     */
    // Scale of image ranges from minScale to maxScale, where minScale == 1
    // when the image is stretched to fit view.
    var currentZoom = 0f
        private set

    // Matrix applied to image. MSCALE_X and MSCALE_Y should always be equal.
    // MTRANS_X and MTRANS_Y are the other values used. prevMatrix is the matrix saved prior to the screen rotating.
    private var touchMatrix: Matrix? = null
    private var prevMatrix: Matrix? = null
    var isZoomEnabled = false
    private var isRotateImageToFitScreen = false

    enum class FixedPixel {
        CENTER, TOP_LEFT, BOTTOM_RIGHT
    }

    var orientationChangeFixedPixel: FixedPixel? = FixedPixel.CENTER
    var viewSizeChangeFixedPixel: FixedPixel? = FixedPixel.CENTER
    private var orientationJustChanged = false

    private enum class State {
        NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM
    }

    private var state: State? = null
    private var userSpecifiedMinScale = 0f
    private var minScale = 0f
    private var maxScaleIsSetByMultiplier = false
    private var maxScaleMultiplier = 0f
    private var maxScale = 0f
    private var superMinScale = 0f
    private var superMaxScale = 0f
    private var floatMatrix: FloatArray? = null
    /**
     * Get zoom multiplier for double tap
     *
     * @return double tap zoom multiplier.
     */
    /**
     * Set custom zoom multiplier for double tap.
     * By default maxScale will be used as value for double tap zoom multiplier.
     *
     */
    var doubleTapScale = 0f
    private var fling: Fling? = null
    private var orientation = 0
    private var touchScaleType: ScaleType? = null
    private var imageRenderedAtLeastOnce = false
    private var onDrawReady = false
    private var delayedZoomVariables: ZoomVariables? = null

    // Size of view and previous view size (ie before rotation)
    private var viewWidth = 0
    private var viewHeight = 0
    private var prevViewWidth = 0
    private var prevViewHeight = 0

    // Size of image when it is stretched to fit view. Before and After rotation.
    private var matchViewWidth = 0f
    private var matchViewHeight = 0f
    private var prevMatchViewWidth = 0f
    private var prevMatchViewHeight = 0f
    private var mScaleDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null
    private var doubleTapListener: OnDoubleTapListener? = null
    private var userTouchListener: OnTouchListener? = null
    private var touchImageViewListener: OnTouchImageViewListener? = null

    init {
        super.setClickable(true)
        orientation = resources.configuration.orientation
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetector(context, GestureListener())
        touchMatrix = Matrix()
        prevMatrix = Matrix()
        floatMatrix = FloatArray(9)
        currentZoom = 1f
        if (touchScaleType == null) {
            touchScaleType = ScaleType.FIT_CENTER
        }
        minScale = 1f
        maxScale = 3f
        superMinScale = SUPER_MIN_MULTIPLIER * minScale
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale
        imageMatrix = touchMatrix
        scaleType = ScaleType.MATRIX
        setState(State.NONE)
        onDrawReady = false
        super.setOnTouchListener(PrivateOnTouchListener())
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.TouchImageView, defStyle, 0)
        try {
            if (!isInEditMode) {
                isZoomEnabled = attributes.getBoolean(R.styleable.TouchImageView_zoom_enabled, true)
            }
        } finally {
            // release the TypedArray so that it can be reused.
            attributes.recycle()
        }
    }

    fun setRotateImageToFitScreen(rotateImageToFitScreen: Boolean) {
        isRotateImageToFitScreen = rotateImageToFitScreen
    }

    override fun setOnTouchListener(onTouchListener: OnTouchListener) {
        userTouchListener = onTouchListener
    }

    fun setOnTouchImageViewListener(onTouchImageViewListener: OnTouchImageViewListener) {
        touchImageViewListener = onTouchImageViewListener
    }

    fun setOnDoubleTapListener(onDoubleTapListener: OnDoubleTapListener) {
        doubleTapListener = onDoubleTapListener
    }

    override fun setImageResource(resId: Int) {
        imageRenderedAtLeastOnce = false
        super.setImageResource(resId)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageBitmap(bm: Bitmap) {
        imageRenderedAtLeastOnce = false
        super.setImageBitmap(bm)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        imageRenderedAtLeastOnce = false
        super.setImageDrawable(drawable)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setImageURI(uri: Uri?) {
        imageRenderedAtLeastOnce = false
        super.setImageURI(uri)
        savePreviousImageValues()
        fitImageToView()
    }

    override fun setScaleType(type: ScaleType) {
        if (type == ScaleType.MATRIX) {
            super.setScaleType(ScaleType.MATRIX)
        } else {
            touchScaleType = type
            if (onDrawReady) {
                //
                // If the image is already rendered, scaleType has been called programmatically
                // and the TouchImageView should be updated with the new scaleType.
                //
                setZoom(this)
            }
        }
    }

    override fun getScaleType(): ScaleType {
        return touchScaleType!!
    }

    /**
     * Returns false if image is in initial, unzoomed state. False, otherwise.
     *
     * @return true if image is zoomed
     */
    val isZoomed: Boolean
        get() = currentZoom != 1f

    /**
     * Return a Rect representing the zoomed image.
     *
     * @return rect representing zoomed image
     */
    val zoomedRect: RectF
        get() {
            if (touchScaleType == ScaleType.FIT_XY) {
                throw UnsupportedOperationException("getZoomedRect() not supported with FIT_XY")
            }
            val topLeft = transformCoordTouchToBitmap(0f, 0f, true)
            val bottomRight = transformCoordTouchToBitmap(viewWidth.toFloat(), viewHeight.toFloat(), true)
            val w = getDrawableWidth(drawable).toFloat()
            val h = getDrawableHeight(drawable).toFloat()
            return RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h)
        }

    /**
     * Save the current matrix and view dimensions
     * in the prevMatrix and prevView variables.
     */
    fun savePreviousImageValues() {
        if (touchMatrix != null && viewHeight != 0 && viewWidth != 0) {
            touchMatrix!!.getValues(floatMatrix)
            prevMatrix!!.setValues(floatMatrix)
            prevMatchViewHeight = matchViewHeight
            prevMatchViewWidth = matchViewWidth
            prevViewHeight = viewHeight
            prevViewWidth = viewWidth
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("instanceState", super.onSaveInstanceState())
        bundle.putInt("orientation", orientation)
        bundle.putFloat("saveScale", currentZoom)
        bundle.putFloat("matchViewHeight", matchViewHeight)
        bundle.putFloat("matchViewWidth", matchViewWidth)
        bundle.putInt("viewWidth", viewWidth)
        bundle.putInt("viewHeight", viewHeight)
        touchMatrix!!.getValues(floatMatrix)
        bundle.putFloatArray("matrix", floatMatrix)
        bundle.putBoolean("imageRendered", imageRenderedAtLeastOnce)
        bundle.putSerializable("viewSizeChangeFixedPixel", viewSizeChangeFixedPixel)
        bundle.putSerializable("orientationChangeFixedPixel", orientationChangeFixedPixel)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state
            currentZoom = bundle.getFloat("saveScale")
            floatMatrix = bundle.getFloatArray("matrix")
            prevMatrix!!.setValues(floatMatrix)
            prevMatchViewHeight = bundle.getFloat("matchViewHeight")
            prevMatchViewWidth = bundle.getFloat("matchViewWidth")
            prevViewHeight = bundle.getInt("viewHeight")
            prevViewWidth = bundle.getInt("viewWidth")
            imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered")
            viewSizeChangeFixedPixel = bundle.getSerializable("viewSizeChangeFixedPixel") as FixedPixel?
            orientationChangeFixedPixel = bundle.getSerializable("orientationChangeFixedPixel") as FixedPixel?
            val oldOrientation = bundle.getInt("orientation")
            if (orientation != oldOrientation) {
                orientationJustChanged = true
            }
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun onDraw(canvas: Canvas) {
        onDrawReady = true
        imageRenderedAtLeastOnce = true
        if (delayedZoomVariables != null) {
            setZoom(delayedZoomVariables!!.scale, delayedZoomVariables!!.focusX, delayedZoomVariables!!.focusY, delayedZoomVariables!!.scaleType)
            delayedZoomVariables = null
        }
        super.onDraw(canvas)
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val newOrientation = resources.configuration.orientation
        if (newOrientation != orientation) {
            orientationJustChanged = true
            orientation = newOrientation
        }
        savePreviousImageValues()
    }

    /**
     * Get the max zoom multiplier.
     *
     * @return max zoom multiplier.
     */
    /**
     * Set the max zoom multiplier to a constant. Default value: 3.
     *
     */
    var maxZoom: Float
        get() = maxScale
        set(max) {
            maxScale = max
            superMaxScale = SUPER_MAX_MULTIPLIER * maxScale
            maxScaleIsSetByMultiplier = false
        }

    /**
     * Set the max zoom multiplier as a multiple of minZoom, whatever minZoom may change to. By
     * default, this is not done, and maxZoom has a fixed value of 3.
     *
     * @param max max zoom multiplier, as a multiple of minZoom
     */
    fun setMaxZoomRatio(max: Float) {
        maxScaleMultiplier = max
        maxScale = minScale * maxScaleMultiplier
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale
        maxScaleIsSetByMultiplier = true
    }

    /**
     * Get the min zoom multiplier.
     *
     * @return min zoom multiplier.
     */// CENTER_CROP
    /**
     * Set the min zoom multiplier. Default value: 1.
     *
     */
    var minZoom: Float
        get() = minScale
        set(min) {
            userSpecifiedMinScale = min
            if (min == AUTOMATIC_MIN_ZOOM) {
                if (touchScaleType == ScaleType.CENTER || touchScaleType == ScaleType.CENTER_CROP) {
                    val drawable = drawable
                    val drawableWidth = getDrawableWidth(drawable)
                    val drawableHeight = getDrawableHeight(drawable)
                    if (drawable != null && drawableWidth > 0 && drawableHeight > 0) {
                        val widthRatio = viewWidth.toFloat() / drawableWidth
                        val heightRatio = viewHeight.toFloat() / drawableHeight
                        minScale = if (touchScaleType == ScaleType.CENTER) {
                            Math.min(widthRatio, heightRatio)
                        } else {  // CENTER_CROP
                            Math.min(widthRatio, heightRatio) / Math.max(widthRatio, heightRatio)
                        }
                    }
                } else {
                    minScale = 1.0f
                }
            } else {
                minScale = userSpecifiedMinScale
            }
            if (maxScaleIsSetByMultiplier) {
                setMaxZoomRatio(maxScaleMultiplier)
            }
            superMinScale = SUPER_MIN_MULTIPLIER * minScale
        }

    /**
     * Reset zoom and translation to initial state.
     */
    fun resetZoom() {
        currentZoom = 1f
        fitImageToView()
    }

    fun resetZoomAnimated() {
        setZoomAnimated(1f, 0.5f, 0.5f)
    }

    /**
     * Set zoom to the specified scale. Image will be centered by default.
     */
    fun setZoom(scale: Float) {
        setZoom(scale, 0.5f, 0.5f)
    }

    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     */
    fun setZoom(scale: Float, focusX: Float, focusY: Float) {
        setZoom(scale, focusX, focusY, touchScaleType)
    }

    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     */
    fun setZoom(scale: Float, focusX: Float, focusY: Float, scaleType: ScaleType?) {
        //
        // setZoom can be called before the image is on the screen, but at this point,
        // image and view sizes have not yet been calculated in onMeasure. Thus, we should
        // delay calling setZoom until the view has been measured.
        //
        if (!onDrawReady) {
            delayedZoomVariables = ZoomVariables(scale, focusX, focusY, scaleType)
            return
        }
        if (userSpecifiedMinScale == AUTOMATIC_MIN_ZOOM) {
            minZoom = AUTOMATIC_MIN_ZOOM
            if (currentZoom < minScale) {
                currentZoom = minScale
            }
        }
        if (scaleType != touchScaleType) {
            setScaleType(scaleType!!)
        }
        resetZoom()
        scaleImage(scale.toDouble(), viewWidth / 2.toFloat(), viewHeight / 2.toFloat(), true)
        touchMatrix!!.getValues(floatMatrix)
        floatMatrix!![Matrix.MTRANS_X] = -(focusX * imageWidth - viewWidth * 0.5f)
        floatMatrix!![Matrix.MTRANS_Y] = -(focusY * imageHeight - viewHeight * 0.5f)
        touchMatrix!!.setValues(floatMatrix)
        fixTrans()
        savePreviousImageValues()
        imageMatrix = touchMatrix
    }

    /**
     * Set zoom parameters equal to another TouchImageView. Including scale, position,
     * and ScaleType.
     */
    fun setZoom(img: TouchImageView) {
        val center = img.scrollPosition
        setZoom(img.currentZoom, center.x, center.y, img.scaleType)
    }

    /**
     * Return the point at the center of the zoomed image. The PointF coordinates range
     * in value between 0 and 1 and the focus point is denoted as a fraction from the left
     * and top of the view. For example, the top left corner of the image would be (0, 0).
     * And the bottom right corner would be (1, 1).
     *
     * @return PointF representing the scroll position of the zoomed image.
     */
    val scrollPosition: PointF
        get() {
            val drawable = drawable ?: return PointF(.5f, .5f)
            val drawableWidth = getDrawableWidth(drawable)
            val drawableHeight = getDrawableHeight(drawable)
            val point = transformCoordTouchToBitmap(viewWidth / 2.toFloat(), viewHeight / 2.toFloat(), true)
            point.x /= drawableWidth.toFloat()
            point.y /= drawableHeight.toFloat()
            return point
        }

    private fun orientationMismatch(drawable: Drawable?): Boolean {
        return viewWidth > viewHeight != drawable!!.intrinsicWidth > drawable.intrinsicHeight
    }

    private fun getDrawableWidth(drawable: Drawable?): Int {
        return if (orientationMismatch(drawable) && isRotateImageToFitScreen) {
            drawable!!.intrinsicHeight
        } else drawable!!.intrinsicWidth
    }

    private fun getDrawableHeight(drawable: Drawable?): Int {
        return if (orientationMismatch(drawable) && isRotateImageToFitScreen) {
            drawable!!.intrinsicWidth
        } else drawable!!.intrinsicHeight
    }

    /**
     * Set the focus point of the zoomed image. The focus points are denoted as a fraction from the
     * left and top of the view. The focus points can range in value between 0 and 1.
     */
    fun setScrollPosition(focusX: Float, focusY: Float) {
        setZoom(currentZoom, focusX, focusY)
    }

    /**
     * Performs boundary checking and fixes the image matrix if it
     * is out of bounds.
     */
    private fun fixTrans() {
        touchMatrix!!.getValues(floatMatrix)
        val transX = floatMatrix!![Matrix.MTRANS_X]
        val transY = floatMatrix!![Matrix.MTRANS_Y]
        var offset = 0f
        if (isRotateImageToFitScreen && orientationMismatch(drawable)) {
            offset = imageWidth
        }
        val fixTransX = getFixTrans(transX, viewWidth.toFloat(), imageWidth, offset)
        val fixTransY = getFixTrans(transY, viewHeight.toFloat(), imageHeight, 0f)
        touchMatrix!!.postTranslate(fixTransX, fixTransY)
    }

    /**
     * When transitioning from zooming from focus to zoom from center (or vice versa)
     * the image can become unaligned within the view. This is apparent when zooming
     * quickly. When the content size is less than the view size, the content will often
     * be centered incorrectly within the view. fixScaleTrans first calls fixTrans() and
     * then makes sure the image is centered correctly within the view.
     */
    private fun fixScaleTrans() {
        fixTrans()
        touchMatrix!!.getValues(floatMatrix)
        if (imageWidth < viewWidth) {
            var xOffset = (viewWidth - imageWidth) / 2
            if (isRotateImageToFitScreen && orientationMismatch(drawable)) {
                xOffset += imageWidth
            }
            floatMatrix!![Matrix.MTRANS_X] = xOffset
        }
        if (imageHeight < viewHeight) {
            floatMatrix!![Matrix.MTRANS_Y] = (viewHeight - imageHeight) / 2
        }
        touchMatrix!!.setValues(floatMatrix)
    }

    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float, offset: Float): Float {
        val minTrans: Float
        val maxTrans: Float
        if (contentSize <= viewSize) {
            minTrans = offset
            maxTrans = offset + viewSize - contentSize
        } else {
            minTrans = offset + viewSize - contentSize
            maxTrans = offset
        }
        if (trans < minTrans) return -trans + minTrans
        return if (trans > maxTrans) -trans + maxTrans else 0f
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) {
            0f
        } else
            delta
    }

    private val imageWidth: Float
        get() = matchViewWidth * currentZoom

    private val imageHeight: Float
        get() = matchViewHeight * currentZoom

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            setMeasuredDimension(0, 0)
            return
        }
        val drawableWidth = getDrawableWidth(drawable)
        val drawableHeight = getDrawableHeight(drawable)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val totalViewWidth = setViewSize(widthMode, widthSize, drawableWidth)
        val totalViewHeight = setViewSize(heightMode, heightSize, drawableHeight)
        if (!orientationJustChanged) {
            savePreviousImageValues()
        }

        // Image view width, height must consider padding
        val width = totalViewWidth - paddingLeft - paddingRight
        val height = totalViewHeight - paddingTop - paddingBottom

        // Set view dimensions
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //
        // Fit content within view.
        //
        // onMeasure may be called multiple times for each layout change, including orientation
        // changes. For example, if the TouchImageView is inside a ConstraintLayout, onMeasure may
        // be called with:
        // widthMeasureSpec == "AT_MOST 2556" and then immediately with
        // widthMeasureSpec == "EXACTLY 1404", then back and forth multiple times in quick
        // succession, as the ConstraintLayout tries to solve its constraints.
        //
        // onSizeChanged is called once after the final onMeasure is called. So we make all changes
        // to class members, such as fitting the image into the new shape of the TouchImageView,
        // here, after the final size has been determined. This helps us avoid both
        // repeated computations, and making irreversible changes (e.g. making the View temporarily too
        // big or too small, thus making the current zoom fall outside of an automatically-changing
        // minZoom and maxZoom).
        //
        viewWidth = w
        viewHeight = h
        fitImageToView()
    }

    /**
     * This function can be called:
     * 1. When the TouchImageView is first loaded (onMeasure).
     * 2. When a new image is loaded (setImageResource|Bitmap|Drawable|URI).
     * 3. On rotation (onSaveInstanceState, then onRestoreInstanceState, then onMeasure).
     * 4. When the view is resized (onMeasure).
     * 5. When the zoom is reset (resetZoom).
     *
     *
     * In cases 2, 3 and 4, we try to maintain the zoom state and position as directed by
     * orientationChangeFixedPixel or viewSizeChangeFixedPixel (if there is an existing zoom state
     * and position, which there might not be in case 2).
     *
     *
     * If the normalizedScale is equal to 1, then the image is made to fit the View. Otherwise, we
     * maintain zoom level and attempt to roughly put the same part of the image in the View as was
     * there before, paying attention to orientationChangeFixedPixel or viewSizeChangeFixedPixel.
     */
    private fun fitImageToView() {
        val fixedPixel = if (orientationJustChanged) orientationChangeFixedPixel else viewSizeChangeFixedPixel
        orientationJustChanged = false
        val drawable = drawable
        if (drawable == null || drawable.intrinsicWidth == 0 || drawable.intrinsicHeight == 0) {
            return
        }
        if (touchMatrix == null || prevMatrix == null) {
            return
        }
        if (userSpecifiedMinScale == AUTOMATIC_MIN_ZOOM) {
            minZoom = AUTOMATIC_MIN_ZOOM
            if (currentZoom < minScale) {
                currentZoom = minScale
            }
        }
        val drawableWidth = getDrawableWidth(drawable)
        val drawableHeight = getDrawableHeight(drawable)

        //
        // Scale image for view
        //
        var scaleX = viewWidth.toFloat() / drawableWidth
        var scaleY = viewHeight.toFloat() / drawableHeight
        when (touchScaleType) {
            ScaleType.CENTER -> {
                scaleY = 1f
                scaleX = scaleY
            }
            ScaleType.CENTER_CROP -> {
                scaleY = Math.max(scaleX, scaleY)
                scaleX = scaleY
            }
            ScaleType.CENTER_INSIDE -> {
                run {
                    scaleY = Math.min(1f, Math.min(scaleX, scaleY))
                    scaleX = scaleY
                }
                run {
                    scaleY = Math.min(scaleX, scaleY)
                    scaleX = scaleY
                }
            }
            ScaleType.FIT_CENTER, ScaleType.FIT_START, ScaleType.FIT_END -> {
                scaleY = Math.min(scaleX, scaleY)
                scaleX = scaleY
            }
            ScaleType.FIT_XY -> {
            }
            else -> {
            }
        }

        // Put the image's center in the right place.
        val redundantXSpace = viewWidth - scaleX * drawableWidth
        val redundantYSpace = viewHeight - scaleY * drawableHeight
        matchViewWidth = viewWidth - redundantXSpace
        matchViewHeight = viewHeight - redundantYSpace
        if (!isZoomed && !imageRenderedAtLeastOnce) {

            // Stretch and center image to fit view
            if (isRotateImageToFitScreen && orientationMismatch(drawable)) {
                touchMatrix!!.setRotate(90f)
                touchMatrix!!.postTranslate(drawableWidth.toFloat(), 0f)
                touchMatrix!!.postScale(scaleX, scaleY)
            } else {
                touchMatrix!!.setScale(scaleX, scaleY)
            }
            when (touchScaleType) {
                ScaleType.FIT_START -> touchMatrix!!.postTranslate(0f, 0f)
                ScaleType.FIT_END -> touchMatrix!!.postTranslate(redundantXSpace, redundantYSpace)
                else -> touchMatrix!!.postTranslate(redundantXSpace / 2, redundantYSpace / 2)
            }
            currentZoom = 1f
        } else {
            // These values should never be 0 or we will set viewWidth and viewHeight
            // to NaN in newTranslationAfterChange. To avoid this, call savePreviousImageValues
            // to set them equal to the current values.
            if (prevMatchViewWidth == 0f || prevMatchViewHeight == 0f) {
                savePreviousImageValues()
            }

            // Use the previous matrix as our starting point for the new matrix.
            prevMatrix!!.getValues(floatMatrix)

            // Rescale Matrix if appropriate
            floatMatrix!![Matrix.MSCALE_X] = matchViewWidth / drawableWidth * currentZoom
            floatMatrix!![Matrix.MSCALE_Y] = matchViewHeight / drawableHeight * currentZoom

            // TransX and TransY from previous matrix
            val transX = floatMatrix!![Matrix.MTRANS_X]
            val transY = floatMatrix!![Matrix.MTRANS_Y]

            // X position
            val prevActualWidth = prevMatchViewWidth * currentZoom
            val actualWidth = imageWidth
            floatMatrix!![Matrix.MTRANS_X] = newTranslationAfterChange(transX, prevActualWidth, actualWidth, prevViewWidth, viewWidth, drawableWidth, fixedPixel)

            // Y position
            val prevActualHeight = prevMatchViewHeight * currentZoom
            val actualHeight = imageHeight
            floatMatrix!![Matrix.MTRANS_Y] = newTranslationAfterChange(transY, prevActualHeight, actualHeight, prevViewHeight, viewHeight, drawableHeight, fixedPixel)

            // Set the matrix to the adjusted scale and translation values.
            touchMatrix!!.setValues(floatMatrix)
        }
        fixTrans()
        imageMatrix = touchMatrix
    }

    /**
     * Set view dimensions based on layout params
     */
    private fun setViewSize(mode: Int, size: Int, drawableWidth: Int): Int {
        val viewSize: Int
        viewSize = when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> Math.min(drawableWidth, size)
            MeasureSpec.UNSPECIFIED -> drawableWidth
            else -> size
        }
        return viewSize
    }

    /**
     * After any change described in the comments for fitImageToView, the matrix needs to be
     * translated. This function translates the image so that the fixed pixel in the image
     * stays in the same place in the View.
     *
     * @param trans                the value of trans in that axis before the rotation
     * @param prevImageSize        the width/height of the image before the rotation
     * @param imageSize            width/height of the image after rotation
     * @param prevViewSize         width/height of view before rotation
     * @param viewSize             width/height of view after rotation
     * @param drawableSize         width/height of drawable
     * @param sizeChangeFixedPixel how we should choose the fixed pixel
     */
    private fun newTranslationAfterChange(trans: Float, prevImageSize: Float, imageSize: Float, prevViewSize: Int, viewSize: Int, drawableSize: Int, sizeChangeFixedPixel: FixedPixel?): Float {
        return if (imageSize < viewSize) {
            //
            // The width/height of image is less than the view's width/height. Center it.
            //
            (viewSize - drawableSize * floatMatrix!![Matrix.MSCALE_X]) * 0.5f
        } else if (trans > 0) {
            //
            // The image is larger than the view, but was not before the view changed. Center it.
            //
            -((imageSize - viewSize) * 0.5f)
        } else {
            //
            // Where is the pixel in the View that we are keeping stable, as a fraction of the
            // width/height of the View?
            //
            var fixedPixelPositionInView = 0.5f // CENTER
            if (sizeChangeFixedPixel == FixedPixel.BOTTOM_RIGHT) {
                fixedPixelPositionInView = 1.0f
            } else if (sizeChangeFixedPixel == FixedPixel.TOP_LEFT) {
                fixedPixelPositionInView = 0.0f
            }
            //
            // Where is the pixel in the Image that we are keeping stable, as a fraction of the
            // width/height of the Image?
            //
            val fixedPixelPositionInImage = (-trans + fixedPixelPositionInView * prevViewSize) / prevImageSize
            //
            // Here's what the new translation should be so that, after whatever change triggered
            // this function to be called, the pixel at fixedPixelPositionInView of the View is
            // still the pixel at fixedPixelPositionInImage of the image.
            //
            -(fixedPixelPositionInImage * imageSize - viewSize * fixedPixelPositionInView)
        }
    }

    private fun setState(state: State) {
        this.state = state
    }

    @Deprecated("")
    fun canScrollHorizontallyFroyo(direction: Int): Boolean {
        return canScrollHorizontally(direction)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        touchMatrix!!.getValues(floatMatrix)
        val x = floatMatrix!![Matrix.MTRANS_X]
        return if (imageWidth < viewWidth) {
            false
        } else if (x >= -1 && direction < 0) {
            false
        } else Math.abs(x) + viewWidth + 1 < imageWidth || direction <= 0
    }

    override fun canScrollVertically(direction: Int): Boolean {
        touchMatrix!!.getValues(floatMatrix)
        val y = floatMatrix!![Matrix.MTRANS_Y]
        return if (imageHeight < viewHeight) {
            false
        } else if (y >= -1 && direction < 0) {
            false
        } else Math.abs(y) + viewHeight + 1 < imageHeight || direction <= 0
    }

    /**
     * Gesture Listener detects a single click or long click and passes that on
     * to the view's listener.
     */
    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            return if (doubleTapListener != null) {
                doubleTapListener!!.onSingleTapConfirmed(e)
            } else performClick()
        }

        override fun onLongPress(e: MotionEvent?) {
            performLongClick()
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            // If a previous fling is still active, it should be cancelled so that two flings
            // are not run simultaneously.
            fling?.cancelFling()
            fling = Fling(velocityX.toInt(), velocityY.toInt())
                    .also { compatPostOnAnimation(it) }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            var consumed = false
            if (e != null && isZoomEnabled) {
                if (doubleTapListener != null) {
                    consumed = doubleTapListener!!.onDoubleTap(e)
                }
                if (state == State.NONE) {
                    val maxZoomScale = if (doubleTapScale == 0f) maxScale else doubleTapScale
                    val targetZoom = if (currentZoom == minScale) maxZoomScale else minScale
                    val doubleTap = DoubleTapZoom(targetZoom, e.x, e.y, false)
                    compatPostOnAnimation(doubleTap)
                    consumed = true
                }
            }
            return consumed
        }

        override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
            return if (doubleTapListener != null) {
                doubleTapListener!!.onDoubleTapEvent(e)
            } else false
        }
    }

    interface OnTouchImageViewListener {
        fun onMove()
    }

    /**
     * Responsible for all touch events. Handles the heavy lifting of drag and also sends
     * touch events to Scale Detector and Gesture Detector.
     */
    private inner class PrivateOnTouchListener : OnTouchListener {

        // Remember last point position for dragging
        private val last = PointF()
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (drawable == null) {
                setState(State.NONE)
                return false
            }
            if (isZoomEnabled) {
                mScaleDetector!!.onTouchEvent(event)
            }
            mGestureDetector!!.onTouchEvent(event)
            val curr = PointF(event.x, event.y)
            if (state == State.NONE || state == State.DRAG || state == State.FLING) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        last.set(curr)
                        if (fling != null) fling!!.cancelFling()
                        setState(State.DRAG)
                    }
                    MotionEvent.ACTION_MOVE -> if (state == State.DRAG) {
                        val deltaX = curr.x - last.x
                        val deltaY = curr.y - last.y
                        val fixTransX = getFixDragTrans(deltaX, viewWidth.toFloat(), imageWidth)
                        val fixTransY = getFixDragTrans(deltaY, viewHeight.toFloat(), imageHeight)
                        touchMatrix!!.postTranslate(fixTransX, fixTransY)
                        fixTrans()
                        last[curr.x] = curr.y
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> setState(State.NONE)
                }
            }
            imageMatrix = touchMatrix

            //
            // User-defined OnTouchListener
            //
            if (userTouchListener != null) {
                userTouchListener!!.onTouch(v, event)
            }

            //
            // OnTouchImageViewListener is set: TouchImageView dragged by user.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }

            //
            // indicate event was handled
            //
            return true
        }
    }

    /**
     * ScaleListener detects user two finger scaling and scales image.
     */
    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            setState(State.ZOOM)
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleImage(detector.scaleFactor.toDouble(), detector.focusX, detector.focusY, true)

            //
            // OnTouchImageViewListener is set: TouchImageView pinch zoomed by user.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
            setState(State.NONE)
            var animateToZoomBoundary = false
            var targetZoom: Float = currentZoom
            if (currentZoom > maxScale) {
                targetZoom = maxScale
                animateToZoomBoundary = true
            } else if (currentZoom < minScale) {
                targetZoom = minScale
                animateToZoomBoundary = true
            }
            if (animateToZoomBoundary) {
                val doubleTap = DoubleTapZoom(targetZoom, (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), true)
                compatPostOnAnimation(doubleTap)
            }
        }
    }

    private fun scaleImage(deltaScale: Double, focusX: Float, focusY: Float, stretchImageToSuper: Boolean) {
        var deltaScaleLocal = deltaScale
        val lowerScale: Float
        val upperScale: Float
        if (stretchImageToSuper) {
            lowerScale = superMinScale
            upperScale = superMaxScale
        } else {
            lowerScale = minScale
            upperScale = maxScale
        }
        val origScale = currentZoom
        currentZoom *= deltaScaleLocal.toFloat()
        if (currentZoom > upperScale) {
            currentZoom = upperScale
            deltaScaleLocal = upperScale / origScale.toDouble()
        } else if (currentZoom < lowerScale) {
            currentZoom = lowerScale
            deltaScaleLocal = lowerScale / origScale.toDouble()
        }
        touchMatrix!!.postScale(deltaScaleLocal.toFloat(), deltaScaleLocal.toFloat(), focusX, focusY)
        fixScaleTrans()
    }

    /**
     * DoubleTapZoom calls a series of runnables which apply
     * an animated zoom in/out graphic to the image.
     */
    private inner class DoubleTapZoom internal constructor(targetZoom: Float, focusX: Float, focusY: Float, stretchImageToSuper: Boolean) : Runnable {
        private val startTime: Long
        private val startZoom: Float
        private val targetZoom: Float
        private val bitmapX: Float
        private val bitmapY: Float
        private val stretchImageToSuper: Boolean
        private val interpolator = AccelerateDecelerateInterpolator()
        private val startTouch: PointF
        private val endTouch: PointF
        override fun run() {
            if (drawable == null) {
                setState(State.NONE)
                return
            }
            val t = interpolate()
            val deltaScale = calculateDeltaScale(t)
            scaleImage(deltaScale, bitmapX, bitmapY, stretchImageToSuper)
            translateImageToCenterTouchPosition(t)
            fixScaleTrans()
            imageMatrix = touchMatrix

            // double tap runnable updates listener with every frame.
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            if (t < 1f) {
                // We haven't finished zooming
                compatPostOnAnimation(this)
            } else {
                // Finished zooming
                setState(State.NONE)
            }
        }

        /**
         * Interpolate between where the image should start and end in order to translate
         * the image so that the point that is touched is what ends up centered at the end
         * of the zoom.
         */
        private fun translateImageToCenterTouchPosition(t: Float) {
            val targetX = startTouch.x + t * (endTouch.x - startTouch.x)
            val targetY = startTouch.y + t * (endTouch.y - startTouch.y)
            val curr = transformCoordBitmapToTouch(bitmapX, bitmapY)
            touchMatrix!!.postTranslate(targetX - curr.x, targetY - curr.y)
        }

        /**
         * Use interpolator to get t
         */
        private fun interpolate(): Float {
            val currTime = System.currentTimeMillis()
            var elapsed = (currTime - startTime) / DEFAULT_ZOOM_TIME.toFloat()
            elapsed = Math.min(1f, elapsed)
            return interpolator.getInterpolation(elapsed)
        }

        /**
         * Interpolate the current targeted zoom and get the delta
         * from the current zoom.
         */
        private fun calculateDeltaScale(t: Float): Double {
            val zoom = startZoom + t * (targetZoom - startZoom).toDouble()
            return zoom / currentZoom
        }

        init {
            setState(State.ANIMATE_ZOOM)
            startTime = System.currentTimeMillis()
            startZoom = currentZoom
            this.targetZoom = targetZoom
            this.stretchImageToSuper = stretchImageToSuper
            val bitmapPoint = transformCoordTouchToBitmap(focusX, focusY, false)
            bitmapX = bitmapPoint.x
            bitmapY = bitmapPoint.y

            // Used for translating image during scaling
            startTouch = transformCoordBitmapToTouch(bitmapX, bitmapY)
            endTouch = PointF((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
        }
    }

    /**
     * This function will transform the coordinates in the touch event to the coordinate
     * system of the drawable that the imageview contain
     *
     * @param x            x-coordinate of touch event
     * @param y            y-coordinate of touch event
     * @param clipToBitmap Touch event may occur within view, but outside image content. True, to clip return value
     * to the bounds of the bitmap size.
     * @return Coordinates of the point touched, in the coordinate system of the original drawable.
     */
    protected fun transformCoordTouchToBitmap(x: Float, y: Float, clipToBitmap: Boolean): PointF {
        touchMatrix!!.getValues(floatMatrix)
        val origW = drawable.intrinsicWidth.toFloat()
        val origH = drawable.intrinsicHeight.toFloat()
        val transX = floatMatrix!![Matrix.MTRANS_X]
        val transY = floatMatrix!![Matrix.MTRANS_Y]
        var finalX = (x - transX) * origW / imageWidth
        var finalY = (y - transY) * origH / imageHeight
        if (clipToBitmap) {
            finalX = Math.min(Math.max(finalX, 0f), origW)
            finalY = Math.min(Math.max(finalY, 0f), origH)
        }
        return PointF(finalX, finalY)
    }

    /**
     * Inverse of transformCoordTouchToBitmap. This function will transform the coordinates in the
     * drawable's coordinate system to the view's coordinate system.
     *
     * @param bx x-coordinate in original bitmap coordinate system
     * @param by y-coordinate in original bitmap coordinate system
     * @return Coordinates of the point in the view's coordinate system.
     */
    protected fun transformCoordBitmapToTouch(bx: Float, by: Float): PointF {
        touchMatrix!!.getValues(floatMatrix)
        val origW = drawable.intrinsicWidth.toFloat()
        val origH = drawable.intrinsicHeight.toFloat()
        val px = bx / origW
        val py = by / origH
        val finalX = floatMatrix!![Matrix.MTRANS_X] + imageWidth * px
        val finalY = floatMatrix!![Matrix.MTRANS_Y] + imageHeight * py
        return PointF(finalX, finalY)
    }

    /**
     * Fling launches sequential runnables which apply
     * the fling graphic to the image. The values for the translation
     * are interpolated by the Scroller.
     */
    private inner class Fling internal constructor(velocityX: Int, velocityY: Int) : Runnable {
        var scroller: CompatScroller?
        var currX: Int
        var currY: Int
        fun cancelFling() {
            if (scroller != null) {
                setState(State.NONE)
                scroller!!.forceFinished(true)
            }
        }

        override fun run() {

            // OnTouchImageViewListener is set: TouchImageView listener has been flung by user.
            // Listener runnable updated with each frame of fling animation.
            if (touchImageViewListener != null) {
                touchImageViewListener!!.onMove()
            }
            if (scroller!!.isFinished) {
                scroller = null
                return
            }
            if (scroller!!.computeScrollOffset()) {
                val newX = scroller!!.currX
                val newY = scroller!!.currY
                val transX = newX - currX
                val transY = newY - currY
                currX = newX
                currY = newY
                touchMatrix!!.postTranslate(transX.toFloat(), transY.toFloat())
                fixTrans()
                imageMatrix = touchMatrix
                compatPostOnAnimation(this)
            }
        }

        init {
            setState(State.FLING)
            scroller = CompatScroller(context)
            touchMatrix!!.getValues(floatMatrix)
            var startX = floatMatrix!![Matrix.MTRANS_X].toInt()
            val startY = floatMatrix!![Matrix.MTRANS_Y].toInt()
            val minX: Int
            val maxX: Int
            val minY: Int
            val maxY: Int
            if (isRotateImageToFitScreen && orientationMismatch(drawable)) {
                startX -= imageWidth.toInt()
            }
            if (imageWidth > viewWidth) {
                minX = viewWidth - imageWidth.toInt()
                maxX = 0
            } else {
                maxX = startX
                minX = maxX
            }
            if (imageHeight > viewHeight) {
                minY = viewHeight - imageHeight.toInt()
                maxY = 0
            } else {
                maxY = startY
                minY = maxY
            }
            scroller!!.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
            currX = startX
            currY = startY
        }
    }

    @TargetApi(VERSION_CODES.GINGERBREAD)
    private inner class CompatScroller internal constructor(context: Context?) {
        var overScroller: OverScroller
        fun fling(startX: Int, startY: Int, velocityX: Int, velocityY: Int, minX: Int, maxX: Int, minY: Int, maxY: Int) {
            overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY)
        }

        fun forceFinished(finished: Boolean) {
            overScroller.forceFinished(finished)
        }

        val isFinished: Boolean
            get() = overScroller.isFinished

        fun computeScrollOffset(): Boolean {
            overScroller.computeScrollOffset()
            return overScroller.computeScrollOffset()
        }

        val currX: Int
            get() = overScroller.currX

        val currY: Int
            get() = overScroller.currY

        init {
            overScroller = OverScroller(context)
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    private fun compatPostOnAnimation(runnable: Runnable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            postOnAnimation(runnable)
        } else {
            postDelayed(runnable, 1000 / 60.toLong())
        }
    }

    private inner class ZoomVariables internal constructor(var scale: Float, var focusX: Float, var focusY: Float, var scaleType: ScaleType?)

    interface OnZoomFinishedListener {
        fun onZoomFinished()
    }

    /**
     * Set zoom to the specified scale with a linearly interpolated animation. Image will be
     * centered around the point (focusX, focusY). These floats range from 0 to 1 and denote the
     * focus point as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     */
    fun setZoomAnimated(scale: Float, focusX: Float, focusY: Float) {
        setZoomAnimated(scale, focusX, focusY, DEFAULT_ZOOM_TIME)
    }

    fun setZoomAnimated(scale: Float, focusX: Float, focusY: Float, zoomTimeMs: Int) {
        val animation = AnimatedZoom(scale, PointF(focusX, focusY), zoomTimeMs)
        compatPostOnAnimation(animation)
    }

    /**
     * Set zoom to the specified scale with a linearly interpolated animation. Image will be
     * centered around the point (focusX, focusY). These floats range from 0 to 1 and denote the
     * focus point as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     *
     * @param listener the listener, which will be notified, once the animation ended
     */
    fun setZoomAnimated(scale: Float, focusX: Float, focusY: Float, zoomTimeMs: Int, listener: OnZoomFinishedListener?) {
        val animation = AnimatedZoom(scale, PointF(focusX, focusY), zoomTimeMs)
        animation.setListener(listener)
        compatPostOnAnimation(animation)
    }

    fun setZoomAnimated(scale: Float, focusX: Float, focusY: Float, listener: OnZoomFinishedListener?) {
        val animation = AnimatedZoom(scale, PointF(focusX, focusY), DEFAULT_ZOOM_TIME)
        animation.setListener(listener)
        compatPostOnAnimation(animation)
    }

    /**
     * AnimatedZoom calls a series of runnables which apply
     * an animated zoom to the specified target focus at the specified zoom level.
     */
    private inner class AnimatedZoom internal constructor(targetZoom: Float, focus: PointF, zoomTimeMillis: Int) : Runnable {
        private val zoomTimeMillis: Int
        private val startTime: Long
        private val startZoom: Float
        private val targetZoom: Float
        private val startFocus: PointF
        private val targetFocus: PointF
        private val interpolator = LinearInterpolator()
        private var listener: OnZoomFinishedListener? = null
        override fun run() {
            val t = interpolate()

            // Calculate the next focus and zoom based on the progress of the interpolation
            val nextZoom = startZoom + (targetZoom - startZoom) * t
            val nextX = startFocus.x + (targetFocus.x - startFocus.x) * t
            val nextY = startFocus.y + (targetFocus.y - startFocus.y) * t
            setZoom(nextZoom, nextX, nextY)
            if (t < 1f) {
                // We haven't finished zooming
                compatPostOnAnimation(this)
            } else {
                // Finished zooming
                setState(State.NONE)
                if (listener != null) listener!!.onZoomFinished()
            }
        }

        /**
         * Use interpolator to get t
         *
         * @return progress of the interpolation
         */
        private fun interpolate(): Float {
            var elapsed = (System.currentTimeMillis() - startTime) / zoomTimeMillis.toFloat()
            elapsed = Math.min(1f, elapsed)
            return interpolator.getInterpolation(elapsed)
        }

        fun setListener(listener: OnZoomFinishedListener?) {
            this.listener = listener
        }

        init {
            setState(State.ANIMATE_ZOOM)
            startTime = System.currentTimeMillis()
            startZoom = currentZoom
            this.targetZoom = targetZoom
            this.zoomTimeMillis = zoomTimeMillis

            // Used for translating image during zooming
            startFocus = scrollPosition
            targetFocus = focus
        }
    }

    companion object {
        private const val DEBUG = "DEBUG"

        // SuperMin and SuperMax multipliers. Determine how much the image can be
        // zoomed below or above the zoom boundaries, before animating back to the
        // min/max zoom boundary.
        private const val SUPER_MIN_MULTIPLIER = .75f
        private const val SUPER_MAX_MULTIPLIER = 1.25f
        private const val DEFAULT_ZOOM_TIME = 500

        /**
         * If setMinZoom(AUTOMATIC_MIN_ZOOM), then we'll set the min scale to include the whole image.
         */
        const val AUTOMATIC_MIN_ZOOM = -1.0f
    }

}
