package com.spinwheel;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Keval on 10-Oct-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class WheelView extends View implements SwipeListener {
    private Context mContext;

    private String[] mPossibilitiesName;
    private int mNoOfPossibilities = 8;


    private String mCenterText = Defaults.DEF_CENTER_TEXT;
    @ColorInt
    private int mCenterCircleColor = Defaults.DEF_CIRCLE_COLOR;


    private int mCircleStrokeWidth = Defaults.DEF_CIRCLE_STROKE_WIDTH;
    @ColorInt
    private int mCircleColor = Defaults.DEF_CIRCLE_COLOR;


    private float mCenterTextSize = Defaults.DEF_CENTER_TEXT_SIZE;
    @ColorInt
    private int mCenterTextColor = Defaults.DEF_CENTER_TEXT_COLOR;


    private Paint mCirclePaint;
    private Paint mCenterCirclePaint;
    private TextPaint mCenterTextPaint;
    private TextPaint mPossibilitiesTextPaint;


    private float mUnitAngle;
    private int mCircleRadius;                      //Radius of the outer circle
    private int mCenterX;                           //Center of the view
    private int mCenterY;                           //Center of the view
    private GestureDetector mGestureDetector;
    @Nullable
    private SpinWheelListener mSpinWheelListener;
    private boolean isRotating = false;


    public WheelView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WheelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init(attrs);
    }

    private void init() {
        mGestureDetector = new GestureDetector(mContext, new GestureListener(this, DensityUtils.getScreenWidth(mContext) / 2));
    }

    private void init(AttributeSet attrs) {
        init();

        TypedArray a = mContext.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SpinWheel,
                0, 0);

        try {
            setCircleColor(a.getColor(R.styleable.SpinWheel_circleColor, Defaults.DEF_CIRCLE_COLOR));
            setCircleStrokeWidth(a.getDimensionPixelSize(R.styleable.SpinWheel_circleStroke, Defaults.DEF_CIRCLE_STROKE_WIDTH));
            setCenterCircleColor(a.getColor(R.styleable.SpinWheel_centerCircleColor, Defaults.DEF_CIRCLE_COLOR));
            setCenterText(a.getString(R.styleable.SpinWheel_centerText));
            setCenterTextColor(a.getColor(R.styleable.SpinWheel_centerTextColor, Defaults.DEF_CENTER_TEXT_COLOR));
            setCenterTextSize(a.getDimensionPixelSize(R.styleable.SpinWheel_centerTextSize, Defaults.DEF_CENTER_TEXT_SIZE));
        } finally {
            a.recycle();
        }
    }

    @ColorInt
    public int getCircleColor() {
        return mCircleColor;
    }

    public void setCircleColor(@ColorInt int circleColor) {
        mCircleColor = circleColor;
        createCirclePaint();
    }

    public int getCircleStrokeWidth() {
        return mCircleStrokeWidth;
    }

    public void setCircleStrokeWidth(int circleStrokeWidth) {
        mCircleStrokeWidth = circleStrokeWidth;
        createCirclePaint();
    }

    public int getCenterCircleColor() {
        return mCenterCircleColor;
    }

    public void setCenterCircleColor(int centerCircleColor) {
        mCenterCircleColor = centerCircleColor;
        createCirclePaint();
    }

    public String getCenterText() {
        return mCenterText;
    }

    public void setCenterText(@Nullable String centerText) {
        if (centerText == null || centerText.isEmpty()) {
            mCenterText = Defaults.DEF_CENTER_TEXT;
        } else if (centerText.length() > Defaults.MAX_CENTER_TEXT_LENGTH) {
            throw new RuntimeException("Center text length must be less than 8 character.");
        } else {
            mCenterText = centerText;
        }

        createCirclePaint();
    }

    public int getCenterTextColor() {
        return mCenterTextColor;
    }

    public void setCenterTextColor(int centerTextColor) {
        mCenterTextColor = centerTextColor;
        createCirclePaint();
    }

    public float getCenterTextSize() {
        return mCenterTextSize;
    }

    public void setCenterTextSize(float centerTextSize) {
        mCenterTextSize = centerTextSize;
        createCirclePaint();
    }

    public void setSpinWheelListener(@Nullable SpinWheelListener spinWheelListener) {
        mSpinWheelListener = spinWheelListener;
    }

    public String[] getPossibilitiesName() {
        return mPossibilitiesName;
    }

    public void setPossibleOutcomeName(String[] possibilitiesName) {
        if (possibilitiesName.length < Defaults.MIN_NO_OF_POSSIBILITIES) {
            throw new IllegalArgumentException("Number of possibilities cannot be less than 2.");
        } else if (possibilitiesName.length > Defaults.MAX_NO_OF_POSSIBILITIES) {
            throw new IllegalArgumentException("Number of possibilities cannot be more than 12.");
        }

        mPossibilitiesName = possibilitiesName;
        mNoOfPossibilities = mPossibilitiesName.length;
        mUnitAngle = 360 / mNoOfPossibilities;

        invalidate();
        requestLayout();
    }

    private void createCirclePaint() {
        //Surrounding circle paint object
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        //center circle paint object
        mCenterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterCirclePaint.setColor(mCenterCircleColor);
        mCenterCirclePaint.setStyle(Paint.Style.FILL);

        //Set the center text pain
        mCenterTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mCenterTextPaint.setTextSize(mCenterTextSize);
        mCenterTextPaint.setColor(mCenterTextColor);
        mCenterTextPaint.setTextAlign(Paint.Align.CENTER);

        //Set the center text pain
        mPossibilitiesTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPossibilitiesTextPaint.setTextSize(mCenterTextSize);
        mPossibilitiesTextPaint.setColor(Color.parseColor("#FF0000"));
        mPossibilitiesTextPaint.setTextAlign(Paint.Align.CENTER);

        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw the circle
        canvas.drawCircle(mCenterX,          //Center of the view
                mCenterY,                    //Center of the view
                mCircleRadius,               //Circle radius
                mCirclePaint);              //Paint

        float newAngle;
        float startX;
        float startY;
        for (int i = 0; i < mNoOfPossibilities; i++) {
            //*************draw lines**************//
            newAngle = (float) (i * mUnitAngle * Defaults.DEGREE_TO_RADIAN);    //calculate new angle that is in radian.

            //calculate the line starting points
            startX = (float) (mCenterX + Math.cos(newAngle) * mCircleRadius);     //x = radius * cos(angle) + mCenterX
            startY = (float) (mCenterY + Math.sin(newAngle) * mCircleRadius);     //y = radius * sin(angle) + mCenterY

            canvas.drawLine(startX,
                    startY,
                    mCenterX,        //End point is center of the circle
                    mCenterY,        //End point is center of the circle
                    mCirclePaint);

            //*************draw possible outcome text**************//
            //calculate new angle
            newAngle = (float) ((i * mUnitAngle * Defaults.DEGREE_TO_RADIAN) + (mUnitAngle / 2 * Defaults.DEGREE_TO_RADIAN));

            //calculate the line starting points
            startX = (float) (mCenterX + Math.cos(newAngle) * (mCircleRadius - mCircleRadius / 2.8));     //x = radius * cos(angle) + mCenterX
            startY = (float) (mCenterY + Math.sin(newAngle) * (mCircleRadius - mCircleRadius / 2.8));     //y = radius * sin(angle) + mCenterY

            canvas.drawText(mPossibilitiesName[i], startX, startY, mPossibilitiesTextPaint);
        }

        //Draw the center circle
        canvas.drawCircle(mCenterX,              //Center of the view
                mCenterY,                        //Center of the view
                (float) (mCircleRadius - (mCircleRadius / 1.5)),     //Radius of the inner center circle
                mCenterCirclePaint);             //Paint

        canvas.drawText(mCenterText,
                mCenterX,
                mCenterY - ((mCenterTextPaint.descent() + mCenterTextPaint.ascent()) / 2),
                mCenterTextPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mViewHeight = MeasureSpec.getSize(widthMeasureSpec);
        int mViewWidth = MeasureSpec.getSize(heightMeasureSpec);

        mCenterX = mViewHeight / 2;  //Center of the view
        mCenterY = mViewWidth / 2;   //Center of the view

        mCircleRadius = Math.min(mViewWidth / 2, mViewHeight / 2) - Defaults.DEF_CIRCLE_PADDING;    //Radius of the outer circle

        this.setMeasuredDimension(mViewWidth, mViewHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void startSpinning(float velocity) {
        isRotating = true;

        //calculate rotation angle
        float rotationAngle = (velocity / 1000) * 360;
        if (rotationAngle % mUnitAngle == 0)
            rotationAngle += 5;    //Check if the final rotation angle is exactly at the boundary of two region, than add 5 degrees

        //calculate rotation duration
        long duration = (long) (Math.abs(velocity) / 1.5);

        animate().rotation(rotationAngle)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isRotating = true;
                        if (mSpinWheelListener != null) mSpinWheelListener.onRotationStarted();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isRotating = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        isRotating = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    @Override
    public void onSwipeTop(float velocity) {
        if (isRotating) return;
        startSpinning(velocity);
    }

    @Override
    public void onSwipeBottom(float velocity) {
        if (isRotating) return;
        startSpinning(velocity);
    }

    public void reset() {
        setRotation(0);
    }
}

