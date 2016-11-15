package com.realcomp.prime.record.io;

import com.realcomp.prime.schema.FieldList;
import com.realcomp.prime.schema.SchemaException;
import java.io.IOException;
import java.util.logging.Logger;

public abstract class BaseRecordReader extends BaseRecordReaderWriter implements RecordReader{

    private static final Logger logger = Logger.getLogger(BaseRecordReader.class.getName());


    protected FieldList defaultFieldList;
    protected int fieldListCount;

    public BaseRecordReader(){
        super();
    }

    public BaseRecordReader(BaseRecordReader copy){
        super(copy);
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException{
        super.open(context);
        if (schema != null){
            /* As an optimization during classification, cache the default FieldList and number of
             * FieldLists in the schema.
             */
            defaultFieldList = schema.getDefaultFieldList();
            fieldListCount = schema.getFieldLists().size();
        }
    }
}
