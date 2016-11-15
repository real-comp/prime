package com.realcomp.prime.record.io;

import java.io.IOException;

/**
 * A common pattern to perform work on multiple Records.
 *
 */
public interface RecordProcessor extends AutoCloseable{

    
    /**
     * @param reader
     * @param writer
     * @return number of leads processed
     * @throws IOException 
     */
    long process(RecordReader reader, RecordWriter writer) throws IOException;
    
    @Override
    void close() throws IOException;
}
