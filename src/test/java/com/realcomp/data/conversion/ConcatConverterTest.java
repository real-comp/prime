package com.realcomp.data.conversion;

import com.realcomp.data.record.Record;
import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ConcatConverterTest extends MultiFieldConverterTest{

    public ConcatConverterTest(){
        converter = new Concat();
    }

    @Test
    @Override
    public void testNullInput() throws ConversionException{

        assertEquals("", converter.convert(null, new Record()));

        try{
            converter.convert("", null);
            fail("Expected IllegalArgumentException on null input");
        }
        catch (IllegalArgumentException expected){
        }
    }

    @Test
    public void testConvert() throws Exception{

        Concat concat = new Concat();
        List<String> fields = new ArrayList<String>();
        fields.add("a");
        fields.add("b");
        concat.setFields(fields);

        Record record = new Record();
        record.put("a", "real");
        record.put("b", "comp");

        assertEquals("realcomp", concat.convert("", record));

        concat.setDelimiter(" ");
        assertEquals("real comp", concat.convert("", record));

        concat.setDelimiter("-");
        assertEquals("real-comp", concat.convert("", record));

    }

    @Test
    public void testMissingField() throws Exception{

        Concat concat = new Concat();
        List<String> fields = new ArrayList<String>();
        fields.add("a");
        fields.add("c");  //does not exist
        concat.setFields(fields);

        Record record = new Record();
        record.put("a", "real");
        record.put("b", "comp");


        concat.convert("", record); //ok



    }

    @Test
    public void testCopyOf(){
        Concat a = new Concat();
        a.setDelimiter(",");
        Concat b = a.copyOf();
        assertEquals(a, b);
        assertEquals(",", b.getDelimiter());
    }

    @Test
    @Override
    public void testEquals(){

        super.testEquals();

        Concat a = new Concat();
        Concat b = new Concat();
        assertEquals(a, b);
        a.setDelimiter(",");
        assertFalse(a.equals(b));
        b.setDelimiter(",");
        assertEquals(a, b);

        List<String> fields = new ArrayList<String>();
        fields.add("a");
        fields.add("b");

        a.setFields(fields);
        assertFalse(a.equals(b));

        fields.remove("b");
        b.setFields(fields);
        assertFalse(a.equals(b));

        fields.add("b");
        b.setFields(fields);
        assertEquals(a, b);

    }

    @Test
    public void testHashCode(){
        Concat a = new Concat();
        Concat b = new Concat();
        assertEquals(a.hashCode(), b.hashCode());
        a.setDelimiter(",");
        assertTrue(a.hashCode() != b.hashCode());

    }
}
