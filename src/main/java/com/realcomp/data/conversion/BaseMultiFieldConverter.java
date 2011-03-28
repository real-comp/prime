package com.realcomp.data.conversion;

import java.util.List;

/**
 *
 * @author krenfro
 */
public abstract class BaseMultiFieldConverter implements MultiFieldConverter{

    protected List<String> fieldNames;

    @Override
    public List<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BaseMultiFieldConverter other = (BaseMultiFieldConverter) obj;
        if (this.fieldNames != other.fieldNames && (this.fieldNames == null || !this.fieldNames.equals(other.fieldNames)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.fieldNames != null ? this.fieldNames.hashCode() : 0);
        return hash;
    }
}
