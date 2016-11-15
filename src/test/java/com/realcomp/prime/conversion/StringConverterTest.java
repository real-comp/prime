package com.realcomp.prime.conversion;

import com.realcomp.prime.DataType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class StringConverterTest extends ConverterTest{

    public StringConverterTest(){
        converter = new StringConverter();
    }

    @Override
    @Test
    public void testSupportedTypes(){

        for (DataType type : DataType.values()){
            if (type == DataType.STRING){
                assertTrue(converter.getSupportedTypes().contains(type));
            }
            else{
                assertFalse(converter.getSupportedTypes().contains(type));
            }
        }
    }
}
