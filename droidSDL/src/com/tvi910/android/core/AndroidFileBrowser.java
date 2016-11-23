package com.tvi910.android.core;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;
import android.content.SharedPreferences;

import com.tvi910.android.R;

public class AndroidFileBrowser extends ListActivity {
    private static final String TAG = "AndroidFileBrowser";

    public static interface Filter {
        /**
         * Filter determines whether or not File can appear in teh file 
         * browser. 
         *
         * @param file the file to filter. 
         * @returns boolean true if the file should appear in the browser, n
         *     false if not.
         */
        public boolean filter(File file);
    }

	private ArrayList<String> directoryEntries = new ArrayList<String>();
    TextView _pathTextView = null;

    private SharedPreferences _settings = null;
    private ArrayList<Filter> _filterList = null;

	private static File currentDirectory = null;

    private static final String ROMPATH_KEY = "romfile";
    private static final String DIRECTORY_KEY = "romdirectory";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.android_file_browser);

        _pathTextView = (TextView)findViewById(R.id.fileBrowserPath);

        // restore saved preferences
        _settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // set up the initial directory
        String romPath = _settings.getString(DIRECTORY_KEY, "/");
        Log.d(TAG, "romPath = " + romPath);
        currentDirectory = openRoot(romPath);

        // TODO: get Filter implementations from the icicle and register them.
		browseToRoot();
	}

    private File openRoot(String path) {
        try {
            File f = new File(path);
            if (f.exists() && f.isDirectory()) {
                return f;
            }
            else if (null != f.getParent()) {
                return openRoot(f.getParent());
            }
            else {
                return null;
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

	/**
	 * This function browses to the
	 * root-directory of the file-system.
	 */
	private void browseToRoot() {
		browseTo(currentDirectory);
    }

	/**
	 * This function browses up one level
	 * according to the field: currentDirectory
	 */
	private void upOneLevel(){
		if(this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}

	private void browseTo(final File aDirectory){
		if (aDirectory.isDirectory()){
            File ptr = this.currentDirectory;
			this.currentDirectory = aDirectory;
            try {
                File[] files = aDirectory.listFiles();
                if (null == files) {
                    Log.d(TAG, "files null");
                    this.currentDirectory = ptr;
                    fill(this.currentDirectory.listFiles());
                }
                else {
                    Log.d(TAG, "files are good");
                    fill(aDirectory.listFiles());
                }
            }
            catch (Throwable e) {
                Log.d(TAG, "exception = " + e.getMessage());
                this.currentDirectory = ptr;
                fill(this.currentDirectory.listFiles());
            }
		} else {
            Intent i = new Intent();
            i.putExtra(ROMPATH_KEY, aDirectory.getAbsolutePath());
            i.putExtra(DIRECTORY_KEY, this.currentDirectory.getAbsolutePath());
            setResult(RESULT_OK, i);
            finish();
		}
	}

	private void fill(File[] files) {

        // files can be null, we set it to be an empty array and log the
        // incident
        if (null == files) {
            files = new File[0];
            Log.v("com.tvi910.android.core.AndroidFileBrowser", "files is null in fill()");
        }

		this.directoryEntries.clear();

		// Add the "." and the ".." == 'Up one level'
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
		if (this.currentDirectory.getParent() != null) {
//			this.directoryEntries.add("../");
            currentPathStringLenght++;
        }

        for (File file : files) {
            if (file.canRead()) {
                if (applyFilters(file)) {
                    if (file.isDirectory()) {
                        this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght) + "/");
                    }
                    else {
                        this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght));
                    }
                }
            }
        }

        Collections.sort(directoryEntries);

		if (this.currentDirectory.getParent() != null) {
			this.directoryEntries.add(0, "../");
        }
//		this.directoryEntries.add(0, "[ " + this.currentDirectory.getAbsolutePath() + " ]");
        _pathTextView.setText("[ " + this.currentDirectory.getAbsolutePath() + " ]");


		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.file_row, this.directoryEntries);

		this.setListAdapter(directoryList);
	}

    private boolean applyFilters(File file) {
        if (null == _filterList) {
            return true;
        }
        else {
            for (Filter f : _filterList) {
                if (!f.filter(file)) {
                    return false;
                }
            }
        }
        return true;
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
        int selectionRowID = position;
		String selectedFileString = this.directoryEntries.get(selectionRowID);
		if (selectedFileString.equals("[ " + this.currentDirectory.getAbsolutePath()  + " ]")) {
			// Refresh
			this.browseTo(this.currentDirectory);
		} else if(selectedFileString.equals("../")){
			this.upOneLevel();
		} else {
			File clickedFile = new File(this.currentDirectory.getAbsolutePath() + "/"
                + this.directoryEntries.get(selectionRowID));
			if(clickedFile != null)
				this.browseTo(clickedFile);
		}
	}
}
