package com.droid2600;

import com.tvi910.android.core.AndroidFileBrowser;

import java.io.File;

import java.util.HashSet;

public class RomFilter implements AndroidFileBrowser.Filter {

    private static final HashSet<String> _extensions = new HashSet<String>(20);

    static {
        _extensions.add(".bin");
        _extensions.add(".BIN");
        _extensions.add(".zip");
        _extensions.add(".ZIP");
        _extensions.add(".a26");
        _extensions.add(".A26");
        _extensions.add(".z26");
        _extensions.add(".Z26");
        _extensions.add(".car");
        _extensions.add(".CAR");
        _extensions.add(".atr");
        _extensions.add(".ATR");
        _extensions.add(".rom");
        _extensions.add(".ROM");
    }

    public boolean filter(File file) {
        try {
            if (file.isDirectory()) {
                return true;
            }
            else if (file.length() > 40000) {
                return false;
            }
            else {
                String str = file.getPath().substring(file.getPath().length()-4);
                if (_extensions.contains(str)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        catch (Throwable e) {
            return false;
        }
    }
}
