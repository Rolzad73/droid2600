package com.tvi910.android.core;

import android.content.Context;

public abstract class VirtualController {

    private Context _context;
    private boolean _isActive;

    protected VirtualController(Context context) {
        _context = context;
        _isActive = false;
    }

    protected Context getContext() {
        return _context;
    }

    public boolean getIsActive() {
        return _isActive;
    }

    protected void setIsActive(boolean isActive) {
        _isActive = isActive;
    }

    public void activate() {
        privActivate();
        _isActive = true;
    }

    public void deactivate() {
        privDeactivate();
        _isActive = false;
    }
    
    /**
     * A sticky controller will be made active when a non-sticky 
     * controller is deactivated 
     */
    public boolean isSticky() {
        return true;
    }

    protected abstract void privActivate();
    protected abstract void privDeactivate();

}
