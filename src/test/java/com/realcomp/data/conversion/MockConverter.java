package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import com.realcomp.data.Operation;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author krenfro
 */
public class MockConverter extends ComplexConverter{

    @Override
    public Object convert(Object value) throws ConversionException{
        return value;
    }

    @Override
    public Operation copyOf(){
        return new MockConverter();
    }
}