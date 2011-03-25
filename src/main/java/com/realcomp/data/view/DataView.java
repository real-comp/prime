
package com.realcomp.data.view;

import com.realcomp.data.record.Record;
import com.realcomp.data.schema.xml.DataViewConverter;
import com.realcomp.data.validation.ValidationException;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 *
 * @author krenfro
 */
@XStreamAlias("view")
@XStreamConverter(DataViewConverter.class)
public interface DataView {

    void parse(Record record) throws ValidationException;
}
