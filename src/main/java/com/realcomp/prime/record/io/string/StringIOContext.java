package com.realcomp.prime.record.io.string;

import com.realcomp.prime.io.AppendableByteArrayInputStream;
import com.realcomp.prime.record.io.*;

/**
 * Unmodifiable context one-at-a-time String based I/O operations.
 *
 */
public class StringIOContext extends IOContext{

    protected StringIOContext(IOContextBuilder builder){
        super(builder);
    }

    public void append(String next){
        ((AppendableByteArrayInputStream) in).append(next.getBytes());
    }
}
