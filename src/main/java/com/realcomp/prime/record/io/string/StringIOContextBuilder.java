package com.realcomp.prime.record.io.string;

import com.realcomp.prime.io.AppendableByteArrayInputStream;
import com.realcomp.prime.record.io.*;
import java.io.InputStream;
import java.io.OutputStream;

public class StringIOContextBuilder extends IOContextBuilder{

    public StringIOContextBuilder(){
        super();
        in = new AppendableByteArrayInputStream();
    }

    public StringIOContextBuilder(IOContext context){
        super(context);
        in = new AppendableByteArrayInputStream();
    }

    @Override
    public IOContextBuilder in(InputStream in){
        if (in instanceof AppendableByteArrayInputStream){
            return super.in(in);
        }
        else{
            throw new UnsupportedOperationException("InputStream must be instance of AppendableByteArrayInputStream.");
        }
    }

    @Override
    public IOContextBuilder out(OutputStream out){
        throw new UnsupportedOperationException("output not supported.");
    }

    @Override
    public StringIOContext build(){
        return new StringIOContext(this);
    }
}
