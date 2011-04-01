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
import com.realcomp.data.record.Record;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ViewTest {

    public ViewTest() {
    }

    private class MyTestView extends View{
        private String data;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
    
    private class MyTestViewFactory implements ViewFactory<MyTestView>{

        @Override
        public boolean isBuildable(Class clazz) {
            return MyTestView.class.isAssignableFrom(clazz);
        }

        @Override
        public MyTestView build(Record record) {
            MyTestView v = new MyTestView();
            v.setData(record.get("data").toString());
            return v;
        }
    }

    @Test
    public void testView() throws SchemaException, IOException, ValidationException, ConversionException{


        FileSchema schema = new FileSchema();
        schema.addViewFactory(new MyTestViewFactory());
        schema.addField(new SchemaField("data"));

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.setSchema(schema);

        reader.open(new ByteArrayInputStream("kyle\nbrandon".getBytes()));

        MyTestView v = reader.readAs(MyTestView.class);
        assertEquals("kyle", v.getData());

        v = reader.readAs(MyTestView.class);
        assertEquals("brandon", v.getData());

        reader.close();
    }

}