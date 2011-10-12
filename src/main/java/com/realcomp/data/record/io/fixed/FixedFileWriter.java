package com.realcomp.data.record.io.fixed;

import com.realcomp.data.record.io.BaseRecordWriter;
import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.validation.ValidationException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author krenfro
 */
public class FixedFileWriter extends BaseRecordWriter{

    protected static final Logger logger = Logger.getLogger(BaseRecordWriter.class.getName());
    protected BufferedWriter writer;
    protected boolean header = false;


    public FixedFileWriter(){
        super();
    }
    
    @Override
    public void open(OutputStream out) throws IOException{
        close();
        super.open(out);
        writer = new BufferedWriter(new OutputStreamWriter(out, charset));
    }


    @Override
    public void close(){
        if (writer != null)
            IOUtils.closeQuietly(writer);
        super.close();
    }


    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException{

        //optionally write header record
        if (!beforeFirstOperationsRun && header){
            writeHeader();
        }
        
        super.write(record);
        writer.newLine();
    }
    
    
    /**
     * Write a header record, constructed from a Record.
     * 
     * @param record
     * @throws IOException
     * @throws ValidationException
     * @throws ConversionException
     */
    protected void writeHeader() throws IOException, ValidationException, ConversionException{

        //No operations should be run on the Record, so a temporary schema
        // is created with no operations.
        try {
            FileSchema originalSchema = getSchema();
            FileSchema headerSchema = new FileSchema(getSchema());
            for (FieldList fields : headerSchema.getFieldLists()){
                for (Field field: fields)
                    field.clearOperations();
            }
            
            setSchema(headerSchema);
            super.write(getHeader());
            writer.newLine();
            writer.flush();
            setSchema(originalSchema); //put back the original schema
        }
        catch (SchemaException ex) {
            throw new IOException("Unable to create temporary header schema: " + ex.getMessage());
        }
    }
    
    
    protected Record getHeader(){
        Record retVal = new Record();
        for(Field field: schema.getDefaultFieldList())
            retVal.put(field.getName(), field.getName());
        return retVal;
    }
    

    @Override
    protected void write(Record record, Field field)
            throws ValidationException, ConversionException, IOException{

        context.setRecord(record);
        context.setKey(field.getName());
        List<Object> values = surgeon.operate(field.getOperations(), context);
        writer.write(resize((String) DataType.STRING.coerce(values.isEmpty() ? "" : values.get(0)), field.getLength()));
    }

    protected String resize(String s, int length){
        return StringUtils.rightPad(s, length).substring(0, length);
    }

    @Override
    public void setSchema(FileSchema schema) throws SchemaException {
        for (FieldList fields: schema.getFieldLists())
            ensureFieldLengthsSpecified(fields);
        super.setSchema(schema);
    }


    protected void ensureFieldLengthsSpecified(FieldList fields) throws SchemaException{
        for (Field field: fields)
            if (field.getLength() <= 0)
                throw new SchemaException("field length not specified for: " + field);
    }

    
    protected int getExpectedLength(List<Field> fields){

        assert(fields != null);
        int retVal = 0;
        for (Field field: fields)
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
        if (this.header != other.header)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.header ? 1 : 0);
        return hash;
    }

}
