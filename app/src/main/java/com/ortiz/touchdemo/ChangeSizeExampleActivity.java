package com.ortiz.touchdemo;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.touch.R;
import com.ortiz.touchview.TouchImageView;

public class ChangeSizeExampleActivity extends Activity {
    private TouchImageView image;
    private FrameLayout imageContainer;

    ValueAnimator xSizeAnimator = new ValueAnimator();
    ValueAnimator ySizeAnimator = new ValueAnimator();
    private int xSizeAdjustment = 0;
    private int ySizeAdjustment = 0;

    private static final ImageView.ScaleType[] scaleTypes = { ImageView.ScaleType.CENTER, ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE, ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_CENTER };
    private int scaleTypeIndex = 0;

    private SizeBehaviorAdjuster resizeAdjuster;
    private SizeBehaviorAdjuster rotateAdjuster;

    // TODO: Understand CENTER_CROP resizing itself on scroll.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_size);
        image = findViewById(R.id.img);
        image.setBackgroundColor(Color.LTGRAY);
        image.setMinZoom(TouchImageView.AUTOMATIC_MIN_ZOOM);
        image.setMaxZoom(6.0f);
        imageContainer = findViewById(R.id.image_container);

        findViewById(R.id.left).setOnClickListener(new SizeAdjuster(-1, 0));
        findViewById(R.id.right).setOnClickListener(new SizeAdjuster(1, 0));
        findViewById(R.id.up).setOnClickListener(new SizeAdjuster(0, -1));
        findViewById(R.id.down).setOnClickListener(new SizeAdjuster(0, 1));

        resizeAdjuster = new SizeBehaviorAdjuster(false, "resize: ");
        rotateAdjuster = new SizeBehaviorAdjuster(true, "rotate: ");
        findViewById(R.id.resize).setOnClickListener(resizeAdjuster);
        findViewById(R.id.rotate).setOnClickListener(rotateAdjuster);

        findViewById(R.id.switch_scaletype_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaleTypeIndex = (scaleTypeIndex + 1) % scaleTypes.length;
                ImageView.ScaleType scaleType = scaleTypes[scaleTypeIndex];
                ((Button) v).setText(scaleType.name());
                image.setScaleType(scaleType);
                image.resetZoom();
            }
        });

        if (savedInstanceState != null) {
            scaleTypeIndex = savedInstanceState.getInt("scaleTypeIndex");
            ((Button) findViewById(R.id.switch_scaletype_button)).setText(scaleTypes[scaleTypeIndex].name());
            resizeAdjuster.setIndex((Button) findViewById(R.id.resize), savedInstanceState.getInt("resizeAdjusterIndex"));
            rotateAdjuster.setIndex((Button) findViewById(R.id.rotate), savedInstanceState.getInt("rotateAdjusterIndex"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("scaleTypeIndex", scaleTypeIndex);
        outState.putInt("resizeAdjusterIndex", resizeAdjuster.index);
        outState.putInt("rotateAdjusterIndex", rotateAdjuster.index);
    }

    private void adjustImageSize() {
        double width = imageContainer.getMeasuredWidth() * Math.pow(1.1, xSizeAdjustment);
        double height = imageContainer.getMeasuredHeight() * Math.pow(1.1, ySizeAdjustment);
        xSizeAnimator.cancel();
        ySizeAnimator.cancel();
        xSizeAnimator = ValueAnimator.ofInt(image.getWidth(), (int) width);
        ySizeAnimator = ValueAnimator.ofInt(image.getHeight(), (int) height);
        xSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
                layoutParams.width = (Integer) animation.getAnimatedValue();
                image.setLayoutParams(layoutParams);
            }
        });
        ySizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
                layoutParams.height = (Integer) animation.getAnimatedValue();
                image.setLayoutParams(layoutParams);
            }
        });
        xSizeAnimator.setDuration(200);
        ySizeAnimator.setDuration(200);
        xSizeAnimator.start();
        ySizeAnimator.start();
    }

    private class SizeAdjuster implements View.OnClickListener {
        int dx, dy;

        public SizeAdjuster(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public void onClick(View v) {
            int newXScale = Math.min(0, xSizeAdjustment + dx);
            int newYScale = Math.min(0, ySizeAdjustment + dy);
            if (newXScale == xSizeAdjustment && newYScale == ySizeAdjustment) {
                return;
            }
            xSizeAdjustment = newXScale;
            ySizeAdjustment = newYScale;
            adjustImageSize();
        }
    }

    private class SizeBehaviorAdjuster implements View.OnClickListener {
        final TouchImageView.ViewSizeChangeBehavior[] values = TouchImageView.ViewSizeChangeBehavior.values();
        final String[] names = {"mid", "top-left", "bottom-right"};
        private int index = 0;
        private boolean forOrientationChanges;
        private String buttonPrefix;

        public SizeBehaviorAdjuster(boolean forOrientationChanges, String buttonPrefix) {
            this.forOrientationChanges = forOrientationChanges;
            this.buttonPrefix = buttonPrefix;
        }

        @Override
        public void onClick(View v) {
            setIndex((Button) v, (index + 1) % values.length);
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(Button b, int index) {
            this.index = index;
            if (forOrientationChanges) {
                image.setOrientationChangeBehavior(values[index]);
            } else {
                image.setViewSizeChangeBehavior(values[index]);
            }
            b.setText(buttonPrefix + names[index]);
        }
    }
}
