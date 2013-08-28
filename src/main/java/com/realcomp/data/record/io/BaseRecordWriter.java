package com.realcomp.data.record.io;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author krenfro
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

        write(record, schema.classify(record));
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
