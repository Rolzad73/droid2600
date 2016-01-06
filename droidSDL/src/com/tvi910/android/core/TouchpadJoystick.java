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
 * Widget represents an on screen dpad (joystick for you old-timers). This
 * provides the following on-screen controls:
 *     1) a 4 position dpad. Diagonals are represented by turning on adjacent
 *        dpad position.s
 *     2) a single fire button.
 *
 * @author Trent McNair
 * @since August 2010
 */
public class TouchpadJoystick extends VirtualController {

    public final static int SMALLEST = 110;
    public final static int SMALL = 150;
    public final static int MEDIUM = 190;
    public final static int LARGE = 230;

    private Context _context;
    private DPadView _dpadView = null;

    // set the deadzone
    private static final int DEADZONE = 2;
    private float _posDeadzone = DEADZONE;
    private float _negDeadzone = DEADZONE * -1;

    private static final int PADDING = 15;

    private int _screenDivide;
    private int _verticalDivide;
    private int _buttonsTop;
    private int _buttonsLeft;
    private int _buttonsRight;

    // joystick positions - set up for bit operations.
    public static final int POS_CENTER = 0;
    public static final int POS_E = 1;
    public static final int POS_S = 2;
    public static final int POS_W = 4;
    public static final int POS_N = 8;

    private int _position = POS_CENTER;
    private int _newPosition = 0;

    // xOrigin and yOrigin define 0,0 virtual cartesian coordinates.
    private float xOrigin = 0;
    private float yOrigin = 0;

    private float relx, rely;

    private static final int INVALID_POINTER_ID = -1;

    // Pointer currently acting on the dpad
    private int _dpadPointer = INVALID_POINTER_ID;
    // Pointer currently acting on the fire button
    private int _firePointer = INVALID_POINTER_ID;

    private boolean _buttonsOnLeft;

    // defines whether or not this has two distinct trigger buttons.
    // The trigger area will be split between the top and bottom parts of the
    // screen.
    private boolean _twoTriggers;

    public TouchpadJoystick(
            Context context,
            boolean layoutOnTop,
            boolean buttonsOnLeft,
            int screenWidth,
            int screenHeight,
            int dpadSize) {

        super(context);

        final int dpadRadius = dpadSize/2;
        _screenDivide = screenWidth/2;
        _buttonsOnLeft = buttonsOnLeft;
        _verticalDivide = screenHeight/2;
        // console button geometry
        _buttonsTop = screenHeight-44;
        _buttonsLeft = (screenWidth/2) - 104;
        _buttonsRight = (screenWidth/2) + 104;

        _context = context;
        _dpadView = new DPadView(context);

        _twoTriggers = false;

        if (layoutOnTop) {
            yOrigin = PADDING + dpadRadius;
            if (buttonsOnLeft) {
                _dpadView.updateLayout(screenWidth - dpadSize - PADDING, PADDING, dpadSize, dpadSize);
                xOrigin = screenWidth - PADDING - dpadRadius;
            }
            else {
                _dpadView.updateLayout(PADDING,PADDING,dpadSize,dpadSize);
                xOrigin = PADDING + dpadRadius;
            }
        }
        else {
            yOrigin = screenHeight - PADDING - dpadRadius;
            if (buttonsOnLeft) {
                _dpadView.updateLayout(screenWidth - dpadSize - PADDING, screenHeight - dpadSize - PADDING, dpadSize, dpadSize);
                xOrigin = screenWidth - PADDING - dpadRadius;
            }
            else {
                _dpadView.updateLayout(PADDING, screenHeight - dpadSize - PADDING, dpadSize, dpadSize);
                xOrigin = PADDING + dpadRadius;
            }
        }
    }

    public void reconfigure(
            boolean layoutOnTop,
            boolean buttonsOnLeft,
            int screenWidth,
            int screenHeight,
            int dpadSize) {

        final int dpadRadius = dpadSize/2;
        _screenDivide = screenWidth/2;
        _buttonsOnLeft = buttonsOnLeft;
        // console button geometry
        _buttonsTop = screenHeight-44;
        _buttonsLeft = (screenWidth/2) - 52;
        _buttonsRight = (screenWidth/2) + 52;

        if (layoutOnTop) {
            yOrigin = PADDING + dpadRadius;
            if (buttonsOnLeft) {
                _dpadView.updateLayout(screenWidth - dpadSize - PADDING, PADDING, dpadSize, dpadSize);
                xOrigin = screenWidth - PADDING - dpadRadius;
            }
            else {
                _dpadView.updateLayout(PADDING,PADDING,dpadSize,dpadSize);
                xOrigin = PADDING + dpadRadius;
            }
        }
        else {
            yOrigin = screenHeight - PADDING - dpadRadius;
            if (buttonsOnLeft) {
                _dpadView.updateLayout(screenWidth - dpadSize - PADDING, screenHeight - dpadSize - PADDING, dpadSize, dpadSize);
                xOrigin = screenWidth - PADDING - dpadRadius;
            }
            else {
                _dpadView.updateLayout(PADDING, screenHeight - dpadSize - PADDING, dpadSize, dpadSize);
                xOrigin = PADDING + dpadRadius;
            }
        }
    }

    public DPadView getDPadView() {
        return _dpadView;
    }

    private void changePosition() {

        if ((_position & POS_N) != (_newPosition & POS_N)) {
            if ((_newPosition & POS_N) == 0) {
                SDLInterface.upOff();
            }
            else {
                SDLInterface.upOn();
            }
        }
        if ((_position & POS_S) != (_newPosition & POS_S)) {
            if ((_newPosition & POS_S) == 0) {
                SDLInterface.downOff();
            }
            else {
                SDLInterface.downOn();
            }
        }
        if ((_position & POS_E) != (_newPosition & POS_E)) {
            if ((_newPosition & POS_E) == 0) {
                SDLInterface.rightOff();
            }
            else {
                SDLInterface.rightOn();
            }
        }
        if ((_position & POS_W) != (_newPosition & POS_W)) {
            if ((_newPosition & POS_W) == 0) {
                SDLInterface.leftOff();
            }
            else {
                SDLInterface.leftOn();
            }
        }

        _position = _newPosition;
    }

    private boolean downAction(MotionEvent ev, int pointerId, float x, float y) {
        if (_buttonsOnLeft  ? x < _screenDivide : x > _screenDivide ) {
            // if the trigger area isn't currently claimed
            if (_firePointer == INVALID_POINTER_ID) {
                _firePointer = pointerId;
                SDLInterface.triggerOn();
                return true;
            }
        }
        else if (_dpadPointer == INVALID_POINTER_ID) {
            // if the _dpadPointer isn't currently claimed...
            _dpadPointer = pointerId;
            handleMove(ev);
            return true;
        }

        return false;
    }

    private boolean upAction(MotionEvent ev, int pointerId) {
        if (_dpadPointer == pointerId) {
            if (_position != POS_CENTER) {
                _newPosition = POS_CENTER;
                changePosition();
            }
            _dpadPointer =  INVALID_POINTER_ID;
            return true;
        }
        else if (_firePointer == pointerId) {
            SDLInterface.triggerOff();
            _firePointer =  INVALID_POINTER_ID;
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
                if (_dpadPointer != INVALID_POINTER_ID) {
                    handleMove(event);
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
                if (_dpadPointer == event.getPointerId(0)) {
                // this also means no active fingers on the screen
                    if (_position != POS_CENTER) {
                        _newPosition = POS_CENTER;
                        changePosition();
                    }
                    _dpadPointer =  INVALID_POINTER_ID;
                }
                else if (_firePointer == event.getPointerId(0)) {
                    SDLInterface.triggerOff();
                    _firePointer =  INVALID_POINTER_ID;
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

    private void handleMove(MotionEvent event) {

        // Find the index of the active pointer and fetch its position
        final int pointerIndex = event.findPointerIndex(_dpadPointer);
        relx = event.getX(pointerIndex) - xOrigin;
        rely = event.getY(pointerIndex) - yOrigin;

        _newPosition = 0;

        if (relx > _posDeadzone ) { // E
            if (rely < _negDeadzone) { // NE
                rely = Math.abs(rely);
                if (relx > (rely * 3 / 4)) _newPosition = (_newPosition | POS_E);
                if (rely > (relx * 3 / 4)) _newPosition = (_newPosition | POS_N);
            }
            else if (rely > _posDeadzone) { // SE
                if (relx > (rely * 3 / 4)) _newPosition = (_newPosition | POS_E);
                if (rely > (relx * 3 / 4)) _newPosition = (_newPosition | POS_S);
            }
        }
        else if (relx < _negDeadzone) { // W
            relx = Math.abs(relx);
            if (rely > _posDeadzone) { // SW
                if (relx > (rely * 3 / 4)) _newPosition = (_newPosition | POS_W);
                if (rely > (relx * 3 / 4)) _newPosition = (_newPosition | POS_S);
            }
            else if (rely < _negDeadzone) { // NW
                rely = Math.abs(rely);
                if (relx > (rely * 3 / 4)) _newPosition = (_newPosition | POS_W);
                if (rely > (relx * 3 / 4)) _newPosition = (_newPosition | POS_N);
            }
        }

        if (_newPosition != _position) {
            changePosition();
        }
    }

    public void addToAbsoluteLayout(AbsoluteLayout al, Display display) {
        al.addView(getDPadView());
    }

    public void setVisibility(int visibility) {
        getDPadView().setVisibility(visibility);
    }

    public void privActivate() {
        setVisibility(0);
    }

    public void privDeactivate() {
        setVisibility(4);
    }
    
}
