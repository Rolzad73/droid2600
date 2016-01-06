package com.droid2600;

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
import android.widget.EditText;
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

import com.tvi910.mediadb.Metadata;
import com.tvi910.mediadb.MetadataSource;

/**
 * Droid2600Activity - main activity for the Droid2600 application.
 */
public class Droid2600Activity extends Activity {

    static final private int PREFERENCES_ID = Menu.FIRST;
    static final private int PREFERENCES_ACTIVITY_RETURN = Menu.FIRST + 3;
    static final private int FILE_CHOOSER_ACTIVITY_RETURN = Menu.FIRST + 4;
    static final private int MAIN_ACTIVITY_RETURN = Menu.FIRST + 5;
    static final private int RELOAD_PREFERENCES_ID = Menu.FIRST + 6;
    static final private int ABOUT_ID = Menu.FIRST + 7;
    static final private int ABOUT_RETURN = Menu.FIRST + 8;

    private TextView _romFileTextView;

    // handle to default shared preferences.
    private SharedPreferences _preferences = null;

    // save the original volume so it can be restored when the emulator exits
    private int _originalVolume = -1;

    private String _romPath = null;

    private MetadataSource _metadataSource = null;

    public Droid2600Activity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        AtariKeys.init();
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        _preferences = PreferenceManager.getDefaultSharedPreferences(this);

        _metadataSource = StellaMetadataSource.getInstance();

        String romFile = _preferences.getString("romfile", "");
        String romDirectory = _preferences.getString("romdirectory", "");

        // if there is no rom directory, then set /sdcard/Droid2600 as 
        // the default.
        if ("".equals(romDirectory)) {
            SharedPreferences.Editor editor = _preferences.edit();
            editor.putString("romdirectory", "/sdcard/Droid2600");
            editor.commit();
        }

        // restore the keymap from application preferences
        Keymap.getInstance().reload(_preferences, AtariKeys.getInstance());

        // Inflate our UI from its XML layout description.
        setContentView(R.layout.droid2600_activity);

        Typeface atariFont = Typeface.createFromAsset(getAssets(), "fonts/Erasdemi.ttf");

        // grab the textview
        _romFileTextView = (TextView)findViewById(R.id.romFileTextView);
        _romFileTextView.setTypeface(atariFont);

        if (null == romFile || romFile == "") {
            _romFileTextView.setText("");
        }
        else {
            Metadata md = _metadataSource.findMetadataForFile(romFile);
            if (null == md || md.getTitle().length() == 0 || md.getTitle().lastIndexOf("(") <= 0) {
                _romFileTextView.setText(FileUtils.removePathAndExtension(romFile).toUpperCase());
            }
            else {
                _romFileTextView.setText(md.getTitle().substring(0,md.getTitle().lastIndexOf("(")));
            }
            _romPath = romFile;
        }


        Display display = getWindowManager().getDefaultDisplay();

        ButtonPanel buttonPanel;
        if (display.getWidth() > display.getHeight()) {
            _romFileTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 40f);
            findViewById(R.id.droidActivityLayout).getBackground().setLevel(1);
            buttonPanel = new ButtonPanel(
                this, // context
                atariFont, // custom font
                2, // number of grid columns
                1, // number of grid rows
                50, // percent of desired width fill
                20, // percent of desired height fill
                50, // x offset (50 = center)
                75, // y offset (50 = center)
                3f,
                0); // slider button
        }
        else {
            _romFileTextView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 30f);
            findViewById(R.id.droidActivityLayout).getBackground().setLevel(0);
            buttonPanel = new ButtonPanel(
                this, // context
                atariFont, // custom font
                2, // number of grid columns
                1, // number of grid rows
                90, // percent of desired width fill
                15, // percent of desired height fill
                50, // x offset (0 = center)
                75, // y offset (0 = center)
                3f,
                0); // slider button
        }

        buttonPanel.setButton(0,0, 
            Color.argb(255, 38, 38, 38), 
            Color.argb(228, 255, 201, 59), 
            "Select ROM",
            new ButtonCallback() {
                public void onButtonUp() {
                    doFindRom();
                }
            } );

        buttonPanel.setButton(1,0, 
            Color.argb(255, 38, 38, 38), 
            Color.argb(228, 255, 201, 59), 
            "PLAY!",
            new ButtonCallback() {
                public void onButtonUp() {
                    // if a rom has been selected, run the emulator, otherwise 
                    // run the rom chooser.
                    if (null == _romPath) {
                        doFindRom();
                    }
                    else {
                        doStartEmulator();
                    }
                }
            } );

        AbsoluteLayout al = new AbsoluteLayout(this);
        addContentView(al, new ViewGroup.LayoutParams(display.getWidth(),display.getHeight()));
        buttonPanel.addToLayout((ViewGroup)al);
        buttonPanel.showPanel();

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
//        menu.add(0, ABOUT_ID, 0, R.string.aboutButton).setShortcut('2', 'r');
//        return true;


        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        return true;

    }

    private void reloadDefaultPreferences() {
        final Droid2600Activity ctx = this;
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
     * Launch the preferences activity, wait for it to return
     */
    void doPreferences() {
        Intent launchPreferencesIntent = new Intent(Intent.ACTION_VIEW).setClass(this, Droid2600Preferences.class);
        startActivityForResult(launchPreferencesIntent, PREFERENCES_ACTIVITY_RETURN);
    }

    /**
     * Launch the about activity, wait for it to return
     */
    void doAbout() {
        Intent launchPreferencesIntent = new Intent(Intent.ACTION_VIEW).setClass(this, About.class);
        startActivityForResult(launchPreferencesIntent, ABOUT_RETURN);
    }

    /**
     * Launch the rom finder activity.
     */
    void doFindRom() {
        // TODO: register rom filter
        Intent launchFindRomIntent = new Intent().setClass(this, AndroidFileBrowser.class);
        startActivityForResult(launchFindRomIntent, FILE_CHOOSER_ACTIVITY_RETURN);
    }

    /**
     * Launch the emulator activity.
     */
    void doStartEmulator() {
        // set the volume limit
        setVolume();
        Intent launchEmulatorIntent = new Intent(Intent.ACTION_VIEW).setClass(this, MainActivity.class);
        startActivityForResult(launchEmulatorIntent, MAIN_ACTIVITY_RETURN);
    }

    /**
     * A call-back for when the user presses the clear button.
     */
    OnTouchListener _gameRomTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP: {
                    doFindRom();
                }
            }
            return false;
        }
    };

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
            Log.v("Droid2600Activity.extractInts", "Exception: " + e.toString());
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
            Log.v("Droid2600Activity", "error while changing volume: "
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
            Log.v("Droid2600Activity", "error while changing volume: "
                + e.toString());
        }
    }

    private void writeConfig() {
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            StringBuffer sb = new StringBuffer();
//            sb.append("timing = " + prefs.getString("timing", "Sleep") + "\n");
            if (prefs.getBoolean("sound", true)) {
                sb.append("sound = 1\n");
            }
            else {
                sb.append("sound = 0\n");
            }
            sb.append("volume = " + prefs.getString("volume", "50") + "\n");
            sb.append("uimessages = 1\n");
            String s = sb.toString();
            FileOutputStream fos = new FileOutputStream(new File(
                android.os.Environment.getDataDirectory()
                + "/data/com.droid2600/droid2600config"));
            fos.write(s.getBytes());
            fos.close();
        }
        catch (Throwable e) {
            Log.v("Droid2600Activity.writeConfig", "Exception: " + e.toString());
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
            //reloadKeymap();
            Keymap.getInstance().reload(_preferences, AtariKeys.getInstance());
//            writeConfig();
            setVolume();
        }
        else if (requestCode == MAIN_ACTIVITY_RETURN) {
            AccelerometerJoystick js = AccelerometerJoystick.getInstance();
            if (null != js) {
                Log.v("Droid2600Activity", "unregister");
                js.unregister();
            }
        }
        else if (requestCode == FILE_CHOOSER_ACTIVITY_RETURN) {
            // the file chooser returns a fileName
            if (null != data) {
                _romPath = data.getStringExtra("romfile");
                String romDirectory = data.getStringExtra("romdirectory");
                SharedPreferences.Editor editor = _preferences.edit();
                editor.putString("romfile", _romPath);
                editor.putString("romdirectory", romDirectory);
                editor.commit();

                Metadata md = _metadataSource.findMetadataForFile(_romPath);

                if (null == md || md.getTitle().length() == 0) {
                    _romFileTextView.setText(FileUtils.removePathAndExtension(_romPath).toUpperCase());
                }
                else {
                    _romFileTextView.setText(md.getTitle().substring(0,md.getTitle().lastIndexOf("(")));
                }
//                _romFileTextView.setText(FileUtils.removePathAndExtension(_romPath).toUpperCase());
//                _romFileTextView.setText(gameName);
            }
        }
    }
}
