package com.realcomp.data.schema.xml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Writes properties, dynamically, to a JavaBean
 *
 * @author krenfro
 */
public class PropertyWriter {

    /**
     *
     * @param bean
     * @param properties to be applied
     * @throws DynamicPropertyException
     */
    public void write(Object bean, Map<String,String> properties)
            throws DynamicPropertyException{

        if (properties == null || properties.isEmpty())
            return;
        
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors())
                setProperty(pd.getName(), properties.get(pd.getName()), pd, bean);
        }
        catch (IntrospectionException ex) {
            throw new DynamicPropertyException(ex);
        }
        catch (IllegalAccessException ex) {
            throw new DynamicPropertyException(ex);
        }
        catch (IllegalArgumentException ex) {
            throw new DynamicPropertyException(ex);
        }
        catch (InvocationTargetException ex) {
            throw new DynamicPropertyException(ex);
        }
    }

     protected void setProperty(String name, String value, PropertyDescriptor pd, Object bean)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{

        if (name == null)
            throw new IllegalArgumentException("name is null");
        if (bean == null)
            throw new IllegalArgumentException("bean is null");
        if (pd == null)
            throw new IllegalArgumentException("pd is null");
        if (name.isEmpty())
            throw new IllegalArgumentException("name is empty");
        if (value == null)
            return;

        Class propertyType = pd.getPropertyType();
        if (propertyType.isInstance(value)){
            pd.getWriteMethod().invoke(bean, value);
        }
        else if(propertyType.isEnum()){
            pd.getWriteMethod().invoke(bean, Enum.valueOf(propertyType, value));
        }
        else if (pd.getWriteMethod() != null){
            
            //try java.lang.* Objects

            try{
                pd.getWriteMethod().invoke(bean, Integer.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}


            try{
                pd.getWriteMethod().invoke(bean, Integer.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(bean, Float.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(bean, Long.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(bean, Double.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                pd.getWriteMethod().invoke(bean, Short.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            try{
                if (value.length() == 1){
                    pd.getWriteMethod().invoke(bean, Character.valueOf(value.charAt(0)));
                    return;
                }
            }
            catch(IllegalArgumentException ignored){}
            
         
            try{
                pd.getWriteMethod().invoke(bean, Boolean.valueOf(value));
                return;
            }
            catch(IllegalArgumentException ignored){}

            throw new IllegalStateException("unable to set property for: " + name);
        }
    }
}
