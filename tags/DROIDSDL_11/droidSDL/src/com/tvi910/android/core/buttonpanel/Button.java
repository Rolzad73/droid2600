package com.tvi910.android.core.buttonpanel;

import android.graphics.Paint;
import android.graphics.Rect;

import android.util.Log;

/**
 * Class representing a button to be used with ButtonPanel. Immutable. 
 */
class Button {

    private ButtonCallback _callback;
    private Paint _bgPaint;
    private Paint _strokePaint;
    private Paint _textPaint;
    private Rect _rect;
    private String _text;
    private int _fontX;
    private int _fontY;
    private Button _toggle;
    private boolean _hasToggle;
    private int _colspan;

    /**
     * @param bgcolor a color in argb format (see android.graphics.Color) 
     * @param fgcolor a color in argb format (see android.graphics.Color)
     * @param colspan the number of columns to the right that this button extends.
     */
    Button(
            ButtonCallback c, 
            Paint bgPaint, 
            Paint strokePaint, 
            Paint textPaint, 
            String text, 
            int fontX, 
            int fontY, 
            Rect rect,
            int colspan) {
        _callback = c;
        _bgPaint = bgPaint;
        _strokePaint = strokePaint;
        _textPaint = textPaint;
        _rect = rect;
        _text = text;
        _fontX = fontX;
        _fontY = fontY;
        _toggle = this;
        _hasToggle = false;
        _colspan = colspan;
    }

    int getColspan() {
        return _colspan;
    }

    void setToggle(Button toggle) {
        toggle._toggle = this;
        _toggle = toggle;
        _hasToggle = true;
// Log.v("Button", "_toggle=" + _toggle + ", getToggle()=" + getToggle() + ", toggle=" + toggle);  
    }

    Button getToggle() {
        return _toggle;
    }

    boolean getHasToggle() {
        return _hasToggle;
    }

    ButtonCallback getCallback() {
        return _callback;
    }

    void onButtonUp() {
        _callback.onButtonUp();
    }

    Paint getBgPaint() {
        return _bgPaint;
    }

    Paint getStrokePaint() {
        return _strokePaint;
    }

    Paint getTextPaint() {
        return _textPaint;
    }

    int getX() { return _rect.left; }
    int getY() { return _rect.top; }

    Rect getRect() {
        return _rect;
    }   

    String getText() {
        return _text;
    }

    int getFontX() {
        return _fontX;
    }

    int getFontY() {
        return _fontY;
    }
}
