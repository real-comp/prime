package com.realcomp.data.record;

import com.realcomp.data.record.io.IOContextBuilder;
import com.realcomp.data.record.io.RecordReader;
import com.realcomp.data.record.io.RecordReaderFactory;
import com.realcomp.data.schema.RelationalSchema;
import com.realcomp.data.schema.Schema;
import com.realcomp.data.schema.SchemaFactory;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RelationalRecordJoinerTest{


    @Test
    public void join() throws Exception{


        RelationalSchema relationalSchema = SchemaFactory.buildRelationalSchema(this.getClass().getResourceAsStream("relational.schema"));

        RelationalRecordJoiner joiner = new RelationalRecordJoiner();
        joiner.setRelationalSchema(relationalSchema);


        Schema schema = SchemaFactory.buildSchema(this.getClass().getResourceAsStream("PropertyData.schema"));
        try (RecordReader recordReader = RecordReaderFactory.build(schema)){
            recordReader.open(new IOContextBuilder().in(this.getClass().getResourceAsStream("PropertyData.txt")).schema(schema).build());
            joiner.addRecord(recordReader.read(), schema);
        }

        schema = SchemaFactory.buildSchema(this.getClass().getResourceAsStream("PropertyData_Exemptions.schema"));
        try (RecordReader recordReader = RecordReaderFactory.build(schema)){
            recordReader.open(new IOContextBuilder().in(this.getClass().getResourceAsStream("PropertyData_Exemptions.txt")).schema(schema).build());
            joiner.addRecord(recordReader.read(), schema);
            joiner.addRecord(recordReader.read(), schema);
        }

        schema = SchemaFactory.buildSchema(this.getClass().getResourceAsStream("PropertyLocation.schema"));
        try (RecordReader recordReader = RecordReaderFactory.build(schema)){
            recordReader.open(new IOContextBuilder().in(this.getClass().getResourceAsStream("PropertyLocation.txt")).schema(schema).build());
            joiner.addRecord(recordReader.read(), schema);
        }

        schema = SchemaFactory.buildSchema(this.getClass().getResourceAsStream("PropertyUse2014.schema"));
        try (RecordReader recordReader = RecordReaderFactory.build(schema)){
            recordReader.open(new IOContextBuilder().in(this.getClass().getResourceAsStream("PropertyUse2014.txt")).schema(schema).build());
            joiner.addRecord(recordReader.read(), schema);
        }


        List<Record> records = joiner.join();
        for (Record record: records){
            assertNotNull(record.get("PropertyData"));
            assertEquals("C2", record.getString("PropertyData.PropertyUse2014.State_Use_Code"));
            assertEquals("115", record.getString("PropertyData.PropertyLocation.Street_Num"));
            assertEquals(2, record.getList("PropertyData.PropertyData_Exemptions").size());
        }


    }

}