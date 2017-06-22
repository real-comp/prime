package com.realcomp.prime.schema.xml;

import com.realcomp.prime.DataType;
import com.realcomp.prime.schema.Field;
import com.realcomp.prime.schema.FieldList;
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
 */
public class FieldListConverter implements Converter{

    private static final Logger logger = Logger.getLogger(FieldListConverter.class.getName());

    @Override
    public boolean canConvert(Class type){
        return FieldList.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc){

        FieldList fieldList = (FieldList) o;
        if (fieldList.isDefault()){
            writer.addAttribute("default", "true");
        }
        
        Pattern classifier = fieldList.getClassifier();
        if (classifier != null && !classifier.equals(FieldList.DEFAULT_CLASSIFIER)){
            writer.addAttribute("classifier", classifier.toString());
        }

        for (Field field : fieldList){
            writer.startNode("field");
            mc.convertAnother(field);
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc){

        FieldList fieldList = new FieldList();

        //The classifier attribute is the old classification mechanism and is deprecated
        String classifier = reader.getAttribute("classifier");
        if (classifier != null){
            fieldList.setClassifier(Pattern.compile(classifier));
        }

        String defaultFieldList = reader.getAttribute("default");
        if (defaultFieldList != null){
            fieldList.setDefault(Boolean.parseBoolean(defaultFieldList));
        }

        String name = reader.getAttribute("name");
        if (name != null){
            fieldList.setName(name);
        }

        while (reader.hasMoreChildren()){
            reader.moveDown();
            Field f = (Field) uc.convertAnother(fieldList, Field.class);
            if (f.getType() == null){
                f.setType(DataType.STRING);
            }
            fieldList.add(f);
            reader.moveUp();
        }
        return fieldList;
    }
}
