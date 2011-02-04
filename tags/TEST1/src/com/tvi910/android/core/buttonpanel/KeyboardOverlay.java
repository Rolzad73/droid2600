package com.tvi910.android.core.buttonpanel;

import android.content.Context;
import android.view.ViewGroup;
import android.graphics.Color;

import com.tvi910.android.core.VirtualController;

import com.tvi910.android.sdl.SDLKeysym;
import com.tvi910.android.sdl.SDLInterface;

public class KeyboardOverlay extends VirtualController {

    public static boolean _setLowrMode = false;

    public static class NormalCallback implements ButtonCallback {
        public int _keyCode;
        NormalCallback(int keyCode) {
            _keyCode = keyCode;
        }
        public void onButtonUp() {
            // clumsy work-around for the weird capslock situation in 
            // the emulator. This is essentially pressing the "Lowr" 
            // button on older ataris or toggling caps mode in the newer 
            // ones.
            if (!_setLowrMode) {
                _setLowrMode = true;
                SDLInterface.nativeKeyCycle(SDLKeysym.SDLK_CAPSLOCK);
                SDLInterface.nativeKeyCycle(SDLKeysym.SDLK_CAPSLOCK);
            }
            SDLInterface.nativeKeyCycle(_keyCode);
        }   
    }

    private boolean _landscape;

    // unicode arrows: http://en.wikipedia.org/wiki/Arrow_(symbol)
    // unicode misc technical http://en.wikipedia.org/wiki/Miscellaneous_Technical_(Unicode)

    private static class ButtonInfo {

        String name;
        int colspan;
        ButtonCallback callback;

        ButtonInfo(String name, ButtonCallback callback, int colspan) {
            this.name = name;
            this.colspan = colspan;
            this.callback = callback;
        }
    }

    private static final ButtonInfo[] buttonInfo1 = {
        // row 1
        new ButtonInfo("1", new NormalCallback(SDLKeysym.SDLK_1), 1),
        new ButtonInfo("2", new NormalCallback(SDLKeysym.SDLK_2), 1),
        new ButtonInfo("3", new NormalCallback(SDLKeysym.SDLK_3), 1),
        new ButtonInfo("4", new NormalCallback(SDLKeysym.SDLK_4), 1),
        new ButtonInfo("5", new NormalCallback(SDLKeysym.SDLK_5), 1),
        new ButtonInfo("6", new NormalCallback(SDLKeysym.SDLK_6), 1),
        new ButtonInfo("7", new NormalCallback(SDLKeysym.SDLK_7), 1),
        new ButtonInfo("8", new NormalCallback(SDLKeysym.SDLK_8), 1),
        new ButtonInfo("9", new NormalCallback(SDLKeysym.SDLK_9), 1),
        new ButtonInfo("0", new NormalCallback(SDLKeysym.SDLK_0), 1),
        // row 2
        new ButtonInfo("Q", new NormalCallback(SDLKeysym.SDLK_Q), 1),
        new ButtonInfo("W", new NormalCallback(SDLKeysym.SDLK_W), 1),
        new ButtonInfo("E", new NormalCallback(SDLKeysym.SDLK_E), 1),
        new ButtonInfo("R", new NormalCallback(SDLKeysym.SDLK_R), 1),
        new ButtonInfo("T", new NormalCallback(SDLKeysym.SDLK_T), 1),
        new ButtonInfo("Y", new NormalCallback(SDLKeysym.SDLK_Y), 1),
        new ButtonInfo("U", new NormalCallback(SDLKeysym.SDLK_U), 1),
        new ButtonInfo("I", new NormalCallback(SDLKeysym.SDLK_I), 1),
        new ButtonInfo("O", new NormalCallback(SDLKeysym.SDLK_O), 1),
        new ButtonInfo("P", new NormalCallback(SDLKeysym.SDLK_P), 1),
        // row 3
        new ButtonInfo("A", new NormalCallback(SDLKeysym.SDLK_A), 1),
        new ButtonInfo("S", new NormalCallback(SDLKeysym.SDLK_S), 1),
        new ButtonInfo("D", new NormalCallback(SDLKeysym.SDLK_D), 1),
        new ButtonInfo("F", new NormalCallback(SDLKeysym.SDLK_F), 1),
        new ButtonInfo("G", new NormalCallback(SDLKeysym.SDLK_G), 1),
        new ButtonInfo("H", new NormalCallback(SDLKeysym.SDLK_H), 1),
        new ButtonInfo("J", new NormalCallback(SDLKeysym.SDLK_J), 1),
        new ButtonInfo("K", new NormalCallback(SDLKeysym.SDLK_K), 1),
        new ButtonInfo("L", new NormalCallback(SDLKeysym.SDLK_L), 1),
        new ButtonInfo(";", new NormalCallback(SDLKeysym.SDLK_SEMICOLON), 1),
        // row 4
        new ButtonInfo("TAB", new NormalCallback(SDLKeysym.SDLK_TAB), 1),
        new ButtonInfo("Z", new NormalCallback(SDLKeysym.SDLK_Z), 1),
        new ButtonInfo("X", new NormalCallback(SDLKeysym.SDLK_X), 1),
        new ButtonInfo("C", new NormalCallback(SDLKeysym.SDLK_C), 1),
        new ButtonInfo("V", new NormalCallback(SDLKeysym.SDLK_V), 1),
        new ButtonInfo("B", new NormalCallback(SDLKeysym.SDLK_B), 1),
        new ButtonInfo("N", new NormalCallback(SDLKeysym.SDLK_N), 1),
        new ButtonInfo("M", new NormalCallback(SDLKeysym.SDLK_M), 1),
        new ButtonInfo("\u21b5", new NormalCallback(SDLKeysym.SDLK_RETURN), 2),
        new ButtonInfo("\u21b5", new NormalCallback(SDLKeysym.SDLK_RETURN), 0),
        // row 5
        new ButtonInfo("...", null, 2),
        new ButtonInfo("...", null, 0),
        new ButtonInfo("/", new NormalCallback(SDLKeysym.SDLK_SLASH), 1),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 4),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo(",", new NormalCallback(SDLKeysym.SDLK_COMMA), 1),
        new ButtonInfo(".", new NormalCallback(SDLKeysym.SDLK_PERIOD), 1),
        new ButtonInfo("\u21e6", new NormalCallback(SDLKeysym.SDLK_BACKSPACE), 1)
    };

    private static final ButtonInfo[] buttonInfo2 = {
        // row 1
        new ButtonInfo("!", new NormalCallback(SDLKeysym.SDLK_EXCLAIM), 1),
        new ButtonInfo("@", new NormalCallback(SDLKeysym.SDLK_AT), 1),
        new ButtonInfo("#", new NormalCallback(SDLKeysym.SDLK_HASH), 1),
        new ButtonInfo("$", new NormalCallback(SDLKeysym.SDLK_DOLLAR), 1),
        new ButtonInfo("%", new NormalCallback(SDLKeysym.SDLK_PERCENT), 1),
        new ButtonInfo("^", new NormalCallback(SDLKeysym.SDLK_CARET), 1),
        new ButtonInfo("&", new NormalCallback(SDLKeysym.SDLK_AMPERSAND), 1),
        new ButtonInfo("*", new NormalCallback(SDLKeysym.SDLK_ASTERISK), 1),
        new ButtonInfo("(", new NormalCallback(SDLKeysym.SDLK_LEFTPAREN), 1),
        new ButtonInfo(")", new NormalCallback(SDLKeysym.SDLK_RIGHTPAREN), 1),
        // row 2
        new ButtonInfo("q", new NormalCallback(SDLKeysym.SDLK_q), 1),
        new ButtonInfo("w", new NormalCallback(SDLKeysym.SDLK_w), 1),
        new ButtonInfo("e", new NormalCallback(SDLKeysym.SDLK_e), 1),
        new ButtonInfo("r", new NormalCallback(SDLKeysym.SDLK_r), 1),
        new ButtonInfo("t", new NormalCallback(SDLKeysym.SDLK_t), 1),
        new ButtonInfo("y", new NormalCallback(SDLKeysym.SDLK_y), 1),
        new ButtonInfo("u", new NormalCallback(SDLKeysym.SDLK_u), 1),
        new ButtonInfo("i", new NormalCallback(SDLKeysym.SDLK_i), 1),
        new ButtonInfo("o", new NormalCallback(SDLKeysym.SDLK_o), 1),
        new ButtonInfo("p", new NormalCallback(SDLKeysym.SDLK_p), 1),
        // row 3
        new ButtonInfo("a", new NormalCallback(SDLKeysym.SDLK_a), 1),
        new ButtonInfo("s", new NormalCallback(SDLKeysym.SDLK_s), 1),
        new ButtonInfo("d", new NormalCallback(SDLKeysym.SDLK_d), 1),
        new ButtonInfo("f", new NormalCallback(SDLKeysym.SDLK_f), 1),
        new ButtonInfo("g", new NormalCallback(SDLKeysym.SDLK_g), 1),
        new ButtonInfo("h", new NormalCallback(SDLKeysym.SDLK_h), 1),
        new ButtonInfo("j", new NormalCallback(SDLKeysym.SDLK_j), 1),
        new ButtonInfo("k", new NormalCallback(SDLKeysym.SDLK_k), 1),
        new ButtonInfo("l", new NormalCallback(SDLKeysym.SDLK_l), 1),
        new ButtonInfo(":", new NormalCallback(SDLKeysym.SDLK_COLON), 1),
        // row 4
        new ButtonInfo("TAB", new NormalCallback(SDLKeysym.SDLK_TAB), 1),
        new ButtonInfo("z", new NormalCallback(SDLKeysym.SDLK_z), 1),
        new ButtonInfo("x", new NormalCallback(SDLKeysym.SDLK_x), 1),
        new ButtonInfo("c", new NormalCallback(SDLKeysym.SDLK_c), 1),
        new ButtonInfo("v", new NormalCallback(SDLKeysym.SDLK_v), 1),
        new ButtonInfo("b", new NormalCallback(SDLKeysym.SDLK_b), 1),
        new ButtonInfo("n", new NormalCallback(SDLKeysym.SDLK_n), 1),
        new ButtonInfo("m", new NormalCallback(SDLKeysym.SDLK_m), 1),
        new ButtonInfo("\u21b5", new NormalCallback(SDLKeysym.SDLK_RETURN), 2),
        new ButtonInfo("\u21b5", new NormalCallback(SDLKeysym.SDLK_RETURN), 0),
        // row 5
        new ButtonInfo("...", null, 2),
        new ButtonInfo("...", null, 0),
        new ButtonInfo("?", new NormalCallback(SDLKeysym.SDLK_QUESTION), 1),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 4),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo("<", new NormalCallback(SDLKeysym.SDLK_LESS), 1),
        new ButtonInfo(">", new NormalCallback(SDLKeysym.SDLK_GREATER), 1),
        new ButtonInfo("\u21e6", new NormalCallback(SDLKeysym.SDLK_BACKSPACE), 1)
    };

    private static final ButtonInfo[] buttonInfo3 = {
        // row 1
        new ButtonInfo("\"", new NormalCallback(SDLKeysym.SDLK_QUOTEDBL), 1),
        new ButtonInfo("'", new NormalCallback(SDLKeysym.SDLK_QUOTE), 1),
        new ButtonInfo("-", new NormalCallback(SDLKeysym.SDLK_MINUS), 1),
        new ButtonInfo("_", new NormalCallback(SDLKeysym.SDLK_UNDERSCORE), 1),
        new ButtonInfo("|", new NormalCallback(SDLKeysym.SDLK_PIPE), 1),
        new ButtonInfo("=", new NormalCallback(SDLKeysym.SDLK_EQUALS), 1),
        new ButtonInfo("+", new NormalCallback(SDLKeysym.SDLK_PLUS), 1),
        new ButtonInfo("\\", new NormalCallback(SDLKeysym.SDLK_BACKSLASH), 1),
        new ButtonInfo("[", new NormalCallback(SDLKeysym.SDLK_LEFTBRACKET), 1),
        new ButtonInfo("]", new NormalCallback(SDLKeysym.SDLK_RIGHTBRACKET), 1),
        // row 2
        new ButtonInfo("Q", new NormalCallback(SDLKeysym.SDLK_Q), 1),
        new ButtonInfo("W", new NormalCallback(SDLKeysym.SDLK_W), 1),
        new ButtonInfo("E", new NormalCallback(SDLKeysym.SDLK_E), 1),
        new ButtonInfo("R", new NormalCallback(SDLKeysym.SDLK_R), 1),
        new ButtonInfo("T", new NormalCallback(SDLKeysym.SDLK_T), 1),
        new ButtonInfo("Y", new NormalCallback(SDLKeysym.SDLK_Y), 1),
        new ButtonInfo("U", new NormalCallback(SDLKeysym.SDLK_U), 1),
        new ButtonInfo("I", new NormalCallback(SDLKeysym.SDLK_I), 1),
        new ButtonInfo("O", new NormalCallback(SDLKeysym.SDLK_O), 1),
        new ButtonInfo("P", new NormalCallback(SDLKeysym.SDLK_P), 1),
        // row 3
        new ButtonInfo("A", new NormalCallback(SDLKeysym.SDLK_A), 1),
        new ButtonInfo("S", new NormalCallback(SDLKeysym.SDLK_S), 1),
        new ButtonInfo("D", new NormalCallback(SDLKeysym.SDLK_D), 1),
        new ButtonInfo("F", new NormalCallback(SDLKeysym.SDLK_F), 1),
        new ButtonInfo("G", new NormalCallback(SDLKeysym.SDLK_G), 1),
        new ButtonInfo("H", new NormalCallback(SDLKeysym.SDLK_H), 1),
        new ButtonInfo("J", new NormalCallback(SDLKeysym.SDLK_J), 1),
        new ButtonInfo("K", new NormalCallback(SDLKeysym.SDLK_K), 1),
        new ButtonInfo("L", new NormalCallback(SDLKeysym.SDLK_L), 1),
        new ButtonInfo(";", new NormalCallback(SDLKeysym.SDLK_SEMICOLON), 1),
        // row 4
        new ButtonInfo("TAB", new NormalCallback(SDLKeysym.SDLK_TAB), 1),
        new ButtonInfo("Z", new NormalCallback(SDLKeysym.SDLK_Z), 1),
        new ButtonInfo("X", new NormalCallback(SDLKeysym.SDLK_X), 1),
        new ButtonInfo("C", new NormalCallback(SDLKeysym.SDLK_C), 1),
        new ButtonInfo("V", new NormalCallback(SDLKeysym.SDLK_V), 1),
        new ButtonInfo("B", new NormalCallback(SDLKeysym.SDLK_B), 1),
        new ButtonInfo("N", new NormalCallback(SDLKeysym.SDLK_N), 1),
        new ButtonInfo("M", new NormalCallback(SDLKeysym.SDLK_M), 1),
        new ButtonInfo("\u21b5", new NormalCallback(SDLKeysym.SDLK_RETURN), 2),
        new ButtonInfo("\u21b5", new NormalCallback(SDLKeysym.SDLK_RETURN), 0),
        // row 5
        new ButtonInfo("...", null, 2),
        new ButtonInfo("...", null, 0),
        new ButtonInfo("/", new NormalCallback(SDLKeysym.SDLK_SLASH), 1),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 4),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo("", new NormalCallback(SDLKeysym.SDLK_SPACE), 0),
        new ButtonInfo(",", new NormalCallback(SDLKeysym.SDLK_COMMA), 1),
        new ButtonInfo(".", new NormalCallback(SDLKeysym.SDLK_PERIOD), 1),
        new ButtonInfo("\u21e6", new NormalCallback(SDLKeysym.SDLK_BACKSPACE), 1)
    };

    private ButtonPanel _buttonPanel;
    private Context _context;


    public KeyboardOverlay(Context context, boolean landscape, int alphaLevel) {
        super(context);
        _landscape = landscape;
        _context = context;
        createKeyboard();
        setupKeyboard(0, alphaLevel);
    }

    private void createKeyboard() {
        if (_landscape) {
            _buttonPanel = new ButtonPanel(
                _context,
                null,
                10, // number of grid columns
                5, // number of grid rows
                100, // percent of desired width fill
                60, // percent of desired height fill
                50, // x offset (0 = center)
                100, // y offset (0 = center)
                0, // aspect ratio (1=square)
                1);

            _buttonPanel.setPadding(.50f);
        }
        else {
            _buttonPanel = new ButtonPanel(
                _context,
                null,
                10, // number of grid columns
                5, // number of grid rows
                100, // percent of desired width fill
                40, // percent of desired height fill
                50, // x offset (0 = center)
                100, // y offset (0 = center)
                0, // aspect ratio (1=square)
                1);
            _buttonPanel.setPadding(.85f);
        }
    }

    private void setupKeyboard(final int idx, final int alphaLevel) {
        final ButtonInfo bi[];
        if (0 == idx) bi = buttonInfo1; 
        else if (1 == idx) bi = buttonInfo2; 
        else bi = buttonInfo3; 

        for (int col=0; col<10; col++) {
            for (int row=0; row<5; row++) {
                final int fcol = col;
                final int frow = row;
                if (bi[(10*row)+col].colspan > 0) {
                    if (null == bi[(10*frow)+fcol].callback) {
                        _buttonPanel.setButton(col,row, 
                            Color.argb(alphaLevel, 38, 38, 38), 
                            Color.argb(alphaLevel, 228, 228, 228), 
                            bi[(10*row)+col].name,
                            new ButtonCallback() {
                                public void onButtonUp() {
                                    if (0 == idx) setupKeyboard(1, alphaLevel);
                                    else if (1 == idx) setupKeyboard(2, alphaLevel);
                                    else if (2 == idx) setupKeyboard(0, alphaLevel);
                                }
                            },
                            bi[(10*row)+col].colspan);
                    }
                    else {
                        _buttonPanel.setButton(col,row, 
                            Color.argb(alphaLevel, 38, 38, 38), 
                            Color.argb(alphaLevel, 228, 228, 228), 
                            bi[(10*row)+col].name,
                            bi[(10*row)+col].callback,
                            bi[(10*row)+col].colspan);
                    }
                }
            }
        }

        _buttonPanel.invalidate();
    }


    public ButtonPanel getButtonPanel() {
        return _buttonPanel;
    }

    @Override
    public  boolean isSticky() {
        return false;
    }

    public void privActivate() {
        _buttonPanel.showPanel();
    }

    public void privDeactivate() {
        _buttonPanel.hidePanel();
    }
}

