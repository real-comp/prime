/*
 */

package com.realcomp.data.trait;

import java.io.ByteArrayInputStream;
import com.realcomp.data.record.reader.DelimitedFileReader;
import com.realcomp.data.schema.SchemaException;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.trait.common.NameTrait;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.record.Record;
import com.realcomp.data.validation.ValidationException;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class TraitTest {

    public TraitTest() {
    }


    private class TestNameTrait implements NameTrait(){


        private Record record;

        @Override
        public String getName() {
            return record.get("name").toString();
        }

        @Override
        public void parse(Record record) throws ValidationException {
            this.record = record;
        }

    }

    @Test
    public void testTraits() throws SchemaException, IOException{

        FileSchema schema = new FileSchema();
        schema.addTrait(new TestNameTrait());
        schema.addField(new SchemaField("name"));

        DelimitedFileReader reader = new DelimitedFileReader();
        reader.setSchema(schema);

        reader.open(new ByteArrayInputStream("kyle\nbrandon".getBytes()));

        reader.read()
        reader.close();
    }

}