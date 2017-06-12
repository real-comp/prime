package com.realcomp.prime.util;

import com.realcomp.prime.conversion.ConversionException;
import com.realcomp.prime.record.io.IOContextBuilder;
import com.realcomp.prime.schema.SchemaException;
import com.realcomp.prime.schema.SchemaFactory;
import com.realcomp.prime.validation.Severity;
import com.realcomp.prime.validation.ValidationException;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;


public class ReformatTest {


    @Test
    public void filterToFile() throws IOException, SchemaException, ValidationException, ConversionException{

        Reformat reformat = new Reformat();
        IOContextBuilder inputBuilder = new IOContextBuilder();
        inputBuilder.schema(
                SchemaFactory.buildSchema(ReformatTest.class.getResourceAsStream("reformat-input.schema")));
        inputBuilder.in(
                new BufferedInputStream(ReformatTest.class.getResourceAsStream("reformat-test.csv")));

        File good = File.createTempFile("ReformatTest-", ".tmp");
        good.deleteOnExit();

        File bad = File.createTempFile("ReformatTest-", ".tmp");
        bad.deleteOnExit();

        IOContextBuilder outputBuilder = new IOContextBuilder();
        outputBuilder.schema(
                SchemaFactory.buildSchema(ReformatTest.class.getResourceAsStream("reformat-output.schema")));
        outputBuilder.out(new BufferedOutputStream(new FileOutputStream(good)));

        inputBuilder.validationExceptionThreshold(Severity.MEDIUM);
        outputBuilder.validationExceptionThreshold(Severity.MEDIUM);

        reformat.setIn(inputBuilder.build());
        reformat.setOut(outputBuilder.build());
        reformat.setErr(new BufferedOutputStream(new FileOutputStream(bad)));
        reformat.setFilter(true);
        reformat.reformat();

        assertEquals(3, countLines(good));
        assertEquals(3, countLines(bad));
    }

    private int countLines(File file) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int count = 0;
        while (reader.readLine() != null){
            count++;
        }
        return count;
    }

}