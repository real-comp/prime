package com.realcomp.data.schema.xml;

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
                String value = getProperty(pd, o);
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

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {

       try {
            Class operationClass = uc.getRequiredType();
            BeanInfo beanInfo = Introspector.getBeanInfo(operationClass);
            Operation op = (Operation) operationClass.newInstance();

            for (PropertyDescriptor pd: beanInfo.getPropertyDescriptors()){
                setProperty(pd.getName(), reader.getAttribute(pd.getName()), pd, op);
            }

            return op;
        }
        catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
        catch (InstantiationException ex) {
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
