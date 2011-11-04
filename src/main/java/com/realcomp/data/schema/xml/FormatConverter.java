package com.realcomp.data.schema.xml;

import com.realcomp.data.record.io.Format;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.Iterator;
import java.util.Map;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize an Operation
 *
 * @author krenfro
 */
public class FormatConverter implements Converter{
    

    
    
    @Override
    public boolean canConvert(Class type){
        return Format.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {

        Format format = (Format) o;
        writer.addAttribute("type", format.getType());
        for (Map.Entry<String,String> entry: format.getAttributes().entrySet()){            
            writer.addAttribute(entry.getKey(), entry.getValue());
        }
    }


    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        
        Format format = new Format(reader.getAttribute("type"));
        
        Iterator<String> itr = reader.getAttributeNames();
        while (itr.hasNext()){         
            String name = itr.next();
            if (!name.equals("type")){
                format.setAttribute(name, reader.getAttribute(name));
            }
        }
        
        return format;
    }

}
