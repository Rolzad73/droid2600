package com.tvi910.android.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.security.MessageDigest;

public class FileUtils {

    public static String removePathAndExtension(String s) {

        String retVal;

        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf("/");
        if (lastSeparatorIndex == -1) {
            retVal = s;
        } else {
            retVal = s.substring(lastSeparatorIndex + 1);
        }

        // Remove the extension.
        int extensionIndex = retVal.lastIndexOf(".");
        if (extensionIndex == -1) {
            return retVal;
        }

        return retVal.substring(0, extensionIndex);
    }

    public static String md5Digest(String filename) 
    throws Exception {
        InputStream fis = null;
        try {
            fis =  new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            byte[] b = complete.digest();
            StringBuffer result = new StringBuffer();
            for (int i=0; i < b.length; i++) {
                result.append(
                    Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 ));
            }

            return result.toString();
        }
        finally {
            if (null != fis) {
                fis.close();
            }
        }
    }

    /**
     * Quietly delete a file. Swallows all fs related errors.
     */
    public static void delete(String fileName) {
        try {
            File f = new File(fileName);

            // Make sure the file or directory exists and isn't write protected
            if (f.exists() && f.canWrite() && !f.isDirectory()) {
                f.delete();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
