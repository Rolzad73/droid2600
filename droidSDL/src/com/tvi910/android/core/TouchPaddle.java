package com.tvi910.android.core;

import android.app.Activity;

import android.content.Context;

import android.graphics.*;
import android.graphics.Paint.Style;

import android.util.FloatMath;

import android.widget.ImageView;

import android.os.Bundle;

import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import android.widget.AbsoluteLayout;

import android.util.Log;

import com.tvi910.android.sdl.SDLInterface;

import com.tvi910.android.sdl.SDLKeysym;

import com.tvi910.android.R;

/**
 * This class implements an on-screen virtual paddle controller. 
 *
 * @author Trent McNair
 * @since August 2010
 */
public class TouchPaddle extends VirtualController {
	
    private static final int INVALID_POINTER_ID = -1;

    // Pointer currently associated with the paddle
    private int _paddlePointer = INVALID_POINTER_ID;

    // Pointer currently acting on the fire button
    private int _triggerPointer = INVALID_POINTER_ID;

    private int _lastX = 0;
    private int _lastY = 0;

    private int _verticalScreenCenter;

    public TouchPaddle(Context context, int screenHeight) {
        super(context);
        _verticalScreenCenter = screenHeight/2;
    }

    private boolean downAction(MotionEvent ev, int pointerId, float x, float y) {
        // if nothing has claimed the touch area
        if (y < _verticalScreenCenter) {
            if (_triggerPointer == INVALID_POINTER_ID) {
                _triggerPointer = pointerId;
                SDLInterface.triggerOn();
                return true;
            }
            else {
                return false;
            }
        }
        else {
            if (_paddlePointer == INVALID_POINTER_ID) {
                _paddlePointer = pointerId;
                handleMove(ev, 0);
                return true;
            }
            else {
                return false;
            }
        }
    }

    private boolean upAction(MotionEvent ev, int pointerId) {
        if (_paddlePointer == pointerId) {
            // release the pointer
            handleMove(ev, 1);
            _paddlePointer = INVALID_POINTER_ID;
            return true;
        }
        else if (_triggerPointer == pointerId) {
            SDLInterface.triggerOff();
            _triggerPointer =  INVALID_POINTER_ID;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {

        final int action = event.getAction();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                return downAction(event, event.getPointerId(0), event.getX(), event.getY());
            }
            case MotionEvent.ACTION_MOVE: {
                if (_paddlePointer != INVALID_POINTER_ID) {
                    handleMove(event, 2);
                }
                else {
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                return upAction(event, event.getPointerId(0));
            }
            case MotionEvent.ACTION_CANCEL: {
                if (_paddlePointer == event.getPointerId(0)) {
                    _paddlePointer =  INVALID_POINTER_ID;
                }
                else if (_triggerPointer == event.getPointerId(0)) {
                    SDLInterface.triggerOff();
                    _triggerPointer =  INVALID_POINTER_ID;
                }
                else {
                    return false;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                // ignore if dpad and fire pointers are already assigned
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                return downAction(event, event.getPointerId(pointerIndex),
                    event.getX(pointerIndex),
                    event.getY(pointerIndex));
            }
            case MotionEvent.ACTION_POINTER_UP: {
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                return upAction(event, pointerId);
            }
        }

        return true;
    }

    private void handleMove(MotionEvent event, int action) {
        
        final int pointerIndex = event.findPointerIndex(_paddlePointer);
        final int xpos = (int)event.getX(pointerIndex);
        final int ypos = (int)event.getY(pointerIndex);
        if (xpos != _lastX || ypos != _lastY || action < 2) {
            SDLInterface.nativeMouse(xpos, ypos, action, 0, 0, 0);
            _lastX = xpos;
            _lastY = ypos;
        }
    }

    @Override
    public void privActivate() {
    }

    @Override 
    public void privDeactivate() {
    }
}
