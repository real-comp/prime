package com.realcomp.data.schema.xml;

import com.realcomp.data.record.io.DynamicPropertyGetter;
import com.realcomp.data.record.io.DynamicPropertySetter;
import com.realcomp.data.record.io.RecordWriter;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a RecordWrier
 * 
 * @author krenfro
 */
public class RecordWriterConverter  extends DynamicPropertyGetter implements Converter{

    public RecordWriterConverter(){
        super();
        addIgnoredProperty("class");
        addIgnoredProperty("schema");
        addIgnoredProperty("count");
        addIgnoredProperty("beforeFirstOperationsRun");
   }

    @Override
    public boolean canConvert(Class type){
        return RecordWriter.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        try {
            for(Map.Entry<String,Object> entry: getProperties(o).entrySet())
                writer.addAttribute(entry.getKey(), entry.getValue().toString());
        }
        catch (IntrospectionException ex) {
            Logger.getLogger(RecordReaderConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConversionException(ex);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader stream, UnmarshallingContext uc) {

       try {
            Class readerClass = Class.forName(stream.getAttribute("class"));
            RecordWriter writer = (RecordWriter) readerClass.newInstance();
            DynamicPropertySetter propSetter = new DynamicPropertySetter();
            propSetter.addIgnoredProperty("class");
            Map<String,Object> properties = new HashMap<String,Object>();
            Iterator<String> itr = stream.getAttributeNames();
            while(itr.hasNext()){
                String name = itr.next();
                String value = stream.getAttribute(name);
                if (value != null){
                    properties.put(name, value);
                }
            }
            propSetter.setProperties(writer, properties);
            return writer;
        }
        catch (IntrospectionException ex) {
            throw new ConversionException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new ConversionException(ex);
        }
        catch (InstantiationException ex) {
            throw new ConversionException(ex);
        }
        catch (ClassNotFoundException ex) {
            throw new ConversionException(ex);
        }
    }


    /**
     * Override default implementation to remove <i>class</i> and <i>schema</i> attributes
     * from the serialization.
     * Also ignores validationExceptionThreshold if it is the default.
     *
     * @param name
     * @param value
     * @return
     */
    @Override
    public boolean isValidProperty(String name, Object value){

        boolean valid = super.isValidProperty(name, value);

        if (valid){
            if (name.equals("validationExceptionThreshold"))
                if (value.toString().equals(RecordWriter.DEFAULT_VALIDATION_THREASHOLD.toString()))
                    valid = false;
        }

        return valid;
    }


}
