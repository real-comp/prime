
package com.realcomp.data.conversion;

import com.realcomp.data.Operation;

/**
 *
 * @author krenfro
 */
public interface Converter extends Operation{

    /**
     * @param value not null
     * @return the converted value, not null
     */
    String convert(String value) throws ConversionException;
}
