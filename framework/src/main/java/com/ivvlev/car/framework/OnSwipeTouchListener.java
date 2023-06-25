package com.ivvlev.car.framework;


import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {
    private static final boolean DEBUG = true;
    protected final String LOG_TAG = getClass().getCanonicalName();

    private GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    private View mView = null;

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        mView = view;
        try {

            if (DEBUG) {
                String msg = "0";
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        msg = "ACTION_UP";
                        break;
                    case MotionEvent.ACTION_DOWN:
                        msg = "ACTION_DOWN";
                        break;
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        msg = "ACTION_BUTTON_PRESS";
                        break;
                    case MotionEvent.ACTION_BUTTON_RELEASE:
                        msg = "ACTION_BUTTON_RELEASE";
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        msg = "ACTION_CANCEL";
                        break;
                    case MotionEvent.ACTION_HOVER_ENTER:
                        msg = "ACTION_HOVER_ENTER";
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        msg = "ACTION_HOVER_EXIT";
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                        msg = "ACTION_HOVER_MOVE";
                        break;
                    case MotionEvent.ACTION_MASK:
                        msg = "ACTION_MASK";
                        break;
                    case MotionEvent.ACTION_MOVE:
                        msg = "ACTION_MOVE";
                        break;
                    default:
                        msg = Integer.toString(motionEvent.getAction());
                }
                Log.d(LOG_TAG, msg);
            }
            return gestureDetector.onTouchEvent(motionEvent);
        } finally {
            mView = null;
        }
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return OnSwipeTouchListener.this.onDown(mView);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return onClick(mView);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return onDoubleClick(mView);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onLongClick(mView);
        }

        // Determines the fling velocity and then fires the appropriate swipe event accordingly
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            return onSwipeRight(mView);
                        } else {
                            return onSwipeLeft(mView);
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            return onSwipeDown(mView);
                        } else {
                            return onSwipeUp(mView);
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public boolean onSwipeRight(final View view) {
        return false;
    }

    public boolean onSwipeLeft(final View view) {
        return false;
    }

    public boolean onSwipeUp(final View view) {
        return false;
    }

    public boolean onSwipeDown(final View view) {
        return false;
    }

    public boolean onDown(final View view) {
        return false;
    }

    public boolean onClick(final View view) {
        return false;
    }

    public boolean onDoubleClick(final View view) {
        return false;
    }

    public boolean onLongClick(final View view) {
        return false;
    }

}
