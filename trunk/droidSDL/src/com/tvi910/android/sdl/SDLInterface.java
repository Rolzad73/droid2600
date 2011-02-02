package com.tvi910.android.sdl;

public class SDLInterface {

    private static int _leftKeycode = SDLKeysym.SDLK_KP4;
    private static int _rightKeycode = SDLKeysym.SDLK_KP6;
    private static int _upKeycode = SDLKeysym.SDLK_KP8;
    private static int _downKeycode = SDLKeysym.SDLK_KP5;
    private static int _triggerKeycode = SDLKeysym.SDLK_RCTRL;
    private static int _trigger2Keycode = SDLKeysym.SDLK_RCTRL;

    public static void setLeftKeycode(int code) { _leftKeycode = code; }
    public static void setRightKeycode(int code) { _rightKeycode = code; }
    public static void setUpKeycode(int code) { _upKeycode = code; }
    public static void setDownKeycode(int code) { _downKeycode = code; }
    public static void setTriggerKeycode(int code) { _triggerKeycode = code; }
    public static void setTrigger2Keycode(int code) { _trigger2Keycode = code; }

    private static int _keyCycleDelay = 0;

    public static void setKeyCycleDelay(int keyCycleDelay) {_keyCycleDelay = keyCycleDelay; }

    /**
     * Emit an sdl native keypress
     */
    public static native void nativeKey( int keyCode, int down );

    /**
     * This is here for some applications that need a delay between 
     * key events. 
     */
    public static void nativeKeyCycle(int keyCode) {
        nativeKey(keyCode,1);
        if (_keyCycleDelay > 0) try {Thread.sleep(_keyCycleDelay);}catch (Throwable e) {}
        nativeKey(keyCode,0);
    }

    /**
     * This is here for some applications that need a delay between 
     * key events. 
     */
    public static void nativeKeyCycle(int keyCode, int delay) {
        nativeKey(keyCode,1);
        if (delay > 0) try {Thread.sleep(delay);}catch (Throwable e) {}
        nativeKey(keyCode,0);
    }

    /**
     * Skip mode.
     *    0 = don't skip frames
     *    1 = skip 1/4 frames
     *    2 = skip 1/2 frames
     */
    public static native void nativeSetSkipMode(int skipMode);

    /**
     * SDL Joystick controls. Supports a 4 position,  2 trigger joystick.
     */
    public static void leftOn() { nativeKey(_leftKeycode, 1); }
    public static void leftOff() { nativeKey(_leftKeycode, 0); }
    public static void rightOn() { nativeKey(_rightKeycode, 1); }
    public static void rightOff() { nativeKey(_rightKeycode, 0); }
    public static void upOn() { nativeKey(_upKeycode, 1); }
    public static void upOff() { nativeKey(_upKeycode, 0); }
    public static void downOn() { nativeKey(_downKeycode, 1); }
    public static void downOff() { nativeKey(_downKeycode, 0); }
    public static void triggerOn() { nativeKey(_triggerKeycode, 1); }
    public static void triggerOff() { nativeKey(_triggerKeycode, 0); }
    public static void trigger2On() { nativeKey(_trigger2Keycode, 1); }
    public static void trigger2Off() { nativeKey(_trigger2Keycode, 0); }

    /**
     * Pass an array of command line arguments.
     */
    public static native void nativeInit(String args[]);


    /**
     * For passing mouse events to the SDL layer
     */
    public static native void nativeMouse( int x, int y, int action, int pointerId, int pressure, int radius );

    /**
     * Quit the SDL application. 
     */
    public static native void nativeQuit();

}
