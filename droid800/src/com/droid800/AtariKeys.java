package com.droid800;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tvi910.android.core.ConsoleKeys;
import com.tvi910.android.sdl.SDLKeysym;

/**
 * This class gives each atari 800 input a name and maps it to an SDL
 * keycode.
 */
public class AtariKeys extends ConsoleKeys {

    // look up the console input by the SDL keycode that is mapped to it
    private HashMap<Integer, String> _codeMap;
    // look up an SDL keycode assicaited with a console input
    private HashMap<String, Integer> _nameMap;

    private static final AtariKeys _instance = new AtariKeys();

    private AtariKeys() {
        _codeMap = new HashMap<Integer, String>();

        // for each supported input, map it to an SDL key
        // SDL keys are defined in sdl/include/SDL_keysym.h
//        _codeMap.put(SDLKeysym.SDLK_F9, "QUIT");
//        _codeMap.put(SDLKeysym.SDLK_SPACE, "SPACE");
//        _codeMap.put(SDLKeysym.SDLK_F4, "START");
//        _codeMap.put(SDLKeysym.SDLK_F5, "RESET");
//        _codeMap.put(SDLKeysym.SDLK_F2, "OPTION");
//        _codeMap.put(SDLKeysym.SDLK_F3, "SELECT");
        _codeMap.put(SDLKeysym.SDLK_UP, "UP");
        _codeMap.put(SDLKeysym.SDLK_DOWN, "DOWN");
        _codeMap.put(SDLKeysym.SDLK_LEFT, "LEFT");
        _codeMap.put(SDLKeysym.SDLK_RIGHT, "RIGHT");
        _codeMap.put(SDLKeysym.SDLK_KP_PERIOD, "TRIGGER");
        _codeMap.put(SDLKeysym.SDLK_ESCAPE, "ESCAPE");
        _codeMap.put(SDLKeysym.SDLK_F1, "F1");

        // enough for bounty bob
//        _codeMap.put(SDLKeysym.SDLK_1, "1");
//        _codeMap.put(SDLKeysym.SDLK_2, "2");
//        _codeMap.put(SDLKeysym.SDLK_3, "3");
//        _codeMap.put(SDLKeysym.SDLK_4, "4");
/*
        _codeMap.put(SDLKeysym.SDLK_5, "5");
        _codeMap.put(SDLKeysym.SDLK_6, "6");
        _codeMap.put(SDLKeysym.SDLK_7, "7");
        _codeMap.put(SDLKeysym.SDLK_8, "8");
        _codeMap.put(SDLKeysym.SDLK_9, "9");
        _codeMap.put(SDLKeysym.SDLK_0, "0");
*/

        // walk through the code
        _nameMap = new HashMap<String, Integer>();
        for (Map.Entry<Integer, String> me : _codeMap.entrySet()) {
            _nameMap.put(me.getValue(), me.getKey());
        }
    }

    public static final void init() {
    }

    public String getName(int keyCode) {
        String name = _codeMap.get(keyCode);
        if (null == name) {
            return ("KEY_" + keyCode);
        }
        else {
            return name;
        }
    }

    public Integer getCode(String name) {
        return _nameMap.get(name);
    }

    public List<String> getNames() {
        ArrayList<String> lst = new ArrayList<String>();
        for (String s : _nameMap.keySet()) {
            lst.add(s);
        }
        return lst;
    }

    public List<Integer> getCodes() {
        ArrayList<Integer> lst = new ArrayList<Integer>();
        for (Integer s : _codeMap.keySet()) {
            lst.add(s);
        }
        return lst;
    }
}

