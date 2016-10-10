package com.spinwheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Keval on 10-Oct-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class WheelView extends View {
    private static final int DEF_NO_OF_POSSIBILITIES = 8;
    private static final int MIN_NO_OF_POSSIBILITIES = 8;
    private static final int MAX_NO_OF_POSSIBILITIES = 8;
    private static final int MAX_CENTER_TEXT_LENGTH = 8;
    private static final String DEF_CENTER_TEXT = "Spin";


    private Context mContext;
    private int mNoOfPossibilities;
    private String mCenterText;

    public WheelView(Context context) {
        super(context);
        mContext = context;
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WheelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SpinWheel,
                0, 0);

        try {
            setNoOfPossibilities(a.getInt(R.styleable.SpinWheel_noOfPossibilities, DEF_NO_OF_POSSIBILITIES));
            setCenterText(a.getString(R.styleable.SpinWheel_centerText));
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

    public void setNoOfPossibilities(int noOfPossibilities) {
        if (noOfPossibilities < MIN_NO_OF_POSSIBILITIES || noOfPossibilities > MAX_NO_OF_POSSIBILITIES) {
            throw new RuntimeException("Number of possibilities must be between 2 to 10.");
        }
        mNoOfPossibilities = noOfPossibilities;

        invalidate();
        requestLayout();
    }

    public String getCenterText() {
        return mCenterText;
    }

    public void setCenterText(@Nullable String centerText) {
        if (centerText == null || centerText.isEmpty()) {
            mCenterText = DEF_CENTER_TEXT;
        } else if (centerText.length() > MAX_CENTER_TEXT_LENGTH) {
            throw new RuntimeException("Center text length must be less than 8 character.");
        } else {
            mCenterText = centerText;
        }

        invalidate();
        requestLayout();
    }


}
