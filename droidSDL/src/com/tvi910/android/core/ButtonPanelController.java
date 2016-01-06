package com.tvi910.android.core;

import android.content.Context;

import com.tvi910.android.core.buttonpanel.ButtonPanel;

public class ButtonPanelController extends VirtualController {

    private final ButtonPanel _buttonPanel;

    public ButtonPanelController(Context context, ButtonPanel buttonPanel) {
        super(context);
        _buttonPanel = buttonPanel;
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
