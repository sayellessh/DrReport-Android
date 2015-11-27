package net.swaas.drinfo.core;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by SwaaS on 6/11/2015.
 */
public class TouchExtendedClickListener implements View.OnTouchListener {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view);
    }

    GestureDetector mGestureDetector;

    public TouchExtendedClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (v != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(v);
            return false;
        }
        return false;
    }
}
