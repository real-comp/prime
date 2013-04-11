package com.realcomp.data.record.io.string;

import com.realcomp.data.io.AppendableByteArrayInputStream;
import com.realcomp.data.record.io.*;

/**
 * Unmodifiable context one-at-a-time String based I/O operations.
 *
 * @author krenfro
 */
public class StringIOContext extends IOContext{

    protected StringIOContext(IOContextBuilder builder){
        super(builder);
    }

    public void append(String next){
        ((AppendableByteArrayInputStream) in).append(next.getBytes());
    }
}
