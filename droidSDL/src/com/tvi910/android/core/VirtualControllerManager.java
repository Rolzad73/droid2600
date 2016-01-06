package com.tvi910.android.core;

import java.util.HashMap;

public class VirtualControllerManager {

    private HashMap<String, VirtualController> _controllerMap;
    private VirtualController _activeController;
    private VirtualController _lastController;

    public VirtualControllerManager() {
        _controllerMap = new HashMap<String, VirtualController>();
        _activeController = null;
        _lastController = null;
    }

    public void add(String name, VirtualController vc) {
        _controllerMap.put(name, vc);
        _activeController = vc;
        if (vc.isSticky()) _lastController = vc;
    }

    /**
     * Activate the controller identified by String a. 
     */
    public void setActiveController(String a) {
        VirtualController controller = _controllerMap.get(a);
        if (null != controller) {
            // deactivate all controls then activate this one
            for (VirtualController vc : _controllerMap.values()) {
                vc.deactivate();         
            }
            controller.activate(); 
            if (_activeController.isSticky()) {
                _lastController = _activeController;
            }
            _activeController = controller;
        }
    }

    /**
     * Activate the controller identified by String a. 
     */
    public void activateLastController() {
        // deactivate all controls then reactivate the last one
        for (VirtualController vc : _controllerMap.values()) {
            vc.deactivate();         
        }
        if (null != _lastController) {
            _lastController.activate(); 
            _activeController = _lastController;
        }
    }


    public void hideAll() {

        for (VirtualController vc : _controllerMap.values()) {
            vc.deactivate();         
        }
        if (_activeController.isSticky()) {
            _lastController = _activeController;
        }
    }

    public VirtualController getActiveController() {
        return _activeController;
    }

    public VirtualController getLastController() {
        return _lastController;
    }
}
