package com.realcomp.data.record.io;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A common pattern to do work on groups of Records.
 */
public abstract class GroupingRecordProcessor implements RecordProcessor, Closeable{

    protected int groupSize;

    public GroupingRecordProcessor(int groupSize){
        if (groupSize < 0){
            throw new IllegalArgumentException("groupSize < 0");
        }
        this.groupSize = groupSize;
    }


    public abstract long process(List<Record> records, RecordWriter writer) throws IOException;

    /**
     * @param reader
     * @param writer
     * @return number of leads processed
     * @throws IOException 
     */
    public long process(RecordReader reader, RecordWriter writer) throws IOException{

        long count = 0;
        List<Record> current = new ArrayList<>();
        try {
            Record record = reader.read();
            while (record != null) {
                current.add(record);
                if (current.size() >= groupSize) {
                    count += process(current, writer);
                    current = new ArrayList<>();
                }
                record = reader.read();
            }
            if (current.size() >= 0) {
                count += process(current, writer);
            }
        }
        catch (ValidationException | ConversionException | SchemaException ex) {
            throw new IOException(ex);
        }

        return count;
    }


    @Override
    public void close() throws IOException{
    }
}
