package com.realcomp.data.conversion;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.record.Record;

/**
 *
 * @author krenfro
 */
public interface MultiFieldConverter extends MultiFieldOperation{

    /**
     * @param value not null
     * @return the converted value, not null
     */
    String convert(String value, Record record) throws ConversionException;
}
