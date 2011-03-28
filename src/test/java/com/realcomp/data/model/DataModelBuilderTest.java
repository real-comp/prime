/*
 */

package com.realcomp.data.model;

import com.realcomp.data.validation.field.Key;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.IntegerField;
import java.util.ArrayList;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.Table;
import com.realcomp.data.record.Record;
import com.realcomp.data.schema.RelationalSchema;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class DataModelBuilderTest {

    public DataModelBuilderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    /**
     * Testing single table schema with single record.
     * @throws SchemaException
     */
    @Test
    public void testSimpleBuild() throws SchemaException{

        List<Record> records = new ArrayList<Record>();
        Record r = new Record();
        r.put("id", new IntegerField("id", 400));
        records.add(r);
        
        RelationalSchema schema = getRelationalSchema();
        DataModel dataModel = DataModelBuilder.build(schema, records);

        assertNotNull(dataModel);
        assertEquals(400, dataModel.getData().get("id").getValue());
        assertNull(dataModel.getDataViewClassNames());
    }

    protected RelationalSchema getRelationalSchema() throws SchemaException{

        RelationalSchema schema = new RelationalSchema();
        schema.setName("test");
        schema.setVersion("1.0");

        Table data = new Table("data");
        SchemaField field = new SchemaField();
        field.addOperation(new Key());
        data.addKey(field);
        schema.addTable(data);

        return schema;
    }
}