package com.realcomp.data.schema.xml;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize an Map&lt;String,String&gt;
 *
 * @author krenfro
 */
public class AttributesConverter extends MapConverter{

    public AttributesConverter(){
        super(null);
    }

    public AttributesConverter(Mapper mapper){
        super(mapper);
    }

    @Override
    public boolean canConvert(Class type){
        return Map.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext mc){

        Map<String, String> map = (Map) source;
        for (Entry<String, String> entry : map.entrySet()){
            writer.addAttribute(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc){

        Map<String, String> map = new HashMap<String, String>();

        Iterator<String> itr = reader.getAttributeNames();
        while (itr.hasNext()){
            String name = itr.next();
            map.put(name, reader.getAttribute(name));
        }

        return map;
    }
}
