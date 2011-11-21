package com.realcomp.data.schema.xml;

import com.realcomp.data.schema.Field;
import com.realcomp.data.schema.FieldList;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize an Operation
 *
 * @author krenfro
 */
public class FieldListConverter implements Converter{
    
    private static final Logger logger = Logger.getLogger(FieldListConverter.class.getName());
    
    
    @Override
    public boolean canConvert(Class type){
        return FieldList.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {

        FieldList fieldList = (FieldList) o;
        if (!fieldList.isDefaultClassifier())
            writer.addAttribute("classifier", fieldList.getClassifier().toString());
            
       
        for (Field field: fieldList){
            writer.startNode("field");
            mc.convertAnother(field);
            writer.endNode();
        }
    }


    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        
        FieldList fieldList = new FieldList();        
        String classifier = reader.getAttribute("classifier");
        if (classifier != null){
            fieldList.setClassifier(Pattern.compile(classifier));
        }
        
        while (reader.hasMoreChildren()){
            reader.moveDown();
            Field f = (Field) uc.convertAnother(fieldList, Field.class);
            fieldList.add(f);
            reader.moveUp();
        }
        return fieldList;
    }

}