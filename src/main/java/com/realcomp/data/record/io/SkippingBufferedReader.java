package com.realcomp.data.record.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Special BufferedReader that allows for the skipping of leading and trailing lines.
 *
 * @author krenfro
 */
public class SkippingBufferedReader extends Reader{

    protected BufferedReader reader;
    protected int skipLeading = 0;
    protected int skipTrailing = 0;
    protected int length = 0;
    protected Queue<String> queue = null;

    public SkippingBufferedReader(Reader in){
        reader = new BufferedReader(in);
    }

    public int getSkipLeading(){
        return skipLeading;
    }

    public void setSkipLeading(int leading){
        if (leading < 0){
            throw new IllegalArgumentException("leading < 0");
        }
        this.skipLeading = leading;
    }

    public int getSkipTrailing(){
        return skipTrailing;
    }

    public void setSkipTrailing(int trailing){
        if (trailing < 0){
            throw new IllegalArgumentException("trailing < 0");
        }

        this.skipTrailing = trailing;
    }

    public int getLength(){
        return length;
    }

    public void setLength(int length){
        if (length < 0){
            throw new IllegalArgumentException("length < 0");
        }
        this.length = length;
    }

    public String readLine() throws IOException{
        fillQueue();
        return queue.size() <= skipTrailing ? null : queue.remove();
    }

    private void fillQueue() throws IOException{

        if (queue == null){
            queue = new LinkedList<>();

            //skip leading records
            for (int x = 0; x < skipLeading; x++){
                readLine();
            }
        }

        //fill queue to trailing + 1 records
        String record;
        for (int x = queue.size(); x <= skipTrailing; x++){
            record = readRecord();
            if (record != null){
                queue.add(record);
            }
        }
    }

    private String readFixedLengthString() throws IOException{
        if (length < 0){
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        int off = 0;
        char[] cbuf = new char[length];
        int len = length;
        while (n < len){
            int count = reader.read(cbuf, off + n, len - n);
            if (count < 0){
                return null;
            }
            n += count;
        }
        return new String(cbuf);
    }

    private String readRecord() throws IOException{
        String record;
        if (length == 0){
            record = reader.readLine();
        }
        else{
            record = readFixedLengthString();
        }

        return record;
    }

    @Override
    public void close() throws IOException{
        reader.close();
        queue = null;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException{
        return reader.read(cbuf, off, len);
    }
}
