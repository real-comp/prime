package com.realcomp.data;

import java.util.List;

/**
 * Special Operation that operates on multiple fields.
 * 
 * @author krenfro
 */
public interface MultiFieldOperation extends Operation{

    public List<String> getFieldNames();
    public void setFieldNames(List<String> fieldNames);
}
