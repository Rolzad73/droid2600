package com.tvi910.mediadb;

/**
 * Interface for getting ContentMetadata from a source implementation.
 */
public interface MetadataSource {

    /**
     * Look up metadata for the specified file. An md5 sum of te file will 
     * be made to accomplish the lookup.
     * 
     * @param file
     */
    public Metadata findMetadataForFile(String file);

    /** 
     * Look up metadata for the md5 of a resource
     */
    public Metadata findMetadataForMd5(String md5);
}
    
