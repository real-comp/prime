package com.realcomp.prime.conversion;

import com.realcomp.prime.DataType;
import com.realcomp.prime.MultiFieldOperation;
import com.realcomp.prime.record.Record;
import java.util.List;

/**
 *
 * @author krenfro
 */
public interface MultiFieldConverter extends MultiFieldOperation{

    /**
     * @param value not null
     * @return the converted value, not null
     */
    Object convert(Object value, Record record) throws ConversionException;

    /**
     *
     * @return List of DataTypes supported by this converter.
     */
    List<DataType> getSupportedTypes();
}
