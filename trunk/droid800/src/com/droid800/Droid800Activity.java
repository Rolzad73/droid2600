package com.droid800;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.media.AudioManager;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import java.util.Map;

import com.tvi910.android.core.AccelerometerJoystick;
import com.tvi910.android.core.AndroidFileBrowser;
import com.tvi910.android.core.AndroidKeys;
import com.tvi910.android.core.Keymap;

import com.tvi910.android.core.util.FileUtils;

import com.tvi910.android.core.buttonpanel.ButtonCallback;
import com.tvi910.android.core.buttonpanel.ButtonPanel;

import com.tvi910.android.sdl.SDLInterface;

/**
 * Droid800Activity - main activity for the Droid800 application.
 */
public class Droid800Activity extends Activity {

    static final private int PREFERENCES_ID = Menu.FIRST;
    static final private int FIND_ID = Menu.FIRST + 1;
    static final private int START_ID = Menu.FIRST + 2;
    static final private int PREFERENCES_ACTIVITY_RETURN = Menu.FIRST + 3;
    static final private int FILE_CHOOSER_ACTIVITY_RETURN = Menu.FIRST + 4;
    static final private int MAIN_ACTIVITY_RETURN = Menu.FIRST + 5;
    static final private int RELOAD_PREFERENCES_ID = Menu.FIRST + 6;
    static final private int OS_FILE_CHOOSER_ACTIVITY_RETURN = Menu.FIRST + 7;
    static final private int ABOUT_RETURN = Menu.FIRST + 8;

    private TextView _romFileTextView;
    private TextView _osRomFileTextView;
    private TextView _osTypeView;

    // handle to default shared preferences.
    private SharedPreferences _preferences = null;

    // save the original volume so it can be restored when the emulator exits
    private int _originalVolume = -1;

    private String _osRomPath = null;
    private String _romPath = null;

    private boolean _startup = true;

    public Droid800Activity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        AtariKeys.init();
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String romFile = _preferences.getString("romfile", "");
        String osRom = _preferences.getString("osrom", "");
        String romDirectory = _preferences.getString("romdirectory", "");

        // if there is no rom directory, then set /sdcard/Droid800 as 
        // the default.
        if ("".equals(romDirectory)) {
            SharedPreferences.Editor editor = _preferences.edit();
            editor.putString("romdirectory", "/sdcard/Droid800");
            editor.commit();
        }

        // if old versions of Droid800 have set skipFrames, we reset it here
        String skipFrames = _preferences.getString("skipFrames", "");
        if (!skipFrames.equals("0")) {
            SharedPreferences.Editor editor = _preferences.edit();
            editor.putString("skipFrames", "0");
            editor.commit();
        }

        // restore the keymap from application preferences
        Keymap.getInstance().reload(_preferences, AtariKeys.getInstance());

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.droid800_activity);

        Typeface atariFont = Typeface.createFromAsset(getAssets(), "fonts/ATARCC__.TTF");

        // Find the text editor view inside the layout, because we
        // want to do various programmatic things with it.
        _romFileTextView = (TextView) findViewById(R.id.editor);
        _romFileTextView.setTypeface(atariFont);
        _romFileTextView.setTextScaleX(.75f);
        _osRomFileTextView = (TextView) findViewById(R.id.os_editor);
        _osRomFileTextView.setTypeface(atariFont);
        _osRomFileTextView.setTextScaleX(.75f);
        _osTypeView = (TextView) findViewById(R.id.os_type);
        _osTypeView.setTypeface(atariFont);
        _osTypeView.setTextScaleX(.75f);

        if (romFile == "") {
            _romFileTextView.setText("FILE:");
        }
        else {
            _romFileTextView.setText("FILE:" + FileUtils.removePathAndExtension(romFile).toUpperCase());
            _romPath = romFile;
        }

        if (osRom == "") {
            _osRomFileTextView.setText("OS ROM:");
        }
        else {
            _osRomFileTextView.setText("OS ROM:" + FileUtils.removePathAndExtension(osRom).toUpperCase());
            _osRomPath = osRom;
        }

        _osTypeView.setText("SYSTEM:" + _preferences.getString("systemType", "800XL"));

        Display display = getWindowManager().getDefaultDisplay();
        int[] buttonPos = { 0, 0, 1, 0, 2, 0, 3, 0 };
        ButtonPanel buttonPanel;
        if (display.getWidth() > display.getHeight()) {
            _romFileTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 30f);
            _osRomFileTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 30f);
            _osTypeView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 30f);
            findViewById(R.id.droidActivityLayout).getBackground().setLevel(1);
            buttonPanel = new ButtonPanel(
                this, // context
                null, //atariFont, // custom font 
                4, // number of grid columns
                1, // number of grid rows
                80, // percent of desired width fill
                20, // percent of desired height fill
                50, // x offset (50 = center)
                80, // y offset (50 = center)
                2.5f,// aspect ratio (1=square)
                0); 
        }
        else {
            _romFileTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 20f);
            _osRomFileTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 20f);
            _osTypeView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 20f);
            findViewById(R.id.droidActivityLayout).getBackground().setLevel(0);
            buttonPanel = new ButtonPanel(
                this, // context
                null, //atariFont, // custom font
                2, // number of grid columns
                2, // number of grid rows
                80, // percent of desired width fill
                20, // percent of desired height fill
                50, // x offset (0 = center)
                85, // y offset (0 = center)
                2.5f,// aspect ratio (1=square)
                0); 
            buttonPos[2] = 0;
            buttonPos[3] = 1;
            buttonPos[4] = 1;
            buttonPos[5] = 0;
            buttonPos[6] = 1;
            buttonPos[7] = 1;
        }

        buttonPanel.setPadding(.80f);

        buttonPanel.setButton(buttonPos[0], buttonPos[1],
            Color.argb(128, 38, 38, 38), 
            Color.argb(228, 255, 255, 255), 
            "Load OS",
            new ButtonCallback() {
                public void onButtonUp() {
                    doFindOsRom();
                }
            } );

        buttonPanel.setButton(buttonPos[2], buttonPos[3],
            Color.argb(128, 38, 38, 38), 
            Color.argb(228, 255, 255, 255), 
            "Load File",
            new ButtonCallback() {
                public void onButtonUp() {
                    doFindRom();
                }
            } );

        buttonPanel.setButton(buttonPos[4], buttonPos[5],
            Color.argb(128, 38, 38, 38), 
            Color.argb(228, 255, 255, 255), 
            "Clear",
            new ButtonCallback() {
                public void onButtonUp() {
                    doClear();
                }
            } );

        buttonPanel.setButton(buttonPos[6], buttonPos[7],
            Color.argb(128, 38, 38, 38), 
            Color.argb(228, 255, 255, 255), 
            "PLAY!",
            new ButtonCallback() {
                public void onButtonUp() {
// Allow the emulator to be started without a rom selected.
//                    // if a rom has been selected, run the emulator, otherwise 
//                    // run the rom chooser.
//                    if (null == _osRomPath) {
//                        doFindOsRom();
//                    }
//                    else if (null == _osRomPath) {
//                        doFindRom();
//                    }
//                    else {
//                        doStartEmulator();
//                    }
                    doStartEmulator();
                }
            } );

        AbsoluteLayout al = new AbsoluteLayout(this);
        addContentView(al, new ViewGroup.LayoutParams(display.getWidth(),display.getHeight()));
        buttonPanel.addToLayout((ViewGroup)al);
        buttonPanel.showPanel();

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

// TODO: this is buggy at the moment.
//        // if there already os and programs defined, go straight to emulation.
//        if (null != romFile && romFile.length()>0 && null != osRom && osRom.length()>0 && _startup) {
//            doStartEmulator();
//        }
    }

    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        menu.add(0, PREFERENCES_ID, 0, R.string.preferencesButton).setShortcut('0', 'p');
//        menu.add(0, RELOAD_PREFERENCES_ID, 0, R.string.reloadPreferencesButton).setShortcut('1', 'r');

        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        return true;
    }

    private void reloadDefaultPreferences() {
        final Droid800Activity ctx = this;
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Reload Default Preferences")
            .setMessage("Do you want to reload the default prefernces? This will delete your current settings.")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.clear();
                    ed.commit();
                    PreferenceManager.setDefaultValues(ctx, R.xml.preferences, false);
                    FileUtils.delete(android.os.Environment.getDataDirectory() + "/data/com.droid800/atari800.cfg");
                }
        })
        .setNegativeButton("No", null)
        .show();
    }

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preferences:
                doPreferences();
                return true;
            case R.id.menu_reload:
                reloadDefaultPreferences();
                return true;
            case R.id.menu_about:
                doAbout();
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    /**
     * Launch the about activity, wait for it to return
     */
    void doAbout() {
        Intent launchPreferencesIntent = new Intent(Intent.ACTION_VIEW).setClass(this, About.class);
        startActivityForResult(launchPreferencesIntent, ABOUT_RETURN);
    }

    /**
     * Launch the preferences activity, wait for it to return
     */
    void doPreferences() {
        Intent launchPreferencesIntent = new Intent().setClass(this, Droid800Preferences.class);
        startActivityForResult(launchPreferencesIntent, PREFERENCES_ACTIVITY_RETURN);
    }

    /**
     * Launch the rom finder activity.
     */
    void doFindRom() {
        // TODO: register RomFilter
        Intent launchFindRomIntent = new Intent().setClass(this, AndroidFileBrowser.class);
        startActivityForResult(launchFindRomIntent, FILE_CHOOSER_ACTIVITY_RETURN);
    }

    /**
     * Launch the rom finder activity.
     */
    void doClear() {

        // clear the OS
        SharedPreferences.Editor editor = _preferences.edit();
        editor.putString("osrom", "");
        editor.commit();
        _osRomPath = null;
        _osRomFileTextView.setText("OS ROM:");

        // clear the program 
        editor = _preferences.edit();
        editor.putString("romfile", "");
        editor.commit();
        _romPath = null;
        _romFileTextView.setText("FILE:");
    }

    /**
     * Launch the rom finder activity.
     */
    void doFindOsRom() {
        // TODO: register RomFilter
        Intent launchFindRomIntent = new Intent().setClass(this, AndroidFileBrowser.class);
        startActivityForResult(launchFindRomIntent, OS_FILE_CHOOSER_ACTIVITY_RETURN);
    }

    /**
     * Launch the emulator activity.
     */
    void doStartEmulator() {
        // set the volume limit
        setVolume();
        Intent launchEmulatorIntent = new Intent().setClass(this, MainActivity.class);
        startActivityForResult(launchEmulatorIntent, MAIN_ACTIVITY_RETURN);
    }

    private int[] extractInts(String str) {
        try {
            String strings[] = str.split(",");
            if (null != strings && strings.length > 0) {
                int ints[] = new int[strings.length];
                for (int i=0; i<strings.length; i++) {
                    ints[i] = AndroidKeys.getCode(strings[i]).intValue();
                }
                return ints;
            }
            else {
                return new int[0];
            }
        }
        catch (Throwable e) {
            Log.v("Droid800Activity.extractInts", "Exception: " + e.toString());
            return new int[0];
        }
    }

    private void setVolume() {
        try {
            // if the current volume is higher than our configured max, then
            // lower it.

            AudioManager am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
            _originalVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            int originalVolumePercent = (_originalVolume*100)/am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int configuredMaxVolume = Integer.parseInt(_preferences.getString("volume", "80"));
            if (originalVolumePercent > configuredMaxVolume) {
                float savedVolume = (float)Integer.parseInt(_preferences.getString("volume", "50"));
                float newVolume = (savedVolume / 100f) * am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                am.setStreamVolume (
                    AudioManager.STREAM_MUSIC,
                    (int)newVolume,
                    0);
            }
            else {
                _originalVolume = -1;
            }
        }
        catch (Throwable e) {
            Log.v("Droid800Activity", "error while changing volume: "
                + e.toString());
        }
    }

    private void restoreVolume() {
        try {
            if (_originalVolume != -1) {
                AudioManager am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
                am.setStreamVolume (
                    AudioManager.STREAM_MUSIC,
                    _originalVolume,
                    0);
                _originalVolume = -1;
            }
        }
        catch (Throwable e) {
            Log.v("Droid800Activity", "error while changing volume: "
                + e.toString());
        }
    }

    /**
     * Call back when sub activities finish.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // The preferences returned if the request code is what we had given
        // earlier in startSubActivity
        if (requestCode == PREFERENCES_ACTIVITY_RETURN) {
            // adjust the keymap.
            Keymap.getInstance().reload(_preferences, AtariKeys.getInstance());
            setVolume();
            _osTypeView.setText("SYSTEM:" + _preferences.getString("systemType", "800XL"));
        }
        else if (requestCode == MAIN_ACTIVITY_RETURN) {
            _startup = false;
            AccelerometerJoystick js = AccelerometerJoystick.getInstance();
            if (null != js) {
                Log.v("Droid800Activity", "unregister");
                js.unregister();
            }
        }
        else if (requestCode == FILE_CHOOSER_ACTIVITY_RETURN) {
            // the file chooser returns a fileName
            if (null != data) {
                String romPath = data.getStringExtra("romfile");
                String romDirectory = data.getStringExtra("romdirectory");
                SharedPreferences.Editor editor = _preferences.edit();
                editor.putString("romfile", romPath);
                editor.putString("romdirectory", romDirectory);
                editor.commit();
                _romPath = romPath;
                _romFileTextView.setText("FILE:" + FileUtils.removePathAndExtension(_romPath).toUpperCase());
            }
        }
        else if (requestCode == OS_FILE_CHOOSER_ACTIVITY_RETURN) {
            // the file chooser returns a fileName
            if (null != data) {
                String romPath = data.getStringExtra("romfile");
                String romDirectory = data.getStringExtra("romdirectory");
                SharedPreferences.Editor editor = _preferences.edit();
                editor.putString("osrom", romPath);
                editor.putString("romdirectory", romDirectory);
                editor.commit();
                _osRomPath = romPath;
                _osRomFileTextView.setText("OS ROM:" + FileUtils.removePathAndExtension(_osRomPath).toUpperCase());
            }
        }
    }
}
