package com.realcomp.prime.conversion;

import com.realcomp.prime.Operation;

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