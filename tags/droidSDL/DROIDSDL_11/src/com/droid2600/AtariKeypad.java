package com.droid2600;

import android.content.Context;
import android.view.ViewGroup;
import android.graphics.Color;

import com.tvi910.android.sdl.SDLKeysym;
import com.tvi910.android.sdl.SDLInterface;

import com.tvi910.android.core.VirtualController;

import com.tvi910.android.core.buttonpanel.ButtonCallback;
import com.tvi910.android.core.buttonpanel.ButtonPanel;

public class AtariKeypad extends VirtualController {

    public static class NormalCallback implements ButtonCallback {
        public int _keyCode;
        NormalCallback(int keyCode) {
            _keyCode = keyCode;
        }
        public void onButtonUp() {
            SDLInterface.nativeKeyCycle(_keyCode);
        }   
    }

    private boolean _landscape;

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

    // right player
    private static final ButtonInfo[] buttonInfo = {
        // row 1
        new ButtonInfo("1", new NormalCallback(SDLKeysym.SDLK_1), 1),
        new ButtonInfo("2", new NormalCallback(SDLKeysym.SDLK_2), 1),
        new ButtonInfo("3", new NormalCallback(SDLKeysym.SDLK_3), 1),
        new ButtonInfo("1", new NormalCallback(SDLKeysym.SDLK_8), 1),
        new ButtonInfo("2", new NormalCallback(SDLKeysym.SDLK_9), 1),
        new ButtonInfo("3", new NormalCallback(SDLKeysym.SDLK_0), 1),

        new ButtonInfo("4", new NormalCallback(SDLKeysym.SDLK_i), 1),
        new ButtonInfo("5", new NormalCallback(SDLKeysym.SDLK_o), 1),
        new ButtonInfo("6", new NormalCallback(SDLKeysym.SDLK_p), 1),
        new ButtonInfo("4", new NormalCallback(SDLKeysym.SDLK_q), 1),
        new ButtonInfo("5", new NormalCallback(SDLKeysym.SDLK_w), 1),
        new ButtonInfo("6", new NormalCallback(SDLKeysym.SDLK_e), 1),

        new ButtonInfo("7", new NormalCallback(SDLKeysym.SDLK_k), 1),
        new ButtonInfo("8", new NormalCallback(SDLKeysym.SDLK_l), 1),
        new ButtonInfo("9", new NormalCallback(SDLKeysym.SDLK_SEMICOLON), 1),
        new ButtonInfo("7", new NormalCallback(SDLKeysym.SDLK_a), 1),
        new ButtonInfo("8", new NormalCallback(SDLKeysym.SDLK_s), 1),
        new ButtonInfo("9", new NormalCallback(SDLKeysym.SDLK_d), 1),

        new ButtonInfo("*", new NormalCallback(SDLKeysym.SDLK_COMMA), 1),
        new ButtonInfo("0", new NormalCallback(SDLKeysym.SDLK_PERIOD), 1),
        new ButtonInfo("#", new NormalCallback(SDLKeysym.SDLK_SLASH), 1),
        new ButtonInfo("*", new NormalCallback(SDLKeysym.SDLK_z), 1),
        new ButtonInfo("0", new NormalCallback(SDLKeysym.SDLK_x), 1),
        new ButtonInfo("#", new NormalCallback(SDLKeysym.SDLK_c), 1)
    };

    private ButtonPanel _buttonPanel;
    private Context _context;

    public AtariKeypad(Context context, boolean landscape) {
        super(context);
        _landscape = landscape;
        _context = context;
        createKeyboard();
        setupKeyboard();
    }

    private void createKeyboard() {
        if (_landscape) {
            _buttonPanel = new ButtonPanel(
                _context,
                null,
                6, // number of grid columns
                4, // number of grid rows
                60, // percent of desired width fill
                70, // percent of desired height fill
                50, // x offset (0 = center)
                50, // y offset (0 = center)
                0, // aspect ratio (1=square)
                0);

            _buttonPanel.setPadding(.50f);
        }
        else {
            _buttonPanel = new ButtonPanel(
                _context,
                null,
                6, // number of grid columns
                4, // number of grid rows
                80, // percent of desired width fill
                30, // percent of desired height fill
                80, // x offset (0 = center)
                50, // y offset (0 = center)
                0, // aspect ratio (1=square)
                0);
            _buttonPanel.setPadding(.50f);
        }
    }

    private void setupKeyboard() {
        final ButtonInfo bi[] = buttonInfo;

        for (int col=0; col<6; col++) {
            for (int row=0; row<4; row++) {
                final int fcol = col;
                final int frow = row;
                if (bi[(6*row)+col].colspan > 0) {
                     _buttonPanel.setButton(col,row, 
                         Color.argb(64, 38, 38, 38), 
                         Color.argb(128, 228, 228, 228), 
                         bi[(6*row)+col].name,
                         bi[(6*row)+col].callback,
                         bi[(6*row)+col].colspan);
                }
            }
        }

        _buttonPanel.invalidate();
    }


    public ButtonPanel getButtonPanel() {
        return _buttonPanel;
    }

    public void privActivate() {
        _buttonPanel.showPanel();
    }

    public void privDeactivate() {
        _buttonPanel.hidePanel();
    }
}

