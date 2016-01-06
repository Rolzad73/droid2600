package com.tvi910.android.core;

import android.app.Activity;

import android.content.Context;

import android.graphics.*;
import android.graphics.Paint.Style;

import android.util.FloatMath;

import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;

import android.widget.AbsoluteLayout;

import android.util.Log;

/**
 * Draws an on-screen D-pad.
 *
 * @author Trent McNair
 * @since August 2010
 */
class DPadView extends View {

    /**
     * Force a layout change to this view
     */
    public void updateLayout(int x, int y, int width, int height) {
        setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x, y));
        requestLayout();
    }

    public DPadView(Context context) {
        super(context);
    }

    @Override protected void onDraw(Canvas canvas) {
        final RectF arcRect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        final Paint dimPaint = new Paint();
        dimPaint.setARGB(96, 255,255,255);
        dimPaint.setAntiAlias(true);
        canvas.drawArc(arcRect, 330, 60, false, dimPaint);
        canvas.drawArc(arcRect, 60, 60, false, dimPaint);
        canvas.drawArc(arcRect, 150, 60, false, dimPaint);
        canvas.drawArc(arcRect, 240, 60, false, dimPaint);
    }
}
