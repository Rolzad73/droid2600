package com.tvi910.android.sdl;

import android.app.Activity;

import android.content.SharedPreferences;

import android.preference.PreferenceManager;

import javax.microedition.khronos.opengles.GL10;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import android.util.Log;

import java.util.List;

class DemoRenderer extends GLSurfaceView_SDL.Renderer {

    boolean firstCall = true;

    public DemoRenderer(Activity _context, List<String> sdlMainArgs)
    {
        _sdlMainArgs = sdlMainArgs;
        context = _context;
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // nativeInit();
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        //gl.glViewport(0, 0, w, h);
        Log.v("Video.onSurfaceChanged", "w=" + w + ", h=" + h);
        nativeResize(w, h);
    }

    public void onDrawFrame(GL10 gl) {

        nativeInitJavaCallbacks();

        // Make main thread priority lower so audio thread won't get underrun
        //Thread.currentThread().setPriority((Thread.currentThread().getPriority() + Thread.MIN_PRIORITY)/2);

        System.loadLibrary("application");
        System.loadLibrary("sdl_main");

        StringBuffer sb = new StringBuffer();
        sb.append("starting sdl application with args: ");

        String args[] = null;
        if (null == _sdlMainArgs) {
            args = new String[0];
        }
        else {
            args = new String[_sdlMainArgs.size()];
            for (int i=0; i<_sdlMainArgs.size(); i++) {
                args[i] = _sdlMainArgs.get(i);
                sb.append("" + i + "=" + args[i] + " ");
            }
        }

        Log.v("DemoRenderer", sb.toString());

        SDLInterface.nativeInit(args);

        System.exit(0);
    }

    private void setSkipMode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String skipMode = prefs.getString("skipFrames", "0");
        if (skipMode == null || skipMode.equals("0")) {
            return;
        }
        try {
            SDLInterface.nativeSetSkipMode(Integer.parseInt(skipMode));
        }
        catch (Throwable e) {
            Log.v("Droid2600Activity", "error while setting skip mode: "
                + e.toString());
        }
    }

    public int swapBuffers() // Called from native code, returns 1 on success, 0 when GL context lost (user put app to background)
    {

        synchronized (this) {
            this.notify();
        }

        if (firstCall) {
            firstCall = false;
            setSkipMode();
        }

        //Thread.yield();
        int r = super.SwapBuffers() ? 1 : 0;
        return r;
    }

    public void exitApp() {
         nativeDone();
    };

    private native void nativeInitJavaCallbacks();
    private native void nativeResize(int w, int h);
    private native void nativeDone();

    private Activity context = null;
    private List<String> _sdlMainArgs;

    private EGL10 mEgl = null;
    private EGLDisplay mEglDisplay = null;
    private EGLSurface mEglSurface = null;
    private EGLContext mEglContext = null;
}
