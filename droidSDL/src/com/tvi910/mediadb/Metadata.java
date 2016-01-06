package com.tvi910.mediadb;

/**
 * Structure for holding media metadata.
 */
public class Metadata {

    // the title of the content.
    private String _title;

    // an md5sum of the content
    private String _md5sum;

    public Metadata(String md5sum, String title) {
        _title = title;
        _md5sum = md5sum;
    }

    public String getTitle() { return _title; }
    public String getMd5sum() { return _md5sum; }
}
