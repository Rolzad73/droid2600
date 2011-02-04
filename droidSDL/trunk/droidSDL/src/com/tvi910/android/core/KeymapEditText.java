package com.tvi910.android.core;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import java.util.Map;

/**
 * Custom EditText, intercepts key presses and converts them to string
 * representations of SDL keycodes.
 */
public class KeymapEditText extends EditText {

    private final String _keyString;
    private final int _keyCode;

    private String _newString;
    private String _oldString;

    // map console button names to sdl keycodes.
    private ConsoleKeys _consoleKeys;

    /**
     * Constructor.
     *
     * @param context @see android.widget.EditText
     * @param attrs @see android.widget.EditText
     * @param key
     */
    public KeymapEditText(
            Context context,
            AttributeSet attrs,
            String key) {
        super(context, attrs);
        _keyString = key;
        _consoleKeys = ConsoleKeys.getInstance();
        _keyCode = _consoleKeys.getCode(key).intValue();
        setInputType(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, final KeyEvent event) {
         return true;
    }

    @Override
    protected void onWindowVisibilityChanged(int val) {
        if (val == View.VISIBLE) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            setText(prefs.getString(_keyString, ""));
            _newString = null;
            _oldString = null;
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, final KeyEvent event) {
        _newString = AndroidKeys.getName(keyCode);
        _oldString = ((((TextView)this).getText()).toString());

        // replace the text if it has changed
        if (_newString != null && !_newString.equals(_oldString)) {
            setText(_newString);
        }

        return true;
    }

    public void commit() {

        if (null == _newString || null == _oldString) {
            return;
        }

        // now walk all the shared preferences and for any duplicate we
        // find, reset it.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String dup = null;
        for (String atariString : _consoleKeys.getNames()) {
            String androidString = prefs.getString(atariString, "");
            if (androidString.equals(_newString)) {
                dup = atariString;
                break;
            }
        }

        SharedPreferences.Editor editor = prefs.edit();
        if (null != dup) {
            // clear the entry from the
            editor.putString(dup, "");
        }
        editor.putString(_keyString, _newString);
        editor.commit();

        setText(_newString);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
         return true;
     }
}