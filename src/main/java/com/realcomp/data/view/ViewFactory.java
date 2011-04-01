package com.realcomp.data.view;

import com.realcomp.data.record.Record;
import com.realcomp.data.schema.xml.ViewFactoryConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 *
 * @author krenfro
 */
@XStreamAlias("viewFactory")
@XStreamConverter(ViewFactoryConverter.class)
public interface ViewFactory<V extends View>{

    public boolean isBuildable(Class clazz);

    public V build(Record record);

}
