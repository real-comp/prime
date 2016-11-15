package com.realcomp.prime.util;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.io.IOContextBuilder;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.schema.SchemaFactory;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class ReformatTest {

    public ReformatTest() {
    }


    @Test
    public void reformatToSameSchema() throws IOException, SchemaException, ValidationException, ConversionException{

        Reformat reformat = new Reformat();

        IOContextBuilder inputBuilder = new IOContextBuilder();
        inputBuilder.schema(
                SchemaFactory.buildSchema(ReformatTest.class.getResourceAsStream("ReformatToSelfTest.schema")));
        inputBuilder.in(
                new BufferedInputStream(ReformatTest.class.getResourceAsStream("ReformatToSelf.tab")));

        File temp = File.createTempFile("ReformatTest-", ".tmp");
        //temp.deleteOnExit();

        IOContextBuilder outputBuilder = new IOContextBuilder();
        outputBuilder.schema(
                SchemaFactory.buildSchema(ReformatTest.class.getResourceAsStream("ReformatToSelfTest.schema")));
        outputBuilder.out(new BufferedOutputStream(new FileOutputStream(temp)));


        reformat.setFilter(true);
        inputBuilder.validationExceptionThreshold(Severity.MEDIUM);
        outputBuilder.validationExceptionThreshold(Severity.MEDIUM);

        reformat.setIn(inputBuilder.build());
        reformat.setOut(outputBuilder.build());
        reformat.reformat();
        

        BufferedReader reader = new BufferedReader(new FileReader(temp));
        String s = reader.readLine();
        int count = 0;
        while (s != null){
            count++;
            s = reader.readLine();
        }

        assertEquals(798, count);

    }
}