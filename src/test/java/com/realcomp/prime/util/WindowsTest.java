package com.realcomp.prime.util;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.Record;
import com.realcomp.prime.record.io.IOContext;
import com.realcomp.prime.record.io.IOContextBuilder;
import com.realcomp.prime.record.io.RecordWriter;
import com.realcomp.prime.record.io.RecordWriterFactory;
import com.realcomp.prime.schema.Schema;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.schema.SchemaFactory;
import com.realcomp.prime.validation.ValidationException;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WindowsTest{


    @Test
    public void testWriteWindowsFile() throws IOException, SchemaException, ValidationException, ConversionException{


        Schema schema = SchemaFactory.buildSchema(WindowsTest.class.getResourceAsStream("windows-test.schema"));
        File temp = File.createTempFile("WindowsTest-", ".csv");
        System.out.println(temp.toString());
        FileOutputStream out = new FileOutputStream(temp);
        IOContext ctx = new IOContextBuilder().schema(schema).out(out).build();
        RecordWriter writer = RecordWriterFactory.build(schema);
        writer.open(ctx);

        Record record = new Record();
        record.put("address1", "123 MAIN");
        record.put("zip", "75218");
        writer.write(record);
        writer.write(record);

        writer.close();
        ctx.close();
        out.close();

    }
}
