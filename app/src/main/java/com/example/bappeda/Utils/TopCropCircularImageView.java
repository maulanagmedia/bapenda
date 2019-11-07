package com.example.bappeda.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class TopCropCircularImageView extends ImageView {
    public TopCropCircularImageView(Context context) {
        super(context);
        init();
    }

    public TopCropCircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopCropCircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        recomputeImgMatrix();
        createCircular(canvas);
        super.onDraw(canvas);
    }

    private void init() {
        setScaleType(ScaleType.MATRIX);
    }

    private void createCircular(Canvas canvas){
        float radius;

        radius = (float)this.getHeight() / 2;

        Path path = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
    }

    private void recomputeImgMatrix() {

        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        final Matrix matrix = getImageMatrix();

        float scale;
        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
    }
}
