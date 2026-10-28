package dev.josegaldamez.tickethn_uth.details;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/** Keeps map gestures from being intercepted by the surrounding vertical scroll. */
public final class MapGestureContainer extends FrameLayout {
    public MapGestureContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        boolean handled = super.dispatchTouchEvent(event);
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return handled;
    }
}
