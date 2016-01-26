/*
 * Copyright (C) 2016 Tafia Gu of The Unic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package unic.props;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Display bubble shape image.
 * <p/>
 * Attr: ref unic.props.R.styleable#BubbleImageAttr_radius
 * Attr: ref unic.props.R.styleable#BubbleImageAttr_orientation
 * Attr: ref unic.props.R.styleable#BubbleImageAttr_vertex_x_dis
 * Attr: ref unic.props.R.styleable#BubbleImageAttr_vertex_y_dis
 * Attr: ref unic.props.R.styleable#BubbleImageAttr_heline_length
 * Attr: ref unic.props.R.styleable#BubbleImageAttr_max_dimen
 * Attr: ref unic.props.R.styleable#BubbleImageAttr_min_dimen
 * <p/>
 */
public class BubbleImageView extends ImageView {

    private Bitmap mSrcBitmap = null;

    private static final String TAG = BubbleImageView.class.getSimpleName();


    public static final int RIGHT = 0;
    public static final int LEFT = 1;

    /**
     * The distance between the vertex and image.
     */
    private float mVertexX = 30f, mVertexY = 50f;

    /**
     * The radius of round corners.
     */
    private float mRadius = 12f;


    /**
     * The max/min dimension of this image.
     */
    private float mMaxDimension = 360f, mMinDimension = 180f;

    /**
     * the hemline length of the triangle
     */
    private float mHemlineLength = 27f;

    /**
     * the direction.
     */
    private int mOrientationMode = LEFT;

    private Paint mPaint;

    private Path mPath;

    public BubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        setImageDrawable();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BubbleImageAttr);
        mRadius = a.getDimension(R.styleable.BubbleImageAttr_radius, 12f);
        mVertexX = a.getDimension(R.styleable.BubbleImageAttr_vertex_x_dis, 30f);
        mVertexY = a.getDimension(R.styleable.BubbleImageAttr_vertex_y_dis, 50f);
        mHemlineLength = a.getDimension(R.styleable.BubbleImageAttr_heline_length, 27f);
        mMaxDimension = a.getDimension(R.styleable.BubbleImageAttr_max_dimen, 360f);
        mMinDimension = a.getDimension(R.styleable.BubbleImageAttr_min_dimen, 180f);
        a.recycle();
    }


    @Override
    public void setImageBitmap(Bitmap bm) {
        mSrcBitmap = makeBubbleBitmap(bm);
        super.setImageBitmap(mSrcBitmap);
//        recycleBitmap(bm);
    }

    @Override
    public void setImageResource(int resId) {

        Drawable drawable = getResources().getDrawable(resId);
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            Log.w(TAG, "Cannot support this drawable:" + (null == drawable ? "nil" : drawable.getClass()));
            return;
        }

        setImageBitmap(bitmap);

    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        setImageDrawable();
    }

    public BubbleImageView(Context context) {
        super(context);
        setImageDrawable();

    }


    private void setImageDrawable() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        BitmapDrawable d = (BitmapDrawable) getDrawable();
        if (null == d) {
            Log.i(TAG, "Not set drawable");
            return;
        }

        Bitmap original = d.getBitmap();
        if (null == original || original.isRecycled()) {
            Log.i(TAG, "Bitmap is null or recycled");
            return;
        }

        mSrcBitmap = makeBubbleBitmap(original);
        Drawable drawable = new BitmapDrawable(getResources(), mSrcBitmap);
        super.setImageDrawable(drawable);
//        recycleBitmap(original);
    }


    /**
     * Get the proper showing size of this picture.
     *
     * @param targetWidth
     * @param targetHeight
     * @return int[0] is width, int[1] is height.
     */
    private int[] getProperSize(int targetWidth, int targetHeight) {

        if (targetWidth > targetHeight) {
            if (targetWidth > mMaxDimension) {
                int temp = targetWidth;
                targetWidth = (int) mMaxDimension;
                targetHeight = targetHeight * targetWidth / temp;
            } else if (targetWidth < mMinDimension) {
                int temp = targetWidth;
                targetWidth = (int) mMinDimension;
                targetHeight = targetHeight * targetWidth / temp;
            }
        } else {
            if (targetHeight > mMaxDimension) {
                int temp = targetHeight;
                targetHeight = (int) mMaxDimension;
                targetWidth = targetWidth * targetHeight / temp;
            } else if (targetHeight < mMinDimension) {
                int temp = targetHeight;
                targetHeight = (int) mMinDimension;
                targetWidth = targetWidth * targetHeight / temp;
            }
        }

        return new int[]{targetWidth, targetHeight};
    }

    private Bitmap makeBubbleBitmap(Bitmap original) {
        int[] size = getProperSize(original.getWidth(), original.getHeight());

        int targetWidth = size[0];
        int targetHeight = size[1];

        if (mOrientationMode == LEFT) {
            drawLeftPath(targetWidth, targetHeight);
        } else if (mOrientationMode == RIGHT) {
            drawRightPath(targetWidth, targetHeight);
        } else {
            throw new IllegalArgumentException("OrientationMode is illegal.");
        }
        return makeBubbleBitmap(original, targetWidth, targetHeight);
    }


    /**
     * Set the direction of the triangle.
     *
     * @param orientation Pass {@link #LEFT} or {@link #RIGHT}. Default
     *                    value is {@link #LEFT}.
     * @attr ref unic.props.R.styleable#BubbleImageAttr_orientation
     */
    public void setOrientation(int orientation) {
        if (mOrientationMode != orientation) {
            mOrientationMode = orientation;
        }
    }

    /**
     * Release the image bitmap.
     */
    public void release() {
        recycleBitmap(mSrcBitmap);
    }


    /**
     * Recycle the bitmap.
     *
     * @param recycle
     */
    private void recycleBitmap(Bitmap recycle) {
        if (null != recycle && !recycle.isRecycled()) {
            recycle.recycle();
            recycle = null;
        }
    }

    private Bitmap makeBubbleBitmap(Bitmap original, int width, int height) {

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, width, height, false);
        Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dest);
        canvas.clipPath(mPath); // clip the image.
        canvas.drawBitmap(scaledBitmap, 0, 0, mPaint);
        recycleBitmap(scaledBitmap);

        return dest;

    }

    private void drawRightPath(int targetWidth, int targetHeight) {
        mPath = new Path();

        float halfHemline = mHemlineLength / 2f;


        // Draw the triangle.
        mPath.moveTo(targetWidth - mVertexX, mVertexY + halfHemline);
        mPath.lineTo(targetWidth, mVertexY);
        mPath.lineTo(targetWidth - mVertexX, mVertexY - halfHemline);
        // End

        mPath.lineTo(targetWidth - mVertexX, mRadius);

        // Get the dimater of the arc.
        float diameter = mRadius * 2;

        RectF arc = new RectF();

        // The right top round corner.
        arc.left = targetWidth - mVertexX - diameter;
        arc.top = 0;
        arc.right = targetWidth - mVertexX;
        arc.bottom = diameter;
        mPath.arcTo(arc, 0, -90);

        mPath.lineTo(mRadius, 0);


        // The left top round corner.
        arc.left = 0;
        arc.top = 0;
        arc.right = diameter;
        arc.bottom = diameter;
        mPath.arcTo(arc, 270, -90);


        mPath.lineTo(0, targetHeight - mRadius);

        // The left bottom round corner.
        arc.left = 0;
        arc.top = targetHeight - diameter;
        arc.right = diameter;
        arc.bottom = targetHeight;
        mPath.arcTo(arc, 180, -90);

        mPath.lineTo(targetWidth - mRadius, targetHeight);

        // The right bottom round corner.
        arc.left = targetWidth - mVertexX - diameter;
        arc.top = targetHeight - diameter;
        arc.right = targetWidth - mVertexX;
        arc.bottom = targetHeight;
        mPath.arcTo(arc, 90, -90);

        mPath.close();
    }

    private void drawLeftPath(int targetWidth, int targetHeight) {
        mPath = new Path();

        float halfHemline = mHemlineLength / 2f;

        // Draw the triangle.
        mPath.moveTo(mVertexX, mVertexY + halfHemline);
        mPath.lineTo(0, mVertexY);
        mPath.lineTo(mVertexX, mVertexY - halfHemline);
        // End

        mPath.lineTo(mVertexX, mRadius);

        //
        float diameter = mRadius * 2;

        RectF arc = new RectF();

        // The left top round corner.
        arc.left = mVertexX;
        arc.top = 0;
        arc.right = mVertexX + diameter;
        arc.bottom = diameter;
        mPath.arcTo(arc, 180, 90);


        mPath.lineTo(targetWidth - mRadius, 0);

        // The right top round corner.
        arc.left = targetWidth - diameter;
        arc.top = 0;
        arc.right = targetWidth;
        arc.bottom = diameter;
        mPath.arcTo(arc, 270, 90);


        mPath.lineTo(targetWidth, targetHeight - mRadius);

        // The right bottom round corner.
        arc.left = targetWidth - diameter;
        arc.top = targetHeight - diameter;
        arc.right = targetWidth;
        arc.bottom = targetHeight;
        mPath.arcTo(arc, 0, 90);

        mPath.lineTo(mVertexX + mRadius, targetHeight);

        // The left bottom round corner.
        arc.left = mVertexX;
        arc.top = targetHeight - diameter;
        arc.right = mVertexX + diameter;
        arc.bottom = targetHeight;
        mPath.arcTo(arc, 90, 90);

        mPath.close();
    }

}
