package com.realcomp.data.schema.xml;

import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize an Operation
 *
 * @author krenfro
 */
public class FieldListConverter implements Converter{
    

    public FieldListConverter(){
        super();
    }
    
    
    @Override
    public boolean canConvert(Class type){
        return FieldList.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {

        FieldList fieldList = (FieldList) o;
        writer.startNode("fields");
        if (!fieldList.isDefaultClassifier())
            writer.addAttribute("classifier", fieldList.getClassifier().toString());
        
        for (Field field: fieldList){
            System.err.println(field);
            writer.flush();
            mc.convertAnother(field);
        }
        writer.endNode();
    }


    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        
        FieldList fieldList = new FieldList();
        while (reader.hasMoreChildren()){
            Field f = (Field) uc.convertAnother(reader.getValue(), Field.class);
            fieldList.add(f);
        }
        return fieldList;
    }

}
