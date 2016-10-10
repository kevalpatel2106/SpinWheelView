package com.spinwheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Keval on 10-Oct-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class WheelView extends View {
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

    private int mViewHeight;
    private int mViewWidth;

    private Paint mCirclePaint;
    private Paint mCenterCirclePaint;
    private TextPaint mCenterTextPaint;

    private float mUnitAngle;

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
        mUnitAngle = 360 / mNoOfPossibilities;
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

    /**
     * @return Number of possibilities of the outcome.
     */
    public int getNoOfPossibilities() {
        return mNoOfPossibilities;
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

        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int circleRadius = Math.min(mViewWidth / 2, mViewHeight / 2) - Defaults.DEF_CIRCLE_PADDING; //Radius of the outer circle
        int centerX = mViewHeight / 2;  //Center of the view
        int centerY = mViewWidth / 2;   //Center of the view

        //Draw the circle
        canvas.drawCircle(centerX,          //Center of the view
                centerY,                    //Center of the view
                circleRadius,               //Circle radius
                mCirclePaint);              //Paint

        //draw lines
        float newAngle = 0;
        float startX;
        float startY;
        for (int i = 0; i < mNoOfPossibilities; i++) {
            newAngle = (float) (i * mUnitAngle * Defaults.DEGREE_TO_RADIAN);    //calculate new angle

            //calculate the line starting points
            startX = (float) (centerX + Math.cos(newAngle) * circleRadius);     //x = radius * cos(angle) + centerX
            startY = (float) (centerY + Math.sin(newAngle) * circleRadius);     //y = radius * sin(angle) + centerY

            canvas.drawLine(startX,
                    startY,
                    centerX,        //End point is center of the circle
                    centerY,        //End point is center of the circle
                    mCirclePaint);
        }

        //Draw the center circle
        canvas.drawCircle(centerX,              //Center of the view
                centerY,                        //Center of the view
                (float) (circleRadius - (circleRadius / 1.5)),     //Radius of the inner center circle
                mCenterCirclePaint);             //Paint

        canvas.drawText(mCenterText,
                centerX,
                centerY - ((mCenterTextPaint.descent() + mCenterTextPaint.ascent()) / 2),
                mCenterTextPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewHeight = MeasureSpec.getSize(widthMeasureSpec);
        mViewWidth = MeasureSpec.getSize(heightMeasureSpec);

        this.setMeasuredDimension(mViewWidth, mViewHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
