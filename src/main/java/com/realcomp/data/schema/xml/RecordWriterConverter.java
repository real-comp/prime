package com.realcomp.data.schema.xml;

import com.realcomp.data.record.writer.RecordWriter;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a RecordWrier
 * 
 * @author krenfro
 */
public class RecordWriterConverter extends RecordReaderConverter{


    @Override
    public boolean canConvert(Class type){
        return RecordWriter.class.isAssignableFrom(type);
    }

}
