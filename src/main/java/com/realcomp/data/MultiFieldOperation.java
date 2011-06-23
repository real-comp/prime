package com.realcomp.data;

import java.util.List;

/**
 * Special Operation that operates on multiple fields.
 * 
 * @author krenfro
 */
public interface MultiFieldOperation extends Operation{

    public List<String> getFields();
    public void setFields(List<String> fieldNames);
}
