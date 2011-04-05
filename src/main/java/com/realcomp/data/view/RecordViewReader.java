package com.realcomp.data.view;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * Simple ViewReader that reads RecordViews.
 *
 * @author krenfro
 */
public class RecordViewReader<T extends RecordView> extends BaseViewReader{

    protected static final Logger logger = Logger.getLogger(RecordViewReader.class.getName());

    private Class clazz;

    public RecordViewReader(RecordReader reader, Class clazz){
        super(reader);
        if (clazz == null)
            throw new IllegalArgumentException("clazz is null");
        this.clazz = clazz;
    }

    @Override
    public T read() throws IOException, ValidationException, ConversionException,
                                SchemaException {

        Record record = reader.read();
        T retVal = null;

        if (record != null){
            try {
                Constructor c = clazz.getConstructor(Record.class);
                retVal = (T) c.newInstance(record);
            }
            catch (NoSuchMethodException ex) {
                throw new IOException(ex);
            }
            catch (SecurityException ex) {
                throw new IOException(ex);
            }
            catch (InstantiationException ex) {
                throw new IOException(ex);
            }
            catch (IllegalAccessException ex) {
                throw new IOException(ex);
            }
            catch (IllegalArgumentException ex) {
                throw new ConversionException(ex);
            }
            catch (InvocationTargetException ex) {
                throw new IOException(ex);
            }
        }

        return null;
    }

    @Override
    public boolean supports(Class clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }

}
