package com.realcomp.data.conversion;

import com.realcomp.data.Field;
import com.realcomp.data.record.Record;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("concat")
public class Concat extends BaseMultiFieldConverter{
    
    @Override
    public String convert(String value, Record record) throws ConversionException {

        String retVal = "".concat(value);
        for (String fieldName: fieldNames){
            Field field = record.get(fieldName);
            if (field == null)
                throw new MissingFieldException(fieldName);
            retVal = retVal.concat(field.getValue().toString());
        }
        return retVal;
    }

}
