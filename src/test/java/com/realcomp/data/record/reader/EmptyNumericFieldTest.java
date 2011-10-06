package com.realcomp.data.record.reader;

import com.realcomp.data.schema.FieldList;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.DataType;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.Field;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * If a field is defined as a numeric type, and the source is "",
 * it should be resolved as 'null' and not placed in the Record.
 *
 * @author krenfro
 */
public class EmptyNumericFieldTest {

    public EmptyNumericFieldTest() {
    }


    @Test
    public void testEmptyFields() throws Exception {

        FieldList fields = getSchema().getDefaultFieldList();

        String[] data = new String[]{"123","456","789"};
        DelimitedFileReader instance = new DelimitedFileReader();
        instance.setSchema(getSchema());
        Record record = instance.loadRecord(fields, data);
        assertEquals(123d, (Integer) record.get("int"), .001d);
        assertEquals(456d, (Float) record.get("float"), .001d);
        assertEquals(789d, (Double) record.get("double"), .001d);

        data = new String[]{"","",""};
        instance = new DelimitedFileReader();
        instance.setSchema(getSchema());
        record = instance.loadRecord(fields, data);
        assertEquals(0, record.get("int"));
        assertEquals(0f, record.get("float"));
        assertEquals(0d, record.get("double"));


    }

    protected FileSchema getSchema() throws SchemaException{

        FileSchema schema = new FileSchema();
        schema.setName("test");
        schema.setVersion("0");
        FieldList fields = new FieldList();
        fields.add(new Field("int", DataType.INTEGER));
        fields.add(new Field("float", DataType.FLOAT));
        fields.add(new Field("double", DataType.DOUBLE));
        schema.addFieldList(fields);
        return schema;
    }

}