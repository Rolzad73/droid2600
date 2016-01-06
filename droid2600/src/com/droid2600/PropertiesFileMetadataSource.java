package com.droid2600;

import com.tvi910.android.core.util.FileUtils;

import com.tvi910.mediadb.Metadata;
import com.tvi910.mediadb.MetadataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.BufferedReader;


public class PropertiesFileMetadataSource implements MetadataSource {

    private String _propertiesFilename;

    public PropertiesFileMetadataSource(String propertiesFile) {
        _propertiesFilename = propertiesFile;
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

    public Metadata findMetadataForMd5(String md5) {

        String name = null;
        String publisher = "";
        boolean foundMd5 = false;
        BufferedReader br = null;
        File propertiesFile = new File(_propertiesFilename);

        try {
            br = new BufferedReader(new FileReader(propertiesFile));
            String line = null;
            while (null != (line = br.readLine())) {
                if (line.length() == 0) {
                    if (foundMd5 && null != name) {
                        return new Metadata(md5, name);
                    }
                }
                else if (false == foundMd5) {
                    if (line.indexOf("Cartridge.MD5") == 1) {
                        if (md5.equals(line.substring(line.indexOf(" \"")+2, line.lastIndexOf("\"")))) {
                            foundMd5 = true;
                            System.out.println("found md5: " + md5);
                        }
                    }
                }
                else if (line.indexOf("Cartridge.Name") == 1) {
                    name = line.substring(line.indexOf(" \"")+2, line.lastIndexOf("\""));
                }
                else if (line.indexOf("Cartridge.Manufacturer") == 1) {
                    publisher = line.substring(line.indexOf(" \"")+2, line.lastIndexOf("\""));
                }
            }
            if (foundMd5 && null != name) {
                return new Metadata(md5, name);
            }
            else {
                return null;
            }
        }
        catch (Throwable e) {
            return null;
        }
        finally {
            if (null != br) {
                try {
                    br.close();
                }
                catch (Throwable ee) {
                    ee.printStackTrace();
                }
            }
        }
    }
}
