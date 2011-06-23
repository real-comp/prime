package com.realcomp.data.conversion;

import com.realcomp.data.record.Record;

/**
 *
 * @author krenfro
 */
@com.realcomp.data.annotation.Converter("concat")
public class Concat extends BaseMultiFieldConverter{

    protected String delimiter = "";

    @Override
    public String convert(String value, Record record) throws ConversionException {

        String retVal = "".concat(value);
        boolean needDelimiter = false;
        for (String fieldName: fieldNames){

            if (needDelimiter)
                retVal = retVal.concat(delimiter);
            needDelimiter = true;

            Object temp = record.get(fieldName);
            if (temp == null){
                throw new MissingFieldException(fieldName);
            }
            retVal = retVal.concat(temp.toString());
        }
        return retVal;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Concat other = (Concat) obj;
        if ((this.delimiter == null) ? (other.delimiter != null) : !this.delimiter.equals(other.delimiter)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.delimiter != null ? this.delimiter.hashCode() : 0);
        return hash;
    }

    

}
