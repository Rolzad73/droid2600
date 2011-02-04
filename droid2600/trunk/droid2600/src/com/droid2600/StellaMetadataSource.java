package com.droid2600;

import com.tvi910.android.core.util.FileUtils;

import com.tvi910.mediadb.Metadata;
import com.tvi910.mediadb.MetadataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.BufferedReader;

public class StellaMetadataSource implements MetadataSource {

    private static final StellaMetadataSource _instance 
        = new StellaMetadataSource();

    public static StellaMetadataSource getInstance() {
        return _instance;
    }

    private StellaMetadataSource() {
        System.loadLibrary("sdl");
        System.loadLibrary("application");
    }

    public Metadata findMetadataForFile(String file) {
        try {
            String md5sum = FileUtils.md5Digest(file);
            if (null != md5sum) {
                return findMetadataForMd5(md5sum);
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    /** 
     * Look up metadata for the md5 of a resource
     */
    public Metadata findMetadataForMd5(String md5) {
        try {
            String title = nativeLookupCartTitle(md5);
            if (null != title) return new Metadata(md5, title);
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public native String nativeLookupCartTitle(String md5);
}
