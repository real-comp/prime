package com.realcomp.prime.record.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * A RecordProcessor that writes filtered records to a RecordWriter.
 * 
 */
public interface RecordFilter extends Closeable, RecordProcessor{
    
    /**
     * @param reader
     * @param writer
     * @param filtered filtered records written here
     * @return number of records processed
     * @throws IOException 
     */
    long filter(RecordReader reader, RecordWriter writer, RecordWriter filtered) throws IOException;
}
