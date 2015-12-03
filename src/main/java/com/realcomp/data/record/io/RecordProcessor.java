package com.realcomp.data.record.io;

import java.io.Closeable;
import java.io.IOException;


public interface RecordProcessor extends Closeable{
    
    /**
     * @param reader
     * @param writer
     * @return number of leads processed
     * @throws IOException 
     */
    long process(RecordReader reader, RecordWriter writer) throws IOException;
    
    @Override
    void close();
}
