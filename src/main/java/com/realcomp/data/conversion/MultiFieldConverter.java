package com.realcomp.data.conversion;

import com.realcomp.data.DataType;
import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.record.Record;
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
