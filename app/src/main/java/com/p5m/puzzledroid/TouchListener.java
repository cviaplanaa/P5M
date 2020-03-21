package com.p5m.puzzledroid;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import timber.log.Timber;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class TouchListener implements View.OnTouchListener {
    private float xDelta;
    private float yDelta;
    private PuzzleController activity;

    public TouchListener(PuzzleController activity) {
        Timber.i("TouchListener");
        this.activity = activity;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Timber.i("onTouch");
        float x = motionEvent.getRawX();
        float y = motionEvent.getRawY();
        final double tolerance = sqrt(pow(view.getWidth(), 2) + pow(view.getHeight(), 2)) / 10;

        PieceController piece = (PieceController) view;
        if (!piece.movable) {
            return true;
        }

        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xDelta = x - lParams.leftMargin;
                yDelta = y - lParams.topMargin;
                piece.bringToFront();
                break;
            case MotionEvent.ACTION_MOVE:
                lParams.leftMargin = (int) (x - xDelta);
                lParams.topMargin = (int) (y - yDelta);
                view.setLayoutParams(lParams);
                break;
            case MotionEvent.ACTION_UP:
                int xDiff = StrictMath.abs(piece.x - lParams.leftMargin);
                int yDiff = StrictMath.abs(piece.y - lParams.topMargin);
                if (xDiff <= tolerance && yDiff <= tolerance) {
                    lParams.leftMargin = piece.x;
                    lParams.topMargin = piece.y;
                    piece.setLayoutParams(lParams);
                    piece.movable = false;
                    sendViewToBack(piece);
                    activity.checkEnd();
                }
                break;
        }

        return true;
    }

    public void sendViewToBack(final View child) {
        Timber.i("sendViewToBack");
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }
}