/*
 */

package com.realcomp.data.view;

import com.realcomp.data.conversion.ConversionException;
import com.realcomp.data.validation.ValidationException;
import java.io.ByteArrayInputStream;
import com.realcomp.data.record.reader.DelimitedFileReader;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.schema.FileSchema;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ViewReaderTest {

    public ViewReaderTest() {
    }

    @Test
    public void testView() throws SchemaException, IOException, ValidationException, ConversionException{


        FileSchema schema = new FileSchema();
        schema.addViewReader(ExampleViewReader.class);
        schema.addField(new SchemaField("data"));

        DelimitedFileReader reader = new DelimitedFileReader();
        schema.setReader(reader);        

        reader.open(new ByteArrayInputStream("kyle\nbrandon".getBytes()));

        ExampleViewReader viewReader = (ExampleViewReader) schema.getViewReader(ExampleView.class);

        ExampleView v = viewReader.read();
        assertEquals("kyle", v.getData());

        v = viewReader.read();
        assertEquals("brandon", v.getData());

        viewReader.close();
    }

}