package com.realcomp.data.schema.xml;

import com.realcomp.data.MultiFieldOperation;
import com.realcomp.data.Operation;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Uses xStream, JavaBeans and reflection to dynamically serialize/de-serialize an Operation
 *
 * @author krenfro
 */
public class OperationConverter implements Converter{
    

    @Override
    public boolean canConvert(Class type){
        return Operation.class.isAssignableFrom(type);
    }


    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(o.getClass());
            for (PropertyDescriptor pd: beanInfo.getPropertyDescriptors()){

                String value = null;

                if (o instanceof MultiFieldOperation && pd.getName().equals("fields")){
                    value = getFieldNames(((MultiFieldOperation)o).getFields());
                }
                else{
                    value = getProperty(pd, o);
                }
                
                if (value != null)
                    writer.addAttribute(pd.getName(), value);
            }
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
        catch (IntrospectionException ex) {
            throw new IllegalStateException(ex);
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

    protected void marshal(MultiFieldOperation op, HierarchicalStreamWriter writer, MarshallingContext mc){

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(op.getClass());
            for (PropertyDescriptor pd: beanInfo.getPropertyDescriptors()){

                if (pd.getName().equals("fields")){

                    String fieldNames = "";
                    boolean needDelimiter = false;
                    for (String field: op.getFields()){
                        if (needDelimiter)
                            fieldNames = fieldNames.concat(",");
                        fieldNames = fieldNames.concat(field);
                        needDelimiter = true;
                    }

                    writer.addAttribute("fields", fieldNames);                        
                }
                else{
                    String value = getProperty(pd, op);
                    if (value != null)
                        writer.addAttribute(pd.getName(), value);
                }
            }
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
        catch (IntrospectionException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        
        try {
            Class operationClass = uc.getRequiredType();
            BeanInfo beanInfo = Introspector.getBeanInfo(operationClass);
            Operation op = (Operation) operationClass.newInstance();            
            Set<String> successful = new HashSet<String>();

            for (PropertyDescriptor pd: beanInfo.getPropertyDescriptors()){

                if (op instanceof MultiFieldOperation && pd.getName().equals("fields")){
                    ((MultiFieldOperation) op).setFields(getFieldNames(reader.getAttribute(pd.getName())));
                    successful.add(pd.getName());
                }
                else{
                    setProperty(pd.getName(), reader.getAttribute(pd.getName()), pd, op);
                    successful.add(pd.getName());
                }
            }
            
            //ensure that all named properties were actually set.
            Iterator<String> itr = reader.getAttributeNames();
            while (itr.hasNext()){
                String attribute = itr.next();
                if (!successful.contains(attribute))
                    throw new IllegalArgumentException(
                            "Unable to set property for attribute named: " + attribute);
            }

            return op;
        }
        catch (InstantiationException ex) {
            throw new IllegalStateException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
        catch (IntrospectionException ex) {
            throw new IllegalStateException(ex);
        }
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
