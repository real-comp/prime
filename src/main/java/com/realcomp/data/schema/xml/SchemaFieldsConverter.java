package com.realcomp.data.schema.xml;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
import com.realcomp.data.schema.FileSchema;
import com.realcomp.data.schema.SchemaField;
import com.realcomp.data.validation.Validator;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Converts a ScheamField map to/from xml
 * @author krenfro
 */
public class SchemaFieldsConverter implements Converter{
    

    public SchemaFieldsConverter(){
        super();
    }
    
    
    @Override
    public boolean canConvert(Class type){
        return Map.class.isAssignableFrom(type);
    }


    /**
     * to xml
     * @param o
     * @param writer
     * @param mc 
     */
    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {

        for (Map.Entry<Pattern, List<SchemaField>> entry: ((Map<Pattern, List<SchemaField>>) o).entrySet()){

            writer.startNode("fields");
            if (!entry.getKey().equals(FileSchema.DEFAULT_CLASSIFIER)){
                writer.addAttribute("classifier", entry.getKey().toString());
            }

            for (SchemaField field: entry.getValue()){
                mc.convertAnother(field);
            }

            writer.endNode();
        }
    }


    protected String getFieldNames(List<String> names){

        String fieldNames = "";
        boolean needDelimiter = false;
        for (String field: names){
            if (needDelimiter)
                fieldNames = fieldNames.concat(",");
            fieldNames = fieldNames.concat(field);
            needDelimiter = true;
        }

        return fieldNames;
    }

    protected List<String> getFieldNames(String names){

        List<String> retVal = new ArrayList<String>();
        for (String name: names.split(","))
            retVal.add(name.trim());

        return retVal;
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        
        Map<Pattern, List<SchemaField>> fields = new HashMap<Pattern, List<SchemaField>>();

        if (reader.hasMoreChildren()){
            reader.moveDown();

            String classifier = reader.getAttribute("classifier");                
            List<SchemaField> schemaFields = (List<SchemaField>) uc.convertAnother(uc.currentObject(), List.class);
            fields.put(
                    classifier == null ? FileSchema.DEFAULT_CLASSIFIER : Pattern.compile(classifier), 
                    schemaFields);
            reader.moveUp();
        }

        return fields;
    }

    protected String getProperty(PropertyDescriptor pd, Object o)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{

        if (pd.getName().equals("class"))
            return null;

        Object value = pd.getReadMethod().invoke(o, (Object[]) null);


        //don't write default validator severity
        if (value != null && o instanceof Validator && pd.getName().equals("severity")){
            if (value.toString().equals(Validator.DEFAULT_SEVERITY.toString()))
                value = null;
        }

        return value == null ? null : value.toString();
    }


    protected void setProperty(String name, String value, PropertyDescriptor pd, Operation operation)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{

        if (name == null)
            throw new IllegalArgumentException("name is null");
        if (operation == null)
            throw new IllegalArgumentException("operation is null");
        if (pd == null)
            throw new IllegalArgumentException("pd is null");
        if (name.isEmpty())
            throw new IllegalArgumentException("name is empty");
        if (value == null)
            return;
        if (name.equals("class"))
            return;


        Class propertyType = pd.getPropertyType();
        if (propertyType.isInstance(value)){
            pd.getWriteMethod().invoke(operation, value);
        }
        else if(propertyType.isEnum()){
            pd.getWriteMethod().invoke(operation, Enum.valueOf(propertyType, value));
        }
        else{

            //try java.lang.* Objects
            try{
                pd.getWriteMethod().invoke(operation, Integer.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(operation, Float.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(operation, Long.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(operation, Double.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(operation, Short.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                if (value.length() == 1){
                    pd.getWriteMethod().invoke(operation, Character.valueOf(value.charAt(0)));
                    return;
                }
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(operation, Boolean.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            throw new IllegalStateException("unable to set property for: " + name);
        }
    }
}
