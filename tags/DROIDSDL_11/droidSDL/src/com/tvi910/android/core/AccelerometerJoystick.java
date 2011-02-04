package com.tvi910.android.core;

import android.app.Activity;

import android.content.Context;

import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.tvi910.android.sdl.SDLInterface;

/**
 * This class implements a virtual joystick using android accelerometer readings.
 * When an accelerometer event is read, this class normalizes it. That is, if
 * the accelerometer readings are beyond a minimum or maximum threshold value,
 * the readings are adjusted to a fixed positive or negative value. Z axis
 * readings are ignored.
 */
public class AccelerometerJoystick extends VirtualController implements SensorEventListener {

	private SensorManager _manager = null;

    // threshold is the distance from "middle"
    private float _xMinThreshold = -0.8f;
    private float _xMaxThreshold = 0.8f;

    // y is skewed a bit so that the screen is tilted forward during play.
    private float _yMinThreshold = 3.5f;
    private float _yMaxThreshold = 5.5f;

    // current x and y are the current normalized positions for x and y
    // accelerometer.
    private int _currentx = 0;
    private int _currenty = 0;

    private boolean _registered = false;

    private static AccelerometerJoystick _instance = null;

    // TODO: don't hard code this
    private boolean _horizontalOrientation = true;

    public static AccelerometerJoystick getInstance(Activity context) {
        synchronized (AccelerometerJoystick.class) {
            if (null == _instance) {
                _instance = new AccelerometerJoystick(context);
            }
            return _instance;
        }
    }

    public static AccelerometerJoystick getInstance() {
        synchronized (AccelerometerJoystick.class) {
            return _instance;
        }
    }


    public void register() {
        synchronized (this) {
            if (!_registered) {
                _manager.registerListener(this, _manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
                _registered = true;
            }
        }
    }

    public void unregister() {
        synchronized (this) {
            if (_registered) {
                _manager.unregisterListener(this);
                _registered = false;
            }
        }
    }

	private AccelerometerJoystick(Activity context) {
        super((Context)context);
        _manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void setXMinThreshold(float xMinThreshold) {
        _xMinThreshold = xMinThreshold;
    }

    public void setXMaxThreshold(float xMaxThreshold) {
        _xMaxThreshold = xMaxThreshold;
    }

    public void seYMinThreshold(float yMinThreshold) {
        _yMinThreshold = yMinThreshold;
    }

    public void setYMaxThreshold(float yMaxThreshold) {
        _yMaxThreshold = yMaxThreshold;
    }

    public synchronized void stop() {
        if( _manager != null ) {
            _manager.unregisterListener(this);
        }
    }

    public synchronized void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // normalize the accelerometer data - if the input is beyond our
            // set thresholds then we set it to a fixed positive or negative
            // value.
            int thisx = (event.values[1] > _xMinThreshold && event.values[1] < _xMaxThreshold)
                ? 0
                : ((event.values[1] <= _xMinThreshold) ? -1 : 1);
            int thisy = (event.values[0] > _yMinThreshold && event.values[0] < _yMaxThreshold)
                ? 0
                : ((event.values[0] <= _yMinThreshold) ? 1 : -1);

            // swap x and y if we are using vertical orientation
            if (!_horizontalOrientation) {
                int temp = thisx;
                thisx = thisy;
                thisy = temp;
            }

            if (thisx != _currentx) {
                if (_currentx == 1) { SDLInterface.rightOff();}
                else if (_currentx == -1) { SDLInterface.leftOff();}
                if (thisx == 1) { SDLInterface.rightOn(); }
                else if (thisx == -1) { SDLInterface.leftOn(); }
                _currentx = thisx;
            }

            if (thisy != _currenty) {
                if (_currenty == 1) { SDLInterface.upOff();}
                else if (_currenty == -1) { SDLInterface.downOff();}
                if (thisy == 1) { SDLInterface.upOn();}
                else if (thisy == -1) { SDLInterface.downOn();}
                _currenty = thisy;
            }
        }
    }

    public synchronized void onAccuracyChanged(Sensor s, int a) {
    }
    
    public void privActivate() {
        register();
    }

    public void privDeactivate() {
        unregister();
    }
}


