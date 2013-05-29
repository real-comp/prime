package com.realcomp.data.conversion;

import java.util.List;
import java.util.ArrayList;
import com.realcomp.data.DataType;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krenfro
 */
public class ComplexConverterTest extends ConverterTest{

    public ComplexConverterTest(){
        converter = new MockConverter();
    }

    @Override
    @Test
    public void testSupportedTypes(){

        super.testSupportedTypes();
        List<DataType> types = new ArrayList<DataType>();
        types.add(DataType.STRING);
        types.add(DataType.INTEGER);
        types.add(DataType.LONG);
        types.add(DataType.FLOAT);
        types.add(DataType.DOUBLE);
        types.add(DataType.BOOLEAN);
        types.add(DataType.MAP);
        types.add(DataType.LIST);

        assertTrue(converter.getSupportedTypes().containsAll(types));
    }
}
