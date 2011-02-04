package com.tvi910.android.core;

import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines a list of android keys by keycode and name, and provides
 * methods for converting between the two.
 */
public class AndroidKeys {

    public static final int KEYMAP_SIZE = (KeyEvent.getMaxKeyCode() + 1);

    private static HashMap<Integer, String> _codeMap;
    private static HashMap<String, Integer> _nameMap;

    static {
        _codeMap = new HashMap<Integer, String>();
        // first populate the map with default values for all codes
        for (int i=0; i < KEYMAP_SIZE; i++) {
            _codeMap.put(i, ("KEY_" + i));
        }
        // then add friendly key names if we know them
        _codeMap.put(4,  "RETURN");
        _codeMap.put(17, "STAR");
        _codeMap.put(18, "POUND");
        _codeMap.put(19, "DPAD UP");
        _codeMap.put(20, "DPAD DOWN");
        _codeMap.put(21, "DPAD LEFT");
        _codeMap.put(22, "DPAD RIGHT");
        _codeMap.put(23, "DPAD CENTER");
        _codeMap.put(24, "VOLUME DOWN");
        _codeMap.put(25, "VOLUME UP");
        _codeMap.put(27, "CAMERA");
        _codeMap.put(29, "A");
        _codeMap.put(30, "B");
        _codeMap.put(31, "C");
        _codeMap.put(32, "D");
        _codeMap.put(33, "E");
        _codeMap.put(34, "F");
        _codeMap.put(35, "G");
        _codeMap.put(36, "H");
        _codeMap.put(37, "I");
        _codeMap.put(38, "J");
        _codeMap.put(39, "K");
        _codeMap.put(40, "L");
        _codeMap.put(41, "M");
        _codeMap.put(42, "N");
        _codeMap.put(43, "O");
        _codeMap.put(44, "P");
        _codeMap.put(45, "Q");
        _codeMap.put(46, "R");
        _codeMap.put(47, "S");
        _codeMap.put(48, "T");
        _codeMap.put(49, "U");
        _codeMap.put(50, "V");
        _codeMap.put(51, "W");
        _codeMap.put(52, "X");
        _codeMap.put(53, "Y");
        _codeMap.put(54, "Z");
        _codeMap.put(55, "COMMA");
        _codeMap.put(56, "PERIOD");
        _codeMap.put(57, "LEFT ALT");
        _codeMap.put(58, "RIGHT ALT");
        _codeMap.put(59, "LEFT SHIFT");
        _codeMap.put(60, "RIGHT SHIFT");
        _codeMap.put(62, "SPACE");
        _codeMap.put(66, "ENTER");
        _codeMap.put(67, "DEL");
        _codeMap.put(74, "QUESTION MARK");
        _codeMap.put(76, "FORWARD SLASH");
        _codeMap.put(77, "AT");
        _codeMap.put(80, "FOCUS");
        _codeMap.put(82, "MENU");
        _codeMap.put(84, "SEARCH");

        _nameMap = new HashMap<String, Integer>();
        for (Map.Entry<Integer, String> me : _codeMap.entrySet()) {
            _nameMap.put(me.getValue(), me.getKey());
        }
    }

    private AndroidKeys() {}

    public static String getName(int keyCode) {
        String name = _codeMap.get(keyCode);
        if (null == name) {
            return ("KEY_" + keyCode);
        }
        else {
            return name;
        }
    }

    public static Integer getCode(String name) {
        return _nameMap.get(name);
    }
}


