package com.realcomp.data.schema.xml;

import com.realcomp.data.record.reader.RecordReader;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize a RecordReader
 * 
 * @author krenfro
 */
public class RecordReaderConverter extends PropertyReader implements Converter{
    
    public RecordReaderConverter(){
        super();
        addIgnoredProperty("class");
        addIgnoredProperty("schema");
        addIgnoredProperty("count");
        addIgnoredProperty("beforeFirstOperationsRun");
   }

    @Override
    public boolean canConvert(Class type){
        return RecordReader.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        try {
            for(Map.Entry<String,Object> entry: read(o).entrySet())
                writer.addAttribute(entry.getKey(), entry.getValue().toString());
        }
        catch (DynamicPropertyException ex) {
            Logger.getLogger(RecordReaderConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ConversionException(ex);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

       try {
            Class parserClass = Class.forName(reader.getAttribute("class"));
            RecordReader parser = (RecordReader) parserClass.newInstance();
            PropertyWriter writer = new PropertyWriter();
            Map<String,String> properties = new HashMap<String,String>();
            Iterator<String> itr = reader.getAttributeNames();
            while(itr.hasNext()){
                String name = itr.next();
                String value = reader.getAttribute(name);
                if (value != null)
                    properties.put(name, value);
            }
            writer.write(parser, properties);
            return parser;
        }
        catch (DynamicPropertyException ex) {
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
                if (value.toString().equals(RecordReader.DEFAULT_VALIDATION_THREASHOLD.toString()))
                    valid = false;
        }
        
        return valid;
    }
  
}
