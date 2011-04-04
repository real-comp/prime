package com.realcomp.data.view;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.record.Record;
import com.realcomp.data.record.reader.RecordReader;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;

/**
 *
 * @author krenfro
 */
public class ExampleViewReader extends BaseViewReader {

    public ExampleViewReader(RecordReader reader){
        super(reader);
    }
    
    @Override
    public ExampleView read() throws IOException, ValidationException, ConversionException, SchemaException {

        ExampleView view = null;
        Record record = reader.read();
        if (record != null){
            view = new ExampleView();
            view.setData(record.get("data").toString());
        }
        return view;
    }

    @Override
    public boolean supports(Class clazz) {
        return ExampleView.class.isAssignableFrom(clazz);
    }

}
