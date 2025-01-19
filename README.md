[![](https://jitpack.io/v/MikeOrtiz/TouchImageView.svg)](https://jitpack.io/#hannesa2/TouchImageView)

# TouchImageView for Android

## Capabilities

TouchImageView extends ImageView and supports all of ImageView’s functionality. In addition, TouchImageView adds pinch zoom, dragging, fling, double tap zoom functionality and other animation polish. The intention is for TouchImageView to  mirror as closely as possible the functionality of zoomable images in Gallery apps.

## Download 
Repository available on https://jitpack.io/#MikeOrtiz/TouchImageView

```Gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
```Gradle
dependencies {
    implementation 'com.github.MikeOrtiz:TouchImageView:1.4.1' // last SupportLib version
    // or
    implementation 'com.github.MikeOrtiz:TouchImageView:$LAST_VERSION' // Android X
}

```

## Examples

Please view the sample app which includes examples of the following functionality:

#### Single TouchImageView

Basic use of a single TouchImageView. Includes usage of `OnTouchImageViewListener`, `getScrollPosition()`, `getZoomedRect()`, `isZoomed()`, and `getCurrentZoom()`.

#### ViewPager Example

TouchImageViews placed in a ViewPager like the Gallery app.

#### Mirroring Example

Mirror two TouchImageViews using `onTouchImageViewListener` and `setZoom()`.

#### Switch Image Example

Click on TouchImageView to cycle through images. Note that the zoom state is maintained though the images are switched.

#### Switch ScaleType Example

Click on TouchImageView to cycle through supported ScaleTypes.

#### Resize Example

Click on the arrow buttons to change the shape and size of the TouchImageView. See how the view looks when it shrinks with various "resize" settings. Read ChangeSizeExampleActivity.java's comment for advice on how to set up a TouchImageView that's going to be resized.

## Limitations

TouchImageView does not yet support pinch image rotation. Also, `FIT_START` and `FIT_END` scaleTypes are not yet supported.
	    
## API

Get the current zoom. This is the zoom relative to the initial scale, not the original resource.

    float getCurrentZoom();

Get the max zoom multiplier.

    float getMaxZoom();

Get the min zoom multiplier.

    float getMinZoom();

Return the point at the center of the zoomable image. The `PointF` coordinates range in value between 0 and 1 and the focus point is denoted as a fraction from the left and top of the view. For example, the top left corner of the image would be (0, 0). And the bottom right corner would be (1, 1).

    PointF getScrollPosition();

Return a `RectF` representing the zoomed image.

    RectF getZoomedRect();

Returns `false` if image is in initial, unzoomed state. `True`, otherwise.

    boolean isZoomed();

Reset zoom and translation to initial state.

    void resetZoom();

Set the max zoom multiplier. Default value is 3.

    void setMaxZoom(float max);

Set the min zoom multiplier. Default value is 1. Set to `TouchImageView.AUTOMATIC_MIN_ZOOM` to make it possible to see the whole image.

    void setMinZoom(float min);
    
Set the max zoom multiplier to stay at a fixed multiple of the min zoom multiplier.

    void setMaxZoomRatio(float max);

Set the focus point of the zoomed image. The focus points are denoted as a fraction from the left and top of the view. The focus points can range in value between 0 and 1.

    void setScrollPosition(float focusX, float focusY);

Set zoom to the specified scale. Image will be centered by default.

    void setZoom(float scale);

Set zoom to the specified scale. Image will be centered around the point (focusX, focusY). These floats range from 0 to 1 and denote the focus point as a fraction from the left and top of the view. For example, the top left corner of the image would be (0, 0). And the bottom right corner would be (1, 1).

    void setZoom(float scale, float focusX, float focusY);

Set zoom to the specified scale. Image will be centered around the point (focusX, focusY). These floats range from 0 to 1 and denote the focus point as a fraction from the left and top of the view. For example, the top left corner of the image would be (0, 0). And the bottom right corner would be (1, 1).

    void setZoom(float scale, float focusX, float focusY, ScaleType scaleType);

Set zoom parameters equal to another `TouchImageView`. Including scale, position, and `ScaleType`.

    void setZoom(TouchImageView img);
    
Set which part of the image should remain fixed if the TouchImageView is resized.
    
    setViewSizeChangeFixedPixel(FixedPixel fixedPixel)
    
Set which part of the image should remain fixed if the screen is rotated.

    setOrientationChangeFixedPixel(FixedPixel fixedPixel)

## License

TouchImageView is available under the MIT license. See the LICENSE file for more info.
