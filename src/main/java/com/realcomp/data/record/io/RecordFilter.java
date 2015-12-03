package com.realcomp.data.record.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * A RecordProcessor that writes filtered records to a RecordWriter.
 * 
 * @author krenfro
 */
public interface RecordFilter extends Closeable, RecordProcessor{
    
    /**
     * @param reader
     * @param writer
     * @param filtered filtered records written here
     * @return number of records processed
     * @throws IOException 
     */
    public abstract long filter(RecordReader reader, RecordWriter writer, RecordWriter filtered) throws IOException;
}
