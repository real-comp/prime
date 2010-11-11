package com.realcomp.data;

import com.realcomp.data.conversion.ConversionException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class FieldFactoryTest {

    public FieldFactoryTest() {
    }


    /**
     * Test of create method, of class FieldFactory.
     */
    @Test
    public void testCreate_DataType() {
        for (DataType type: DataType.values())
            assertEquals(type, FieldFactory.create(type).getType());
    }

    /**
     * Test of create method, of class FieldFactory.
     */
    @Test
    public void testCreate_DataType_String() throws Exception {

        assertEquals("", FieldFactory.create(DataType.STRING, "").getValue());
        assertEquals("asdf", FieldFactory.create(DataType.STRING, "asdf").getValue());
        
        assertEquals(-1, FieldFactory.create(DataType.INTEGER, "-1").getValue());
        assertEquals(0, FieldFactory.create(DataType.INTEGER, "0").getValue());
        assertEquals(1, FieldFactory.create(DataType.INTEGER, "1").getValue());

        assertEquals(-1l, FieldFactory.create(DataType.LONG, "-1").getValue());
        assertEquals(0l, FieldFactory.create(DataType.LONG, "0").getValue());
        assertEquals(1l, FieldFactory.create(DataType.LONG, "1").getValue());

        assertEquals(-1.0f, FieldFactory.create(DataType.FLOAT, "-1").getValue());
        assertEquals(0.0f, FieldFactory.create(DataType.FLOAT, "0").getValue());
        assertEquals(1.0f, FieldFactory.create(DataType.FLOAT, "1").getValue());
        assertEquals(1.1f, FieldFactory.create(DataType.FLOAT, "1.1").getValue());

        assertEquals(-1.0d, FieldFactory.create(DataType.DOUBLE, "-1").getValue());
        assertEquals(0.0d, FieldFactory.create(DataType.DOUBLE, "0").getValue());
        assertEquals(1.0d, FieldFactory.create(DataType.DOUBLE, "1").getValue());
        assertEquals(1.1d, FieldFactory.create(DataType.DOUBLE, "1.1").getValue());

        assertNull(FieldFactory.create(DataType.NULL, "ignored").getValue());

        try{
            FieldFactory.create(DataType.LIST, "asdf");
            fail("should have thrown ConversionException");
        }
        catch(ConversionException expected){}

        try{
            FieldFactory.create(DataType.MAP, "asdf");
            fail("should have thrown ConversionException");
        }
        catch(ConversionException expected){}
        
    }

}