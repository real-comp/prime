package com.realcomp.data.record.writer;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.ValidationException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author krenfro
 */
public class FixedFileWriter extends BaseFileWriter{

    protected static final Logger logger = Logger.getLogger(BaseFileWriter.class.getName());
    protected BufferedWriter writer;


    @Override
    public void open(OutputStream out){
        close();
        super.open(out);
        writer = new BufferedWriter(new OutputStreamWriter(out));
    }

    @Override
    public void close(){
        if (writer != null)
            IOUtils.closeQuietly(writer);
        super.close();
    }


    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException{

        super.write(record);
        writer.newLine();
    }

    @Override
    protected void write(Record record, SchemaField field)
            throws ValidationException, ConversionException, IOException{

        writer.write(resize(toString(record, field), field.getLength()));
    }

    protected String resize(String s, int length){
        return StringUtils.rightPad(s, length).substring(0, length);
    }

    @Override
    public void setSchema(FileSchema schema) throws SchemaException {
        ensureFieldLengthsSpecified(schema.getFields());
        super.setSchema(schema);
    }


    protected void ensureFieldLengthsSpecified(List<SchemaField> fields) throws SchemaException{
        for (SchemaField field: fields)
            if (field.getLength() <= 0)
                throw new SchemaException("field length not specified for: " + field);
    }

    
    protected int getExpectedLength(List<SchemaField> fields){

        assert(fields != null);
        int retVal = 0;
        for (SchemaField field: fields)
            retVal = retVal + field.getLength();
        return retVal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FixedFileWriter other = (FixedFileWriter) obj;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
