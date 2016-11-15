package com.realcomp.prime;

import java.util.List;

/**
 * Special Operation that operates on multiple fields.
 *
 */
public interface MultiFieldOperation extends Operation{

    List<String> getFields();

    void setFields(List<String> fieldNames);
}
