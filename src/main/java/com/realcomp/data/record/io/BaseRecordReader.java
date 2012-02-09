package com.realcomp.data.record.io;

import com.realcomp.data.Operation;
import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.schema.BeforeFirstField;
import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.transform.TransformContext;
import com.realcomp.data.transform.Transformer;
import com.realcomp.data.transform.ValueSurgeon;
import com.realcomp.data.validation.ValidationException;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author krenfro
 */
public abstract class BaseRecordReader implements RecordReader{

    private static final Logger logger = Logger.getLogger(BaseRecordReader.class.getName());

    protected RecordFactory recordFactory;
    protected int skipLeading = 0;
    protected int skipTrailing = 0;

    @XStreamOmitField
    protected long count;
        
    protected boolean beforeFirstOperationsRun = false;  
    protected TransformContext transformContext;
    protected ValueSurgeon surgeon;
    protected IOContext ioContext;

    public BaseRecordReader(){
        transformContext = new TransformContext();
        surgeon = new ValueSurgeon();
    }
    
    public BaseRecordReader(BaseRecordReader copy){
        skipLeading = copy.skipLeading;
        skipTrailing = copy.skipTrailing;
    }
    

    /**
     *
     * @return number of leading records to skip. default 0
     */
    public int getSkipLeading() {
        return skipLeading;
    }

    /**
     *
     * @param skipLeading number of leading records to skip. >= 0
     */
    public void setSkipLeading(int skipLeading) {
        if (skipLeading < 0)
            throw new IllegalArgumentException(
                    String.format("skipLeading out of range: %s < 0", skipLeading));
        if (ioContext != null)
            throw new IllegalArgumentException("Cannot setSkipLeading after open()");
        this.skipLeading = skipLeading;
    }

    /**
     *
     * @return number of trailing records to skip. default 0
     */
    public int getSkipTrailing() {
        return skipTrailing;
    }

    /**
     *
     * @param skipTrailing number of trailing records to skip. >= 0
     */
    public void setSkipTrailing(int skipTrailing) {
        if (skipTrailing < 0)
            throw new IllegalArgumentException(
                    String.format("skipTrailing out of range: %s < 0", skipTrailing));
        if (ioContext != null)
            throw new IllegalArgumentException("Cannot setSkipTrailing after open()");
        this.skipTrailing = skipTrailing;
    }

    @Override
    public void open(IOContext context) throws IOException, SchemaException{
        if (context == null)
            throw new IllegalArgumentException("context is null");

        if (context.getSchema() != null){
            recordFactory = new RecordFactory(context.getSchema());
            recordFactory.setValidationExceptionThreshold(context.getValidationExeptionThreshold());
        }
        
        close();    
        ioContext = context;
        beforeFirstOperationsRun = false;
        count = 0;
    }
           

    @Override
    public void close(){
        IOUtils.closeQuietly(ioContext.getIn());
    }

    protected void executeAfterLastOperations() throws ValidationException, ConversionException{

        if (ioContext.getSchema() != null){
            List<Operation> operations = ioContext.getSchema().getAfterLastOperations();
            if (operations != null && !operations.isEmpty()){
                Transformer transformer = new Transformer();
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                transformer.setFields(fields);
                transformer.setAfter(operations);
                TransformContext context = new TransformContext();
                context.setValidationExceptionThreshold(context.getValidationExceptionThreshold());
                context.setRecordCount(this.getCount());
                context.setSchema(ioContext.getSchema());
                transformer.transform(context);
            }
        }
    }

    protected void executeBeforeFirstOperations() throws ValidationException, ConversionException{

        if (ioContext.getSchema() != null){
            List<Operation> operations = ioContext.getSchema().getBeforeFirstOperations();            
            if (operations != null && !operations.isEmpty()){
                List<Field> fields = new ArrayList<Field>();
                fields.add(new BeforeFirstField());
                Transformer transformer = new Transformer();
                transformer.setFields(fields);
                transformer.setBefore(operations);
                TransformContext context = new TransformContext();
                context.setValidationExceptionThreshold(ioContext.getValidationExeptionThreshold());
                context.setRecordCount(this.getCount());
                context.setSchema(ioContext.getSchema());
                transformer.transform(context);
            }
        }
    }

    
    /** {@inheritDoc} */
    @Override
    public long getCount(){
        return count;
    }
    
    @Override
    public IOContext getIOContext(){
        return ioContext;
    }
}
