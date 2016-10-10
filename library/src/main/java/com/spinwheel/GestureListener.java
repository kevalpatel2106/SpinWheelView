package com.spinwheel;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Keval on 10-Oct-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;

    private SwipeListener mSwipeListener;
    private int mCenterX;

    GestureListener(SwipeListener listener, int centerX) {
        mSwipeListener = listener;
        mCenterX = centerX;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffY) > SWIPE_THRESHOLD) {
                if (diffY > 0) {
                    mSwipeListener.onSwipeBottom((e2.getX() < mCenterX ? -1 : 1) * velocityY);
                } else {
                    mSwipeListener.onSwipeTop((e2.getX() < mCenterX ? -1 : 1) * velocityX);
                }
            }
            result = true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
}

