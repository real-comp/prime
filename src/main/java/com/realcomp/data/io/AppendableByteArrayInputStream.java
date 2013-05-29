package com.realcomp.data.io;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ByteArrayInputStream that supports appending byte[] after the InputStream has been constructed.
 * <p>Not fit for general use.</p>
 * <p>Similar to the PipedInput/OutputStream in the JDK, but safe for a single Thread.</p>
 *
 * The realcomp-data IOContext contains a single InputStream that is used to read Records. In Hadoop, each Map operation
 * consists of a single Text entry being read from an InputSplit. It is prohibitively expensive to construct a new
 * realcomp-data IOContext and RecordReader for each record in the InputSplit.
 *
 * This special InputStream allows for Text objects (converted to byte[]) from the InputSplit to be 'appended' to the
 * InputStream. The realcomp-data RecordReader can be instantiated once for multiple Hadoop map() operations.
 *
 * The append() should be followed by a single RecordReader.read() operation.
 *
 *
 * @author krenfro
 */
public class AppendableByteArrayInputStream extends ByteArrayInputStream{

    private Queue<byte[]> data;

    public AppendableByteArrayInputStream(){
        this(new byte[0]);
    }

    public AppendableByteArrayInputStream(byte[] initial){
        super(initial);
        data = new LinkedList<byte[]>();
    }

    public synchronized void append(byte[] bytes){
        data.add(bytes);
    }

    @Override
    public synchronized int read(){
        int result = super.read();

        if (result == -1 && !data.isEmpty()){
            reloadBuffer();
            result = super.read();
        }

        return result;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len){

        if (b == null){
            throw new NullPointerException();
        }
        else if (off < 0 || len < 0 || len > b.length - off){
            throw new IndexOutOfBoundsException();
        }
        if (pos >= count){
            reloadBuffer();
            if (pos >= count){
                return -1;
            }
        }

        if (pos + len > count){
            len = count - pos;
        }
        if (len <= 0){
            reloadBuffer();
            len = count - pos;
            if (len <= 0){
                return 0;
            }
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    private void reloadBuffer(){
        if (!data.isEmpty()){
            this.buf = data.remove();
            this.pos = 0;
            this.count = buf.length;
            this.mark = 0;
        }
    }

    @Override
    public synchronized int available(){
        int result = super.available();
        if (result == 0){
            reloadBuffer();
            result = super.available();
        }

        return result;
    }

    @Override
    public boolean markSupported(){
        return false;
    }
}
