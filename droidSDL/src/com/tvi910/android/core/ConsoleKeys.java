package com.tvi910.android.core;

import java.util.List;

/**
 * Mapping console key names to codes and vice-versa.
 */
public abstract class ConsoleKeys {

    private static ConsoleKeys _instance = null;

    /**
     * Set the instance. Called implicitly by the subclass constructor. Note
     * that constructing multiple instances of ConsoleKeys will result in
     * an Error being thrown.
     */
    private synchronized final void setInstance(ConsoleKeys consoleKeys) {
        if (_instance != null) {
            throw new Error("ConsoleKeys.setInstance called multiple times");
        }
        else {
            _instance = consoleKeys;
        }
    }

    /**
     * Default constructor - sets the instance variable
     */
    protected ConsoleKeys() {
        setInstance(this);
    }

    public static final ConsoleKeys getInstance() {
        if (_instance == null) {
            throw new Error("ConsoleKeys.getInstance called before init");
        }
        else {
            return _instance;
        }
    }

    abstract public String getName(int keyCode);
    abstract public Integer getCode(String name);
    abstract public List<String> getNames();
    abstract public List<Integer> getCodes();

}


