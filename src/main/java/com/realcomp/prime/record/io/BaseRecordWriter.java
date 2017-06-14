package com.realcomp.prime.record.io;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.validation.RecordValidationException;
import com.realcomp.prime.validation.ValidationException;

import java.io.IOException;
import java.util.List;

/**
 *
 *
 * The format specific implementation should not write partial records.
 */
public abstract class BaseRecordWriter extends BaseRecordReaderWriter implements RecordWriter{

    public BaseRecordWriter(){
        super();
    }

    public BaseRecordWriter(BaseRecordWriter copy){
        super(copy);
    }

    @Override
    public void write(Record record)
            throws IOException, ValidationException, ConversionException, SchemaException{

        if (schema == null){
            throw new IllegalStateException("schema not specified");
        }
        if (record == null){
            throw new IllegalArgumentException("record is null");
        }

        if (!beforeFirstOperationsRun){
            executeBeforeFirstOperations();
            beforeFirstOperationsRun = true;
        }

        try{
            write(record, schema.classify(record));
        }
        catch(ValidationException ex){
            if (ex instanceof RecordValidationException){
                ((RecordValidationException) ex).setRecord(record);
                throw ex;
            }
            else{
                throw new RecordValidationException(ex, record);
            }
        }
        count++;
    }

    protected void write(Record record, FieldList fields)
            throws ValidationException, ConversionException, IOException{

        for (Field field : fields){
            write(record, field);
        }
    }

    protected abstract void write(Record record, Field field)
            throws ValidationException, ConversionException, IOException;

    /**
     * @param record
     * @return first two fields from the record
     * @throws SchemaException
     */
    protected String getRecordIdentifier(Record record) throws SchemaException{
        if (record == null || record.isEmpty()){
            return "";
        }
        String id = "";
        List<Field> fields = schema.classify(record);
        if (fields.size() > 0){
            id = id.concat(record.get(fields.get(0).getName()).toString());
        }

        if (fields.size() > 1){
            id = id.concat(":");
            id = id.concat(record.get(fields.get(0).getName()).toString());
        }

        return id;
    }
}
